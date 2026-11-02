package com.example.ui.screens.more

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NationalTeam
import com.example.data.model.WorldNationalTeamsData
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دليل التشغيل واللوائح التنظيمية", fontWeight = FontWeight.Bold) },
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
            GuideSectionCard(
                title = "1. تسجيل الدخول وهوية اللاعبين",
                content = "• يتم الدخول بحساب Google حصراً.\n" +
                        "• يلتزم التطبيق بإظهار الاسم فقط دون صورة المستخدم حمايةً للخصوصية والمظهر الرياضي الموحد.\n" +
                        "• لا توجد أي إعلانات أو مدفوعات داخل التطبيق."
            )

            GuideSectionCard(
                title = "2. صلاحيات المالك الحصرية وقفل النمط",
                content = "• المالك هو المنشئ والمدير والمعتمد الوحيد للبطولة؛ لا وجود لأي رتبة مشرفين.\n" +
                        "• عند إنشاء البطولة يختار المالك إما «أشارك لاعباً» أو «منظّم فقط» ويُقفل الخيار نهائياً ولا يمكن تغييره:\n" +
                        "  - المشارك: يُحسب ضمن مقاعد البطولة ويدخل الروليت كالجميع.\n" +
                        "  - المنظّم فقط: لا يُحسب في المقاعد ولا يدخل الروليت.\n" +
                        "• يحق للمالك المشاركة كلاعب في بطولات أنشأها ملاك آخرون."
            )

            GuideSectionCard(
                title = "3. قانون البث الإلزامي عبر التليجرام",
                content = "• يُلزم جميع اللاعبين بالبث المباشر لمباريات البطولة عبر قناة التليجرام «صناعة البطولات» حصراً وخارج التطبيق.\n" +
                        "• يبدأ مالك القناة الاتصال أولاً، ثم ينضم اللاعبان، ويفتح مسؤول روم المباراة كاميرا موجهة لشاشة اللعب فقط.\n" +
                        "• المشاهدة عامة ومفتوحة للجميع لضمان النزاهة.\n" +
                        "• المخالفة الأولى إنذار، والتكرار قد يؤدي لقرار خسارة من المالك.\n" +
                        "• تطبيق TOURNA لا يبث ولا يراقب ولا يسجل مخالفات البث تلقائياً؛ القرار والتقدير لمالك البطولة."
            )

            GuideSectionCard(
                title = "4. روليت المنتخبات المستقلة والحجز الفوري",
                content = "• يمتلك التطبيق قاعدة بيانات شاملة لكافة منتخبات قارات العالم بلا استثناء للتصنيف أو الشعبية.\n" +
                        "• لكل بطولة روليت مستقل، ويحدد المالك عدد محاولات الدوران.\n" +
                        "• عند تشغيل اللاعب للروليت وتوقفه، يُحجز المنتخب الظاهر فوراً باسمه ويُزال من المتاح لكافة اللاعبين منعاً للتعارض.\n" +
                        "• لا يوجد أي اختيار يدوي بديل للروليت."
            )

            GuideSectionCard(
                title = "5. زمن المباراة، التبديلات، واعتماد التشكيلة",
                content = "• زمن المباراة المعتمد هو 10 دقائق (5 دقائق لكل شوط).\n" +
                        "• الحد الأقصى للتبديلات هو 6 تبديلات لكل فريق.\n" +
                        "• يرفع اللاعب لقطة شاشة لتشكيلته، ويستخرج التطبيق آلياً الأسماء والأرقام.\n" +
                        "• الصورة هي المصدر الوحيد، وممنوع التعديل أو الإدخال اليدوي.\n" +
                        "• لا تبدأ أي مباراة قبل أن يعتمد المالك تشكيلتي الطرفين."
            )

            GuideSectionCard(
                title = "6. النتائج والاعتراضات بعد المباراة",
                content = "• الفائز يدخل النتيجة النهائية فقط (الأهداف) بلا أسماء مسجلين أو تفاصيل فردية.\n" +
                        "• لا تعتمد النتيجة ولا تحدث جداول المجموعات إلا بعد موافقة المالك.\n" +
                        "• يحق لأي طرف تقديم اعتراض رسمي بعد المباراة داخل التطبيق وإرفاق الأدلة.\n" +
                        "• تتحول الحالة إلى «اعتراض قيد المراجعة» وتُعلّق المصادقة لحين بت المالك."
            )

            GuideSectionCard(
                title = "7. حالات الاتصال التلقائية",
                content = "• تظهر مؤشرات الاتصال بالمواجهات فقط بعد قيام المالك باعتماد ونشر القرعة.\n" +
                        "• الأخضر: متصل، الأحمر: غير متصل، البرتقالي: انقطاع حديث مع مدة الثواني.\n" +
                        "• لا يترتب على انقطاع الاتصال أي خسارة تلقائية، والقرار للمالك."
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GuideSectionCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = EmeraldGreenDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsExplorerScreen(
    allTeams: List<NationalTeam>,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedContinent by remember { mutableStateOf("الكل") }

    val filtered = allTeams.filter { team ->
        (selectedContinent == "الكل" || team.continentAr == selectedContinent) &&
                (searchQuery.isBlank() || team.nameAr.contains(searchQuery) || team.nameEn.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("موسوعة منتخبات العالم", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("بحث عن منتخب أو دولة...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

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

            Text(
                text = "العدد المعروض: ${filtered.size} منتخب",
                fontSize = 11.sp,
                color = Color.Gray
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { team ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(team.flagEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(team.nameAr, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${team.nameEn} • ${team.continentAr}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Text("⭐".repeat(team.ratingStars), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
