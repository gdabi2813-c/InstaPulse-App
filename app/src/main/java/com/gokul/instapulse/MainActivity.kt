package com.gokul.instapulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstaPulseApp()
        }
    }
}

private val LightBg = Color(0xFFF7F7FA)
private val LightCard = Color.White
private val LightText = Color.Black
private val LightSubText = Color.Gray
private val LightInsightCard = Color(0xFFEDE7F6)
private val DarkBg = Color(0xFF121212)
private val DarkCard = Color(0xFF1E1E1E)
private val DarkText = Color.White
private val DarkSubText = Color(0xFFAAAAAA)
private val DarkInsightCard = Color(0xFF2D1B4E)

data class Reel(val title: String, val views: String, val engagement: String)

@Composable
fun InstaPulseApp() {

    var selectedTab by remember { mutableIntStateOf(0) }
    var isDarkMode by remember { mutableStateOf(false) }
    var showSplash by remember { mutableStateOf(true) }
    var splashAlpha by remember { mutableStateOf(1f) }

    val reels = remember {
        mutableStateListOf(
            Reel("Reel #1", "1.2M Views", "❤️ 84K   💬 3.2K"),
            Reel("Reel #2", "845K Views", "❤️ 52K   💬 1.8K"),
            Reel("Reel #3", "620K Views", "❤️ 38K   💬 950"),
            Reel("Reel #4", "430K Views", "❤️ 27K   💬 620"),
            Reel("Reel #5", "310K Views", "❤️ 19K   💬 410")
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1800)
        splashAlpha = 0f
        delay(400)
        showSplash = false
    }

    val animatedAlpha by animateFloatAsState(targetValue = splashAlpha, animationSpec = tween(400), label = "splash")
    val scale by animateFloatAsState(targetValue = if (showSplash) 1f else 0.8f, animationSpec = tween(800), label = "scale")

    val bgColor = if (isDarkMode) DarkBg else LightBg
    val cardColor = if (isDarkMode) DarkCard else LightCard
    val textColor = if (isDarkMode) DarkText else LightText
    val subTextColor = if (isDarkMode) DarkSubText else LightSubText
    val insightCardColor = if (isDarkMode) DarkInsightCard else LightInsightCard
    val colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {

        MaterialTheme(colorScheme = colorScheme) {

            Scaffold(
                containerColor = bgColor,
                bottomBar = {
                    NavigationBar(containerColor = if (isDarkMode) DarkCard else Color.White) {
                        NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.Home, "Home", modifier = Modifier.size(26.dp)) }, label = { Text("Home") })
                        NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.PlayCircle, "Reels", modifier = Modifier.size(26.dp)) }, label = { Text("Reels") })
                        NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Default.Person, "Profile", modifier = Modifier.size(26.dp)) }, label = { Text("Profile") })
                    }
                }
            ) { paddingValues ->

                Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(paddingValues)) {

                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = { isDarkMode = !isDarkMode }) {
                            Icon(if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, "Toggle", modifier = Modifier.size(28.dp), tint = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFF7B2FF7))
                        }
                    }

                    when (selectedTab) {
                        0 -> HomeScreen(bgColor, cardColor, textColor, subTextColor, insightCardColor)
                        1 -> ReelsScreen(bgColor, cardColor, textColor, subTextColor, reels) { showAddDialog = true }
                        2 -> ProfileScreen(bgColor, cardColor, textColor, subTextColor)
                    }
                }
            }
        }

        if (showAddDialog) {
            AddReelDialog(
                cardColor = cardColor,
                textColor = textColor,
                subTextColor = subTextColor,
                onDismiss = { showAddDialog = false },
                onAdd = { title, views, engagement ->
                    reels.add(0, Reel(title, views, engagement))
                    showAddDialog = false
                }
            )
        }

        if (showSplash) {
            Column(
                modifier = Modifier.fillMaxSize().background(Color(0xFF7B2FF7)).alpha(animatedAlpha).scale(scale),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("📊", fontSize = 80.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Text("InstaPulse", fontSize = 42.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Your Instagram Growth Hub", fontSize = 16.sp, color = Color(0xFFE0D4FF))
                Spacer(modifier = Modifier.height(30.dp))
                Text("Loading...", fontSize = 14.sp, color = Color(0xFFE0D4FF))
            }
        }
    }
}

@Composable
fun AddReelDialog(cardColor: Color, textColor: Color, subTextColor: Color, onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {

    var title by remember { mutableStateOf("") }
    var views by remember { mutableStateOf("") }
    var likes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = cardColor,
        title = { Text("➕ Add New Reel", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor) },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Reel Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = views, onValueChange = { views = it }, label = { Text("Views (e.g. 500K)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = likes, onValueChange = { likes = it }, label = { Text("Likes (e.g. 30K)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && views.isNotBlank() && likes.isNotBlank()) {
                        onAdd(title, "$views Views", "❤️ $likes   💬 0")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2FF7))
            ) {
                Text("Add Reel", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel", color = subTextColor)
            }
        }
    )
}

@Composable
fun HomeScreen(bgColor: Color, cardColor: Color, textColor: Color, subTextColor: Color, insightCardColor: Color) {
    var expandedCard by remember { mutableIntStateOf(-1) }
    var chartAnimationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        chartAnimationPlayed = true
    }

    val weekData = listOf(
        "Mon" to 120f,
        "Tue" to 180f,
        "Wed" to 90f,
        "Thu" to 250f,
        "Fri" to 310f,
        "Sat" to 280f,
        "Sun" to 200f
    )

    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("InstaPulse", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7B2FF7))
        Text("Your Instagram Growth Hub", fontSize = 14.sp, color = subTextColor)
        Spacer(modifier = Modifier.height(25.dp))
        Text("Good Morning, Creator 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(18.dp))

        ExpandableStatCard("Followers", "12.4K", "+324 this week", expandedCard == 0, { expandedCard = if (expandedCard == 0) -1 else 0 }, cardColor, textColor, subTextColor, listOf("This Week" to "+324", "This Month" to "+1.2K", "Non-Followers" to "8.1K", "Engaged Followers" to "4.3K"))
        Spacer(modifier = Modifier.height(12.dp))
        ExpandableStatCard("Reach", "284K", "+18% this week", expandedCard == 1, { expandedCard = if (expandedCard == 1) -1 else 1 }, cardColor, textColor, subTextColor, listOf("From Home" to "142K", "From Explore" to "89K", "From Hashtags" to "38K", "From Profile" to "15K"))
        Spacer(modifier = Modifier.height(12.dp))
        ExpandableStatCard("Engagement", "7.82%", "+12.5% this week", expandedCard == 2, { expandedCard = if (expandedCard == 2) -1 else 2 }, cardColor, textColor, subTextColor, listOf("Likes" to "21.4K", "Comments" to "2.1K", "Shares" to "4.8K", "Saves" to "3.2K"))
        Spacer(modifier = Modifier.height(22.dp))

        // 📊 Weekly Growth Chart
        Text("📊 Weekly Followers Growth", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weekData.forEachIndexed { index, (day, value) ->
                        val maxValue = weekData.maxOf { it.second }
                        val targetHeight = (value / maxValue) * 140f
                        val animatedHeight by animateFloatAsState(
                            targetValue = if (chartAnimationPlayed) targetHeight else 0f,
                            animationSpec = tween(durationMillis = 800, delayMillis = index * 80),
                            label = "bar_$index"
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                            Text(
                                "${value.toInt()}",
                                fontSize = 10.sp,
                                color = subTextColor,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(animatedHeight.dp)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        if (value == maxValue) Color(0xFF7B2FF7)
                                        else Color(0xFF7B2FF7).copy(alpha = 0.5f)
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(day, fontSize = 11.sp, color = subTextColor)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("🔥 Best day: Friday — 310 new followers!", fontSize = 13.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text("🤖 AI Growth Insight", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(10.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = insightCardColor)) {
            Text("Your motivational Reels are performing well. Try creating more content around your best-performing topics.", modifier = Modifier.padding(18.dp), fontSize = 15.sp, color = textColor)
        }

        Spacer(modifier = Modifier.height(22.dp))
        Text("🔥 Best Reel", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(10.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Your Best Performing Reel", fontSize = 15.sp, color = subTextColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text("1.2M Views", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(5.dp))
                Text("❤️ 84K   💬 3.2K   🔄 12K   🔖 8K", fontSize = 14.sp, color = subTextColor)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ExpandableStatCard(title: String, value: String, growth: String, isExpanded: Boolean, onClick: () -> Unit, cardColor: Color, textColor: Color, subTextColor: Color, details: List<Pair<String, String>>) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }.animateContentSize(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(title, fontSize = 14.sp, color = subTextColor)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(value, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Text("↑ $growth", fontSize = 13.sp, color = Color(0xFF16A34A))
                }
                Text(if (isExpanded) "▲" else "▼", fontSize = 20.sp, color = subTextColor)
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(15.dp))
                details.forEach { (label, val_) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, fontSize = 14.sp, color = subTextColor)
                        Text(val_, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
                    }
                }
            }
        }
    }
}

@Composable
fun ReelsScreen(bgColor: Color, cardColor: Color, textColor: Color, subTextColor: Color, reels: List<Reel>, onAddClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(rememberScrollState()).padding(20.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("📱 Your Reels", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textColor)
            Button(
                onClick = { onAddClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2FF7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(modifier = Modifier.size(4.dp))
                Text("Add Reel", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        reels.forEach { reel ->
            ReelCard(reel.title, reel.views, reel.engagement, cardColor, textColor, subTextColor)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ReelCard(title: String, views: String, engagement: String, cardColor: Color, textColor: Color, subTextColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(5.dp))
            Text(views, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7B2FF7))
            Spacer(modifier = Modifier.height(5.dp))
            Text(engagement, fontSize = 14.sp, color = subTextColor)
        }
    }
}

@Composable
fun ProfileScreen(bgColor: Color, cardColor: Color, textColor: Color, subTextColor: Color) {
    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(rememberScrollState()).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("👤", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text("@gokul_creator", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(5.dp))
        Text("Content Creator • Motivation & Lifestyle", fontSize = 14.sp, color = subTextColor)
        Spacer(modifier = Modifier.height(25.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("12.4K", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor); Text("Followers", fontSize = 13.sp, color = subTextColor) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("892", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor); Text("Following", fontSize = 13.sp, color = subTextColor) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("247", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor); Text("Posts", fontSize = 13.sp, color = subTextColor) }
        }

        Spacer(modifier = Modifier.height(25.dp))
        Text("📊 Account Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(12.dp))
        StatCard("Total Posts", "247", "+12 this month", cardColor, textColor, subTextColor)
        Spacer(modifier = Modifier.height(12.dp))
        StatCard("Avg. Reach per Reel", "89K", "+15% this month", cardColor, textColor, subTextColor)
    }
}

@Composable
fun StatCard(title: String, value: String, growth: String, cardColor: Color, textColor: Color, subTextColor: Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, fontSize = 14.sp, color = subTextColor)
            Spacer(modifier = Modifier.height(5.dp))
            Text(value, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text("↑ $growth", fontSize = 13.sp, color = Color(0xFF16A34A))
        }
    }
}
