package com.wolfo.storycraft.presentation.features.story_view.details

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.domain.model.Choice
import com.wolfo.storycraft.domain.model.Page
import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.presentation.features.story_list.Error
import com.wolfo.storycraft.presentation.features.story_list.Loading
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoryDetailsScreen(
    storyId: Long?,
    viewModel: StoryDetailsViewModel = koinViewModel(),
    onReadStory: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val loadFullUiState by viewModel.loadFullUiState.collectAsState()

    when {
        uiState.isLoading -> Loading()
        uiState.error != null -> Error(uiState.error!!)
        uiState.story != null -> StoryDetails(story = uiState.story!!, onReadStory =  {
            viewModel.loadStoryFullById().run {
                if(loadFullUiState.success)
                    onReadStory(it)
            }
        })
        else -> { viewModel.attemptLoadStory()
            Text(text = "Прочитайте чужую историю или создайте свою!")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoryDetails(
    story: StoryBase,
    onReadStory: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Параллакс эффект для фона
    val parallaxFactor = 0.3f
    val offset = (scrollState.value * parallaxFactor).coerceAtMost(0f)

    // Анимация появления
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            // Иммерсивный фон с параллакс эффектом
            AsyncImage(
                model = "https://mir-s3-cdn-cf.behance.net/project_modules/disp/7ee53b58387179.59fa3579a45c6.jpg",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = offset
                        alpha = 0.9f
                    },
                colorFilter = ColorFilter.tint(
                    color = colorScheme.surface.copy(alpha = 0.2f),
                    blendMode = BlendMode.Overlay
                )
            )

            // Затемнение с градиентом
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            0f to colorScheme.surface.copy(alpha = 0.7f),
                            0.3f to Color.Transparent,
                            0.7f to Color.Transparent,
                            1f to colorScheme.surface.copy(alpha = 0.8f),
                            startY = 0f,
                            endY = screenHeight.value
                        )
                    )
            )

            // Основной контент
            Scaffold(
                containerColor = Color.Transparent,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { onReadStory(story.id) },
                        modifier = Modifier
                            .padding(16.dp)
                            .shadow(16.dp, shape = CircleShape),
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Читать",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            ) { padding ->
                CustomScrollableColumn(
                    scrollState = scrollState,
                    contentPadding = padding,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(screenHeight * 0.4f))

                    // Заголовок с декоративным элементом
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(
                                    color = colorScheme.primary,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .align(Alignment.Start)
                        )

                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Автор с аватаркой
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = colorScheme.primary.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = colorScheme.primary.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Автор",
                                    tint = colorScheme.primary
                                )
                            }

                            Text(
                                text = "Неизвестный автор",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    // Теги с анимацией
                    AnimatedVisibility(
                        visible = true,// !story.tags.isNullOrEmpty(),
                        enter = fadeIn() + expandVertically(),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val testTags = listOf("Детектив", "Мистика", "Триллер")
                            testTags.forEach { tag ->
                                Log.d("TAGS", "Visible")
                                ElevatedFilterChip(
                                    selected = false,
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    colors = FilterChipDefaults.elevatedFilterChipColors(
                                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                        labelColor = colorScheme.onSurfaceVariant
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = colorScheme.outline.copy(alpha = 0.3f),
                                        enabled = true,
                                        selected = false
                                    ),
                                    elevation = FilterChipDefaults.elevatedFilterChipElevation(
                                        elevation = 4.dp)
                                )
                            }
                        }
                    }

                    // Описание с кастомной карточкой
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "О ПУТЕШЕСТВИИ",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = colorScheme.primary,
                                    letterSpacing = 1.2.sp
                                ),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Text(
                                text = story.description ?: "Эта история ждет своего рассказа...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = colorScheme.onSurfaceVariant,
                                    lineHeight = 24.sp
                                )
                            )
                        }
                    }

                    // Статистика с иконками
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            PremiumInfoChip(
                                icon = Icons.Filled.Menu,
                                text = "${12} стр.",
                                subText = "Объем"
                            )

                            PremiumInfoChip(
                                icon = Icons.Filled.Face,
                                text = formatNumber(1300),
                                subText = "Просмотры"
                            )

                            PremiumInfoChip(
                                icon = Icons.Filled.Star,
                                text = "4.8",
                                subText = "Рейтинг"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// Кастомная стеклянная карточка
@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(
            1.dp,
            colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            colorScheme.surface.copy(alpha = 0.5f),
                            colorScheme.surface.copy(alpha = 0.9f)
                        )
                    )
                )
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to Color.Transparent,
                                0.3f to colorScheme.primary.copy(alpha = 0.05f),
                                1f to Color.Transparent
                            ),
                            blendMode = BlendMode.Overlay
                        )
                    }
                }
        ) {
            content()
        }
    }
}

// Премиум чип с дополнительным текстом
@Composable
private fun PremiumInfoChip(icon: ImageVector, text: String, subText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = subText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Кастомная колонка с прокруткой
@Composable
private fun CustomScrollableColumn(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(contentPadding),
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}

private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${number / 1_000_000}M"
        number >= 1_000 -> "${number / 1_000}K"
        else -> number.toString()
    }
}

@Preview(showBackground = true)
@Composable
fun StoryFullPreview() {
    val testStory = Story(
        id = 1L,
        authorId = 42L,
        startPageId = 101L,
        title = "Тайна старого особняка",
        description = "Вы получаете письмо от давно пропавшего дяди с просьбой посетить его старый особняк. То, что вы там найдёте, изменит вашу жизнь навсегда...",
        tags = listOf("Мистика", "Детектив", "Ужасы", "Интерактив"),
        coverImageUrl = "https://img-s1.onedio.com/id-56b45c2e91735e2e1213540f/rev-0/w-1200/h-1807/f-jpg/s-fef7b295b93f76dd12bd41994b903b15c014e0c4.jpg",
        isPublished = true,
        pages = listOf(
            // Стартовая страница
            Page(
                id = 101L,
                storyId = 1L,
                pageText = """
                Вы стоите у ворот старинного особняка Винтерхолл. 
                Вчера вы получили странное письмо от дяди Альберта, которого не видели 10 лет: 
                "Племянник, срочно приезжай в особняк. То, что я обнаружил, не должно быть утрачено. 
                Если я не открою дверь - используй ключ под горшком с геранью."
                
                Ветер шевелит опавшие листья. Ворота скрипят. 
                Что вы делаете?
            """.trimIndent(),
                isEndingPage = false,
                choices = listOf(
                    Choice(1L, 101L, "Войти через главные ворота", 102L),
                    Choice(2L, 101L, "Обойти дом вокруг", 103L),
                    Choice(3L, 101L, "Проверить горшок с геранью", 104L)
                )
            ),

            // Развилка 1
            Page(
                id = 102L,
                storyId = 1L,
                pageText = """
                Вы толкаете массивные дубовые двери, и они с скрипом открываются. 
                В холле царит полумрак. Пахнет пылью и чем-то ещё... медью? 
                На стене висит портрет дяди с трещиной через лицо. 
                Впереди три двери: в библиотеку, в столовую и наверх по лестнице.
            """.trimIndent(),
                isEndingPage = false,
                choices = listOf(
                    Choice(4L, 102L, "Идти в библиотеку", 105L),
                    Choice(5L, 102L, "Идти в столовую", 106L),
                    Choice(6L, 102L, "Подняться по лестнице", 107L)
                )
            ),

            // Развилка 2
            Page(
                id = 103L,
                storyId = 1L,
                pageText = """
                Вы обходите особняк сбоку. Заросший сад, разбитые окна подвала... 
                Внезапно вы замечаете свежие следы, ведущие к деревянной двери в фундаменте. 
                Рядом валяется ржавый гаечный ключ.
            """.trimIndent(),
                isEndingPage = false,
                choices = listOf(
                    Choice(7L, 103L, "Попытаться открыть дверь", 108L),
                    Choice(8L, 103L, "Вернуться к парадному входу", 101L),
                    Choice(9L, 103L, "Взять ключ и осмотреться", 109L)
                )
            ),

            // Развилка 3
            Page(
                id = 104L,
                storyId = 1L,
                pageText = """
                Под горшком оказывается небольшой железный ключ и записка:
                "Только для крайнего случая. Кабинет, нижний ящик."
                В этот момент вы слышите шум из-за угла - кто-то идёт!
            """.trimIndent(),
                isEndingPage = false,
                choices = listOf(
                    Choice(10L, 104L, "Спрятаться за кустами", 110L),
                    Choice(11L, 104L, "Быстро войти в дом", 102L),
                    Choice(12L, 104L, "Остаться на месте", 201L) // Плохая концовка
                )
            ),

            // Библиотека
            Page(
                id = 105L,
                storyId = 1L,
                pageText = """
                Библиотека завалена книгами и бумагами. На столе - открытый том 
                "Тайные культы Европы" с закладкой на главе про местный регион. 
                На полке замечаете фотоальбом с вырванными страницами.
            """.trimIndent(),
                isEndingPage = false,
                choices = listOf(
                    Choice(13L, 105L, "Изучить книгу", 202L),
                    Choice(14L, 105L, "Проверить фотоальбом", 203L),
                    Choice(15L, 105L, "Вернуться в холл", 102L)
                )
            ),

            // Столовая (ловушка)
            Page(
                id = 106L,
                storyId = 1L,
                pageText = """
                Столовая выглядит странно - накрыт свежий ужин на двоих. 
                В бокалах красное вино. Вы подходите ближе и замечаете, 
                что одно из блюд покрыто плесенью, а в другом... шевелятся черви.
            """.trimIndent(),
                isEndingPage = false,
                choices = listOf(
                    Choice(16L, 106L, "Быстро выйти", 102L),
                    Choice(17L, 106L, "Осмотреть кухню", 204L), // Плохая концовка
                    Choice(18L, 106L, "Попробовать вино", 205L) // Смерть
                )
            ),

            // Лестница наверх
            Page(
                id = 107L,
                storyId = 1L,
                pageText = """
                Лестница скрипит под вашим весом. Наверху длинный коридор 
                с пятью дверями. Последняя дверь приоткрыта, оттуда доносится шёпот. 
                Вдруг шёпот прекращается...
            """.trimIndent(),
                isEndingPage = false,
                choices = listOf(
                    Choice(19L, 107L, "Подойти к приоткрытой двери", 206L),
                    Choice(20L, 107L, "Проверить первую дверь", 207L),
                    Choice(21L, 107L, "Вернуться вниз", 102L)
                )
            ),

            // Концовка 1 (плохая)
            Page(
                id = 201L,
                storyId = 1L,
                pageText = """
                К вам подходит незнакомец в плаще. "Альберт слишком много знал", - говорит он 
                перед тем как всё погружается во тьму. Вы больше не увидите солнечного света...
                
                КОНЕЦ.
            """.trimIndent(),
                isEndingPage = true,
                choices = emptyList()
            ),

            // Концовка 2 (нейтральная)
            Page(
                id = 202L,
                storyId = 1L,
                pageText = """
                В книге вы находите координаты и план. Похоже, дядя исследовал 
                древний культ и спрятал доказательства. Вы забираете книгу и 
                уходите, чтобы передать её властям.
                
                КОНЕЦ (продолжение следует...)
            """.trimIndent(),
                isEndingPage = true,
                choices = emptyList()
            ),

            // Концовка 3 (хорошая)
            Page(
                id = 206L,
                storyId = 1L,
                pageText = """
                В комнате вы находите дядю Альберта! Он жив, но связан. 
                Освободив его, вы вместе выбираетесь из особняка. 
                Оказывается, он раскрыл мафиозную схему мэра города.
                
                Спустя месяц мафия разгромлена, а вы получаете награду. 
                Особняк теперь ваш!
                
                ХЭППИ ЭНД!
            """.trimIndent(),
                isEndingPage = true,
                choices = emptyList()
            )
        )
    )
// StoryDetails(testStory, { })
}