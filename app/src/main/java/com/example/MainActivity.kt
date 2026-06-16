package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- Models ---

enum class AppTab {
    SUMMARY, ANALYSIS, QUIZ
}

data class StoryStage(
    val id: Int,
    val title: String,
    val text: String,
    val icon: ImageVector,
    val color: Color
)

data class AccordionSection(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val content: @Composable () -> Unit
)

data class QuizQuestion(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

// --- ViewModel ---

class MainViewModel : ViewModel() {
    private val _activeTab = MutableStateFlow(AppTab.SUMMARY)
    val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

    private val _currentStageIndex = MutableStateFlow(0)
    val currentStageIndex: StateFlow<Int> = _currentStageIndex.asStateFlow()

    private val _expandedAccordionId = MutableStateFlow<String?>("plot")
    val expandedAccordionId: StateFlow<String?> = _expandedAccordionId.asStateFlow()

    // Quiz State
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _isAnswerChecked = MutableStateFlow(false)
    val isAnswerChecked: StateFlow<Boolean> = _isAnswerChecked.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    // Data lists
    val storyStages = listOf(
        StoryStage(
            0,
            "The Awakening",
            "As winter ends and snow melts, the forest fairy wakes to begin her yearly task of bringing spring to the forest.",
            Icons.Default.WbSunny,
            Color(0xFFFFB74D)
        ),
        StoryStage(
            1,
            "The Silent Longing",
            "Despite her love for her work, the fairy feels a deep sense of solitude, wishing she had a friend to share her labor with.",
            Icons.Default.FavoriteBorder,
            Color(0xFFE57373)
        ),
        StoryStage(
            2,
            "The Discovery",
            "While bringing life to the valleys and mountains, she discovers a plant with mysterious, unknown buds.",
            Icons.Default.Yard,
            Color(0xFF81C784)
        ),
        StoryStage(
            3,
            "The Miracle",
            "Upon touching a bud, it blooms into a new fairy. Soon, every bud opens to reveal a colorful group of new friends.",
            Icons.Default.Celebration,
            Color(0xFFBA68C8)
        ),
        StoryStage(
            4,
            "The Gift of the Spirit",
            "The new fairies explain they were sent by the Spirit of the Forest, who saw her loneliness and granted her wish for companionship.",
            Icons.Default.AutoAwesome,
            Color(0xFF4DD0E1)
        )
    )

    val quizQuestions = listOf(
        QuizQuestion(
            0,
            "What was the fairy’s specific wish at the beginning of the story?",
            listOf(
                "To stop the snow from melting.",
                "To have a fairy friend to work with.",
                "To find a new forest to live in.",
                "To sleep through the springtime."
            ),
            1,
            "The main fairy loved her work but felt solitude and longed for a fairy companion to share her labor."
        ),
        QuizQuestion(
            1,
            "What action caused the first new fairy to be born?",
            listOf(
                "The fairy sang a beautiful song.",
                "The sunbeams hit the plant directly.",
                "The fairy laid her tiny hand on a bud.",
                "The Spirit of the Forest spoke to the plant."
            ),
            2,
            "Upon laying her tiny hand on a newly discovered bud, the bud immediately opened to bloom into a new fairy."
        ),
        QuizQuestion(
            2,
            "Why did the Spirit of the Forest send the new fairies?",
            listOf(
                "Because the forest was too large for one fairy to manage.",
                "Because the birds were lonely in their nests.",
                "Because the Spirit saw the fairy crying and roaming in solitude.",
                "To help the fairy shake the snow off the trees."
            ),
            2,
            "The Spirit of the Forest saw the main fairy roaming in deep solitude wishing for a companion, and empathetically granted her wish."
        ),
        QuizQuestion(
            3,
            "Where did the new fairies come from?",
            listOf(
                "They flew in from a neighboring forest.",
                "They were hidden inside the many buds of a new plant.",
                "They woke up from under the melting snow.",
                "They were born from the rays of the sun."
            ),
            1,
            "They were contained inside the mysterious flower buds of the newly discovered plant."
        ),
        QuizQuestion(
            4,
            "How is the narrator of this story classified?",
            listOf(
                "First-person (The Fairy).",
                "Second-person (The Reader).",
                "Third-person omniscient.",
                "Third-person limited."
            ),
            2,
            "The narrative is guided by a third-person omniscient narrator who knows the fairy's inner feelings and the intentions of the Forest Spirit."
        )
    )

    fun selectTab(tab: AppTab) {
        _activeTab.value = tab
    }

    fun nextStage() {
        if (_currentStageIndex.value < storyStages.size - 1) {
            _currentStageIndex.value += 1
        }
    }

    fun prevStage() {
        if (_currentStageIndex.value > 0) {
            _currentStageIndex.value -= 1
        }
    }

    fun jumpToStage(index: Int) {
        if (index in storyStages.indices) {
            _currentStageIndex.value = index
        }
    }

    fun toggleAccordion(id: String) {
        if (_expandedAccordionId.value == id) {
            _expandedAccordionId.value = null
        } else {
            _expandedAccordionId.value = id
        }
    }

    fun selectQuizOption(index: Int) {
        if (!_isAnswerChecked.value) {
            _selectedOptionIndex.value = index
        }
    }

    fun checkQuizAnswer() {
        val currentQuestion = quizQuestions[_currentQuestionIndex.value]
        val selected = _selectedOptionIndex.value
        if (selected != null && !_isAnswerChecked.value) {
            _isAnswerChecked.value = true
            if (selected == currentQuestion.correctIndex) {
                _quizScore.value += 1
            }
        }
    }

    fun proceedQuiz() {
        if (_currentQuestionIndex.value < quizQuestions.size - 1) {
            _currentQuestionIndex.value += 1
            _selectedOptionIndex.value = null
            _isAnswerChecked.value = false
        } else {
            _quizCompleted.value = true
        }
    }

    fun restartQuiz() {
        _currentQuestionIndex.value = 0
        _selectedOptionIndex.value = null
        _isAnswerChecked.value = false
        _quizScore.value = 0
        _quizCompleted.value = false
    }
}

// --- Main UI screen components ---

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) { innerPadding ->
                    SpringFriendsMainScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SpringFriendsMainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Premium Hero Banner Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_spring_hero),
                contentDescription = "Scenic magical forest banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Warm cinematic dark-gradient overlay for high legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Forest,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Interactive Story App",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Spring with Friends",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Explore the magic of companionship and nature's warmth",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp
                    ),
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        // --- Styled Top Navigation Tab Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabItems = listOf(
                AppTab.SUMMARY to Triple(Icons.Default.MenuBook, "Summary", "tab_summary"),
                AppTab.ANALYSIS to Triple(Icons.Default.Psychology, "Analysis", "tab_analysis"),
                AppTab.QUIZ to Triple(Icons.Default.Quiz, "Quiz", "tab_quiz")
            )

            tabItems.forEach { (tab, details) ->
                val (icon, title, testTagStr) = details
                val isSelected = activeTab == tab
                val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(containerColor)
                        .clickable { viewModel.selectTab(tab) }
                        .testTag(testTagStr),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = contentColor
                        )
                    }
                }
            }
        }

        // --- Active Module Screen Rendering ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            when (activeTab) {
                AppTab.SUMMARY -> InteractiveSummaryView(viewModel)
                AppTab.ANALYSIS -> LiteraryAnalysisView(viewModel)
                AppTab.QUIZ -> ComprehensionQuizView(viewModel)
            }
        }
    }
}

// ================= MODULE 1: INTERACTIVE SUMMARY =================

@Composable
fun InteractiveSummaryView(viewModel: MainViewModel) {
    val currentIdx by viewModel.currentStageIndex.collectAsState()
    val stages = viewModel.storyStages
    val activeStage = stages[currentIdx]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("summary_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with active step counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STORY STAGE ${currentIdx + 1} OF ${stages.size}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    ),
                    color = activeStage.color
                )

                // Fairy spark indicator icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(activeStage.color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = activeStage.icon,
                        contentDescription = null,
                        tint = activeStage.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Linear Progress Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                stages.indices.forEach { index ->
                    val isVisited = index <= currentIdx
                    val widthFraction = if (index == currentIdx) 2.2f else 1f
                    Box(
                        modifier = Modifier
                            .weight(widthFraction)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                color = if (isVisited) activeStage.color else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.jumpToStage(index) }
                            .testTag("indicator_stage_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated text and content reveal
            AnimatedContent(
                targetState = activeStage,
                transitionSpec = {
                    slideInHorizontally { width -> if (targetState.id > initialState.id) width else -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> if (targetState.id > initialState.id) -width else width } + fadeOut()
                },
                label = "StoryStageTransition"
            ) { stage ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stage.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stage.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 26.sp,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Navigation Arrows with correct touch size (>48dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = { viewModel.prevStage() },
                    enabled = currentIdx > 0,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_prev_stage"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Stage"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Back")
                }

                Button(
                    onClick = { viewModel.nextStage() },
                    enabled = currentIdx < stages.size - 1,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_next_stage"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Stage"
                    )
                }
            }
        }
    }
}

// ================= MODULE 2: LITERARY ANALYSIS =================

@Composable
fun LiteraryAnalysisView(viewModel: MainViewModel) {
    val expandedId by viewModel.expandedAccordionId.collectAsState()

    val sections = listOf(
        AccordionSection("plot", "Plot Structure", Icons.Default.Schema) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlotItem("Exposition", "The end of wintertime in the forest as spring begins.")
                PlotItem("Rising Action", "The fairy discovers a new plant filled with unknown buds.")
                PlotItem("Climax", "The fairy touches a bud, and a new fairy is born.")
                PlotItem("Falling Action", "The remaining buds open, revealing even more fairies.")
                PlotItem("Resolution", "The fairies stay together to work and be friends.")
            }
        },
        AccordionSection("narrator", "Narrator Type", Icons.Default.Visibility) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Third-person omniscient",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "The story is told by an all-knowing narrator who describes not only the visual actions (such as the melting snow and newly discovered plant), but also dives deep into the main fairy's internal feelings of loneliness and desire for companionship, as well as the Spirit of the Forest's empathetic motivations.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        AccordionSection("characters", "Character Profiles", Icons.Default.People) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CharacterItem(
                    name = "The Main Fairy",
                    subtitle = "Deligent & Longing",
                    description = "Diligent and loving toward her labor, yet carries a hidden sadness and a longing for companionship."
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                CharacterItem(
                    name = "The Spirit of the Forest",
                    subtitle = "Observant & Compassionate",
                    description = "An observant, empathetic entity who witnessed the fairy's solitude and intervened to help her."
                )
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                CharacterItem(
                    name = "The New Fairies",
                    subtitle = "Cheerful Companions",
                    description = "A diverse, colorful group born from flower buds to work side-by-side with the main fairy."
                )
            }
        },
        AccordionSection("setting", "Setting Description", Icons.Default.Landscape) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "A Forest in Transition",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "A forest moving from the cold of winter (characterized by melting snow) to the warm vibrancy of spring (visualized by emerging sunbeams, mysterious plant blossoms, and joyous birdsong). This mirrors the emotional shift of the main fairy from cold isolation to warm friendship.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        AccordionSection("themes", "Core Themes", Icons.Default.School) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CoreThemeItem("Solitude to Companionship", "The beautiful transitional journey of moving from a lonely period of hard labor to find support and companionship.")
                CoreThemeItem("Reward for Faithful Labor", "Her dedication and love toward preparing the forest for spring is answered with a miraculous companionship blessing.")
                CoreThemeItem("Restorative Power of Nature", "Spring itself is a symbol of rebirth, recovery, and warm social awakening after deep cold winter.")
            }
        },
        AccordionSection("mood", "Tone & Mood", Icons.Default.Mood) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Melancholy to Whimsical & Uplifting",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Initially calm and slightly melancholy (focusing on the silent longing of the fairy), transitioning quickly into a whimsical, hopeful, and uplifting atmosphere once the bud is touched and a community of beautiful companions is discovered.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        AccordionSection("symbols", "Key Symbols", Icons.Default.Star) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SymbolItem(
                    symbol = "The Melting Snow",
                    significance = "Represents the 'shaking off' of sadness and the gradual end of a lonely, cold period of life."
                )
                SymbolItem(
                    symbol = "The Flower Buds",
                    significance = "Symbolize hidden potential and the beautiful, unexpected birth of new friendships."
                )
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Literary Analysis",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        sections.forEach { section ->
            val isExpanded = expandedId == section.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleAccordion(section.id) }
                    .testTag("accordion_${section.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 1.dp else 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = spring())
                        .padding(16.dp)
                ) {
                    // Header Row (has active tap feedback)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = section.icon,
                                contentDescription = section.title,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Content Section (Visible only when expanded)
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            section.content()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlotItem(stage: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(44.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
        Column {
            Text(
                text = stage,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CharacterItem(name: String, subtitle: String, description: String) {
    Column {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CoreThemeItem(title: String, summary: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Done,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SymbolItem(symbol: String, significance: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = symbol,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = significance,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ================= MODULE 3: COMPREHENSION QUIZ =================

@Composable
fun ComprehensionQuizView(viewModel: MainViewModel) {
    val qIdx by viewModel.currentQuestionIndex.collectAsState()
    val selectedOpt by viewModel.selectedOptionIndex.collectAsState()
    val isChecked by viewModel.isAnswerChecked.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val completed by viewModel.quizCompleted.collectAsState()
    val questions = viewModel.quizQuestions

    if (completed) {
        QuizResultsCard(score = score, total = questions.size, onReset = { viewModel.restartQuiz() })
    } else {
        val currentQuestion = questions[qIdx]

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quiz progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COMPREHENSION QUIZ",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Question ${qIdx + 1} of ${questions.size}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // High legibility question surface card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = currentQuestion.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 26.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Options List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isOptionSelected = selectedOpt == index
                    val optionCode = when (index) {
                        0 -> "A"
                        1 -> "B"
                        2 -> "C"
                        else -> "D"
                    }

                    // Compute background color depending on selected / checked / correctness states
                    val containerColor = when {
                        isChecked && index == currentQuestion.correctIndex -> Color(0xFFE8F5E9) // Correct answer Green background
                        isChecked && isOptionSelected && selectedOpt != currentQuestion.correctIndex -> Color(0xFFFFEBEE) // Wrong selection Red background
                        isOptionSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) // Normal selection color
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        isChecked && index == currentQuestion.correctIndex -> Color(0xFF4CAF50) // Correct green border
                        isChecked && isOptionSelected && selectedOpt != currentQuestion.correctIndex -> Color(0xFFE50914) // Wrong red border
                        isOptionSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    }

                    val contentColor = when {
                        isChecked && index == currentQuestion.correctIndex -> Color(0xFF1B5E20)
                        isChecked && isOptionSelected && selectedOpt != currentQuestion.correctIndex -> Color(0xFFB71C1C)
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isChecked) { viewModel.selectQuizOption(index) }
                            .testTag("quiz_option_${optionCode.lowercase()}_$qIdx"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        border = BorderStroke(width = 1.5.dp, color = borderColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Letter indicator circle
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        color = if (isOptionSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionCode,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isOptionSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isOptionSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                ),
                                color = contentColor,
                                modifier = Modifier.weight(1f)
                            )

                            // Status trailing icon during review
                            if (isChecked) {
                                if (index == currentQuestion.correctIndex) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct Answer",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else if (isOptionSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Incorrect Answer",
                                        tint = Color(0xFFE50914),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Immediate Explanatory feedback (rendered only when checked)
            AnimatedVisibility(
                visible = isChecked,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                val isSelectionCorrect = selectedOpt == currentQuestion.correctIndex
                val feedbackBoxColor = if (isSelectionCorrect) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                val feedbackBorderColor = if (isSelectionCorrect) Color(0xFFC8E6C9) else Color(0xFFFFE0B2)
                val textAccentColor = if (isSelectionCorrect) Color(0xFF1B5E20) else Color(0xFFE65100)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = feedbackBoxColor, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, color = feedbackBorderColor, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelectionCorrect) Icons.Default.Check else Icons.Default.Info,
                            contentDescription = null,
                            tint = textAccentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isSelectionCorrect) "Correct Match!" else "Incorrect. Review Analysis",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = textAccentColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQuestion.explanation,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = textAccentColor.copy(alpha = 0.9f)
                    )
                }
            }

            // Quiz Control Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!isChecked) {
                    Button(
                        onClick = { viewModel.checkQuizAnswer() },
                        enabled = selectedOpt != null,
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .testTag("quiz_check_answer_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Check Answer",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.proceedQuiz() },
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .testTag("quiz_next_question_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (qIdx == questions.size - 1) "View Results" else "Next Question",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizResultsCard(score: Int, total: Int, onReset: () -> Unit) {
    val rating = when {
        score == total -> "Forest Fairy Master!"
        score >= total * 0.7f -> "Excellent Reader!"
        else -> "Spring Explorer!"
    }

    val themeColor = when {
        score == total -> Color(0xFF4CAF50)
        score >= total * 0.7f -> Color(0xFFFFB74D)
        else -> Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_results_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, themeColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(themeColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (score == total) Icons.Default.EmojiEvents else Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Quiz Completed!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$score / $total",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = themeColor
            )

            Text(
                text = rating,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                ),
                color = themeColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Congratulations! You have successfully completed the comprehension check for 'Spring with Friends.' Your dedicated reading has granted companionship with knowledge!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onReset,
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .testTag("quiz_restart_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restart Quiz"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Restart Quiz",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
