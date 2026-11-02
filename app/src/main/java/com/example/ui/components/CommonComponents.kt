package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun ConnectionBadge(
    connectionState: ConnectionState,
    disconnectDurationSeconds: Long = 0L,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (connectionState) {
        ConnectionState.ONLINE -> Pair(StatusOnlineGreen, "متصل")
        ConnectionState.OFFLINE -> Pair(StatusOfflineRed, "غير متصل (${disconnectDurationSeconds}ث)")
        ConnectionState.RECENT_DISCONNECT -> Pair(StatusRecentOrange, "انقطاع حديث (${disconnectDurationSeconds}ث)")
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MatchStatusChip(
    status: String,
    hasDispute: Boolean = false,
    disputeStatus: String = "NONE"
) {
    if (hasDispute && disputeStatus == "UNDER_REVIEW") {
        Surface(
            color = StatusPurpleDispute.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, StatusPurpleDispute.copy(alpha = 0.5f))
        ) {
            Text(
                text = "⚠️ اعتراض قيد المراجعة",
                color = StatusPurpleDispute,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        return
    }

    val (bg, textColor, label) = when (status) {
        "UPCOMING" -> Triple(Color(0xFF3B82F6).copy(alpha = 0.15f), Color(0xFF2563EB), "قادمة")
        "LIVE" -> Triple(StatusOnlineGreen.copy(alpha = 0.15f), StatusOnlineGreen, "مباشرة الآن")
        "COMPLETED" -> Triple(Color(0xFF6B7280).copy(alpha = 0.15f), Color(0xFF4B5563), "مكتملة")
        "POSTPONED" -> Triple(StatusRecentOrange.copy(alpha = 0.15f), StatusRecentOrange, "مؤجلة")
        else -> Triple(Color.Gray.copy(alpha = 0.1f), Color.Gray, status)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TournamentStatusBadge(status: String) {
    val (bg, color, text) = when (status) {
        "REGISTRATION_OPEN" -> Triple(EmeraldGreenContainer, EmeraldGreenDark, "التسجيل مفتوح")
        "FULL" -> Triple(GoldContainer, TrophyGoldDark, "مكتمل العدد")
        "DRAFT_DRAW" -> Triple(Color(0xFFE0E7FF), Color(0xFF3730A3), "قرعة مسودة")
        "IN_PROGRESS" -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "البطولة جارية")
        "COMPLETED" -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), "مكتملة")
        else -> Triple(Color(0xFFF3F4F6), Color.Black, status)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun RouletteWheelVisual(
    isSpinning: Boolean,
    wonTeam: NationalTeam?,
    onSpinClick: () -> Unit,
    spinsRemaining: Int,
    enabled: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "روليت المنتخبات المستقلة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = TrophyGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "المحاولات المتبقية: $spinsRemaining",
                        color = TrophyGoldDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Wheel Outer Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(190.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .border(
                            4.dp,
                            Brush.sweepGradient(
                                listOf(TrophyGold, EmeraldGreenPrimary, TrophyGoldLight, EmeraldGreenDark, TrophyGold)
                            ),
                            CircleShape
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .rotate(if (isSpinning) rotation else 0f),
                    contentAlignment = Alignment.Center
                ) {
                    // Segment lines
                    for (i in 0 until 8) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(170.dp)
                                .rotate(i * 45f)
                                .background(TrophyGold.copy(alpha = 0.25f))
                        )
                    }
                }

                // Center Trophy / Result Emblem
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 6.dp,
                    border = androidx.compose.foundation.BorderStroke(3.dp, TrophyGold),
                    modifier = Modifier.size(105.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        if (isSpinning) {
                            Text(text = "⚽", fontSize = 32.sp)
                            Text(
                                text = "يدور...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreenPrimary
                            )
                        } else if (wonTeam != null) {
                            Text(text = wonTeam.flagEmoji, fontSize = 28.sp)
                            Text(
                                text = wonTeam.nameAr,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Text(text = "🏆", fontSize = 30.sp)
                            Text(
                                text = "اضغط للتشغيل",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onSpinClick,
                enabled = enabled && !isSpinning && spinsRemaining > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldGreenPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("spin_roulette_button")
            ) {
                Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSpinning) "جاري حجز المنتخب آلياً..." else "تشغيل الروليت وحجز المنتخب فوراً",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚡ بمجرد توقف الروليت، يُحجز المنتخب الظاهر فوراً باسمك ويُزال من المتاح للجميع بدون أي اختيار يدوي بديل.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
