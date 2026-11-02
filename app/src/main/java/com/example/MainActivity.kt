package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ConnectionState
import com.example.ui.components.ConnectionBadge
import com.example.ui.screens.more.GuideScreen
import com.example.ui.screens.more.MoreScreen
import com.example.ui.screens.more.TeamsExplorerScreen
import com.example.ui.screens.my_matches.MatchDetailScreen
import com.example.ui.screens.my_matches.MyMatchesScreen
import com.example.ui.screens.tournament_detail.TournamentDetailScreen
import com.example.ui.screens.tournaments.CreateTournamentScreen
import com.example.ui.screens.tournaments.TournamentsScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkThemeOverride by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val darkTheme = isDarkThemeOverride ?: systemDark

            TournaTheme(darkTheme = darkTheme) {
                TournaApp(viewModel = viewModel, isDarkTheme = darkTheme)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournaApp(
    viewModel: MainViewModel = viewModel(),
    isDarkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allTournaments by viewModel.allTournaments.collectAsStateWithLifecycle()
    val selectedTournament by viewModel.currentTournament.collectAsStateWithLifecycle()
    val participants by viewModel.currentParticipants.collectAsStateWithLifecycle()
    val currentMatches by viewModel.currentMatches.collectAsStateWithLifecycle()
    val currentStandings by viewModel.currentStandings.collectAsStateWithLifecycle()
    val currentLogs by viewModel.currentLogs.collectAsStateWithLifecycle()
    val myMatches by viewModel.myMatches.collectAsStateWithLifecycle()
    val selectedMatch by viewModel.selectedMatch.collectAsStateWithLifecycle()
    val allNationalTeams by viewModel.allNationalTeams.collectAsStateWithLifecycle()
    val complaints by viewModel.complaints.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTournamentTab.collectAsStateWithLifecycle()
    val isRouletteSpinning by viewModel.isRouletteSpinning.collectAsStateWithLifecycle()
    val wonTeam by viewModel.lastWonTeam.collectAsStateWithLifecycle()
    val isAnalyzingSquad by viewModel.isAnalyzingSquad.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val connectionState by viewModel.networkMonitor.connectionState.collectAsStateWithLifecycle()
    val disconnectDuration by viewModel.networkMonitor.disconnectDurationSeconds.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // LaunchedEffect to display UI messages in Snackbar
    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUiMessage()
        }
    }

    // BackHandler for secondary screens
    BackHandler(enabled = currentScreen !is AppScreen.Tournaments && currentScreen !is AppScreen.MyMatches && currentScreen !is AppScreen.More) {
        viewModel.navigateTo(AppScreen.Tournaments)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen is AppScreen.Tournaments || currentScreen is AppScreen.MyMatches || currentScreen is AppScreen.More) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = TrophyGold,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🏆", fontSize = 18.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "صُنّاع البطولات | TOURNA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "eFootball • PES • FIFA • EA FC",
                                    fontSize = 10.sp,
                                    color = EmeraldGreenPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    actions = {
                        // User Name display (Google Account - Name only without photo!)
                        if (currentUser != null) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = EmeraldGreenContainer.copy(alpha = 0.5f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = null,
                                        tint = EmeraldGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentUser!!.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreenDark
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            val isMainTab = currentScreen is AppScreen.Tournaments ||
                    currentScreen is AppScreen.MyMatches ||
                    currentScreen is AppScreen.More

            if (isMainTab) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Tournaments,
                        onClick = { viewModel.navigateTo(AppScreen.Tournaments) },
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "البطولات") },
                        label = { Text("البطولات", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_tournaments")
                    )
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.MyMatches,
                        onClick = { viewModel.navigateTo(AppScreen.MyMatches) },
                        icon = { Icon(Icons.Default.SportsSoccer, contentDescription = "مبارياتي") },
                        label = { Text("مبارياتي", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_my_matches")
                    )
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.More,
                        onClick = { viewModel.navigateTo(AppScreen.More) },
                        icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "المزيد") },
                        label = { Text("المزيد", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("nav_more")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Tournaments -> {
                    TournamentsScreen(
                        tournaments = allTournaments,
                        onTournamentClick = { viewModel.navigateTo(AppScreen.TournamentDetail(it)) },
                        onCreateClick = { viewModel.navigateTo(AppScreen.CreateTournament) }
                    )
                }
                is AppScreen.MyMatches -> {
                    MyMatchesScreen(
                        matches = myMatches,
                        currentUser = currentUser,
                        onMatchClick = { viewModel.navigateTo(AppScreen.MatchDetail(it)) }
                    )
                }
                is AppScreen.More -> {
                    MoreScreen(
                        currentUser = currentUser,
                        complaints = complaints,
                        allTeams = allNationalTeams,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { viewModel.toggleDarkTheme(it) },
                        onUpdateUser = { name, email -> viewModel.updateGoogleUser(name, email) },
                        onSubmitComplaint = { type, title, details -> viewModel.submitComplaint(type, title, details, null) },
                        onOpenGuide = { viewModel.navigateTo(AppScreen.SetupGuide) },
                        onOpenTeamsExplorer = { viewModel.navigateTo(AppScreen.Tournaments) }
                    )
                }
                is AppScreen.CreateTournament -> {
                    CreateTournamentScreen(
                        onBack = { viewModel.navigateTo(AppScreen.Tournaments) },
                        onCreateConfirm = { title, gameTitle, desc, ownerRoleMode, maxP, rAttempts, gCount, qual, thirdPlace ->
                            viewModel.createTournament(
                                title = title,
                                gameTitle = gameTitle,
                                description = desc,
                                ownerRoleMode = ownerRoleMode,
                                maxParticipants = maxP,
                                rouletteAttempts = rAttempts,
                                groupsCount = gCount,
                                qualifyPerGroup = qual,
                                hasThirdPlace = thirdPlace
                            )
                        }
                    )
                }
                is AppScreen.TournamentDetail -> {
                    val tournament = selectedTournament
                    if (tournament != null) {
                        TournamentDetailScreen(
                            tournament = tournament,
                            currentUser = currentUser,
                            participants = participants,
                            matches = currentMatches,
                            standings = currentStandings,
                            logs = currentLogs,
                            allTeams = allNationalTeams,
                            selectedTab = selectedTab,
                            isRouletteSpinning = isRouletteSpinning,
                            wonTeam = wonTeam,
                            onSelectTab = { viewModel.selectTournamentTab(it) },
                            onBack = { viewModel.navigateTo(AppScreen.Tournaments) },
                            onJoinTournament = { consentR, consentB ->
                                viewModel.joinTournament(tournament.id, consentR, consentB)
                            },
                            onSpinRoulette = {
                                val p = participants.find { it.userId == currentUser?.id }
                                if (p != null) {
                                    viewModel.spinRoulette(tournament.id, p.id)
                                }
                            },
                            onMatchClick = { viewModel.navigateTo(AppScreen.MatchDetail(it)) },
                            onGenerateDraft = { viewModel.generateDraftDraw(tournament.id) },
                            onPublishDraw = { viewModel.publishDraw(tournament.id) },
                            onApproveLineup = { matchId, isP1, isApp, reason ->
                                viewModel.ownerReviewLineup(matchId, isP1, isApp, reason)
                            },
                            onApproveResult = { matchId, isApp, note ->
                                viewModel.ownerApproveResult(matchId, isApp, note)
                            },
                            onResolveDispute = { matchId, decision, note ->
                                viewModel.ownerResolveDispute(matchId, decision, note)
                            },
                            onEditSchedule = { matchId, date, time, status ->
                                viewModel.updateSchedule(matchId, date, time, status)
                            }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is AppScreen.MatchDetail -> {
                    val match = selectedMatch
                    if (match != null) {
                        MatchDetailScreen(
                            match = match,
                            currentUser = currentUser,
                            isAnalyzingSquad = isAnalyzingSquad,
                            onBack = {
                                if (selectedTournament != null) {
                                    viewModel.navigateTo(AppScreen.TournamentDetail(selectedTournament!!.id))
                                } else {
                                    viewModel.navigateTo(AppScreen.MyMatches)
                                }
                            },
                            onToggleReady = { viewModel.setPlayerReady(match.id, it) },
                            onUploadLineupImage = { viewModel.uploadAndAnalyzeLineup(match.id, it) },
                            onSubmitScore = { p1, p2 -> viewModel.submitScore(match.id, p1, p2) },
                            onSubmitDispute = { reason, media -> viewModel.submitDispute(match.id, reason, media) }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is AppScreen.SetupGuide -> {
                    GuideScreen(onBack = { viewModel.navigateTo(AppScreen.More) })
                }
                is AppScreen.Complaints -> {
                    // Handled inside MoreScreen
                    viewModel.navigateTo(AppScreen.More)
                }
            }
        }
    }
}
