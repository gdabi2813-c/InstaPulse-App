package com.gokul.instapulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
data class NotificationItem(val icon: String, val title: String, val subtitle: String, val time: String, val color: Color)
data class StoryHighlight(val emoji: String, val label: String, val gradient: List<Color>)

@Composable
fun InstaPulseApp() {

    var selectedTab by remember { mutableIntStateOf(0) }
    var isDarkMode by remember { mutableStateOf(false) }
    var showSplash by remember { mutableStateOf(true) }
    var splashAlpha by remember { mutableStateOf(1f) }
    var showOnboarding by remember { mutableStateOf(true) }
    var onboardingPage by remember { mutableIntStateOf(0) }
    var notifEnabled by remember { mutableStateOf(true) }
    var analyticsEnabled by remember { mutableStateOf(true) }
    var autoRefresh by remember { mutableStateOf(false) }
    var selectedReel by remember { mutableStateOf<Reel?>(null) }
    var showEditProfile by remember { mutableStateOf(false) }

    var profileName by remember { mutableStateOf("Gokul Creator") }
    var profileUsername by remember { mutableStateOf("@gokul_creator") }
    var profileBio by remember { mutableStateOf("Content Creator • Motivation & Lifestyle") }

    val reels = remember {
        mutableStateListOf(
            Reel("Reel #1 - Motivation", "1.2M Views", "❤️ 84K   💬 3.2K"),
            Reel("Reel #2 - Fitness Tips", "845K Views", "❤️ 52K   💬 1.8K"),
            Reel("Reel #3 - Morning Routine", "620K Views", "❤️ 38K   💬 950"),
            Reel("Reel #4 - Success Story", "430K Views", "❤️ 27K   💬 620"),
            Reel("Reel #5 - Mindset Hack", "310K Views", "❤️ 19K   💬 410")
        )
    }

    val notifications = remember {
        mutableStateListOf(
            NotificationItem("🎉", "Milestone Reached!", "You hit 12K followers", "2m ago", Color(0xFF7B2FF7)),
            NotificationItem("❤️", "Reel #1 is trending", "84K likes and counting!", "15m ago", Color(0xFFE1306C)),
            NotificationItem("👤", "New Follower", "rahul_creative started following you", "1h ago", Color(0xFF16A34A)),
            NotificationItem("💬", "New Comment", "priya_motivation commented on your Reel #2", "3h ago", Color(0xFF2196F3)),
            NotificationItem("📈", "Reach Boost", "Your reach increased by 18% this week", "5h ago", Color(0xFFFF9800)),
            NotificationItem("🔖", "High Saves", "Reel #3 got 500+ saves today", "8h ago", Color(0xFF9C27B0)),
            NotificationItem("🤝", "Collaboration Request", "fitness_guru wants to collaborate", "12h ago", Color(0xFF00BCD4)),
            NotificationItem("🏆", "Top Creator", "You're in the top 5% of creators this week!", "1d ago", Color(0xFFFFD54F))
        )
    }

    val highlights = remember {
        mutableStateListOf(
            StoryHighlight("🔥", "Trending", listOf(Color(0xFF7B2FF7), Color(0xFFE1306C))),
            StoryHighlight("📈", "Growth", listOf(Color(0xFF2196F3), Color(0xFF00BCD4))),
            StoryHighlight("🎯", "Goals", listOf(Color(0xFFFF9800), Color(0xFFFF5722))),
            StoryHighlight("💡", "Tips", listOf(Color(0xFF16A34A), Color(0xFF4CAF50))),
            StoryHighlight("🏆", "Wins", listOf(Color(0xFFFFD54F), Color(0xFFFF9800))),
            StoryHighlight("📊", "Stats", listOf(Color(0xFF9C27B0), Color(0xFF7B2FF7)))
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

        if (showOnboarding && !showSplash) {
            OnboardingScreen(onboardingPage, { onboardingPage++ }, { showOnboarding = false })
        } else if (!showSplash) {

            if (showEditProfile) {
                EditProfileScreen(
                    bgColor = bgColor,
                    cardColor = cardColor,
                    textColor = textColor,
                    subTextColor = subTextColor,
                    name = profileName,
                    username = profileUsername,
                    bio = profileBio,
                    onSave = { newName, newUsername, newBio ->
                        profileName = newName
                        profileUsername = newUsername
                        profileBio = newBio
                        showEditProfile = false
                    },
                    onBack = { showEditProfile = false }
                )
            } else if (selectedReel != null) {
                ReelDetailScreen(
                    reel = selectedReel!!,
                    bgColor = bgColor,
                    cardColor = cardColor,
                    textColor = textColor,
                    subTextColor = subTextColor,
                    onBack = { selectedReel = null }
                )
            } else {

                MaterialTheme(colorScheme = colorScheme) {

                    Scaffold(
                        containerColor = bgColor,
                        bottomBar = {
                            NavigationBar(containerColor = if (isDarkMode) DarkCard else Color.White) {
                                NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Default.Home, "Home", modifier = Modifier.size(24.dp)) }, label = { Text("Home") })
                                NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Default.PlayCircle, "Reels", modifier = Modifier.size(24.dp)) }, label = { Text("Reels") })
                                NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Default.Notifications, "Alerts", modifier = Modifier.size(24.dp)) }, label = { Text("Alerts") })
                                NavigationBarItem(selected = selectedTab == 3, onClick = { selectedTab = 3 }, icon = { Icon(Icons.Default.Person, "Profile", modifier = Modifier.size(24.dp)) }, label = { Text("Profile") })
                            }
                        }
                    ) { paddingValues ->

                        Column(modifier = Modifier.fillMaxSize().background(bgColor).padding(paddingValues)) {

                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = { isDarkMode = !isDarkMode }) {
                                    Icon(if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, "Toggle", modifier = Modifier.size(26.dp), tint = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFF7B2FF7))
                                }
                            }

                            when (selectedTab) {
                                0 -> HomeScreen(bgColor, cardColor, textColor, subTextColor, insightCardColor)
                                1 -> ReelsScreen(bgColor, cardColor, textColor, subTextColor, reels, { showAddDialog = true }, { selectedReel = it })
                                2 -> NotificationsScreen(bgColor, cardColor, textColor, subTextColor, notifications)
                                3 -> ProfileScreen(bgColor, cardColor, textColor, subTextColor, isDarkMode, notifEnabled, analyticsEnabled, autoRefresh, highlights, profileName, profileUsername, profileBio, { showEditProfile = true }) { toggleId ->
                                    when (toggleId) {
                                        0 -> isDarkMode = !isDarkMode
                                        1 -> notifEnabled = !notifEnabled
                                        2 -> analyticsEnabled = !analyticsEnabled
                                        3 -> autoRefresh = !autoRefresh
                                    }
                                }
                            }
                        }
                    }
                }

                if (showAddDialog) {
                    AddReelDialog(cardColor, textColor, subTextColor, { showAddDialog = false }) { title, views, engagement ->
                        reels.add(0, Reel(title, views, engagement))
                        showAddDialog = false
                    }
                }
            }
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

// =================== ONBOARDING ===================

data class OnboardingData(val emoji: String, val title: String, val subtitle: String, val gradient: List<Color>)

@Composable
fun OnboardingScreen(page: Int, onNext: () -> Unit, onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingData("📊", "Track Your Growth", "Monitor followers, reach, and engagement in real-time", listOf(Color(0xFF7B2FF7), Color(0xFFE1306C))),
        OnboardingData("📱", "Manage Your Reels", "Add, search, and organize all your reels in one place", listOf(Color(0xFF2196F3), Color(0xFF00BCD4))),
        OnboardingData("🔔", "Stay Updated", "Get instant notifications for milestones and trends", listOf(Color(0xFFFF9800), Color(0xFFFF5722)))
    )
    val current = pages[page]

    Column(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(current.gradient)).padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(current.emoji, fontSize = 100.sp)
        Spacer(modifier = Modifier.height(40.dp))
        Text(current.title, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text(current.subtitle, fontSize = 16.sp, color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(60.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == page) 28.dp else 10.dp, 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (index == page) Color.White else Color.White.copy(alpha = 0.4f))
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = { if (page < 2) onNext() else onFinish() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                if (page < 2) "Next" else "Get Started",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = current.gradient[0],
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (page < 2) {
            TextButton(onClick = { onFinish() }) {
                Text("Skip", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }
    }
}

// =================== EDIT PROFILE ===================

@Composable
fun EditProfileScreen(
    bgColor: Color,
    cardColor: Color,
    textColor: Color,
    subTextColor: Color,
    name: String,
    username: String,
    bio: String,
    onSave: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var editName by remember { mutableStateOf(name) }
    var editUsername by remember { mutableStateOf(username) }
    var editBio by remember { mutableStateOf(bio) }

    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(rememberScrollState()).padding(20.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF7B2FF7), modifier = Modifier.size(28.dp))
            }
            Text("Edit Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(110.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF7B2FF7), Color(0xFFE1306C), Color(0xFFFF9800)))))
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(cardColor), contentAlignment = Alignment.Center) {
                    Text("👤", fontSize = 52.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Change Photo", color = Color(0xFF7B2FF7), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Name", fontSize = 14.sp, color = subTextColor, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Username", fontSize = 14.sp, color = subTextColor, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = editUsername,
            onValueChange = { editUsername = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Bio", fontSize = 14.sp, color = subTextColor, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${editBio.length}/150 characters",
            fontSize = 12.sp,
            color = if (editBio.length > 150) Color(0xFFE1306C) else subTextColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { onSave(editName, editUsername, editBio) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2FF7)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Changes", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onBack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = cardColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cancel", color = textColor, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// =================== REEL DETAIL ===================

@Composable
fun ReelDetailScreen(reel: Reel, bgColor: Color, cardColor: Color, textColor: Color, subTextColor: Color, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(rememberScrollState()).padding(20.dp)) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF7B2FF7), modifier = Modifier.size(28.dp))
            }
            Text("Reel Details", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(24.dp)).background(Brush.verticalGradient(listOf(Color(0xFF7B2FF7), Color(0xFFE1306C)))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎬", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(reel.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total Views", fontSize = 14.sp, color = subTextColor)
                Spacer(modifier = Modifier.height(5.dp))
                Text(reel.views, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7B2FF7))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStatCard("❤️", "Likes", "84K", cardColor, textColor, subTextColor, Modifier.weight(1f))
            DetailStatCard("💬", "Comments", "3.2K", cardColor, textColor, subTextColor, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStatCard("🔄", "Shares", "12K", cardColor, textColor, subTextColor, Modifier.weight(1f))
            DetailStatCard("🔖", "Saves", "8K", cardColor, textColor, subTextColor, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("📊 Engagement Rate", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("9.1%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    Text("↑ 2.3% vs avg", fontSize = 14.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)).background(subTextColor.copy(alpha = 0.2f))) {
                    Box(modifier = Modifier.fillMaxWidth(0.91f).height(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFF16A34A)))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Excellent! This reel is performing above average", fontSize = 12.sp, color = subTextColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("📈 Performance Breakdown", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
            Column(modifier = Modifier.padding(18.dp)) {
                PerformanceBar("Reach", 85, Color(0xFF7B2FF7), subTextColor, textColor)
                Spacer(modifier = Modifier.height(14.dp))
                PerformanceBar("Engagement", 91, Color(0xFFE1306C), subTextColor, textColor)
                Spacer(modifier = Modifier.height(14.dp))
                PerformanceBar("Retention", 72, Color(0xFF2196F3), subTextColor, textColor)
                Spacer(modifier = Modifier.height(14.dp))
                PerformanceBar("Virality", 88, Color(0xFFFF9800), subTextColor, textColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Check out this reel on InstaPulse!")
                    putExtra(Intent.EXTRA_TEXT, "🎬 ${reel.title}\n👀 ${reel.views}\n${reel.engagement}\n\nTracked via InstaPulse — Your Instagram Growth Hub")
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Reel"))
            }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2FF7)), shape = RoundedCornerShape(12.dp)) {
                Text("📤 Share", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Button(onClick = { }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = cardColor), shape = RoundedCornerShape(12.dp)) {
                Text("📌 Pin", color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun DetailStatCard(icon: String, label: String, value: String, cardColor: Color, textColor: Color, subTextColor: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(label, fontSize = 12.sp, color = subTextColor)
        }
    }
}

@Composable
fun PerformanceBar(label: String, percentage: Int, color: Color, subTextColor: Color, textColor: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 14.sp, color = subTextColor)
            Text("$percentage%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(subTextColor.copy(alpha = 0.2f))) {
            Box(modifier = Modifier.fillMaxWidth(percentage / 100f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(color))
        }
    }
}

// =================== ADD REEL DIALOG ===================

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

// =================== HOME SCREEN ===================

@Composable
fun HomeScreen(bgColor: Color, cardColor: Color, textColor: Color, subTextColor: Color, insightCardColor: Color) {
    var expandedCard by remember { mutableIntStateOf(-1) }
    var chartAnimationPlayed by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var pullDistance by remember { mutableStateOf(0f) }
    var refreshTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { delay(500); chartAnimationPlayed = true }
    LaunchedEffect(refreshTriggered) {
        if (refreshTriggered) {
            delay(1500)
            isRefreshing = false
            refreshTriggered = false
            pullDistance = 0f
        }
    }

    val weekData = listOf("Mon" to 120f, "Tue" to 180f, "Wed" to 90f, "Thu" to 250f, "Fri" to 310f, "Sat" to 280f, "Sun" to 200f)

    val rotation by animateFloatAsState(targetValue = if (isRefreshing) 360f else 0f, animationSpec = tween(durationMillis = 1000), label = "rotation")
    val pullAlpha by animateFloatAsState(targetValue = if (pullDistance > 50f) 1f else 0f, animationSpec = tween(200), label = "pullAlpha")

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (pullDistance > 50f || isRefreshing) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFF7B2FF7),
                        modifier = Modifier.size(28.dp).rotate(if (isRefreshing) rotation else pullDistance).alpha(pullAlpha)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isRefreshing) "Refreshing..." else "Pull to refresh",
                        fontSize = 14.sp,
                        color = Color(0xFF7B2FF7),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text("InstaPulse", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7B2FF7))
            Text("Your Instagram Growth Hub", fontSize = 14.sp, color = subTextColor)
            Spacer(modifier = Modifier.height(25.dp))
            Text(if (isRefreshing) "Refreshing data..." else "Good Morning, Creator 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(18.dp))

            ExpandableStatCard("Followers", "12.4K", "+324 this week", expandedCard == 0, { expandedCard = if (expandedCard == 0) -1 else 0 }, cardColor, textColor, subTextColor, listOf("This Week" to "+324", "This Month" to "+1.2K", "Non-Followers" to "8.1K", "Engaged Followers" to "4.3K"))
            Spacer(modifier = Modifier.height(12.dp))
            ExpandableStatCard("Reach", "284K", "+18% this week", expandedCard == 1, { expandedCard = if (expandedCard == 1) -1 else 1 }, cardColor, textColor, subTextColor, listOf("From Home" to "142K", "From Explore" to "89K", "From Hashtags" to "38K", "From Profile" to "15K"))
            Spacer(modifier = Modifier.height(12.dp))
            ExpandableStatCard("Engagement", "7.82%", "+12.5% this week", expandedCard == 2, { expandedCard = if (expandedCard == 2) -1 else 2 }, cardColor, textColor, subTextColor, listOf("Likes" to "21.4K", "Comments" to "2.1K", "Shares" to "4.8K", "Saves" to "3.2K"))
            Spacer(modifier = Modifier.height(22.dp))

            Text("📊 Weekly Followers Growth", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                        weekData.forEachIndexed { index, (day, value) ->
                            val maxValue = weekData.maxOf { it.second }
                            val targetHeight = (value / maxValue) * 140f
                            val animatedHeight by animateFloatAsState(
                                targetValue = if (chartAnimationPlayed) targetHeight else 0f,
                                animationSpec = tween(durationMillis = 800, delayMillis = index * 80),
                                label = "bar_$index"
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                                Text("${value.toInt()}", fontSize = 10.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(modifier = Modifier.width(28.dp).height(animatedHeight.dp).clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)).background(if (value == maxValue) Color(0xFF7B2FF7) else Color(0xFF7B2FF7).copy(alpha = 0.5f)))
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
            Text("📈 Reach Growth Trend", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(12.dp))
            ReachLineChart(cardColor, textColor, subTextColor, chartAnimationPlayed)
            Spacer(modifier = Modifier.height(22.dp))
            Text("🤖 AI Growth Insight", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = insightCardColor)) {
                Text("Your motivational Reels are performing well. Try creating more content around your best-performing topics.", modifier = Modifier.padding(18.dp), fontSize = 15.sp, color = textColor)
            }

            Spacer(modifier = Modifier.height(22.dp))
            Text("📊 Reach Breakdown", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            Spacer(modifier = Modifier.height(12.dp))
            ReachPieChart(cardColor, textColor, subTextColor, chartAnimationPlayed)

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
}

@Composable
fun ExpandableStatCard(title: String, value: String, growth: String, isExpanded: Boolean, onClick: () -> Unit, cardColor: Color, textColor: Color, subTextColor: Color, details: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
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

// =================== REELS SCREEN ===================

@Composable
fun ReelsScreen(bgColor: Color, cardColor: Color, textColor: Color, subTextColor: Color, reels: MutableList<Reel>, onAddClick: () -> Unit, onReelClick: (Reel) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredReels = if (searchQuery.isBlank()) reels else reels.filter { it.title.contains(searchQuery, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("📱 Your Reels", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textColor)
            Button(onClick = { onAddClick() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2FF7)), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp), tint = Color.White)
                Spacer(modifier = Modifier.size(4.dp))
                Text("Add Reel", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("🔍 Search reels...", fontSize = 14.sp, color = subTextColor) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = subTextColor, modifier = Modifier.size(20.dp))
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (searchQuery.isBlank()) "💡 Tap any reel for details • Search to filter"
            else "🔎 ${filteredReels.size} reel${if (filteredReels.size != 1) "s" else ""} found",
            fontSize = 12.sp,
            color = subTextColor
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredReels.isEmpty()) {
            Spacer(modifier = Modifier.height(60.dp))
            Text("🔍", fontSize = 60.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No reels found", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Try a different search", fontSize = 14.sp, color = subTextColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        } else {
            filteredReels.forEach { reel ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onReelClick(reel) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Brush.verticalGradient(listOf(Color(0xFF7B2FF7), Color(0xFFE1306C)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎬", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(reel.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(reel.views, fontSize = 14.sp, color = Color(0xFF7B2FF7), fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(reel.engagement, fontSize = 12.sp, color = subTextColor)
                        }
                        Text("→", fontSize = 20.sp, color = subTextColor)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// =================== NOTIFICATIONS SCREEN ===================

@Composable
fun NotificationsScreen(bgColor: Color, cardColor: Color, textColor: Color, subTextColor: Color, notifications: List<NotificationItem>) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf("All", "Mentions", "Likes")
    val filterIcons = listOf("🔔", "💬", "❤️")

    val filteredNotifications = when (selectedFilter) {
        1 -> notifications.filter { it.icon == "💬" || it.icon == "🤝" }
        2 -> notifications.filter { it.icon == "❤️" }
        else -> notifications
    }

    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("🔔 Notifications", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(5.dp))
        Text("Stay updated with your latest activity", fontSize = 14.sp, color = subTextColor)
        Spacer(modifier = Modifier.height(16.dp))

        // Filter tabs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            filters.forEachIndexed { index, filter ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedFilter == index) Color(0xFF7B2FF7) else cardColor)
                        .clickable { selectedFilter = index }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(filterIcons[index], fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        filter,
                        fontSize = 13.sp,
                        fontWeight = if (selectedFilter == index) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedFilter == index) Color.White else subTextColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("${filteredNotifications.size} notification${if (filteredNotifications.size != 1) "s" else ""}", fontSize = 12.sp, color = subTextColor)
        Spacer(modifier = Modifier.height(12.dp))

        if (filteredNotifications.isEmpty()) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(filterIcons[selectedFilter], fontSize = 56.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text("No ${filters[selectedFilter].lowercase()} yet", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(6.dp))
            Text("You'll see ${filters[selectedFilter].lowercase()} here when they arrive", fontSize = 13.sp, color = subTextColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        } else {
            filteredNotifications.forEach { notification ->
                NotificationCard(notification, cardColor, textColor, subTextColor)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem, cardColor: Color, textColor: Color, subTextColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(notification.color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Text(notification.icon, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(3.dp))
                Text(notification.subtitle, fontSize = 13.sp, color = subTextColor)
            }
            Text(notification.time, fontSize = 11.sp, color = subTextColor)
        }
    }
}

// =================== PROFILE SCREEN ===================

@Composable
fun ProfileScreen(
    bgColor: Color,
    cardColor: Color,
    textColor: Color,
    subTextColor: Color,
    isDarkMode: Boolean,
    notifEnabled: Boolean,
    analyticsEnabled: Boolean,
    autoRefresh: Boolean,
    highlights: List<StoryHighlight>,
    profileName: String,
    profileUsername: String,
    profileBio: String,
    onEditProfile: () -> Unit,
    onToggle: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(rememberScrollState()).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(96.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF7B2FF7), Color(0xFFE1306C), Color(0xFFFF9800)))))
            Box(modifier = Modifier.size(88.dp).clip(CircleShape).background(cardColor), contentAlignment = Alignment.Center) {
                Text("👤", fontSize = 48.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(profileName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(3.dp))
        Text(profileUsername, fontSize = 16.sp, color = Color(0xFF7B2FF7), fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(5.dp))
        Text(profileBio, fontSize = 14.sp, color = subTextColor, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { onEditProfile() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2FF7)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("✏️ Edit Profile", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("12.4K", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor); Text("Followers", fontSize = 13.sp, color = subTextColor) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("892", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor); Text("Following", fontSize = 13.sp, color = subTextColor) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("247", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor); Text("Posts", fontSize = 13.sp, color = subTextColor) }
        }

        Spacer(modifier = Modifier.height(22.dp))
        AchievementsSection(cardColor, textColor, subTextColor)
        Spacer(modifier = Modifier.height(22.dp))
        ContentCalendarSection(cardColor, textColor, subTextColor)
        Spacer(modifier = Modifier.height(22.dp))
        Text("✨ Highlights", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(highlights.size) { index ->
                val highlight = highlights[index]
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(68.dp).clip(CircleShape).background(Brush.linearGradient(highlight.gradient)).border(2.dp, cardColor, CircleShape).clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(highlight.emoji, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(highlight.label, fontSize = 12.sp, color = subTextColor, textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))
        Text("📊 Account Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(12.dp))
        StatCard("Total Posts", "247", "+12 this month", cardColor, textColor, subTextColor)
        Spacer(modifier = Modifier.height(12.dp))
        StatCard("Avg. Reach per Reel", "89K", "+15% this month", cardColor, textColor, subTextColor)

        Spacer(modifier = Modifier.height(25.dp))
        Text("⚙️ Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
            Column(modifier = Modifier.padding(18.dp)) {
                SettingRow("🔔", "Notifications", "Get alerts for new followers, likes & comments", notifEnabled) { onToggle(1) }
                Spacer(modifier = Modifier.height(16.dp))
                SettingRow("🌙", "Dark Mode", "Switch between light and dark theme", isDarkMode) { onToggle(0) }
                Spacer(modifier = Modifier.height(16.dp))
                SettingRow("📊", "Analytics Tracking", "Track your growth and engagement metrics", analyticsEnabled) { onToggle(2) }
                Spacer(modifier = Modifier.height(16.dp))
                SettingRow("🔄", "Auto Refresh", "Automatically refresh data every 5 minutes", autoRefresh) { onToggle(3) }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("ℹ️ About", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text("InstaPulse v2.0", fontSize = 13.sp, color = subTextColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Your Instagram Growth Hub — track followers, reach, engagement & reels all in one place.", fontSize = 13.sp, color = subTextColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Made with ❤️ by @gokul_creator", fontSize = 13.sp, color = subTextColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("🚪 Logout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SettingRow(icon: String, title: String, subtitle: String, checked: Boolean, onToggle: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = if (checked) Color.Black else Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF7B2FF7),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
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

@Composable
fun AchievementsSection(cardColor: Color, textColor: Color, subTextColor: Color) {
    val achievements = listOf(
        Triple("🏆", "Top Creator", "Top 5% this week"),
        Triple("🔥", "Viral Star", "1M+ views on a reel"),
        Triple("📈", "Rising Star", "500+ followers in a month"),
        Triple("⚡", "Consistent", "Posted 30 days straight"),
        Triple("💎", "Highly Engaged", "8%+ engagement rate"),
        Triple("🌟", "Trending", "3 reels trending")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("🏆 Achievements", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(achievements.size) { index ->
                val (emoji, title, desc) = achievements[index]
                Card(
                    modifier = Modifier.width(130.dp).clickable { },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF7B2FF7).copy(alpha = 0.15f), Color(0xFFE1306C).copy(alpha = 0.15f)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(desc, fontSize = 10.sp, color = subTextColor, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun ReachLineChart(cardColor: Color, textColor: Color, subTextColor: Color, animationPlayed: Boolean) {
    val reachData = listOf(120f, 180f, 150f, 220f, 280f, 250f, 310f, 340f)
    val weekLabels = listOf("W1", "W2", "W3", "W4", "W5", "W6", "W7", "W8")

    val animationProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "lineChart"
    )

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Reach (8 weeks)", fontSize = 14.sp, color = subTextColor)
                Text("340K", fontSize = 14.sp, color = Color(0xFF7B2FF7), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val maxValue = reachData.maxOrNull() ?: 1f

                val points = reachData.mapIndexed { index, value ->
                    val x = (canvasWidth / (reachData.size - 1)) * index
                    val y = canvasHeight - (value / maxValue) * (canvasHeight - 20f) - 10f
                    Pair(x, y)
                }

                val animatedPointCount = (points.size * animationProgress).toInt().coerceIn(1, points.size)

                // Draw filled gradient area
                if (animatedPointCount > 1) {
                    val areaPath = Path().apply {
                        moveTo(points[0].first, canvasHeight)
                        for (i in 0 until animatedPointCount) {
                            lineTo(points[i].first, points[i].second)
                        }
                        lineTo(points[animatedPointCount - 1].first, canvasHeight)
                        close()
                    }
                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF7B2FF7).copy(alpha = 0.3f), Color(0xFF7B2FF7).copy(alpha = 0.0f))
                        )
                    )
                }

                // Draw line
                if (animatedPointCount > 1) {
                    val linePath = Path().apply {
                        moveTo(points[0].first, points[0].second)
                        for (i in 1 until animatedPointCount) {
                            lineTo(points[i].first, points[i].second)
                        }
                    }
                    drawPath(
                        path = linePath,
                        color = Color(0xFF7B2FF7),
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }

                // Draw dots
                for (i in 0 until animatedPointCount) {
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = Offset(points[i].first, points[i].second)
                    )
                    drawCircle(
                        color = Color(0xFF7B2FF7),
                        radius = 4f,
                        center = Offset(points[i].first, points[i].second)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                weekLabels.forEach { week ->
                    Text(week, fontSize = 10.sp, color = subTextColor)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("📈 +183% reach growth in 8 weeks!", fontSize = 13.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ReachPieChart(cardColor: Color, textColor: Color, subTextColor: Color, animationPlayed: Boolean) {
    val slices = listOf(
        Triple("Home", 50f, Color(0xFF7B2FF7)),
        Triple("Explore", 31f, Color(0xFFE1306C)),
        Triple("Hashtags", 13f, Color(0xFFFF9800)),
        Triple("Profile", 6f, Color(0xFF2196F3))
    )

    val animationProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "pieChart"
    )

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie chart canvas
                Canvas(modifier = Modifier.size(130.dp)) {
                    val canvasSize = size.minDimension
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                    val radius = canvasSize / 2f
                    var startAngle = -90f

                    slices.forEach { slice ->
                        val label = slice.first
                        val percentage = slice.second
                        val color = slice.third
                        val sweepAngle = (percentage / 100f) * 360f * animationProgress
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = androidx.compose.ui.geometry.Offset(center.x - radius, center.y - radius),
                            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                        )
                        startAngle += sweepAngle
                    }

                    // Center hole for donut effect
                    drawCircle(
                        color = cardColor,
                        radius = radius * 0.55f,
                        center = center
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Legend
                Column(modifier = Modifier.weight(1f)) {
                    slices.forEach { slice ->
                        val label = slice.first
                        val percentage = slice.second
                        val color = slice.third
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(14.dp).clip(RoundedCornerShape(3.dp)).background(color))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, fontSize = 13.sp, color = textColor, modifier = Modifier.weight(1f))
                            Text("${percentage.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("🏠 Home is your top reach source — 50%", fontSize = 12.sp, color = subTextColor)
            }
        }
    }
}

@Composable
fun ContentCalendarSection(cardColor: Color, textColor: Color, subTextColor: Color) {
    val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val scheduledPosts = listOf(
        Triple("Mon", "Reel: Morning Routine", "9:00 AM"),
        Triple("Tue", "Post: Motivation Quote", "1:00 PM"),
        Triple("Thu", "Reel: Fitness Tips", "7:00 PM"),
        Triple("Fri", "Reel: Success Story", "8:00 PM"),
        Triple("Sat", "Post: Behind Scenes", "5:00 PM")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("📅 Content Calendar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text("This week's posting schedule", fontSize = 13.sp, color = subTextColor)
        Spacer(modifier = Modifier.height(14.dp))

        // Week strip
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            weekDays.forEach { day ->
                val hasPost = scheduledPosts.any { it.first == day }
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (hasPost) Color(0xFF7B2FF7).copy(alpha = 0.1f) else cardColor)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(day, fontSize = 11.sp, color = subTextColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.size(8.dp).clip(CircleShape).background(if (hasPost) Color(0xFF7B2FF7) else subTextColor.copy(alpha = 0.3f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scheduled posts list
        scheduledPosts.forEach { (day, title, time) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF7B2FF7).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📅", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("$day • $time", fontSize = 12.sp, color = subTextColor)
                    }
                    Text("🔔", fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text("💡 Tip: Best posting time is 7 PM - 10 PM", fontSize = 12.sp, color = Color(0xFF7B2FF7), fontWeight = FontWeight.Medium)
    }
}
