package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.TournaRepository
import com.example.service.NetworkConnectionMonitor
import com.example.service.SquadImageAnalyzer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Tournaments : AppScreen()
    object MyMatches : AppScreen()
    object More : AppScreen()
    data class TournamentDetail(val tournamentId: String) : AppScreen()
    object CreateTournament : AppScreen()
    data class MatchDetail(val matchId: String) : AppScreen()
    object Complaints : AppScreen()
    object SetupGuide : AppScreen()
}

enum class TournamentTab(val labelAr: String) {
    OVERVIEW("نظرة عامة"),
    PARTICIPANTS("المشاركون"),
    ROULETTE("الروليت"),
    GROUPS("المجموعات والترتيب"),
    BRACKET("المسار"),
    MATCHES("المباريات"),
    STATS("الإحصائيات"),
    MANAGEMENT("الإدارة")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    val repository = TournaRepository(database.tournaDao())
    val networkMonitor = NetworkConnectionMonitor(application)

    // Current navigation state
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Tournaments)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedTournamentTab = MutableStateFlow(TournamentTab.OVERVIEW)
    val selectedTournamentTab: StateFlow<TournamentTab> = _selectedTournamentTab.asStateFlow()

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    fun toggleDarkTheme(enable: Boolean) {
        _isDarkTheme.value = enable
    }

    // Current User (Google Account - Name only without photo)
    val currentUser: StateFlow<AppUser?> = repository.getCurrentUserFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All Tournaments
    val allTournaments: StateFlow<List<Tournament>> = repository.getAllTournamentsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Tournament Details
    private val _selectedTournamentId = MutableStateFlow<String?>(null)
    val selectedTournamentId: StateFlow<String?> = _selectedTournamentId.asStateFlow()

    val currentTournament: StateFlow<Tournament?> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getTournamentByIdFlow(id) else flowOf(null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentParticipants: StateFlow<List<Participant>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getParticipantsFlow(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMatches: StateFlow<List<TournamentMatch>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getMatchesFlow(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStandings: StateFlow<List<GroupStanding>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getStandingsFlow(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentLogs: StateFlow<List<TournamentLog>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getTournamentLogsFlow(id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // User's own matches across all tournaments
    val myMatches: StateFlow<List<TournamentMatch>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getMatchesForUserFlow(user.id) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Match for Details
    private val _selectedMatchId = MutableStateFlow<String?>(null)
    val selectedMatch: StateFlow<TournamentMatch?> = _selectedMatchId
        .flatMapLatest { id ->
            if (id != null) repository.getMatchByIdFlow(id) else flowOf(null)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // National Teams database state
    val allNationalTeams: StateFlow<List<NationalTeam>> = repository.getAllNationalTeamsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Complaints & Suggestions
    val complaints: StateFlow<List<ComplaintSuggestion>> = repository.getAllComplaintsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback message / Snackbar
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Roulette state
    private val _isRouletteSpinning = MutableStateFlow(false)
    val isRouletteSpinning: StateFlow<Boolean> = _isRouletteSpinning.asStateFlow()

    private val _lastWonTeam = MutableStateFlow<NationalTeam?>(null)
    val lastWonTeam: StateFlow<NationalTeam?> = _lastWonTeam.asStateFlow()

    // Squad analysis state
    private val _isAnalyzingSquad = MutableStateFlow(false)
    val isAnalyzingSquad: StateFlow<Boolean> = _isAnalyzingSquad.asStateFlow()

    private val _extractedSquadText = MutableStateFlow<String?>(null)
    val extractedSquadText: StateFlow<String?> = _extractedSquadText.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
        if (screen is AppScreen.TournamentDetail) {
            _selectedTournamentId.value = screen.tournamentId
            _selectedTournamentTab.value = TournamentTab.OVERVIEW
        } else if (screen is AppScreen.MatchDetail) {
            _selectedMatchId.value = screen.matchId
        }
    }

    fun selectTournamentTab(tab: TournamentTab) {
        _selectedTournamentTab.value = tab
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    // Google Sign-In update (Only display name, no avatar/photo)
    fun updateGoogleUser(name: String, email: String) {
        viewModelScope.launch {
            val user = AppUser(
                id = currentUser.value?.id ?: "user_owner_${System.currentTimeMillis()}",
                displayName = name.trim(),
                email = email.trim(),
                isOwnerEligible = true
            )
            repository.saveCurrentUser(user)
            _uiMessage.value = "تم تسجيل الدخول بحساب Google: ${user.displayName}"
        }
    }

    // Create Tournament with locked owner mode
    fun createTournament(
        title: String,
        gameTitle: String,
        description: String,
        ownerRoleMode: OwnerRoleMode,
        maxParticipants: Int,
        rouletteAttempts: Int,
        groupsCount: Int,
        qualifyPerGroup: Int,
        hasThirdPlace: Boolean
    ) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val id = repository.createTournament(
                title = title.trim(),
                gameTitle = gameTitle,
                description = description.trim(),
                ownerId = user.id,
                ownerName = user.displayName,
                ownerRoleMode = ownerRoleMode,
                maxParticipants = maxParticipants,
                rouletteAttempts = rouletteAttempts,
                groupsCount = groupsCount,
                qualifyPerGroup = qualifyPerGroup,
                hasThirdPlaceMatch = hasThirdPlace
            )
            _uiMessage.value = "تم إنشاء البطولة بنجاح! النمط: ${ownerRoleMode.labelAr}"
            navigateTo(AppScreen.TournamentDetail(id))
        }
    }

    // Join tournament
    fun joinTournament(tournamentId: String, consentedRules: Boolean, consentedBroadcast: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val (success, msg) = repository.joinTournamentRequest(
                tournamentId = tournamentId,
                user = user,
                consentedRules = consentedRules,
                consentedBroadcast = consentedBroadcast
            )
            _uiMessage.value = msg
            if (success) {
                _selectedTournamentTab.value = TournamentTab.ROULETTE
            }
        }
    }

    // Spin Roulette
    fun spinRoulette(tournamentId: String, participantId: String) {
        viewModelScope.launch {
            _isRouletteSpinning.value = true
            kotlinx.coroutines.delay(2200) // Realistic spin animation time
            val (success, team) = repository.spinRouletteForParticipant(tournamentId, participantId)
            _isRouletteSpinning.value = false
            if (success && team != null) {
                _lastWonTeam.value = team
                _uiMessage.value = "مبروك! تم حجز منتخب ${team.nameAr} ${team.flagEmoji} باسمك رسمياً وحذفه من المتاح."
            } else {
                _uiMessage.value = "عذراً، نفدت محاولات الروليت المحددة من المالك أو لا توجد منتخبات متبقية."
            }
        }
    }

    // Generate Draft Draw
    fun generateDraftDraw(tournamentId: String) {
        viewModelScope.launch {
            val (success, msg) = repository.generateDraftDraw(tournamentId)
            _uiMessage.value = msg
        }
    }

    // Publish Draw
    fun publishDraw(tournamentId: String) {
        viewModelScope.launch {
            val (success, msg) = repository.publishDrawAndStart(tournamentId)
            _uiMessage.value = msg
        }
    }

    // Update match schedule
    fun updateSchedule(matchId: String, newDate: String, newTime: String, status: MatchStatus) {
        viewModelScope.launch {
            val (success, msg) = repository.updateMatchSchedule(matchId, newDate, newTime, status)
            _uiMessage.value = msg
        }
    }

    // Lineup upload & OCR analysis
    fun uploadAndAnalyzeLineup(matchId: String, imageUri: Uri) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _isAnalyzingSquad.value = true
            val analysis = SquadImageAnalyzer.analyzeLineupImage(getApplication(), imageUri)
            _isAnalyzingSquad.value = false

            if (analysis.isSuccess) {
                _extractedSquadText.value = analysis.formattedSquadText
                repository.submitLineup(
                    matchId = matchId,
                    userId = user.id,
                    imageUri = imageUri.toString(),
                    extractedPlayers = analysis.formattedSquadText
                )
                _uiMessage.value = "تم استخراج التشكيلة الأساسية آلياً وإرسالها للمالك للاعتماد!"
            } else {
                _uiMessage.value = analysis.detectionNotes
            }
        }
    }

    // Owner reviews lineup
    fun ownerReviewLineup(matchId: String, isPlayer1: Boolean, isApproved: Boolean, reason: String?) {
        viewModelScope.launch {
            repository.ownerReviewLineup(matchId, isPlayer1, isApproved, reason)
            _uiMessage.value = if (isApproved) "تم اعتماد التشكيلة بنجاح." else "تم رفض التشكيلة وإبلاغ اللاعب بالسبب."
        }
    }

    // Player confirms readiness
    fun setPlayerReady(matchId: String, isReady: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.updatePlayerReadiness(matchId, user.id, isReady)
            _uiMessage.value = if (isReady) "أنت الآن جاهز للمباراة!" else "تم إلغاء الجاهزية."
        }
    }

    // Winner submits final score
    fun submitScore(matchId: String, p1Score: Int, p2Score: Int, y1: Int = 0, y2: Int = 0) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val (success, msg) = repository.submitMatchResult(
                matchId = matchId,
                winnerUserId = user.id,
                p1Goals = p1Score,
                p2Goals = p2Score,
                yellowP1 = y1,
                yellowP2 = y2
            )
            _uiMessage.value = msg
        }
    }

    // Owner approves match result
    fun ownerApproveResult(matchId: String, isApproved: Boolean, note: String) {
        viewModelScope.launch {
            val (success, msg) = repository.ownerApproveResult(matchId, isApproved, note)
            _uiMessage.value = msg
        }
    }

    // Submit dispute
    fun submitDispute(matchId: String, reason: String, evidenceUri: String?) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val (success, msg) = repository.submitDispute(matchId, user.id, reason, evidenceUri)
            _uiMessage.value = msg
        }
    }

    // Owner resolves dispute
    fun ownerResolveDispute(matchId: String, decision: String, note: String) {
        viewModelScope.launch {
            val (success, msg) = repository.ownerResolveDispute(matchId, decision, note)
            _uiMessage.value = msg
        }
    }

    // Submit complaint / suggestion
    fun submitComplaint(type: ComplaintType, title: String, details: String, mediaUri: String?) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.submitComplaint(
                userId = user.id,
                userName = user.displayName,
                type = type,
                title = title,
                details = details,
                mediaUri = mediaUri
            )
            _uiMessage.value = "تم إرسال طلبك بنجاح! سيتم مراجعته من الإدارة."
        }
    }
}
