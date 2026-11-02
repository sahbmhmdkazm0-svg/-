package com.example.ui.screens.tournament_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.AppUser
import com.example.data.model.Participant
import com.example.data.model.Tournament
import com.example.ui.theme.*

@Composable
fun OverviewTab(
    tournament: Tournament,
    currentUser: AppUser?,
    myParticipant: Participant?,
    activeParticipantsCount: Int,
    onJoinRequest: (consentedRules: Boolean, consentedBroadcast: Boolean) -> Unit
) {
    var consentRules by remember { mutableStateOf(false) }
    var consentBroadcast by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Summary Card
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
                    Column {
                        Text(
                            text = tournament.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "اللعبة: ${tournament.gameTitle} • نظام ${tournament.groupsCount} مجموعات وتأهل إقصائي",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("المالك المعتمد:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(tournament.ownerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column {
                        Text("حالة المالك:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (tournament.ownerRoleMode == "PARTICIPANT") "يشارك كلاعب ⚽" else "منظّم فقط 📋",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TrophyGoldDark
                        )
                    }
                    Column {
                        Text("المقاعد المحجوزة:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$activeParticipantsCount / ${tournament.maxParticipants}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Join Action Button if user not joined
        if (myParticipant == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreenContainer.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, EmeraldGreenLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = EmeraldGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "الانضمام للبطولة متاح الآن",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreenDark,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الانضمام بطلب داخل التطبيق فقط، بلا تواصل خارجي أو إدخال يدوي. لا يُحتسب أو يُقبل إلا بعد حجز المنتخب عبر الروليت.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showJoinDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("open_join_dialog_button")
                    ) {
                        Text("طلب الانضمام والموافقة على الشروط", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreenContainer.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, EmeraldGreenPrimary)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldGreenPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "أنت مسجل في هذه البطولة رسمياً",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreenDark,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (myParticipant.reservedTeamName != null)
                                "منتخبك المحجوز: ${myParticipant.reservedTeamName} ${myParticipant.reservedTeamFlag ?: ""}"
                            else
                                "يرجى التوجه لتبويب «الروليت» لحجز منتخبك فوراً لتثبيت مشاركتك.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // MANDATORY BROADCAST LAW CARD (قانون البث الإلزامي)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = GoldContainer.copy(alpha = 0.35f)),
            border = BorderStroke(1.dp, TrophyGold)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = TrophyGoldDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "قانون البث الإلزامي لكل البطولات",
                        fontWeight = FontWeight.Bold,
                        color = TrophyGoldDark,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• البث حصرياً عبر قناة التليجرام «صناعة البطولات» خارج تطبيق TOURNA.\n" +
                            "• مالك القناة يبدأ الاتصال أولاً، ثم ينضم اللاعبان، ويفتح مسؤول روم المباراة كاميرا موجهة لشاشة اللعب فقط.\n" +
                            "• المشاهدة متاحة للجميع بشفافية تامة.\n" +
                            "• المخالفة الأولى إنذار، والتكرار قد يؤدي لخسارة بقرار المالك.\n" +
                            "• تُقبل أعذار العطل التقني والطوارئ، والقرار النهائي للمالك.\n" +
                            "• تطبيق TOURNA لا يبث ولا يراقب ولا يسجل مخالفات البث.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 19.sp
                )
            }
        }

        // Technical Game Rules Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = EmeraldGreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "قواعد المباريات المعتمدة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• زمن المباراة: 10 دقائق (5 دقائق لكل شوط).\n" +
                            "• التبديلات: 6 تبديلات كحد أقصى لكل فريق.\n" +
                            "• جاهزية اللعب: يؤكد الطرفان الجاهزية قبل بدء المواجهة.\n" +
                            "• اعتماد التشكيلة: يرفع اللاعب صورة تشكيلته ويستخرج التطبيق الأسماء والأرقام آلياً. لا مباراة قبل اعتماد المالك للتشكيلتين.\n" +
                            "• النتيجة: الفائز يدخل النتيجة النهائية فقط، ولا تعتمد إلا بموافقة المالك.\n" +
                            "• الاعتراضات: يحق لأي طرف الاعتراض بعد المباراة داخل التطبيق وإرفاق الأدلة.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Join Request Dialog with Rules & Broadcast Consent
    if (showJoinDialog) {
        AlertDialog(
            onDismissRequest = { showJoinDialog = false },
            title = {
                Text("الموافقة على القوانين والانضمام", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "للانضمام إلى بطولة «${tournament.title}»، يجب مراجعة الشروط والموافقة عليها:",
                        fontSize = 13.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = consentRules,
                            onCheckedChange = { consentRules = it },
                            modifier = Modifier.testTag("consent_rules_checkbox")
                        )
                        Text(
                            text = "أوافق على لوائح البطولة وزمن المباراة (10 د) واستخراج التشكيلة الآلي.",
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = consentBroadcast,
                            onCheckedChange = { consentBroadcast = it },
                            modifier = Modifier.testTag("consent_broadcast_checkbox")
                        )
                        Text(
                            text = "أوافق على قانون البث الإلزامي عبر قناة التليجرام «صناعة البطولات».",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "ملاحظة: سيتم توجيهك مباشرة لتشغيل الروليت وحجز منتخبك لإكمال التسجيل.",
                        fontSize = 11.sp,
                        color = EmeraldGreenPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (consentRules && consentBroadcast) {
                            showJoinDialog = false
                            onJoinRequest(consentRules, consentBroadcast)
                        }
                    },
                    enabled = consentRules && consentBroadcast,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreenPrimary)
                ) {
                    Text("تأكيد الانضمام")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
