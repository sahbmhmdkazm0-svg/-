package com.example.ui.screens.tournament_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.TournamentStatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    tournament: Tournament,
    currentUser: AppUser?,
    participants: List<Participant>,
    matches: List<TournamentMatch>,
    standings: List<GroupStanding>,
    logs: List<TournamentLog>,
    allTeams: List<NationalTeam>,
    selectedTab: TournamentTab,
    isRouletteSpinning: Boolean,
    wonTeam: NationalTeam?,
    onSelectTab: (TournamentTab) -> Unit,
    onBack: () -> Unit,
    onJoinTournament: (consentedRules: Boolean, consentedBroadcast: Boolean) -> Unit,
    onSpinRoulette: () -> Unit,
    onMatchClick: (String) -> Unit,
    onGenerateDraft: () -> Unit,
    onPublishDraw: () -> Unit,
    onApproveLineup: (matchId: String, isPlayer1: Boolean, isApproved: Boolean, reason: String?) -> Unit,
    onApproveResult: (matchId: String, isApproved: Boolean, note: String) -> Unit,
    onResolveDispute: (matchId: String, decision: String, note: String) -> Unit,
    onEditSchedule: (matchId: String, date: String, time: String, status: MatchStatus) -> Unit
) {
    val isOwner = currentUser?.id == tournament.ownerId
    val myParticipant = participants.find { it.userId == currentUser?.id }
    val activeParticipants = participants.filter { it.status == "ACTIVE" }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = tournament.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "اللعبة: ${tournament.gameTitle} • ${tournament.status}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    },
                    actions = {
                        TournamentStatusBadge(status = tournament.status)
                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // 8 Tabs scrollable Row as requested
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = 12.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = EmeraldGreenPrimary
                ) {
                    TournamentTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Tab(
                            selected = isSelected,
                            onClick = { onSelectTab(tab) },
                            text = {
                                Text(
                                    text = tab.labelAr,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                TournamentTab.OVERVIEW -> OverviewTab(
                    tournament = tournament,
                    currentUser = currentUser,
                    myParticipant = myParticipant,
                    activeParticipantsCount = activeParticipants.size,
                    onJoinRequest = onJoinTournament
                )
                TournamentTab.PARTICIPANTS -> ParticipantsTab(
                    participants = participants,
                    tournament = tournament,
                    isDrawPublished = tournament.isDrawPublished
                )
                TournamentTab.ROULETTE -> RouletteTab(
                    tournament = tournament,
                    myParticipant = myParticipant,
                    allTeams = allTeams,
                    participants = participants,
                    isSpinning = isRouletteSpinning,
                    wonTeam = wonTeam,
                    onSpinClick = onSpinRoulette
                )
                TournamentTab.GROUPS -> GroupsStandingsTab(
                    standings = standings,
                    groupsCount = tournament.groupsCount,
                    qualifyPerGroup = tournament.qualifyPerGroup
                )
                TournamentTab.BRACKET -> BracketPathTab(
                    tournament = tournament,
                    matches = matches
                )
                TournamentTab.MATCHES -> MatchesTab(
                    matches = matches,
                    onMatchClick = onMatchClick
                )
                TournamentTab.STATS -> StatsTab(
                    matches = matches,
                    standings = standings,
                    logs = logs
                )
                TournamentTab.MANAGEMENT -> ManagementTab(
                    tournament = tournament,
                    matches = matches,
                    participants = participants,
                    isOwner = isOwner,
                    onGenerateDraft = onGenerateDraft,
                    onPublishDraw = onPublishDraw,
                    onApproveLineup = onApproveLineup,
                    onApproveResult = onApproveResult,
                    onResolveDispute = onResolveDispute,
                    onEditSchedule = onEditSchedule
                )
            }
        }
    }
}
