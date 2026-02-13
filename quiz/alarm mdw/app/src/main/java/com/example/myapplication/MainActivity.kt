package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                QuizApp()
            }
        }
    }
}

// --- Data Models ---
data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

data class Quiz(
    val title: String,
    val description: String,
    val questions: List<Question>
)

// --- Sample Data ---
val sampleQuizzes = listOf(
    Quiz(
        "General Knowledge",
        "Test your general awareness with these fun questions.",
        listOf(
            Question("What is the capital of France?", listOf("London", "Berlin", "Paris", "Madrid"), 2),
            Question("Which planet is known as the Red Planet?", listOf("Earth", "Mars", "Jupiter", "Saturn"), 1),
            Question("What is the largest ocean on Earth?", listOf("Atlantic", "Indian", "Arctic", "Pacific"), 3)
        )
    ),
    Quiz(
        "Science & Nature",
        "Explore the wonders of the natural world.",
        listOf(
            Question("What is the chemical symbol for gold?", listOf("Gd", "Au", "Ag", "Fe"), 1),
            Question("How many planets are in our solar system?", listOf("7", "8", "9", "10"), 1),
            Question("What is the powerhouse of the cell?", listOf("Nucleus", "Ribosome", "Mitochondria", "Cytoplasm"), 2)
        )
    ),
    Quiz(
        "Technology",
        "Are you a true tech enthusiast?",
        listOf(
            Question("Who is the co-founder of Microsoft?", listOf("Steve Jobs", "Bill Gates", "Elon Musk", "Mark Zuckerberg"), 1),
            Question("What does CPU stand for?", listOf("Central Process Unit", "Computer Personal Unit", "Central Processing Unit", "Central Processor Unit"), 2),
            Question("Which programming language is used for Android development?", listOf("Swift", "Kotlin", "C#", "PHP"), 1)
        )
    )
)

enum class Screen {
    HOME, QUIZ, RESULT
}

@Composable
fun QuizApp() {
    var currentScreen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var selectedQuiz by remember { mutableStateOf<Quiz?>(null) }
    var finalScore by rememberSaveable { mutableIntStateOf(0) }
    var userAnswers by remember { mutableStateOf<List<Int>>(emptyList()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn().togetherWith(fadeOut())
            },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                Screen.HOME -> HomeScreen(
                    quizzes = sampleQuizzes,
                    onQuizSelected = { quiz ->
                        selectedQuiz = quiz
                        finalScore = 0
                        userAnswers = emptyList()
                        currentScreen = Screen.QUIZ
                    }
                )
                Screen.QUIZ -> selectedQuiz?.let { quiz ->
                    QuizScreen(
                        quiz = quiz,
                        onQuizComplete = { score, answers ->
                            finalScore = score
                            userAnswers = answers
                            currentScreen = Screen.RESULT
                        }
                    )
                }
                Screen.RESULT -> selectedQuiz?.let { quiz ->
                    ResultScreen(
                        quiz = quiz,
                        userAnswers = userAnswers,
                        score = finalScore,
                        onRestart = {
                            currentScreen = Screen.HOME
                            selectedQuiz = null
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(quizzes: List<Quiz>, onQuizSelected: (Quiz) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quiz Master", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onQuizSelected(quizzes.random()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Start Random Quiz", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Choose a Category",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quizzes) { quiz ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onQuizSelected(quiz) },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = quiz.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(
                                    text = quiz.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${quiz.questions.size} Questions",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { onQuizSelected(quiz) },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(quiz: Quiz, onQuizComplete: (Int, List<Int>) -> Unit) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var runningScore by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateListOf<Int>() }

    val currentQuestion = quiz.questions[currentQuestionIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quiz.title, style = MaterialTheme.typography.titleMedium) },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = "Score: $runningScore",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Question ${currentQuestionIndex + 1}/${quiz.questions.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / quiz.questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(8.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = currentQuestion.text,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isCorrect = index == currentQuestion.correctAnswerIndex
                    val isSelected = selectedOptionIndex == index

                    val backgroundColor = when {
                        hasAnswered && isCorrect -> Color(0xFFC8E6C9) // Green
                        hasAnswered && isSelected -> Color(0xFFFFCDD2) // Red
                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        hasAnswered && isCorrect -> Color(0xFF4CAF50)
                        hasAnswered && isSelected -> Color(0xFFF44336)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outlineVariant
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !hasAnswered) {
                                selectedOptionIndex = index
                            },
                        color = backgroundColor,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ('A' + index).toString(),
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        CircleShape
                                    )
                                    .wrapContentSize(Alignment.Center),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = option,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            if (hasAnswered) {
                                if (isCorrect) {
                                    Icon(Icons.Default.Check, contentDescription = "Correct", tint = Color(0xFF2E7D32))
                                } else if (isSelected) {
                                    Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = Color(0xFFC62828))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (!hasAnswered) {
                        hasAnswered = true
                        val answer = selectedOptionIndex!!
                        userAnswers.add(answer)
                        if (answer == currentQuestion.correctAnswerIndex) {
                            runningScore++
                        }
                    } else {
                        if (currentQuestionIndex < quiz.questions.size - 1) {
                            currentQuestionIndex++
                            selectedOptionIndex = null
                            hasAnswered = false
                        } else {
                            onQuizComplete(runningScore, userAnswers.toList())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedOptionIndex != null,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (!hasAnswered) "Check Answer" 
                           else if (currentQuestionIndex < quiz.questions.size - 1) "Next Question" 
                           else "Finish Quiz",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(quiz: Quiz, userAnswers: List<Int>, score: Int, onRestart: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quiz Results", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val percentage = (score.toFloat() / quiz.questions.size * 100).toInt()
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
                CircularProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$percentage%", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.ExtraBold)
                    Text(text = "$score / ${quiz.questions.size}", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Review Answers", 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(quiz.questions.size) { index ->
                    val question = quiz.questions[index]
                    val userAnswer = userAnswers.getOrNull(index) ?: -1
                    val isCorrect = userAnswer == question.correctAnswerIndex
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "${index + 1}. ${question.text}", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (userAnswer != -1) {
                                Text(
                                    text = "Your Answer: ${question.options[userAnswer]}",
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (!isCorrect) {
                                Text(
                                    text = "Correct Answer: ${question.options[question.correctAnswerIndex]}",
                                    color = Color(0xFF2E7D32),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Back to Home", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
