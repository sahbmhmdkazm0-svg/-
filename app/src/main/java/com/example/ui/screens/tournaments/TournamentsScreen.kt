package com.example.ui.screens.tournaments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.ui.components.TournamentStatusBadge
import com.example.ui.theme.*

@Composable
fun TournamentsScreen(
    tournaments: List<Tournament>,
    onTournamentClick: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredList = when (selectedFilter) {
        "OPEN" -> tournaments.filter { it.status == "REGISTRATION_OPEN" }
        "IN_PROGRESS" -> tournaments.filter { it.status == "IN_PROGRESS" }
        "COMPLETED" -> tournaments.filter { it.status == "COMPLETED" }
        else -> tournaments
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("إنشاء بطولة جديدة", fontWeight = FontWeight.Bold) },
                containerColor = TrophyGold,
                contentColor = Color.Black,
                modifier = Modifier.testTag("create_tournament_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // UEFA Champions League "قريباً" Card as mandated
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A35)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF09142E), Color(0xFF1E3A8A))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = TrophyGold.copy(alpha = 0.2f),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "⭐", fontSize = 22.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "دوري أبطال أوروبا (UCL)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = TrophyGold,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "قريباً",
                                            color = Color.Black,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "نظام الأندية الأوروبية والتصفيات بنسخته الجديدة قيد التطوير والإطلاق قريباً.",
                                    color = Color(0xFFBFDBFE),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("الكل (${tournaments.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "IN_PROGRESS",
                        onClick = { selectedFilter = "IN_PROGRESS" },
                        label = { Text("الجارية") }
                    )
                    FilterChip(
                        selected = selectedFilter == "OPEN",
                        onClick = { selectedFilter = "OPEN" },
                        label = { Text("التسجيل مفتوح") }
                    )
                    FilterChip(
                        selected = selectedFilter == "COMPLETED",
                        onClick = { selectedFilter = "COMPLETED" },
                        label = { Text("المكتملة") }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "لا توجد بطولات في هذا القسم حالياً",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredList) { tournament ->
                    com.example.ui.components.TournamentCard(
                        tournament = tournament,
                        onClick = { onTournamentClick(tournament.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun TournamentItemCard(
    tournament: Tournament,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("tournament_item_${tournament.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldGreenPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = EmeraldGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tournament.gameTitle,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreenPrimary,
                            fontSize = 12.sp
                        )
                    }
                }

                TournamentStatusBadge(status = tournament.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tournament.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = tournament.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = TrophyGoldDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "المالك: ${tournament.ownerName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = if (tournament.ownerRoleMode == "PARTICIPANT")
                        EmeraldGreenContainer.copy(alpha = 0.5f)
                    else
                        Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (tournament.ownerRoleMode == "PARTICIPANT") "المالك: أشارك لاعباً" else "المالك: منظّم فقط",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (tournament.ownerRoleMode == "PARTICIPANT") EmeraldGreenDark else Color(0xFF334155),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نظام ${tournament.groupsCount} مجموعات • الحد: ${tournament.maxParticipants} لاعباً",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "مباراة 10 دقائق (5 للشوط)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = EmeraldGreenLight
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, locale = "ar")
@Composable
fun ActiveTournamentsScreenPreview() {
    TournaTheme {
        TournamentsScreen(
            tournaments = listOf(
                Tournament(
                    id = "preview_1",
                    title = "كأس النخبة العالمية 2026",
                    gameTitle = "eFootball",
                    description = "بطولة رسمية لمنتخبات العالم، تقام بنظام المجموعات والإقصائيات مع بث إلزامي عبر التليجرام.",
                    ownerId = "owner_1",
                    ownerName = "أحمد الصقر",
                    ownerRoleMode = "PARTICIPANT",
                    maxParticipants = 8,
                    status = "IN_PROGRESS",
                    groupsCount = 2
                ),
                Tournament(
                    id = "preview_2",
                    title = "بطولة أبطال فيفا FC26",
                    gameTitle = "EA SPORTS FC",
                    description = "المنافسة الكبرى لأندية ومنتخبات EA FC بنظام المجموعات والتصفيات المباشرة.",
                    ownerId = "owner_2",
                    ownerName = "فيصل القحطاني",
                    ownerRoleMode = "ORGANIZER_ONLY",
                    maxParticipants = 16,
                    status = "IN_PROGRESS",
                    groupsCount = 4
                )
            ),
            onTournamentClick = {},
            onCreateClick = {}
        )
    }
}

