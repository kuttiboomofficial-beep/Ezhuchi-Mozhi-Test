package com.tavrixlabs.ezhuchimozhi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray

data class Quote(val text: String, val category: String)

private val quoteList = listOf(
    Quote("உன்னை நேசிப்பது என் விருப்பம்… உன் நினைவில் வாழ்வது என் பழக்கம்.", "காதல்"),
    Quote("சில மௌனங்கள் ஆயிரம் வார்த்தைகளைவிட வலிமையானவை.", "காதல் தோல்வி"),
    Quote("விழுந்த இடம் முக்கியமில்லை; மீண்டும் எழும் துணிவுதான் முக்கியம்.", "தன்னம்பிக்கை"),
    Quote("நேரம் எல்லாவற்றையும் மாற்றும்; பொறுமை நம்மை வலிமையாக்கும்.", "வாழ்க்கை"),
    Quote("உண்மையான அன்பு உரிமை கேட்காது; மனதைப் புரிந்துகொள்ளும்.", "காதல்"),
    Quote("உன்னை இழந்த வலி, உன்னை நீயே கண்டுபிடிக்க உதவட்டும்.", "காதல் தோல்வி"),
    Quote("மெதுவாக சென்றாலும் பரவாயில்லை; நின்றுவிடாதே.", "தன்னம்பிக்கை"),
    Quote("ஒவ்வொரு நாளும் ஒரு புதிய தொடக்கத்திற்கான வாய்ப்பு.", "வாழ்க்கை"),
    Quote("சொல்லாத உணர்வுகளுக்கும் இதயத்தில் ஒரு மொழி உண்டு.", "காதல்"),
    Quote("பிரிவு முடிவல்ல; சில நேரம் அது நம்மை நமக்கே திருப்பித் தரும்.", "காதல் தோல்வி"),
    Quote("உன் முயற்சி அமைதியாக இருக்கட்டும்; வெற்றி பேசட்டும்.", "தன்னம்பிக்கை"),
    Quote("சிறிய மகிழ்ச்சிகளையும் ரசிக்கத் தெரிந்தால் வாழ்க்கை அழகாகும்.", "வாழ்க்கை")
) + extraQuotes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EzhuchiApp(onShare = { text ->
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }, "கவிதையை பகிரவும்"))
        }) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EzhuchiApp(onShare: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val clipboard = LocalClipboardManager.current
    val prefs = remember { context.getSharedPreferences("ezhuchi_prefs", 0) }
    val saved = remember {
        mutableStateListOf<String>().apply {
            val arr = runCatching { JSONArray(prefs.getString("favorites", "[]")) }.getOrNull()
            if (arr != null) for (i in 0 until arr.length()) add(arr.optString(i))
        }
    }
    val categories = listOf("அனைத்தும்", "காதல்", "காதல் தோல்வி", "தன்னம்பிக்கை", "வாழ்க்கை", "பிடித்தவை")
    var selected by remember { mutableStateOf("அனைத்தும்") }
    var search by remember { mutableStateOf("") }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF5B3A91),
            secondary = androidx.compose.ui.graphics.Color(0xFFE6B65C),
            background = androidx.compose.ui.graphics.Color(0xFFFFFBFF),
            surface = androidx.compose.ui.graphics.Color(0xFFFFFBFF)
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("எழுச்சி மொழி", fontWeight = FontWeight.Bold)
                            Text("Tamil Status & Quotes", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(178.dp)
                        .clip(RoundedCornerShape(22.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sunset_bg),
                        contentDescription = "மாலைச் சூரிய அஸ்தமனம்",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x660F0820))
                            .padding(18.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Text(
                                "🌅 இன்றைய கவிதை",
                                color = Color(0xFFFFD88A),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "உன் உணர்வுகளுக்கு ஓர் எழுத்து 💜",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("கவிதையைத் தேடு…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selected == category,
                            onClick = { selected = category },
                            label = { Text(category) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("${if (selected == "பிடித்தவை") saved.size else quoteList.count { (selected == "அனைத்தும்" || it.category == selected) && it.text.contains(search, ignoreCase = true) }} கவிதைகள்",
                    style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    val filtered = quoteList.filter {
                        (selected == "அனைத்தும்" || selected == "பிடித்தவை" || it.category == selected) &&
                            it.text.contains(search, ignoreCase = true) &&
                            (selected != "பிடித்தவை" || saved.contains(it.text))
                    }
                    items(filtered, key = { it.text }) { quote ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(quote.category, style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(8.dp))
                                Text(quote.text, style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium)
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        if (saved.contains(quote.text)) saved.remove(quote.text)
                                        else saved.add(quote.text)
                                        val arr = JSONArray()
                                        saved.forEach { arr.put(it) }
                                        prefs.edit().putString("favorites", arr.toString()).apply()
                                    }) {
                                        Icon(
                                            if (saved.contains(quote.text)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Favorite"
                                        )
                                    }
                                    TextButton(onClick = {
                                        clipboard.setText(AnnotatedString(quote.text))
                                    }) { Text("நகலெடு") }
                                    TextButton(onClick = { onShare(quote.text) }) { Text("பகிர்") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
