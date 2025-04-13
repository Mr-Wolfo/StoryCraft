package com.wolfo.storycraft.presentation.features.storyreader

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wolfo.storycraft.domain.model.Choice
import com.wolfo.storycraft.domain.model.Page
import com.wolfo.storycraft.domain.model.Story
import com.wolfo.storycraft.domain.model.StoryBase
import com.wolfo.storycraft.presentation.features.storylist.Empty
import com.wolfo.storycraft.presentation.features.storylist.Error
import com.wolfo.storycraft.presentation.features.storylist.Loading
import com.wolfo.storycraft.presentation.features.storylist.StoryList
import org.koin.androidx.compose.koinViewModel

@Composable
fun StoryReaderScreen(
    storyId: Long,
    viewModel: StoryReaderViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    uiState.story?.let {

        val currentPageId = remember { mutableIntStateOf(0) }

        Column(modifier = Modifier.fillMaxSize(),
            Arrangement.SpaceEvenly) {
            Text(text = uiState.story?.pages?.get(currentPageId.intValue)?.pageText ?: "Empty")
            uiState.story!!.pages[currentPageId.intValue].choices.forEach { choice ->
                Button(onClick = {
                    currentPageId.intValue = choice.targetPageId.toInt()-1
                }) { Text(choice.choiceText) }
            }
        }
    }


}

@Composable
fun StoryDetailsScreen(
    storyId: Long?,
    viewModel: StoryReaderViewModel = koinViewModel(),
    onReadStory: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> Loading()
        uiState.error != null -> Error(uiState.error!!)
        uiState.story != null -> StoryDetails(uiState.story!!) { onReadStory(it) }
        else -> { viewModel.attemptLoadStory()
            Text(text = "Прочитайте чужую историю или создайте свою!")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoryDetails(
    story: Story,
    onReadStory: (Long) -> Unit
) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    // Анимация появления
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut()
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { onReadStory(story.id) },
                    icon = { Icon(Icons.Filled.Check, "Читать") },
                    text = { Text("Начать чтение") },
                    modifier = Modifier.padding(bottom = 16.dp),
                    containerColor = colorScheme.primaryContainer,
                    contentColor = colorScheme.onPrimaryContainer
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.primary.copy(alpha = 0.05f),
                                colorScheme.secondary.copy(alpha = 0.05f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    /*// Обложка истории
                    AsyncImage(
                        model = story.coverImageUrl ?: R.drawable.default_story_cover,
                        contentDescription = "Обложка истории",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                    )*/

                    // Заголовок и автор
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        )

                        Text(
                            text = "Автор: ${story.authorId}", // В реальном приложении подставьте имя автора
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    // Теги
                    if (!story.tags.isNullOrEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            story.tags!!.forEach { tag ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(tag) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = colorScheme.surfaceVariant
                                    ),
                                    border = _root_ide_package_.androidx.compose.foundation.BorderStroke(
                                        color = colorScheme.outline,
                                        width = 1.dp
                                    )
                                )
                            }
                        }
                    }

                    // Описание
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Описание",
                                style = MaterialTheme.typography.titleMedium,
                                color = colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = story.description ?: "Автор не добавил описание",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Статистика
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoChip(
                            icon = Icons.Filled.List,
                            text = "${story.pages.size} стр."
                        )

                        InfoChip(
                            icon = Icons.Filled.Face,
                            text = "1.2K" // Замените на реальные данные
                        )

                        InfoChip(
                            icon = Icons.Filled.Star,
                            text = "4.8" // story.averageRating?.toString() ?: "-"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
    StoryDetails(testStory) { }
}