package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.ui.theme.*

@Composable
fun TournamentCard(
    tournament: Tournament,
    activeParticipantsCount: Int = tournament.maxParticipants,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLive = tournament.status == "IN_PROGRESS"
    val isRegistrationOpen = tournament.status == "REGISTRATION_OPEN"

    val cardBorder = when {
        isLive -> BorderStroke(1.5.dp, TrophyGold.copy(alpha = 0.8f))
        isRegistrationOpen -> BorderStroke(1.dp, EmeraldGreenLight.copy(alpha = 0.6f))
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("tournament_card_${tournament.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLive) 4.dp else 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Accent Ribbon (Stadium Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        if (isLive) {
                            Brush.horizontalGradient(listOf(TrophyGold, EmeraldGreenPrimary, TrophyGoldLight))
                        } else if (isRegistrationOpen) {
                            Brush.horizontalGradient(listOf(EmeraldGreenLight, EmeraldGreenContainer, EmeraldGreenPrimary))
                        } else {
                            Brush.horizontalGradient(listOf(Color(0xFF64748B), Color(0xFF94A3B8)))
                        }
                    )
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Header: Sports Emblem + Game Badge + Status Chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Sports Icon Emblem with gradient circle
                        Surface(
                            shape = CircleShape,
                            color = if (isLive) TrophyGold.copy(alpha = 0.18f) else EmeraldGreenPrimary.copy(alpha = 0.12f),
                            border = BorderStroke(1.5.dp, if (isLive) TrophyGold else EmeraldGreenLight),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val sportsIcon = when (tournament.gameTitle) {
                                    "EA SPORTS FC", "FIFA" -> Icons.Default.SportsSoccer
                                    "eFootball", "PES" -> Icons.Default.EmojiEvents
                                    else -> Icons.Default.SportsEsports
                                }
                                Icon(
                                    imageVector = sportsIcon,
                                    contentDescription = null,
                                    tint = if (isLive) TrophyGoldDark else EmeraldGreenPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            // Game Badge
                            Surface(
                                color = EmeraldGreenPrimary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "🎮 ${tournament.gameTitle}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = EmeraldGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "نظام ${tournament.groupsCount} مجموعات • 10 دقائق",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Status Badge with pulsing indicator dot
                    TournamentStatusBadge(status = tournament.status)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tournament Title
                Text(
                    text = tournament.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description
                Text(
                    text = tournament.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar of Participants Capacity
                val fillRatio = (activeParticipantsCount.toFloat() / tournament.maxParticipants.toFloat()).coerceIn(0f, 1f)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المقاعد المحجوزة:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$activeParticipantsCount من ${tournament.maxParticipants} لاعبين",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (fillRatio >= 1f) TrophyGoldDark else EmeraldGreenLight
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { fillRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (fillRatio >= 1f) TrophyGold else EmeraldGreenPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                // Footer: Owner Info & Locked Role Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = TrophyGoldDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "المالك: ${tournament.ownerName}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Owner Mode Locked Badge
                    Surface(
                        color = if (tournament.ownerRoleMode == "PARTICIPANT")
                            EmeraldGreenContainer.copy(alpha = 0.6f)
                        else
                            Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (tournament.ownerRoleMode == "PARTICIPANT") "يشارك كلاعب ⚽" else "منظّم فقط 📋",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tournament.ownerRoleMode == "PARTICIPANT") EmeraldGreenDark else Color(0xFF334155)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
fun TournamentCardPreview() {
    TournaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TournamentCard(
                tournament = Tournament(
                    id = "p1",
                    title = "كأس النخبة العالمية 2026",
                    gameTitle = "eFootball",
                    description = "بطولة رسمية كبرى لمنتخبات العالم، تقام بنظام المجموعات والإقصائيات مع بث إلزامي عبر قناة التليجرام «صناعة البطولات».",
                    ownerId = "o1",
                    ownerName = "أحمد الصقر",
                    ownerRoleMode = "PARTICIPANT",
                    maxParticipants = 8,
                    status = "IN_PROGRESS",
                    groupsCount = 2
                ),
                activeParticipantsCount = 8,
                onClick = {}
            )

            TournamentCard(
                tournament = Tournament(
                    id = "p2",
                    title = "بطولة دوري المحترفين FC26",
                    gameTitle = "EA SPORTS FC",
                    description = "تسجيل مفتوح لجميع اللاعبين، الحجز عبر روليت المنتخبات الفوري وبلا استثناء.",
                    ownerId = "o2",
                    ownerName = "فيصل القحطاني",
                    ownerRoleMode = "ORGANIZER_ONLY",
                    maxParticipants = 16,
                    status = "REGISTRATION_OPEN",
                    groupsCount = 4
                ),
                activeParticipantsCount = 10,
                onClick = {}
            )
        }
    }
}
