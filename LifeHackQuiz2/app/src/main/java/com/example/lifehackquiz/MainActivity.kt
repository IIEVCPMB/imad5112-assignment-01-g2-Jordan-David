package com.example.lifehackquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                LifeHackQuizApp()
            }
        }
    }
}

data class QuizQuestion(val text: String, val isFact: Boolean, val explanation: String)

val questions = listOf(
    QuizQuestion("Putting a wooden spoon over a boiling pot stops it from overflowing.", true, "Wood absorbs moisture and disrupts the surface tension of the bubbles."),
    QuizQuestion("Swiping up to close apps on your phone saves battery.", false, "Force closing apps actually uses more power to restart them from scratch."),
    QuizQuestion("Charging your phone overnight ruins the battery life.", false, "Modern smartphones automatically stop charging when they reach 100%."),
    QuizQuestion("Putting your wet phone in rice dries it out faster.", false, "Rice doesn't absorb moisture fast enough and dust can damage internal components."),
    QuizQuestion("Using incognito mode hides your browsing history from your internet provider.", false, "It only hides history locally on your device; your ISP can still see your traffic."),
    QuizQuestion("Mac computers cannot get viruses.", false, "While less common than Windows, Macs are still vulnerable to malware and viruses."),
    QuizQuestion("More megapixels in a camera always means better photo quality.", false, "Sensor size and image processing are much more important than sheer megapixels."),
    QuizQuestion("Cracking your knuckles causes arthritis.", false, "The sound is just gas bubbles popping in the joint fluid. It does not cause arthritis."),
    QuizQuestion("You need to wait 24 hours before reporting a missing person.", false, "This is a dangerous myth. You should report a missing person immediately."),
    QuizQuestion("Carrots significantly improve your night vision.", false, "While vitamin A is good for eyes, this myth was WWII propaganda to hide the invention of radar.")
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LifeHackQuizApp() {
    var currentScreen by remember { mutableStateOf(Screen.Welcome) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.app_bg_modern),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            },
            label = "Screen Transition"
        ) { screen ->
            when (screen) {
                Screen.Welcome -> WelcomeScreen(onStart = {
                    currentQuestionIndex = 0
                    score = 0
                    currentScreen = Screen.Question
                })
                Screen.Question -> QuestionScreen(
                    questionIndex = currentQuestionIndex,
                    onAnswer = { isCorrect ->
                        if (isCorrect) score++
                    },
                    onNext = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                        } else {
                            currentScreen = Screen.Score
                        }
                    }
                )
                Screen.Score -> ScoreScreen(score = score, onRestart = {
                    currentScreen = Screen.Welcome
                })
            }
        }
    }
}

enum class Screen { Welcome, Question, Score }

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Life Hack Quiz",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Myth or Fact?",
            fontSize = 20.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
        ) {
            Text("Start Challenge", fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
fun QuestionScreen(questionIndex: Int, onAnswer: (Boolean) -> Unit, onNext: () -> Unit) {
    val question = questions[questionIndex]
    var answeredState by remember(questionIndex) { mutableStateOf<Boolean?>(null) }

    val progress by animateFloatAsState(
        targetValue = (questionIndex + 1) / questions.size.toFloat(),
        animationSpec = tween(500),
        label = "Progress Animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Question ${questionIndex + 1} / ${questions.size}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF00E676),
            trackColor = Color.White.copy(alpha = 0.2f)
        )
        
        Spacer(modifier = Modifier.weight(1f))

        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = question.text,
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AnimatedVisibility(
                    visible = answeredState != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val isCorrect = answeredState == question.isFact
                        val resultColor = if (isCorrect) Color(0xFF00E676) else Color(0xFFFF1744)
                        val resultText = if (isCorrect) "Correct!" else "Incorrect!"
                        
                        Text(
                            text = resultText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = resultColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = question.explanation,
                            fontSize = 16.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNext,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
                        ) {
                            Text("Next Question")
                        }
                    }
                }
                
                if (answeredState == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = {
                                answeredState = true
                                onAnswer(true == question.isFact)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF))
                        ) {
                            Text("FACT", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                answeredState = false
                                onAnswer(false == question.isFact)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3D00))
                        ) {
                            Text("MYTH", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ScoreScreen(score: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quiz Complete!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Score",
                    fontSize = 20.sp,
                    color = Color.LightGray
                )
                Text(
                    text = "$score / ${questions.size}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00E676)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
        ) {
            Text("Play Again", fontSize = 18.sp, color = Color.White)
        }
    }
}
