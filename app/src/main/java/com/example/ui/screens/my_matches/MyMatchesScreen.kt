package com.example.ui.screens.my_matches

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.model.AppUser
import com.example.data.model.TournamentMatch
import com.example.ui.components.MatchStatusChip
import com.example.ui.theme.*

@Composable
fun MyMatchesScreen(
    matches: List<TournamentMatch>,
    currentUser: AppUser?,
    onMatchClick: (String) -> Unit
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SportsSoccer, contentDescription = null, tint = EmeraldGreenPrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "مواجهاتي في البطولات",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "تأكيد الجاهزية، رفع التشكيلة آلياً، تسجيل النتيجة والاعتراضات.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (matches.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("لا توجد مباريات مسجلة لك حالياً", fontSize = 14.sp, color = Color.Gray)
                            Text("انضم لبطولة وقم بحجز منتخبك لتظهر مبارياتك هنا.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(matches) { match ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMatchClick(match.id) }
                            .testTag("my_match_card_${match.id}"),
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
                                Text(
                                    text = "${match.scheduledDate} • ${match.scheduledTime}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                MatchStatusChip(
                                    status = match.status,
                                    hasDispute = match.hasDispute,
                                    disputeStatus = match.disputeStatus
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(match.player1Flag, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(match.player1Name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (match.scorePlayer1 != null && match.scorePlayer2 != null)
                                            "${match.scorePlayer1} - ${match.scorePlayer2}"
                                        else "ضد",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(match.player2Name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(match.player2Flag, fontSize = 22.sp)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    match: TournamentMatch,
    currentUser: AppUser?,
    isAnalyzingSquad: Boolean,
    onBack: () -> Unit,
    onToggleReady: (Boolean) -> Unit,
    onUploadLineupImage: (Uri) -> Unit,
    onSubmitScore: (p1: Int, p2: Int) -> Unit,
    onSubmitDispute: (reason: String, evidenceUri: String?) -> Unit
) {
    val isPlayer1 = currentUser?.id == match.player1Id
    val isPlayer2 = currentUser?.id == match.player2Id
    val isMyMatch = isPlayer1 || isPlayer2

    val myReady = if (isPlayer1) match.player1Ready else match.player2Ready
    val myLineupApproved = if (isPlayer1) match.player1LineupApproved else match.player2LineupApproved
    val myExtractedSquad = if (isPlayer1) match.player1ExtractedSquad else match.player2ExtractedSquad

    var showScoreDialog by remember { mutableStateOf(false) }
    var showDisputeDialog by remember { mutableStateOf(false) }

    var p1ScoreInput by remember { mutableStateOf("0") }
    var p2ScoreInput by remember { mutableStateOf("0") }

    var disputeReasonInput by remember { mutableStateOf("") }

    // Visual media picker for lineup screenshot
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadLineupImage(uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل المواجهة", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Match Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${match.scheduledDate} • ${match.scheduledTime}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        MatchStatusChip(
                            status = match.status,
                            hasDispute = match.hasDispute,
                            disputeStatus = match.disputeStatus
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(match.player1Flag, fontSize = 32.sp)
                            Text(match.player1Name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(match.player1Team, fontSize = 11.sp, color = Color.Gray)
                            if (match.player1Ready) {
                                Surface(
                                    color = EmeraldGreenContainer,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text("جاهز ✅", fontSize = 10.sp, color = EmeraldGreenDark, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            }
                        }

                        Surface(
                            color = EmeraldGreenPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (match.scorePlayer1 != null && match.scorePlayer2 != null)
                                    "${match.scorePlayer1} - ${match.scorePlayer2}"
                                else "VS",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldGreenDark,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(match.player2Flag, fontSize = 32.sp)
                            Text(match.player2Name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(match.player2Team, fontSize = 11.sp, color = Color.Gray)
                            if (match.player2Ready) {
                                Surface(
                                    color = EmeraldGreenContainer,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text("جاهز ✅", fontSize = 10.sp, color = EmeraldGreenDark, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "⏱️ قانون اللعب: 10 دقائق (5 لكل شوط) • الحد الأقصى 6 تبديلات لكل فريق.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Readiness Toggle
            if (isMyMatch) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (myReady) EmeraldGreenPrimary else Color.LightGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("تأكيد جاهزية اللعب", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("يؤكد كلا الطرفين جاهزيتهما قبل بدء المباراة.", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = myReady,
                            onCheckedChange = { onToggleReady(it) },
                            modifier = Modifier.testTag("match_ready_switch")
                        )
                    }
                }
            }

            // Lineup Submission & Automatic Extraction
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, if (myLineupApproved) EmeraldGreenPrimary else TrophyGold)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CropOriginal, contentDescription = null, tint = EmeraldGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "صورة التشكيلة والاستخراج الآلي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الصورة هي المصدر الوحيد للتشكيلة، ويستخرج التطبيق الأسماء والأرقام آلياً. ممنوع الإدخال أو التعديل اليدوي، ولا تبدأ المباراة قبل اعتماد المالك للتشكيلتين.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isAnalyzingSquad) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("جاري استخراج أسماء وأرقام اللاعبين من الصورة آلياً...", fontSize = 12.sp)
                        }
                    } else if (myExtractedSquad != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "التشكيلة المستخرجة آلياً:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = EmeraldGreenDark
                                    )
                                    Text(
                                        text = if (myLineupApproved) "معتمدة من المالك ✅" else "قيد مراجعة المالك ⏳",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (myLineupApproved) EmeraldGreenPrimary else StatusRecentOrange
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = myExtractedSquad,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isMyMatch) {
                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary),
                            modifier = Modifier.fillMaxWidth().testTag("upload_lineup_button")
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (myExtractedSquad == null) "رفع صورة تشكيلة المباراة" else "إعادة رفع صورة أوضح")
                        }
                    }
                }
            }

            // Results & Dispute Actions
            if (isMyMatch) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showScoreDialog = true },
                        enabled = match.player1LineupApproved && match.player2LineupApproved && !match.isResultApprovedByOwner,
                        colors = ButtonDefaults.buttonColors(containerColor = TrophyGold),
                        modifier = Modifier.weight(1f).testTag("enter_score_button")
                    ) {
                        Text("تسجيل النتيجة (للفائز)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showDisputeDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusPurpleDispute),
                        border = BorderStroke(1.dp, StatusPurpleDispute),
                        modifier = Modifier.weight(1f).testTag("open_dispute_button")
                    ) {
                        Text("تقديم اعتراض رسمي", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Enter Score Dialog (Winner submits final score only, no goalscorers)
    if (showScoreDialog) {
        AlertDialog(
            onDismissRequest = { showScoreDialog = false },
            title = { Text("تسجيل النتيجة النهائية", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "الفائز يدخل النتيجة النهائية فقط، بلا أسماء مسجلين أو تفاصيل فردية. لن تعتمد النتيجة حتى يوافق عليها مالك البطولة.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = p1ScoreInput,
                        onValueChange = { p1ScoreInput = it.filter { char -> char.isDigit() } },
                        label = { Text("أهداف ${match.player1Name}") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = p2ScoreInput,
                        onValueChange = { p2ScoreInput = it.filter { char -> char.isDigit() } },
                        label = { Text("أهداف ${match.player2Name}") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p1 = p1ScoreInput.toIntOrNull() ?: 0
                        val p2 = p2ScoreInput.toIntOrNull() ?: 0
                        showScoreDialog = false
                        onSubmitScore(p1, p2)
                    }
                ) {
                    Text("إرسال للاعتماد")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScoreDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Submit Dispute Dialog
    if (showDisputeDialog) {
        AlertDialog(
            onDismissRequest = { showDisputeDialog = false },
            title = { Text("تقديم اعتراض رسمي للمالك", fontWeight = FontWeight.Bold, color = StatusPurpleDispute) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "يحق لأي طرف الاعتراض بعد المباراة داخل التطبيق بذكر السبب وإرفاق الأدلة. تعلق المصادقة ويراجع المالك ويقرر.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = disputeReasonInput,
                        onValueChange = { disputeReasonInput = it },
                        label = { Text("سبب الاعتراض بالتفصيل...") },
                        modifier = Modifier.height(100.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (disputeReasonInput.isNotBlank()) {
                            showDisputeDialog = false
                            onSubmitDispute(disputeReasonInput, null)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusPurpleDispute)
                ) {
                    Text("رفع الاعتراض رسمياً")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisputeDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
