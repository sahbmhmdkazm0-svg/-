package com.example.ui.screens.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun MoreScreen(
    currentUser: AppUser?,
    complaints: List<ComplaintSuggestion>,
    allTeams: List<NationalTeam>,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    onUpdateUser: (name: String, email: String) -> Unit,
    onSubmitComplaint: (type: ComplaintType, title: String, details: String) -> Unit,
    onOpenGuide: () -> Unit,
    onOpenTeamsExplorer: () -> Unit
) {
    var showEditUserDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var editEmail by remember { mutableStateOf(currentUser?.email ?: "") }

    var showComplaintDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Card (Name Only without Photo - Mandated by User)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, TrophyGold)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldGreenPrimary,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            // Display Name only without photo
                            Text(
                                text = currentUser?.displayName ?: "مستخدم Google",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "حساب Google موثق • إظهار الاسم فقط دون صورة",
                                fontSize = 11.sp,
                                color = EmeraldGreenLight
                            )
                        }
                    }

                    IconButton(onClick = {
                        editName = currentUser?.displayName ?: ""
                        editEmail = currentUser?.email ?: ""
                        showEditUserDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل الاسم", tint = TrophyGoldDark)
                    }
                }
            }
        }

        // Section: System & Rules
        item {
            Text(
                text = "المظهر والنظام",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Theme Toggle Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TrophyGold.copy(alpha = 0.15f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = TrophyGoldDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "مظهر التطبيق (الوضع الداكن)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDarkTheme) "الوضع الليلي الزمردي مفعل" else "الوضع الفاتح الأنيق مفعل",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleDarkTheme(it) },
                        modifier = Modifier.testTag("theme_mode_switch")
                    )
                }
            }
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.MenuBook,
                title = "دليل الربط والتشغيل الأساسي وقوانين البث",
                subtitle = "شرح كامل لآلية العمل، التليجرام، استخراج التشكيلات، والاعتراضات",
                onClick = onOpenGuide
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Public,
                title = "موسوعة منتخبات العالم (${allTeams.size} منتخب)",
                subtitle = "استعراض كافة منتخبات قارات العالم المعتمدة في روليت البطولة",
                onClick = onOpenTeamsExplorer
            )
        }

        item {
            MoreMenuCard(
                icon = Icons.Default.Feedback,
                title = "الشكاوى والمقترحات وطلبات الميزات",
                subtitle = "قسم مخصص للتواصل مع الإدارة ومتابعة حالة الطلبات والردود",
                onClick = { showComplaintDialog = true }
            )
        }

        // Section: Active Complaints & Suggestions History
        if (complaints.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "سجل الشكاوى والمقترحات (${complaints.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(complaints) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Surface(
                                color = when (item.status) {
                                    "ACCEPTED" -> EmeraldGreenContainer
                                    "REJECTED" -> Color(0xFFFEE2E2)
                                    "COMPLETED" -> GoldContainer
                                    else -> Color(0xFFE0E7FF)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.status,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.details,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (item.ownerReply != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "رد الإدارة: ${item.ownerReply}",
                                    fontSize = 11.sp,
                                    color = EmeraldGreenDark,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Edit Google Profile Dialog
    if (showEditUserDialog) {
        AlertDialog(
            onDismissRequest = { showEditUserDialog = false },
            title = { Text("تعديل اسم اللاعب (حساب Google)", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "التطبيق يلتزم بإظهار الاسم فقط دون صورة المستخدم كما تنص التعليمات.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("الاسم الظاهر في البطولات") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("البريد الإلكتروني") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank()) {
                            onUpdateUser(editName, editEmail)
                            showEditUserDialog = false
                        }
                    }
                ) {
                    Text("حفظ التعديل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditUserDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Submit Complaint / Suggestion Dialog
    if (showComplaintDialog) {
        var complaintType by remember { mutableStateOf(ComplaintType.SUGGESTION) }
        var complaintTitle by remember { mutableStateOf("") }
        var complaintDetails by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showComplaintDialog = false },
            title = { Text("إرسال شكوى أو مقترح أو طلب ميزة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ComplaintType.values().forEach { type ->
                            FilterChip(
                                selected = complaintType == type,
                                onClick = { complaintType = type },
                                label = { Text(type.labelAr, fontSize = 11.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = complaintTitle,
                        onValueChange = { complaintTitle = it },
                        label = { Text("عنوان الطلب") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = complaintDetails,
                        onValueChange = { complaintDetails = it },
                        label = { Text("التفاصيل...") },
                        modifier = Modifier.height(100.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (complaintTitle.isNotBlank() && complaintDetails.isNotBlank()) {
                            onSubmitComplaint(complaintType, complaintTitle, complaintDetails)
                            showComplaintDialog = false
                        }
                    }
                ) {
                    Text("إرسال للإدارة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showComplaintDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun MoreMenuCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = EmeraldGreenPrimary.copy(alpha = 0.12f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = EmeraldGreenPrimary, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray)
        }
    }
}
