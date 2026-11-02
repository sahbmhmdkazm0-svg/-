package com.example.ui.screens.tournaments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OwnerRoleMode
import com.example.data.model.SupportedGame
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(
    onBack: () -> Unit,
    onCreateConfirm: (
        title: String,
        gameTitle: String,
        description: String,
        ownerRoleMode: OwnerRoleMode,
        maxParticipants: Int,
        rouletteAttempts: Int,
        groupsCount: Int,
        qualifyPerGroup: Int,
        hasThirdPlace: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf(SupportedGame.EFOOTBALL) }
    var selectedOwnerMode by remember { mutableStateOf(OwnerRoleMode.PARTICIPANT) }
    var maxParticipants by remember { mutableIntStateOf(8) }
    var rouletteAttempts by remember { mutableIntStateOf(3) }
    var groupsCount by remember { mutableIntStateOf(2) }
    var hasThirdPlace by remember { mutableStateOf(true) }

    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إنشاء بطولة جديدة", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Game Selection
            Text(
                text = "1. اللعبة الإلكترونية للبطولة",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SupportedGame.values().forEach { game ->
                    val isSelected = selectedGame == game
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedGame = game },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) EmeraldGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) BorderStroke(2.dp, TrophyGold) else null
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = game.shortName,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // 2. Title & Description
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("اسم البطولة (مثال: كأس النخبة الرمضانية)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tournament_title_input"),
                singleLine = true,
                isError = showError && title.isBlank()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("وصف وقوانين البطولة") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                maxLines = 3
            )

            // 3. OWNER ROLE MODE (MANDATORY REQUIREMENT 2 - LOCKED PERMANENTLY)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GoldContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, TrophyGold)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = TrophyGoldDark, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "دور المالك (يُقفل نهائياً عند الإنشاء)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TrophyGoldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "المالك وحده ينشئ ويدير ويعتمد؛ لا مشرفين. المشارك يُحسب ويدخل الروليت كالجميع، والمنظّم لا يُحسب ولا يدخل الروليت.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Option A: Participate as player
                        val isPart = selectedOwnerMode == OwnerRoleMode.PARTICIPANT
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedOwnerMode = OwnerRoleMode.PARTICIPANT },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isPart) EmeraldGreenPrimary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isPart) TrophyGold else Color.LightGray)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "أشارك لاعباً ⚽",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isPart) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "يُحسب في المقاعد + يدخل الروليت",
                                    fontSize = 10.sp,
                                    color = if (isPart) Color(0xFFE2E8F0) else Color.Gray
                                )
                            }
                        }

                        // Option B: Organizer only
                        val isOrg = selectedOwnerMode == OwnerRoleMode.ORGANIZER_ONLY
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedOwnerMode = OwnerRoleMode.ORGANIZER_ONLY },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isOrg) EmeraldGreenPrimary else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isOrg) TrophyGold else Color.LightGray)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "منظّم فقط 📋",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isOrg) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "لا يُحسب + لا يدخل الروليت",
                                    fontSize = 10.sp,
                                    color = if (isOrg) Color(0xFFE2E8F0) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // 4. Tournament Format (Groups + Knockout)
            Text(
                text = "4. نظام البطولة (مجموعات وتأهل إقصائي حصراً)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Number of participants
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "عدد اللاعبين:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(8, 16).forEach { num ->
                            FilterChip(
                                selected = maxParticipants == num,
                                onClick = {
                                    maxParticipants = num
                                    groupsCount = if (num == 16) 4 else 2
                                },
                                label = { Text("$num لاعبين") }
                            )
                        }
                    }
                }

                // Number of groups
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "عدد المجموعات:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(2, 4).forEach { count ->
                            FilterChip(
                                selected = groupsCount == count,
                                onClick = { groupsCount = count },
                                label = { Text("$count مجموعات") }
                            )
                        }
                    }
                }
            }

            // 5. Roulette Attempts
            Text(
                text = "5. عدد محاولات الروليت المتاحة لكل لاعب",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1, 2, 3, 5).forEach { attempts ->
                    FilterChip(
                        selected = rouletteAttempts == attempts,
                        onClick = { rouletteAttempts = attempts },
                        label = { Text("$attempts محاولات") }
                    )
                }
            }

            // 6. Match Rules Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "⚖️ القواعد القياسية المعتمدة للبطولة:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• مدة المباراة 10 دقائق (5 لكل شوط).\n• الحد الأقصى 6 تبديلات لكل فريق.\n• رفع صورة التشكيلة مع استخراج آلي للأسماء والأرقام واعتماد المالك.\n• بث إلزامي عبر قناة التليجرام «صناعة البطولات» خارج التطبيق.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = hasThirdPlace,
                    onCheckedChange = { hasThirdPlace = it }
                )
                Text(
                    text = "إقامة مباراة لتحديد المركز الثالث والميدالية البرونزية",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        showError = true
                    } else {
                        onCreateConfirm(
                            title,
                            selectedGame.shortName,
                            if (description.isBlank()) "بطولة تنافسية لمنتخبات العالم." else description,
                            selectedOwnerMode,
                            maxParticipants,
                            rouletteAttempts,
                            groupsCount,
                            2,
                            hasThirdPlace
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldGreenPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_create_tournament_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تثبيت وإنشاء البطولة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
