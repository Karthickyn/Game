package com.example.mathtugofwar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val P1Color = Color(0xFFEF4444)
val P2Color = Color(0xFF3B82F6)
val RopeColor = Color(0xFFD4A373)
val KnotColor = Color(0xFFFCA311)

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Slate900
                ) {
                    val state by viewModel.uiState.collectAsState()
                    AppRouter(state, viewModel)
                }
            }
        }
    }
}

@Composable
fun AppRouter(state: GameState, viewModel: GameViewModel) {
    when (state.appMode) {
        "LAUNCH" -> LaunchScreen(viewModel)
        "COUNTDOWN" -> CountdownScreen(viewModel)
        "PLAYING" -> GameScreen(state, viewModel)
        "GAME_OVER" -> GameOverScreen(state, viewModel)
    }
}

@Composable
fun LaunchScreen(viewModel: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("MATH", fontSize = 60.sp, fontWeight = FontWeight.Black, color = P1Color)
        Text("TUG OF WAR", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = P2Color)
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = { viewModel.startCountdown() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
            modifier = Modifier.size(200.dp, 60.dp)
        ) {
            Text("START 1v1", fontSize = 24.sp)
        }
    }
}

@Composable
fun CountdownScreen(viewModel: GameViewModel) {
    var count by remember { mutableIntStateOf(3) }
    
    LaunchedEffect(Unit) {
        while (count > 0) {
            delay(1000)
            count--
        }
        viewModel.startGamePlay()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val text = if (count > 0) count.toString() else "GO!"
        Text(text, fontSize = 100.sp, fontWeight = FontWeight.Black, color = Color.White)
    }
}

@Composable
fun GameOverScreen(state: GameState, viewModel: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val winText = if (state.winner == 1) "PLAYER 1 WINS!" else "PLAYER 2 WINS!"
        val winColor = if (state.winner == 1) P1Color else P2Color
        
        Text(winText, fontSize = 42.sp, fontWeight = FontWeight.Black, color = winColor)
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { viewModel.startCountdown() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
        ) {
            Text("PLAY AGAIN", fontSize = 20.sp)
        }
    }
}

@Composable
fun GameScreen(state: GameState, viewModel: GameViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Player 1 Area (Top, Rotated)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .rotate(180f)
                .background(Slate800)
        ) {
            PlayerSection(
                name = "PLAYER 1",
                color = P1Color,
                question = state.p1Question.displayStr,
                input = state.p1Input,
                status = state.p1PreviewStatus,
                onKey = { viewModel.handleP1Input(it) },
                onClearWrong = { viewModel.clearP1WrongStatus() }
            )
        }

        // Rope Area
        RopeArea(state.ropePosition)

        // Player 2 Area (Bottom)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Slate800)
        ) {
            PlayerSection(
                name = "PLAYER 2",
                color = P2Color,
                question = state.p2Question.displayStr,
                input = state.p2Input,
                status = state.p2PreviewStatus,
                onKey = { viewModel.handleP2Input(it) },
                onClearWrong = { viewModel.clearP2WrongStatus() }
            )
        }
    }
}

@Composable
fun RopeArea(ropePosition: Int) {
    // ropePosition is -5 to +5
    val offsetMulti = ropePosition.toFloat() / 5f // -1.0 to 1.0
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Slate900),
        contentAlignment = Alignment.Center
    ) {
        // Center line
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.DarkGray))
        
        // Vertical Rope line
        Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(RopeColor))
        
        // The Knot and Avatars moving together
        // Center is 90dp from top. offset Multi ranges -1 to +1. 
        // Max travel is about 70dp each direction
        val yOffset = offsetMulti * 60f
        
        Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.offset(y = yOffset.dp)) {
                
                // Avatar Top
                Box(modifier = Modifier
                    .offset(y = (-40).dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(P1Color)
                    .align(Alignment.Center)
                )
                
                // Knot
                Box(modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(KnotColor)
                    .align(Alignment.Center)
                )
                
                // Avatar Bottom
                Box(modifier = Modifier
                    .offset(y = 40.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(P2Color)
                    .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun PlayerSection(
    name: String,
    color: Color,
    question: String,
    input: String,
    status: String, // IDLE, WRONG, CORRECT
    onKey: (String) -> Unit,
    onClearWrong: () -> Unit
) {
    // Clear wrong status after a delay
    LaunchedEffect(status) {
        if (status == "WRONG") {
            delay(400)
            onClearWrong()
        }
    }

    val displayValue = if (input.isEmpty()) "?" else input
    val displayColor = when (status) {
        "WRONG" -> Color.Red
        "CORRECT" -> Color.Green
        else -> Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(name, color = color, fontWeight = FontWeight.Bold)
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate900)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("$question = ", fontSize = 28.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(displayValue, fontSize = 32.sp, color = displayColor, fontWeight = FontWeight.Black)
        }

        // Keypad
        KeypadMatrix(color, onKey)
    }
}

@Composable
fun KeypadMatrix(color: Color, onKey: (String) -> Unit) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("DEL", "0", "OK")
    )
    
    Column(
        modifier = Modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in keys) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (key in row) {
                    val keyColor = when (key) {
                        "DEL" -> Color(0xFFEF4444)
                        "OK" -> Color(0xFF4ADE80)
                        else -> color.copy(alpha = 0.8f)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate900.copy(alpha=0.6f))
                            .clickable { onKey(key) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(key, fontSize = 24.sp, color = keyColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
