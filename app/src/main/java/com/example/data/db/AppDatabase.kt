package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        Tournament::class,
        Participant::class,
        TournamentMatch::class,
        GroupStanding::class,
        ComplaintSuggestion::class,
        TournamentLog::class,
        AppUser::class,
        NationalTeam::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournaDao(): TournaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tourna_database.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.tournaDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: TournaDao) {
                // 1. Seed World National Teams Database
                dao.insertNationalTeams(WorldNationalTeamsData.allTeams)

                // 2. Default Initial User (Logged in with Google - Name Only without photo)
                val defaultUser = AppUser(
                    id = "user_default_owner",
                    displayName = "الكابتن أحمد الصقر",
                    email = "sahbmhmd088@gmail.com",
                    isOwnerEligible = true
                )
                dao.saveCurrentUser(defaultUser)

                // 3. Showcase Initial Tournament with full configuration
                val tournamentId = "tourna_efootball_cup_2026"
                val sampleTournament = Tournament(
                    id = tournamentId,
                    title = "كأس النخبة العالمية 2026",
                    gameTitle = "eFootball",
                    description = "بطولة رسمية كبرى لمنتخبات العالم، تقام بنظام المجموعات والإقصائيات، مع بث إلزامي عبر قناة التليجرام «صناعة البطولات».",
                    ownerId = defaultUser.id,
                    ownerName = defaultUser.displayName,
                    ownerRoleMode = "PARTICIPANT", // Owner participates as player
                    maxParticipants = 8,
                    status = "IN_PROGRESS",
                    rouletteAttempts = 3,
                    matchDurationMinutes = 10,
                    maxSubstitutions = 6,
                    groupsCount = 2,
                    qualifyPerGroup = 2,
                    hasThirdPlaceMatch = true,
                    isDrawPublished = true,
                    createdAt = System.currentTimeMillis() - 86400000L
                )
                dao.insertTournament(sampleTournament)

                // Seed 8 participants with reserved teams from roulette
                val sampleParticipants = listOf(
                    Participant(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        userId = defaultUser.id,
                        displayName = defaultUser.displayName,
                        reservedTeamId = "DZ",
                        reservedTeamName = "الجزائر",
                        reservedTeamFlag = "🇩🇿",
                        rouletteSpinsRemaining = 2,
                        isOwner = true,
                        status = "ACTIVE",
                        hasConsentedRules = true,
                        hasConsentedBroadcast = true,
                        connectionState = "ONLINE"
                    ),
                    Participant(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        userId = "u2",
                        displayName = "فيصل القحطاني",
                        reservedTeamId = "BR",
                        reservedTeamName = "البرازيل",
                        reservedTeamFlag = "🇧🇷",
                        rouletteSpinsRemaining = 1,
                        isOwner = false,
                        status = "ACTIVE",
                        hasConsentedRules = true,
                        hasConsentedBroadcast = true,
                        connectionState = "ONLINE"
                    ),
                    Participant(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        userId = "u3",
                        displayName = "عمر الشامي",
                        reservedTeamId = "FR",
                        reservedTeamName = "فرنسا",
                        reservedTeamFlag = "🇫🇷",
                        rouletteSpinsRemaining = 0,
                        isOwner = false,
                        status = "ACTIVE",
                        hasConsentedRules = true,
                        hasConsentedBroadcast = true,
                        connectionState = "RECENT_DISCONNECT",
                        disconnectDurationSeconds = 120L
                    ),
                    Participant(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        userId = "u4",
                        displayName = "محمود البدري",
                        reservedTeamId = "ES",
                        reservedTeamName = "إسبانيا",
                        reservedTeamFlag = "🇪🇸",
                        rouletteSpinsRemaining = 2,
                        isOwner = false,
                        status = "ACTIVE",
                        hasConsentedRules = true,
                        hasConsentedBroadcast = true,
                        connectionState = "ONLINE"
                    ),
                    Participant(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        userId = "u5",
                        displayName = "طارق المرزوقي",
                        reservedTeamId = "MA",
                        reservedTeamName = "المغرب",
                        reservedTeamFlag = "🇲🇦",
                        rouletteSpinsRemaining = 1,
                        isOwner = false,
                        status = "ACTIVE",
                        hasConsentedRules = true,
                        hasConsentedBroadcast = true,
                        connectionState = "ONLINE"
                    ),
                    Participant(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        userId = "u6",
                        displayName = "سالم الدوسري",
                        reservedTeamId = "SA",
                        reservedTeamName = "السعودية",
                        reservedTeamFlag = "🇸🇦",
                        rouletteSpinsRemaining = 0,
                        isOwner = false,
                        status = "ACTIVE",
                        hasConsentedRules = true,
                        hasConsentedBroadcast = true,
                        connectionState = "OFFLINE",
                        disconnectDurationSeconds = 480L
                    ),
                    Participant(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        userId = "u7",
                        displayName = "زياد حيدر",
                        reservedTeamId = "AR",
                        reservedTeamName = "الأرجنتين",
                        reservedTeamFlag = "🇦🇷",
                        rouletteSpinsRemaining = 1,
                        isOwner = false,
                        status = "ACTIVE",
                        hasConsentedRules = true,
                        hasConsentedBroadcast = true,
                        connectionState = "ONLINE"
                    ),
                    Participant(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        userId = "u8",
                        displayName = "كريم بن سالم",
                        reservedTeamId = "DE",
                        reservedTeamName = "ألمانيا",
                        reservedTeamFlag = "🇩🇪",
                        rouletteSpinsRemaining = 2,
                        isOwner = false,
                        status = "ACTIVE",
                        hasConsentedRules = true,
                        hasConsentedBroadcast = true,
                        connectionState = "ONLINE"
                    )
                )
                sampleParticipants.forEach { dao.insertParticipant(it) }

                // Seed Standings for Groups (Group A & Group B)
                val standings = listOf(
                    GroupStanding("st_1", tournamentId, "المجموعة (أ)", defaultUser.id, defaultUser.displayName, "الجزائر", "🇩🇿", 1, 1, 0, 0, 2, 1, 1, 3),
                    GroupStanding("st_2", tournamentId, "المجموعة (أ)", "u2", "فيصل القحطاني", "البرازيل", "🇧🇷", 1, 1, 0, 0, 3, 2, 1, 3),
                    GroupStanding("st_3", tournamentId, "المجموعة (أ)", "u3", "عمر الشامي", "فرنسا", "🇫🇷", 1, 0, 0, 1, 2, 3, -1, 0),
                    GroupStanding("st_4", tournamentId, "المجموعة (أ)", "u4", "محمود البدري", "إسبانيا", "🇪🇸", 1, 0, 0, 1, 1, 2, -1, 0),

                    GroupStanding("st_5", tournamentId, "المجموعة (ب)", "u5", "طارق المرزوقي", "المغرب", "🇲🇦", 1, 1, 0, 0, 2, 0, 2, 3),
                    GroupStanding("st_6", tournamentId, "المجموعة (ب)", "u7", "زياد حيدر", "الأرجنتين", "🇦🇷", 1, 0, 1, 0, 1, 1, 0, 1),
                    GroupStanding("st_7", tournamentId, "المجموعة (ب)", "u8", "كريم بن سالم", "ألمانيا", "🇩🇪", 1, 0, 1, 0, 1, 1, 0, 1),
                    GroupStanding("st_8", tournamentId, "المجموعة (ب)", "u6", "سالم الدوسري", "السعودية", "🇸🇦", 1, 0, 0, 1, 0, 2, -2, 0)
                )
                dao.insertStandings(standings)

                // Seed Matches
                val sampleMatches = listOf(
                    TournamentMatch(
                        id = "match_1",
                        tournamentId = tournamentId,
                        stage = "GROUP",
                        groupName = "المجموعة (أ)",
                        roundNumber = 1,
                        player1Id = defaultUser.id,
                        player1Name = defaultUser.displayName,
                        player1Team = "الجزائر",
                        player1Flag = "🇩🇿",
                        player2Id = "u4",
                        player2Name = "محمود البدري",
                        player2Team = "إسبانيا",
                        player2Flag = "🇪🇸",
                        scheduledDate = "2026-11-01",
                        scheduledTime = "20:00",
                        status = "COMPLETED",
                        player1Ready = true,
                        player2Ready = true,
                        player1LineupApproved = true,
                        player2LineupApproved = true,
                        player1ExtractedSquad = "1- بن بوط | 4- توغاي | 2- ماندي | 7- محرز | 10- بن ناصر | 9- بونجاح",
                        player2ExtractedSquad = "1- سيمون | 3- لي نورماند | 14- لابورت | 16- رودري | 10- أولمو | 19- يامال",
                        scorePlayer1 = 2,
                        scorePlayer2 = 1,
                        submittedByWinnerId = defaultUser.id,
                        isResultApprovedByOwner = true,
                        approvalNote = "تم اعتماد النتيجة رسمياً من المالك بعد مراجعة تقرير البث والتليجرام."
                    ),
                    TournamentMatch(
                        id = "match_2",
                        tournamentId = tournamentId,
                        stage = "GROUP",
                        groupName = "المجموعة (أ)",
                        roundNumber = 1,
                        player1Id = "u2",
                        player1Name = "فيصل القحطاني",
                        player1Team = "البرازيل",
                        player1Flag = "🇧🇷",
                        player2Id = "u3",
                        player2Name = "عمر الشامي",
                        player2Team = "فرنسا",
                        player2Flag = "🇫🇷",
                        scheduledDate = "2026-11-01",
                        scheduledTime = "20:30",
                        status = "COMPLETED",
                        player1Ready = true,
                        player2Ready = true,
                        player1LineupApproved = true,
                        player2LineupApproved = true,
                        scorePlayer1 = 3,
                        scorePlayer2 = 2,
                        submittedByWinnerId = "u2",
                        isResultApprovedByOwner = true
                    ),
                    TournamentMatch(
                        id = "match_3",
                        tournamentId = tournamentId,
                        stage = "GROUP",
                        groupName = "المجموعة (أ)",
                        roundNumber = 2,
                        player1Id = defaultUser.id,
                        player1Name = defaultUser.displayName,
                        player1Team = "الجزائر",
                        player1Flag = "🇩🇿",
                        player2Id = "u2",
                        player2Name = "فيصل القحطاني",
                        player2Team = "البرازيل",
                        player2Flag = "🇧🇷",
                        scheduledDate = "2026-11-02",
                        scheduledTime = "21:00",
                        status = "UPCOMING",
                        player1Ready = true,
                        player2Ready = false,
                        player1LineupApproved = true,
                        player1ExtractedSquad = "1- بن بوط | 2- ماندي | 5- بن سبعيني | 8- عوار | 7- محرز | 11- عمورة"
                    ),
                    TournamentMatch(
                        id = "match_4",
                        tournamentId = tournamentId,
                        stage = "GROUP",
                        groupName = "المجموعة (ب)",
                        roundNumber = 1,
                        player1Id = "u5",
                        player1Name = "طارق المرزوقي",
                        player1Team = "المغرب",
                        player1Flag = "🇲🇦",
                        player2Id = "u6",
                        player2Name = "سالم الدوسري",
                        player2Team = "السعودية",
                        player2Flag = "🇸🇦",
                        scheduledDate = "2026-11-01",
                        scheduledTime = "21:30",
                        status = "COMPLETED",
                        player1Ready = true,
                        player2Ready = true,
                        player1LineupApproved = true,
                        player2LineupApproved = true,
                        scorePlayer1 = 2,
                        scorePlayer2 = 0,
                        submittedByWinnerId = "u5",
                        isResultApprovedByOwner = true
                    )
                )
                dao.insertMatches(sampleMatches)

                // Seed logs
                dao.insertLog(
                    TournamentLog(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        title = "نشر القرعة والمواعيد",
                        details = "قام المالك باعتماد المسودة وتثبيت جدول مباريات الجولة الأولى والثانية.",
                        category = "SCHEDULE"
                    )
                )
                dao.insertLog(
                    TournamentLog(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournamentId,
                        title = "اكتمال حجز المنتخبات",
                        details = "اكتمل حجز جميع مقاعد البطولة (8 لاعبين) عبر روليت المنتخبات المستقلة.",
                        category = "ROULETTE"
                    )
                )

                // Seed sample complaint/suggestion
                dao.insertComplaint(
                    ComplaintSuggestion(
                        id = UUID.randomUUID().toString(),
                        userId = "u2",
                        userName = "فيصل القحطاني",
                        type = "FEATURE_REQUEST",
                        title = "إضافة وضع التدريب الحر للروليت",
                        details = "نرجو إتاحة روليت تجريبي في قسم الإحصائيات قبل بدء البطولات للتعرف على المنتخبات.",
                        status = "ACCEPTED",
                        ownerReply = "فكرة ممتازة وتم إدراجها ضمن التحديث القادم للتطبيق بإذن الله."
                    )
                )
            }
        }
    }
}
