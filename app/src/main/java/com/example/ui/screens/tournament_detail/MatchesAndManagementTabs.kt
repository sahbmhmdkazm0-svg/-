package com.example.ui.screens.tournament_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.MatchStatusChip
import com.example.ui.theme.*

@Composable
fun MatchesTab(
    matches: List<TournamentMatch>,
    onMatchClick: (String) -> Unit
) {
    val rounds = matches.groupBy { it.roundNumber }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (matches.isEmpty()) {
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
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = EmeraldGreenPrimary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("جدول المباريات قيد الإنشاء", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "ستظهر المباريات والمواعيد هنا فور قيام مالك البطولة باعتماد ونشر القرعة.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            rounds.forEach { (round, list) ->
                item {
                    Text(
                        text = "الجولة رقم $round",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreenDark
                    )
                }

                items(list) { match ->
                    MatchCardItem(match = match, onClick = { onMatchClick(match.id) })
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun MatchCardItem(
    match: TournamentMatch,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${match.scheduledDate} | ${match.scheduledTime}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (match.groupName != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "• ${match.groupName}", fontSize = 11.sp, color = EmeraldGreenLight)
                    }
                }

                MatchStatusChip(
                    status = match.status,
                    hasDispute = match.hasDispute,
                    disputeStatus = match.disputeStatus
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scoreboard Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Player 1
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = match.player1Flag, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = match.player1Name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Text(
                            text = match.player1Team,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Middle Score Display
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = if (match.scorePlayer1 != null && match.scorePlayer2 != null)
                            "${match.scorePlayer1} - ${match.scorePlayer2}"
                        else "ضد",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (match.scorePlayer1 != null) EmeraldGreenDark else Color.Gray,
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
                        Text(
                            text = match.player2Name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Text(
                            text = match.player2Team,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = match.player2Flag, fontSize = 22.sp)
                }
            }

            // Readiness / Lineup indicator
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (match.player1LineupApproved && match.player2LineupApproved)
                        "✅ تم اعتماد تشكيلتي الطرفين"
                    else "⏳ التشكيلات قيد الاعتماد من المالك",
                    fontSize = 10.sp,
                    color = if (match.player1LineupApproved && match.player2LineupApproved) EmeraldGreenLight else StatusRecentOrange
                )
                Text(
                    text = "10 دقائق (5 للشوط) • 6 تبديلات",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ManagementTab(
    tournament: Tournament,
    matches: List<TournamentMatch>,
    participants: List<Participant>,
    isOwner: Boolean,
    onGenerateDraft: () -> Unit,
    onPublishDraw: () -> Unit,
    onApproveLineup: (matchId: String, isPlayer1: Boolean, isApproved: Boolean, reason: String?) -> Unit,
    onApproveResult: (matchId: String, isApproved: Boolean, note: String) -> Unit,
    onResolveDispute: (matchId: String, decision: String, note: String) -> Unit,
    onEditSchedule: (matchId: String, date: String, time: String, status: MatchStatus) -> Unit
) {
    if (!isOwner) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = StatusOfflineRed, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "صلاحيات الإدارة محصورة بمالك البطولة فقط",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "لا يُسمح لأي مشرفين أو مستخدمين آخرين بالوصول للوحة التحكم أو الاعتمادات.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        return
    }

    var selectedMatchForSchedule by remember { mutableStateOf<TournamentMatch?>(null) }
    var editDate by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section A: Draw Status & Generator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. إدارة القرعة ونشر الجدول الرسمي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EmeraldGreenDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الحالة الحالية: ${tournament.status} • تم النشر: ${if (tournament.isDrawPublished) "نعم" else "مسودة لم تُنشر بعد"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val canGenerate = participants.filter { it.status == "ACTIVE" }.size >= tournament.maxParticipants &&
                            participants.none { it.status == "ACTIVE" && it.reservedTeamId == null }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onGenerateDraft,
                            enabled = canGenerate && !tournament.isDrawPublished,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("توليد مسودة القرعة", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onPublishDraw,
                            enabled = matches.isNotEmpty() && !tournament.isDrawPublished,
                            colors = ButtonDefaults.buttonColors(containerColor = TrophyGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("اعتماد ونشر الجدول", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (!canGenerate && !tournament.isDrawPublished) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚠️ لا يمكن إجراء القرعة قبل اكتمال العدد (${tournament.maxParticipants}) وحجز جميع المنتخبات عبر الروليت.",
                            fontSize = 11.sp,
                            color = StatusRecentOrange
                        )
                    }
                }
            }
        }

        // Section B: Lineup Approval List
        val pendingLineupsMatches = matches.filter {
            (it.player1ExtractedSquad != null && !it.player1LineupApproved) ||
                    (it.player2ExtractedSquad != null && !it.player2LineupApproved)
        }

        item {
            Text(
                text = "2. تشكيلات الفرق بانتظار اعتماد المالك (${pendingLineupsMatches.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TrophyGoldDark
            )
        }

        if (pendingLineupsMatches.isEmpty()) {
            item {
                Text(
                    text = "لا توجد تشكيلات معلقة حالياً.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        } else {
            items(pendingLineupsMatches) { match ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, TrophyGold)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "مباراة: ${match.player1Name} ضد ${match.player2Name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        // Player 1 Squad Check
                        if (match.player1ExtractedSquad != null && !match.player1LineupApproved) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "تشكيلة ${match.player1Name} (${match.player1Team}) المستخرجة آلياً:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldGreenPrimary
                            )
                            Text(
                                text = match.player1ExtractedSquad,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onApproveLineup(match.id, true, true, null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                                ) {
                                    Text("اعتماد التشكيلة", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { onApproveLineup(match.id, true, false, "الصورة غير واضحة، يرجى إعادة الرفع") }
                                ) {
                                    Text("رفض / طلب صورة أوضح", fontSize = 11.sp)
                                }
                            }
                        }

                        // Player 2 Squad Check
                        if (match.player2ExtractedSquad != null && !match.player2LineupApproved) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "تشكيلة ${match.player2Name} (${match.player2Team}) المستخرجة آلياً:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldGreenPrimary
                            )
                            Text(
                                text = match.player2ExtractedSquad,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onApproveLineup(match.id, false, true, null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                                ) {
                                    Text("اعتماد التشكيلة", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { onApproveLineup(match.id, false, false, "الصورة غير واضحة، يرجى إعادة الرفع") }
                                ) {
                                    Text("رفض / طلب صورة أوضح", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section C: Results Approval List
        val pendingResultsMatches = matches.filter {
            it.scorePlayer1 != null && !it.isResultApprovedByOwner
        }

        item {
            Text(
                text = "3. نتائج المباريات بانتظار اعتماد المالك (${pendingResultsMatches.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TrophyGoldDark
            )
        }

        if (pendingResultsMatches.isEmpty()) {
            item {
                Text(
                    text = "لا توجد نتائج مسجلة بانتظار الاعتماد.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        } else {
            items(pendingResultsMatches) { match ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, TrophyGold)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "النتيجة المسجلة: ${match.scorePlayer1} - ${match.scorePlayer2}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EmeraldGreenDark
                        )
                        Text(
                            text = "${match.player1Name} (${match.player1Team}) ضد ${match.player2Name} (${match.player2Team})",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onApproveResult(match.id, true, "تمت المصادقة واعتماد النتيجة رسمياً.") },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                            ) {
                                Text("اعتماد النتيجة وتحديث الترتيب", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onApproveResult(match.id, false, "النتيجة غير صحيحة بناء على مراجعة البث.") }
                            ) {
                                Text("رفض النتيجة", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section D: Disputes List
        val activeDisputes = matches.filter { it.hasDispute && it.disputeStatus == "UNDER_REVIEW" }

        item {
            Text(
                text = "4. الاعتراضات بعد المباراة (${activeDisputes.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = StatusPurpleDispute
            )
        }

        if (activeDisputes.isEmpty()) {
            item {
                Text("لا توجد اعتراضات قيد المراجعة.", fontSize = 12.sp, color = Color.Gray)
            }
        } else {
            items(activeDisputes) { match ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusPurpleDispute.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, StatusPurpleDispute)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "⚠️ اعتراض رسمي في مباراة: ${match.player1Name} ضد ${match.player2Name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StatusPurpleDispute
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "السبب المرفوع: ${match.disputeReason ?: "بدون تفاصيل"}",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onResolveDispute(match.id, "قبول الاعتراض", "تم قبول الاعتراض وإعادة جدولة المباراة.") },
                                colors = ButtonDefaults.buttonColors(containerColor = StatusPurpleDispute)
                            ) {
                                Text("قبول الاعتراض", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onResolveDispute(match.id, "رفض الاعتراض", "تمت مراجعة الأدلة وتثبيت النتيجة الأصلية.") }
                            ) {
                                Text("رفض وتثبيت النتيجة", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section E: Schedule Editor
        item {
            Text(
                text = "5. تعديل تواريخ وأوقات المباريات",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = EmeraldGreenDark
            )
        }

        items(matches.take(6)) { match ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${match.player1Name} vs ${match.player2Name}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${match.scheduledDate} - ${match.scheduledTime}", fontSize = 10.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            selectedMatchForSchedule = match
                            editDate = match.scheduledDate
                            editTime = match.scheduledTime
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary.copy(alpha = 0.8f))
                    ) {
                        Text("تعديل الموعد", fontSize = 10.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Schedule Editor Dialog
    if (selectedMatchForSchedule != null) {
        AlertDialog(
            onDismissRequest = { selectedMatchForSchedule = null },
            title = { Text("تعديل موعد المباراة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "مباراة: ${selectedMatchForSchedule?.player1Name} ضد ${selectedMatchForSchedule?.player2Name}",
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = editDate,
                        onValueChange = { editDate = it },
                        label = { Text("التاريخ (YYYY-MM-DD)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editTime,
                        onValueChange = { editTime = it },
                        label = { Text("الوقت (مثال: 21:00)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val m = selectedMatchForSchedule
                        if (m != null) {
                            onEditSchedule(m.id, editDate, editTime, MatchStatus.UPCOMING)
                            selectedMatchForSchedule = null
                        }
                    }
                ) {
                    Text("حفظ وتحديث الجدول")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMatchForSchedule = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
