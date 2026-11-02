package com.example.ui.screens.tournament_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GroupStanding
import com.example.data.model.Tournament
import com.example.data.model.TournamentLog
import com.example.data.model.TournamentMatch
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GroupsStandingsTab(
    standings: List<GroupStanding>,
    groupsCount: Int,
    qualifyPerGroup: Int
) {
    val groups = standings.groupBy { it.groupName }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (standings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = TrophyGold, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("جداول المجموعات قيد الإعداد", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "سيتم توليد جداول المجموعات فور اكتمال حجز مقاعد البطولة وإجراء القرعة من المالك.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            groups.forEach { (groupName, list) ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = groupName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = EmeraldGreenDark
                                )
                                Text(
                                    text = "الأول والثاني يتأهلان للأدوار الإقصائية",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(EmeraldGreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(20.dp), textAlign = TextAlign.Center)
                                Text("اللاعب / المنتخب", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text("لعب", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
                                Text("ف", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                Text("ت", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                Text("خ", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                Text("+/-", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
                                Text("نقاط", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(32.dp), textAlign = TextAlign.Center, color = TrophyGoldDark)
                            }

                            // Table Rows
                            list.sortedWith(compareByDescending<GroupStanding> { it.points }
                                .thenByDescending { it.goalDifference }
                                .thenByDescending { it.goalsFor }
                            ).forEachIndexed { index, row ->
                                val isQualified = index < qualifyPerGroup
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isQualified) EmeraldGreenContainer.copy(alpha = 0.15f) else Color.Transparent
                                        )
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = if (isQualified) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isQualified) EmeraldGreenPrimary else Color.Gray,
                                        modifier = Modifier.width(20.dp),
                                        textAlign = TextAlign.Center
                                    )
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = row.teamFlag, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Column {
                                            Text(
                                                text = row.playerName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = row.teamName,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Text(text = "${row.played}", fontSize = 11.sp, modifier = Modifier.width(26.dp), textAlign = TextAlign.Center)
                                    Text(text = "${row.won}", fontSize = 11.sp, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                    Text(text = "${row.drawn}", fontSize = 11.sp, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                    Text(text = "${row.lost}", fontSize = 11.sp, modifier = Modifier.width(22.dp), textAlign = TextAlign.Center)
                                    Text(
                                        text = "${row.goalDifference}",
                                        fontSize = 11.sp,
                                        modifier = Modifier.width(26.dp),
                                        textAlign = TextAlign.Center,
                                        color = if (row.goalDifference > 0) EmeraldGreenLight else if (row.goalDifference < 0) StatusOfflineRed else Color.Gray
                                    )
                                    Text(
                                        text = "${row.points}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.width(32.dp),
                                        textAlign = TextAlign.Center,
                                        color = TrophyGoldDark
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun BracketPathTab(
    tournament: Tournament,
    matches: List<TournamentMatch>
) {
    val knockoutMatches = matches.filter { it.stage != "GROUP" }
    val semiMatches = knockoutMatches.filter { it.stage == "SEMI" }
    val finalMatch = knockoutMatches.find { it.stage == "FINAL" }
    val thirdMatch = knockoutMatches.find { it.stage == "THIRD_PLACE" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Champion & Trophy Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldContainer),
                border = androidx.compose.foundation.BorderStroke(2.dp, TrophyGold)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🏆", fontSize = 42.sp)
                    Text(
                        text = "بطل المسار واللقب الرسمي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TrophyGoldDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tournament.championName ?: "بانتظار ختام المباراة النهائية",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (tournament.runnerUpName != null) {
                        Text(
                            text = "🥈 الوصيف: ${tournament.runnerUpName}",
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563)
                        )
                    }
                }
            }
        }

        // Final Match
        if (finalMatch != null) {
            item {
                Text(
                    text = "🥇 المباراة النهائية (Final)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TrophyGoldDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                KnockoutMatchCard(finalMatch, isFinal = true)
            }
        }

        // 3rd Place Match
        if (thirdMatch != null && tournament.hasThirdPlaceMatch) {
            item {
                Text(
                    text = "🥉 مباراة تحديد المركز الثالث",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFCD7F32)
                )
                Spacer(modifier = Modifier.height(6.dp))
                KnockoutMatchCard(thirdMatch, isFinal = false)
            }
        }

        // Semi-finals
        if (semiMatches.isNotEmpty()) {
            item {
                Text(
                    text = "⚔️ نصف النهائي (Semi-Finals)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EmeraldGreenPrimary
                )
            }
            items(semiMatches) { match ->
                KnockoutMatchCard(match, isFinal = false)
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun KnockoutMatchCard(
    match: TournamentMatch,
    isFinal: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFinal) GoldContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isFinal) androidx.compose.foundation.BorderStroke(1.dp, TrophyGold) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFinal) "المباراة النهائية الكبرى" else "مواجهة إقصائية",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFinal) TrophyGoldDark else EmeraldGreenPrimary
                )
                Text(
                    text = "${match.scheduledDate} • ${match.scheduledTime}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player 1
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = match.player1Flag, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(match.player1Name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                        Text(match.player1Team, fontSize = 10.sp, color = Color.Gray)
                    }
                }

                // Score or VS
                Surface(
                    color = EmeraldGreenPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = if (match.scorePlayer1 != null && match.scorePlayer2 != null)
                            "${match.scorePlayer1} - ${match.scorePlayer2}"
                        else "ضد",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldGreenDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Player 2
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(match.player2Name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                        Text(match.player2Team, fontSize = 10.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = match.player2Flag, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun StatsTab(
    matches: List<TournamentMatch>,
    standings: List<GroupStanding>,
    logs: List<TournamentLog>
) {
    val completedMatches = matches.filter { it.status == "COMPLETED" && it.scorePlayer1 != null }
    val totalGoals = completedMatches.sumOf { (it.scorePlayer1 ?: 0) + (it.scorePlayer2 ?: 0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Numbers
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldGreenContainer.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("المباريات المكتملة", fontSize = 11.sp, color = EmeraldGreenDark)
                        Text("${completedMatches.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EmeraldGreenDark)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldContainer.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("إجمالي الأهداف", fontSize = 11.sp, color = TrophyGoldDark)
                        Text("$totalGoals", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TrophyGoldDark)
                    }
                }
            }
        }

        // Official Tournament Audit Log
        item {
            Text(
                text = "سجل الإجراءات والقرارات المعتمدة رسمياً",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(logs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (log.category) {
                        "SCHEDULE" -> Icons.Default.CalendarToday
                        "RESULT" -> Icons.Default.SportsScore
                        "DISPUTE" -> Icons.Default.Gavel
                        "SQUAD" -> Icons.Default.Shield
                        else -> Icons.Default.Info
                    }
                    Icon(icon, contentDescription = null, tint = EmeraldGreenPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(log.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            val df = SimpleDateFormat("HH:mm", Locale.getDefault())
                            Text(df.format(Date(log.timestamp)), fontSize = 10.sp, color = Color.Gray)
                        }
                        Text(log.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
