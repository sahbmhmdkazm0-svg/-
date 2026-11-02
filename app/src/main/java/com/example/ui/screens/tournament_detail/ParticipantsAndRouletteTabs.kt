package com.example.ui.screens.tournament_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.ConnectionBadge
import com.example.ui.components.RouletteWheelVisual
import com.example.ui.theme.*

@Composable
fun ParticipantsTab(
    participants: List<Participant>,
    tournament: Tournament,
    isDrawPublished: Boolean
) {
    val activeList = participants.filter { it.status == "ACTIVE" }
    val waitingList = participants.filter { it.status == "WAITING_LIST" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "المشاركون المعتمدون: ${activeList.size} / ${tournament.maxParticipants}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (!isDrawPublished) {
                            Text(
                                text = "حالات الاتصال تظهر تلقائياً بعد نشر القرعة واعتمادها رسمياً.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "تحديث حالات الاتصال مفعل ومستمر.",
                                fontSize = 11.sp,
                                color = EmeraldGreenLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        items(activeList) { participant ->
            ParticipantRowCard(
                participant = participant,
                showConnectionState = isDrawPublished
            )
        }

        if (waitingList.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "قائمة الانتظار (${waitingList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TrophyGoldDark
                )
            }
            items(waitingList) { participant ->
                ParticipantRowCard(
                    participant = participant,
                    showConnectionState = isDrawPublished
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ParticipantRowCard(
    participant: Participant,
    showConnectionState: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Team flag or default
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = participant.reservedTeamFlag ?: "⏳",
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Display Name only - without photo!
                        Text(
                            text = participant.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (participant.isOwner) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = TrophyGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "المالك",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TrophyGoldDark,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = if (participant.reservedTeamName != null)
                            "المنتخب: ${participant.reservedTeamName}"
                        else
                            "بانتظار تشغيل الروليت وحجز المنتخب",
                        fontSize = 11.sp,
                        color = if (participant.reservedTeamName != null) EmeraldGreenLight else StatusRecentOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Connection state badge (only after draw is published)
            if (showConnectionState) {
                val state = when (participant.connectionState) {
                    "OFFLINE" -> ConnectionState.OFFLINE
                    "RECENT_DISCONNECT" -> ConnectionState.RECENT_DISCONNECT
                    else -> ConnectionState.ONLINE
                }
                ConnectionBadge(
                    connectionState = state,
                    disconnectDurationSeconds = participant.disconnectDurationSeconds
                )
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "محاولات: ${participant.rouletteSpinsRemaining}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RouletteTab(
    tournament: Tournament,
    myParticipant: Participant?,
    allTeams: List<NationalTeam>,
    participants: List<Participant>,
    isSpinning: Boolean,
    wonTeam: NationalTeam?,
    onSpinClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedContinent by remember { mutableStateOf("الكل") }

    val reservedTeamIds = participants.mapNotNull { it.reservedTeamId }.toSet()
    val availableTeams = allTeams.filter { it.id !in reservedTeamIds }

    val filteredAvailable = availableTeams.filter { team ->
        (selectedContinent == "الكل" || team.continentAr == selectedContinent) &&
                (searchQuery.isBlank() || team.nameAr.contains(searchQuery) || team.nameEn.contains(searchQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // User's Roulette Status
        item {
            if (myParticipant != null) {
                RouletteWheelVisual(
                    isSpinning = isSpinning,
                    wonTeam = wonTeam ?: if (myParticipant.reservedTeamName != null)
                        allTeams.find { it.id == myParticipant.reservedTeamId }
                    else null,
                    onSpinClick = onSpinClick,
                    spinsRemaining = myParticipant.rouletteSpinsRemaining,
                    enabled = myParticipant.rouletteSpinsRemaining > 0
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = null, tint = EmeraldGreenPrimary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "روليت البطولة المستقل",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "يجب الانضمام للبطولة أولاً من تبويب «نظرة عامة» لتتمكن من تشغيل الروليت وحجز منتخبك.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Available World National Teams Pool in this Tournament
        item {
            Text(
                text = "قاعدة المنتخبات المتاحة للروليت (${availableTeams.size} منتخب متاح)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("بحث عن منتخب أو قارة...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WorldNationalTeamsData.continents.take(4).forEach { continent ->
                    FilterChip(
                        selected = selectedContinent == continent,
                        onClick = { selectedContinent = continent },
                        label = { Text(continent, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filteredAvailable.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pair.forEach { team ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = team.flagEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = team.nameAr,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = team.continentAr,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
