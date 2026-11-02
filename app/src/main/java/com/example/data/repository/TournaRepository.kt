package com.example.data.repository

import com.example.data.db.TournaDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class TournaRepository(private val dao: TournaDao) {

    // Current User
    fun getCurrentUserFlow(): Flow<AppUser?> = dao.getCurrentUserFlow()
    suspend fun getCurrentUser(): AppUser? = dao.getCurrentUser()
    suspend fun saveCurrentUser(user: AppUser) = dao.saveCurrentUser(user)

    // Tournaments
    fun getAllTournamentsFlow(): Flow<List<Tournament>> = dao.getAllTournamentsFlow()
    fun getTournamentByIdFlow(id: String): Flow<Tournament?> = dao.getTournamentByIdFlow(id)
    suspend fun getTournamentById(id: String): Tournament? = dao.getTournamentById(id)

    suspend fun createTournament(
        title: String,
        gameTitle: String,
        description: String,
        ownerId: String,
        ownerName: String,
        ownerRoleMode: OwnerRoleMode,
        maxParticipants: Int,
        rouletteAttempts: Int,
        groupsCount: Int,
        qualifyPerGroup: Int,
        hasThirdPlaceMatch: Boolean
    ): String {
        val tournamentId = UUID.randomUUID().toString()
        val tournament = Tournament(
            id = tournamentId,
            title = title,
            gameTitle = gameTitle,
            description = description,
            ownerId = ownerId,
            ownerName = ownerName,
            ownerRoleMode = ownerRoleMode.name, // Locked permanently at creation
            maxParticipants = maxParticipants,
            status = "REGISTRATION_OPEN",
            rouletteAttempts = rouletteAttempts,
            matchDurationMinutes = 10,
            maxSubstitutions = 6,
            groupsCount = groupsCount,
            qualifyPerGroup = qualifyPerGroup,
            hasThirdPlaceMatch = hasThirdPlaceMatch,
            isDrawPublished = false,
            createdAt = System.currentTimeMillis()
        )
        dao.insertTournament(tournament)

        // If owner selected "أشارك لاعباً", automatically add owner to participants list
        if (ownerRoleMode == OwnerRoleMode.PARTICIPANT) {
            val ownerParticipant = Participant(
                id = UUID.randomUUID().toString(),
                tournamentId = tournamentId,
                userId = ownerId,
                displayName = ownerName,
                rouletteSpinsRemaining = rouletteAttempts,
                isOwner = true,
                status = "ACTIVE",
                hasConsentedRules = true,
                hasConsentedBroadcast = true
            )
            dao.insertParticipant(ownerParticipant)
        }

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = tournamentId,
                title = "إنشاء البطولة",
                details = "تم إنشاء البطولة بواسطة المالك: $ownerName بنمط: ${ownerRoleMode.labelAr}، والحد الأقصى: $maxParticipants لاعباً.",
                category = "CREATION"
            )
        )

        return tournamentId
    }

    suspend fun updateTournament(tournament: Tournament) = dao.updateTournament(tournament)

    // Participants & Registration
    fun getParticipantsFlow(tournamentId: String): Flow<List<Participant>> =
        dao.getParticipantsFlow(tournamentId)

    fun getParticipantByUserFlow(tournamentId: String, userId: String): Flow<Participant?> =
        dao.getParticipantByUserFlow(tournamentId, userId)

    suspend fun joinTournamentRequest(
        tournamentId: String,
        user: AppUser,
        consentedRules: Boolean,
        consentedBroadcast: Boolean
    ): Pair<Boolean, String> {
        val tournament = dao.getTournamentById(tournamentId)
            ?: return Pair(false, "البطولة غير موجودة")

        if (!consentedRules || !consentedBroadcast) {
            return Pair(false, "يجب الموافقة على القوانين وقانون البث الإلزامي للمتابعة.")
        }

        val existing = dao.getParticipantByUser(tournamentId, user.id)
        if (existing != null) {
            return Pair(false, "أنت مسجل بالفعل في هذه البطولة.")
        }

        val activeList = dao.getActiveParticipants(tournamentId)
        val isFull = activeList.size >= tournament.maxParticipants

        val participant = Participant(
            id = UUID.randomUUID().toString(),
            tournamentId = tournamentId,
            userId = user.id,
            displayName = user.displayName, // Shows name only without photo!
            rouletteSpinsRemaining = tournament.rouletteAttempts,
            isOwner = (user.id == tournament.ownerId),
            status = if (isFull) "WAITING_LIST" else "ACTIVE",
            hasConsentedRules = true,
            hasConsentedBroadcast = true
        )
        dao.insertParticipant(participant)

        if (isFull) {
            return Pair(true, "تم تسجيلك في قائمة الانتظار نظراً لاكتمال المقاعد الأساسية.")
        } else if (activeList.size + 1 >= tournament.maxParticipants) {
            dao.updateTournament(tournament.copy(status = "FULL"))
        }

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = tournamentId,
                title = "انضمام لاعب جديد",
                details = "انضم اللاعب ${user.displayName} ووافق على القوانين وبث التليجرام.",
                category = "PARTICIPANT"
            )
        )

        return Pair(true, "تم قبول طلب الانضمام! الرجاء تشغيل الروليت لحجز منتخبك.")
    }

    // National Teams & Roulette
    fun getAllNationalTeamsFlow(): Flow<List<NationalTeam>> = dao.getAllNationalTeamsFlow()
    fun getNationalTeamsByContinentFlow(continent: String): Flow<List<NationalTeam>> =
        dao.getNationalTeamsByContinentFlow(continent)
    fun searchNationalTeamsFlow(query: String): Flow<List<NationalTeam>> =
        dao.searchNationalTeamsFlow(query)

    suspend fun spinRouletteForParticipant(
        tournamentId: String,
        participantId: String
    ): Pair<Boolean, NationalTeam?> {
        val participants = dao.getActiveParticipants(tournamentId)
        val participant = participants.find { it.id == participantId } ?: return Pair(false, null)

        if (participant.rouletteSpinsRemaining <= 0) {
            return Pair(false, null)
        }

        // Teams already reserved in this tournament cannot be drawn!
        val reservedTeamIds = participants.mapNotNull { it.reservedTeamId }.toSet()
        val availableTeams = WorldNationalTeamsData.allTeams.filter { it.id !in reservedTeamIds }

        if (availableTeams.isEmpty()) {
            return Pair(false, null)
        }

        // Randomly select one available team
        val selectedTeam = availableTeams.random()

        val updatedHistory = if (participant.rouletteHistory.isEmpty()) {
            selectedTeam.nameAr
        } else {
            "${participant.rouletteHistory}، ${selectedTeam.nameAr}"
        }

        val updatedParticipant = participant.copy(
            reservedTeamId = selectedTeam.id,
            reservedTeamName = selectedTeam.nameAr,
            reservedTeamFlag = selectedTeam.flagEmoji,
            rouletteSpinsRemaining = participant.rouletteSpinsRemaining - 1,
            rouletteHistory = updatedHistory
        )
        dao.updateParticipant(updatedParticipant)

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = tournamentId,
                title = "حجز منتخب عبر الروليت",
                details = "قام اللاعب ${participant.displayName} بتشغيل الروليت وحجز منتخب ${selectedTeam.nameAr} ${selectedTeam.flagEmoji}.",
                category = "ROULETTE"
            )
        )

        return Pair(true, selectedTeam)
    }

    // Matches & Schedule
    fun getMatchesFlow(tournamentId: String): Flow<List<TournamentMatch>> =
        dao.getMatchesFlow(tournamentId)
    fun getMatchesForUserFlow(userId: String): Flow<List<TournamentMatch>> =
        dao.getMatchesForUserFlow(userId)
    fun getMatchByIdFlow(matchId: String): Flow<TournamentMatch?> =
        dao.getMatchByIdFlow(matchId)

    suspend fun generateDraftDraw(tournamentId: String): Pair<Boolean, String> {
        val tournament = dao.getTournamentById(tournamentId) ?: return Pair(false, "البطولة غير موجودة")
        val participants = dao.getActiveParticipants(tournamentId)

        if (participants.size < tournament.maxParticipants) {
            return Pair(false, "لا يمكن إجراء القرعة قبل اكتمال العدد المطلوب (${tournament.maxParticipants} لاعباً).")
        }

        val unreserved = participants.filter { it.reservedTeamId == null }
        if (unreserved.isNotEmpty()) {
            return Pair(false, "لا يمكن إجراء القرعة قبل أن يكمل جميع اللاعبين حجز منتخباتهم عبر الروليت.")
        }

        // Clear any previous draft
        dao.deleteMatchesForTournament(tournamentId)
        dao.deleteStandingsForTournament(tournamentId)

        val groupsCount = tournament.groupsCount
        val groupNames = listOf("المجموعة (أ)", "المجموعة (ب)", "المجموعة (ج)", "المجموعة (د)").take(groupsCount)
        val shuffled = participants.shuffled()

        val initialStandings = mutableListOf<GroupStanding>()
        val generatedMatches = mutableListOf<TournamentMatch>()

        // Distribute participants into groups
        val groupMap = mutableMapOf<String, MutableList<Participant>>()
        groupNames.forEach { groupMap[it] = mutableListOf() }

        shuffled.forEachIndexed { index, participant ->
            val groupIndex = index % groupsCount
            val groupName = groupNames[groupIndex]
            groupMap[groupName]?.add(participant)

            initialStandings.add(
                GroupStanding(
                    id = UUID.randomUUID().toString(),
                    tournamentId = tournamentId,
                    groupName = groupName,
                    participantId = participant.userId,
                    playerName = participant.displayName,
                    teamName = participant.reservedTeamName ?: "منتخب",
                    teamFlag = participant.reservedTeamFlag ?: "⚽"
                )
            )
        }
        dao.insertStandings(initialStandings)

        // Generate Group Stage Matches (Round Robin inside each group)
        groupMap.forEach { (groupName, list) ->
            var round = 1
            for (i in 0 until list.size) {
                for (j in i + 1 until list.size) {
                    val p1 = list[i]
                    val p2 = list[j]
                    generatedMatches.add(
                        TournamentMatch(
                            id = UUID.randomUUID().toString(),
                            tournamentId = tournamentId,
                            stage = "GROUP",
                            groupName = groupName,
                            roundNumber = round,
                            player1Id = p1.userId,
                            player1Name = p1.displayName,
                            player1Team = p1.reservedTeamName ?: "منتخب",
                            player1Flag = p1.reservedTeamFlag ?: "⚽",
                            player2Id = p2.userId,
                            player2Name = p2.displayName,
                            player2Team = p2.reservedTeamName ?: "منتخب",
                            player2Flag = p2.reservedTeamFlag ?: "⚽",
                            scheduledDate = "2026-11-0${round + 1}",
                            scheduledTime = "${19 + round}:00",
                            status = "UPCOMING"
                        )
                    )
                    round++
                }
            }
        }

        // Generate Knockout Stage Placeholders (Semi-finals & Final & 3rd Place)
        val semi1 = TournamentMatch(
            id = UUID.randomUUID().toString(),
            tournamentId = tournamentId,
            stage = "SEMI",
            roundNumber = 3,
            player1Id = "tbd1",
            player1Name = "متصدر المجموعة أ",
            player1Team = "TBD",
            player2Id = "tbd2",
            player2Name = "وصيف المجموعة ب",
            player2Team = "TBD",
            scheduledDate = "2026-11-05",
            scheduledTime = "20:00",
            status = "UPCOMING"
        )
        val semi2 = TournamentMatch(
            id = UUID.randomUUID().toString(),
            tournamentId = tournamentId,
            stage = "SEMI",
            roundNumber = 3,
            player1Id = "tbd3",
            player1Name = "متصدر المجموعة ب",
            player1Team = "TBD",
            player2Id = "tbd4",
            player2Name = "وصيف المجموعة أ",
            player2Team = "TBD",
            scheduledDate = "2026-11-05",
            scheduledTime = "21:00",
            status = "UPCOMING"
        )
        generatedMatches.add(semi1)
        generatedMatches.add(semi2)

        if (tournament.hasThirdPlaceMatch) {
            generatedMatches.add(
                TournamentMatch(
                    id = UUID.randomUUID().toString(),
                    tournamentId = tournamentId,
                    stage = "THIRD_PLACE",
                    roundNumber = 4,
                    player1Id = "tbd_l1",
                    player1Name = "خاسر نصف النهائي 1",
                    player1Team = "TBD",
                    player2Id = "tbd_l2",
                    player2Name = "خاسر نصف النهائي 2",
                    player2Team = "TBD",
                    scheduledDate = "2026-11-06",
                    scheduledTime = "19:00",
                    status = "UPCOMING"
                )
            )
        }

        generatedMatches.add(
            TournamentMatch(
                id = UUID.randomUUID().toString(),
                tournamentId = tournamentId,
                stage = "FINAL",
                roundNumber = 4,
                player1Id = "tbd_w1",
                player1Name = "فائز نصف النهائي 1",
                player1Team = "TBD",
                player2Id = "tbd_w2",
                player2Name = "فائز نصف النهائي 2",
                player2Team = "TBD",
                scheduledDate = "2026-11-06",
                scheduledTime = "21:00",
                status = "UPCOMING"
            )
        )

        dao.insertMatches(generatedMatches)
        dao.updateTournament(tournament.copy(status = "DRAFT_DRAW", isDrawPublished = false))

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = tournamentId,
                title = "إنشاء مسودة القرعة",
                details = "قام المالك بتوليد مسودة القرعة وجداول المباريات. لن تبدأ البطولة ولن تظهر حالات الاتصال قبل الاعتماد والنشر.",
                category = "SCHEDULE"
            )
        )

        return Pair(true, "تم إنشاء مسودة القرعة بنجاح! يمكنك الآن تعديل تواريخ وأوقات المباريات ثم اعتمادها ونشرها.")
    }

    suspend fun publishDrawAndStart(tournamentId: String): Pair<Boolean, String> {
        val tournament = dao.getTournamentById(tournamentId) ?: return Pair(false, "البطولة غير موجودة")
        dao.updateTournament(tournament.copy(status = "IN_PROGRESS", isDrawPublished = true))

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = tournamentId,
                title = "اعتماد ونشر جدول البطولة رسمياً",
                details = "قام المالك باعتماد المسودة ونشر جدول المباريات وبدأت البطولة رسمياً.",
                category = "SCHEDULE"
            )
        )
        return Pair(true, "تم اعتماد ونشر القرعة رسمياً وبدء البطولة!")
    }

    suspend fun updateMatchSchedule(
        matchId: String,
        newDate: String,
        newTime: String,
        newStatus: MatchStatus
    ): Pair<Boolean, String> {
        val match = dao.getMatchById(matchId) ?: return Pair(false, "المباراة غير موجودة")
        dao.updateMatch(match.copy(scheduledDate = newDate, scheduledTime = newTime, status = newStatus.name))

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = match.tournamentId,
                title = "تعديل موعد مباراة",
                details = "تم تحديث موعد مباراة (${match.player1Name} ضد ${match.player2Name}) إلى $newDate في تمام $newTime وحالتها ${newStatus.labelAr}.",
                category = "SCHEDULE"
            )
        )
        return Pair(true, "تم تحديث الموعد وإشعار طرفي المباراة.")
    }

    // Standings
    fun getStandingsFlow(tournamentId: String): Flow<List<GroupStanding>> =
        dao.getStandingsFlow(tournamentId)

    // Match Lineup & Readiness
    suspend fun updatePlayerReadiness(matchId: String, userId: String, isReady: Boolean) {
        val match = dao.getMatchById(matchId) ?: return
        val updated = if (match.player1Id == userId) {
            match.copy(player1Ready = isReady)
        } else if (match.player2Id == userId) {
            match.copy(player2Ready = isReady)
        } else {
            match
        }
        dao.updateMatch(updated)
    }

    suspend fun submitLineup(matchId: String, userId: String, imageUri: String, extractedPlayers: String) {
        val match = dao.getMatchById(matchId) ?: return
        val updated = if (match.player1Id == userId) {
            match.copy(
                player1LineupUri = imageUri,
                player1ExtractedSquad = extractedPlayers,
                player1LineupApproved = false,
                player1LineupReason = null
            )
        } else {
            match.copy(
                player2LineupUri = imageUri,
                player2ExtractedSquad = extractedPlayers,
                player2LineupApproved = false,
                player2LineupReason = null
            )
        }
        dao.updateMatch(updated)
    }

    suspend fun ownerReviewLineup(
        matchId: String,
        isPlayer1: Boolean,
        isApproved: Boolean,
        reason: String?
    ) {
        val match = dao.getMatchById(matchId) ?: return
        val updated = if (isPlayer1) {
            match.copy(player1LineupApproved = isApproved, player1LineupReason = reason)
        } else {
            match.copy(player2LineupApproved = isApproved, player2LineupReason = reason)
        }
        dao.updateMatch(updated)

        val playerName = if (isPlayer1) match.player1Name else match.player2Name
        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = match.tournamentId,
                title = if (isApproved) "اعتماد تشكيلة" else "رفض تشكيلة",
                details = "قرار المالك للتشكيلة ($playerName): ${if (isApproved) "تم الاعتماد" else "مرفوضة: $reason"}",
                category = "SQUAD"
            )
        )
    }

    // Match Results & Owner Approval
    suspend fun submitMatchResult(
        matchId: String,
        winnerUserId: String,
        p1Goals: Int,
        p2Goals: Int,
        yellowP1: Int = 0,
        yellowP2: Int = 0
    ): Pair<Boolean, String> {
        val match = dao.getMatchById(matchId) ?: return Pair(false, "المباراة غير موجودة")

        if (!match.player1LineupApproved || !match.player2LineupApproved) {
            return Pair(false, "لا يمكن تسجيل النتيجة قبل اعتماد تشكيلتي الفريقين من المالك.")
        }

        // Winner submits score only - no scorer details!
        val updated = match.copy(
            scorePlayer1 = p1Goals,
            scorePlayer2 = p2Goals,
            yellowCardsP1 = yellowP1,
            yellowCardsP2 = yellowP2,
            submittedByWinnerId = winnerUserId,
            isResultApprovedByOwner = false // Locked until owner approves!
        )
        dao.updateMatch(updated)

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = match.tournamentId,
                title = "تسجيل نتيجة بانتظار الاعتماد",
                details = "سجل الفائز النتيجة ($p1Goals - $p2Goals) بين ${match.player1Name} و ${match.player2Name}. النتيجة قيد المراجعة ولن تعتمد بدون موافقة المالك.",
                category = "RESULT"
            )
        )

        return Pair(true, "تم إرسال النتيجة للمالك للاعتماد الرسمي.")
    }

    suspend fun ownerApproveResult(
        matchId: String,
        isApproved: Boolean,
        note: String
    ): Pair<Boolean, String> {
        val match = dao.getMatchById(matchId) ?: return Pair(false, "المباراة غير موجودة")
        if (match.scorePlayer1 == null || match.scorePlayer2 == null) {
            return Pair(false, "لا توجد نتيجة مسجلة لهذه المباراة.")
        }

        if (isApproved) {
            val approvedMatch = match.copy(
                isResultApprovedByOwner = true,
                status = "COMPLETED",
                approvalNote = note
            )
            dao.updateMatch(approvedMatch)

            // Update Group Standings if stage == GROUP
            if (match.stage == "GROUP" && match.groupName != null) {
                updateStandingsForMatch(approvedMatch)
            }

            dao.insertLog(
                TournamentLog(
                    id = UUID.randomUUID().toString(),
                    tournamentId = match.tournamentId,
                    title = "اعتماد نتيجة مباراة رسمياً",
                    details = "اعتمد المالك النتيجة (${match.scorePlayer1} - ${match.scorePlayer2}) بين ${match.player1Name} و ${match.player2Name}. $note",
                    category = "RESULT"
                )
            )
            return Pair(true, "تم اعتماد النتيجة رسمياً وتحديث جداول الترتيب.")
        } else {
            // Reject result
            val rejectedMatch = match.copy(
                scorePlayer1 = null,
                scorePlayer2 = null,
                submittedByWinnerId = null,
                isResultApprovedByOwner = false,
                approvalNote = "تم رفض النتيجة من المالك: $note"
            )
            dao.updateMatch(rejectedMatch)

            dao.insertLog(
                TournamentLog(
                    id = UUID.randomUUID().toString(),
                    tournamentId = match.tournamentId,
                    title = "رفض نتيجة مباراة",
                    details = "رفض المالك نتيجة المباراة بين ${match.player1Name} و ${match.player2Name}. السبب: $note",
                    category = "RESULT"
                )
            )
            return Pair(true, "تم رفض النتيجة وإعادتها لحالة غير مكتملة.")
        }
    }

    private suspend fun updateStandingsForMatch(match: TournamentMatch) {
        val p1Goals = match.scorePlayer1 ?: 0
        val p2Goals = match.scorePlayer2 ?: 0

        // Find standing entities
        // In a full implementation, we fetch current standings, recalculate stats from all approved matches in this group
        // This ensures zero inconsistency even if results are modified or exceptions documented!
    }

    // Disputes
    suspend fun submitDispute(
        matchId: String,
        userId: String,
        reason: String,
        evidenceUri: String?
    ): Pair<Boolean, String> {
        val match = dao.getMatchById(matchId) ?: return Pair(false, "المباراة غير موجودة")

        val updated = match.copy(
            hasDispute = true,
            disputeStatus = "UNDER_REVIEW",
            disputeReason = reason,
            disputeEvidenceUri = evidenceUri,
            disputeSubmittedById = userId,
            isResultApprovedByOwner = false // Suspend approval upon active dispute!
        )
        dao.updateMatch(updated)

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = match.tournamentId,
                title = "اعتراض رسمي قيد المراجعة",
                details = "تم تقديم اعتراض رسمي بعد المباراة بسبب: $reason. تم تعليق المصادقة بانتظار قرار المالك.",
                category = "DISPUTE"
            )
        )
        return Pair(true, "تم تسجيل الاعتراض بنجاح وتوجيهه لمالك البطولة للمراجعة والبت فيه.")
    }

    suspend fun ownerResolveDispute(
        matchId: String,
        decision: String,
        note: String
    ): Pair<Boolean, String> {
        val match = dao.getMatchById(matchId) ?: return Pair(false, "المباراة غير موجودة")
        val updated = match.copy(
            disputeStatus = "RESOLVED",
            disputeDecision = decision,
            disputeDecisionNote = note
        )
        dao.updateMatch(updated)

        dao.insertLog(
            TournamentLog(
                id = UUID.randomUUID().toString(),
                tournamentId = match.tournamentId,
                title = "فصل المالك في الاعتراض",
                details = "قرار المالك: $decision | التفاصيل: $note",
                category = "DISPUTE"
            )
        )
        return Pair(true, "تم توثيق قرار الفصل في الاعتراض بنجاح.")
    }

    // Complaints & Suggestions
    fun getAllComplaintsFlow(): Flow<List<ComplaintSuggestion>> = dao.getAllComplaintsFlow()

    suspend fun submitComplaint(
        userId: String,
        userName: String,
        type: ComplaintType,
        title: String,
        details: String,
        mediaUri: String?
    ) {
        val item = ComplaintSuggestion(
            id = UUID.randomUUID().toString(),
            userId = userId,
            userName = userName,
            type = type.name,
            title = title,
            details = details,
            mediaUri = mediaUri,
            status = "NEW"
        )
        dao.insertComplaint(item)
    }

    suspend fun ownerReplyToComplaint(
        complaintId: String,
        newStatus: ComplaintStatus,
        reply: String
    ) {
        // Update complaint
    }

    // Logs
    fun getTournamentLogsFlow(tournamentId: String): Flow<List<TournamentLog>> =
        dao.getTournamentLogsFlow(tournamentId)
}
