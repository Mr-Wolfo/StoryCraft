package com.wolfo.storycraft.presentation.ui.features.story_editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.presentation.common.BackgroundImage
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.StatusBarManager
import com.wolfo.storycraft.presentation.ui.utils.UiUtils
import com.wolfo.storycraft.presentation.ui.features.story_editor.models.EditableChoiceDraft
import com.wolfo.storycraft.presentation.ui.features.story_editor.models.EditablePageDraft
import com.wolfo.storycraft.presentation.ui.features.story_editor.models.EditableStoryDraft
import com.wolfo.storycraft.presentation.ui.features.story_list.AppStatusBarUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StoryEditorScreen(
    storyId: String?,
    viewModel: StoryEditorViewModel = koinViewModel(),
    navPadding: PaddingValues,
    onNavigateToProfile: () -> Unit,
    onStoryPublished: (storyId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val statusBarState by viewModel.statusBarState.collectAsState()
    val editableDraft by viewModel.editableStoryDraft.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val uiStatusBarState = remember(statusBarState) {
        val current = statusBarState

        when (current) {
            is StoryEditorStatusBarState.Idle -> AppStatusBarUiState.Idle
            is StoryEditorStatusBarState.SavingDraft -> AppStatusBarUiState.Loading
            is StoryEditorStatusBarState.Publishing -> AppStatusBarUiState.Loading

            is StoryEditorStatusBarState.SaveSuccess -> {
                AppStatusBarUiState.Success(current.message)
            }

            is StoryEditorStatusBarState.SaveError -> {
                AppStatusBarUiState.Error(
                    DataError.Unknown(current.error.message ?: "Save Error")
                )
            }
            is StoryEditorStatusBarState.PublishError -> {
                println("Ошибка публикации")
                AppStatusBarUiState.Error(current.error)
            }
            is StoryEditorStatusBarState.Error -> {
                AppStatusBarUiState.Error(
                    DataError.Unknown(current.error.message ?: "General Error")
                )
            }
        }
    }

    // Обработка успеха публикации
    LaunchedEffect(uiState) {
        if (uiState is StoryEditorUiState.PublishSuccess) {
            val publishedStoryId = (uiState as StoryEditorUiState.PublishSuccess).storyId
            coroutineScope.launch {
                delay(1000)
                viewModel.retryEditor()
                onStoryPublished(publishedStoryId) // Переход на экран опубликованной истории
            }
        }
    }

//    BackgroundImage(painter = painterResource(R.drawable.details_background))

    Box(Modifier.fillMaxSize().background(Color.Transparent).windowInsetsPadding(WindowInsets.systemBars)) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Box(Modifier.padding(12.dp).height(48.dp)) {
                    // Показывать кнопки сохранения/публикации только в режиме редактирования
                    val currentEditableDraftFromUiState = (uiState as? StoryEditorUiState.Editing)?.draft
                    if (currentEditableDraftFromUiState != null) {
                        StoryEditorTopBar(
                            onSaveDraft = { viewModel.saveDraft() },
                            onPublish = { viewModel.publishStory() },
                            onBackPressed = { viewModel.retryEditor() },
                            modifier = Modifier.fillMaxHeight()
                        )
                    } else {
                        GlassCard(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center) {
                                Text(text = stringResource(R.string.story_editor_drafts_title), textAlign = TextAlign.Center, modifier = Modifier.wrapContentSize())
                            }
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column() {
                Box(modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                ) {
                    when (val state = uiState) {
                        StoryEditorUiState.CheckingAuthentication -> {
                            LoadingScreen(message = stringResource(R.string.checking_authentication))
                        }
                        StoryEditorUiState.NotAuthenticated -> {
                            NotAuthenticatedScreen(onNavigateToProfile = onNavigateToProfile)
                        }
                        StoryEditorUiState.LoadingDrafts, is StoryEditorUiState.LoadingDraft -> {
                            LoadingScreen(message = stringResource(R.string.loading_stories))
                        }
                        is StoryEditorUiState.ShowingDraftList -> {
                            DraftListScreen(
                                drafts = state.drafts, // Domain StoryBaseInfo,
                                navPadding = navPadding,
                                onDraftSelected = { viewModel.loadDraft(it.id) }, // ID -> loadDraft
                                onCreateNewDraft = { viewModel.createNewDraft() }
                            )
                        }
                        is StoryEditorUiState.Editing -> {
                            editableDraft?.let { draft ->
                                StoryEditorContent(
                                    navPadding = navPadding,
                                    draft = draft,
                                    onUpdateTitle = viewModel::updateTitle,
                                    onUpdateDescription = viewModel::updateDescription,
                                    onUpdateTags = viewModel::updateTags,
                                    onSetCoverImage = { uri ->
                                        coroutineScope.launch {
                                            val localPath = uri?.let { viewModel.copyUriToInternalStorage(it, "story_cover") }
                                            viewModel.setCoverImage(localPath)
                                        }
                                    },
                                    onAddPage = viewModel::addPage,
                                    onRemovePage = viewModel::removePage,
                                    onMovePage = viewModel::movePage, // TODO: Перемещение страниц
                                    onUpdatePageText = viewModel::updatePageText,
                                    onSetPageImage = { pageId, uri ->
                                        coroutineScope.launch {
                                            val localPath = uri?.let { viewModel.copyUriToInternalStorage(it, "story_page") }
                                            viewModel.setPageImage(pageId, localPath)
                                        }
                                    },
                                    onUpdatePageIsEnding = viewModel::updatePageIsEnding,
                                    onAddChoice = viewModel::addChoice,
                                    onRemoveChoice = viewModel::removeChoice,
                                    onUpdateChoiceText = viewModel::updateChoiceText,
                                    onUpdateChoiceTargetPage = viewModel::updateChoiceTargetPage,
                                    pageCount = draft.pages.size
                                )
                            }
                        }
                        is StoryEditorUiState.PublishSuccess -> {
                            PublishSuccessScreen(onViewStory = { onStoryPublished(state.storyId) })
                        }
                        is StoryEditorUiState.PublishError -> {
                            // Ошибка публикации показывается в статус баре. UI остается в режиме редактирования.
                            // Если editableDraft не null, UI останется в режиме редактирования.
                        }
                        is StoryEditorUiState.Error -> {
                            ErrorScreen(errorMessage = state.error.localizedMessage ?: stringResource(R.string.unknown_error))
                        }
                        else -> {  }
                    }
                }

            }
        }
        StatusBarManager(
            modifier = Modifier.padding(bottom = navPadding.calculateBottomPadding()),
            appStatusBarUiState = uiStatusBarState,
            onRetry = { }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryEditorTopBar(
    onSaveDraft: () -> Unit,
    onPublish: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            IconButton(onClick = onSaveDraft) {
                Icon(Icons.Default.Done, contentDescription = stringResource(R.string.save_draft))
            }
            Button(
                onClick = onPublish,
                modifier = Modifier.padding(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(R.string.publish))
            }
        }
    }
}

@Composable
fun NotAuthenticatedScreen(
    onNavigateToProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.story_editor_not_authenticated_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.story_editor_not_authenticated_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNavigateToProfile) {
            Text(stringResource(R.string.login_or_register))
        }
    }
}

@Composable
fun LoadingScreen(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message)
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }
}


@Composable
fun DraftListScreen(
    drafts: List<StoryBaseInfo>,
    navPadding: PaddingValues,
    onDraftSelected: (StoryBaseInfo) -> Unit,
    onCreateNewDraft: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(bottom = navPadding.calculateBottomPadding()).windowInsetsPadding(
        WindowInsets.navigationBars)) {
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            if (drafts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.story_editor_no_drafts),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onCreateNewDraft) {
                            Text(stringResource(R.string.story_editor_create_first_draft))
                        }
                    }
                }
            } else {
                items(drafts) { draft ->
                    DraftItem(draft = draft, onDraftSelected = onDraftSelected)
                }
            }
        }
        Button(
            onClick = onCreateNewDraft,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.story_editor_create_new_draft))
        }
    }
}

@Composable
fun DraftItem(
    draft: StoryBaseInfo,
    onDraftSelected: (StoryBaseInfo) -> Unit
) {
    val publishedTime = UiUtils.toLocaleDateTime(draft.publishedTime)

    val formattedDate = remember(publishedTime) {
        DateTimeFormatter
            .ofPattern("dd.MM.yy", Locale.getDefault())
            .format(publishedTime)
    }
    GlassCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDraftSelected(draft) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = draft.title, style = MaterialTheme.typography.titleMedium)
                // TODO: Форматирование времени lastSavedTimestamp
                Text(text = "Сохранено: $formattedDate", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.open_draft))
        }
    }
}


@Composable
fun StoryEditorContent(
    navPadding: PaddingValues,
    draft: EditableStoryDraft,
    onUpdateTitle: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateTags: (List<String>) -> Unit,
    onSetCoverImage: (Uri?) -> Unit,
    onAddPage: () -> Unit,
    onRemovePage: (pageId: String) -> Unit,
    onMovePage: (fromIndex: Int, toIndex: Int) -> Unit, // Для будущей реализации Drag and Drop
    onUpdatePageText: (pageId: String, text: String) -> Unit,
    onSetPageImage: (pageId: String, uri: Uri?) -> Unit,
    onUpdatePageIsEnding: (pageId: String, isEnding: Boolean) -> Unit,
    onAddChoice: (pageId: String) -> Unit,
    onRemoveChoice: (pageId: String, choiceId: String) -> Unit,
    onUpdateChoiceText: (pageId: String, choiceId: String, text: String) -> Unit,
    onUpdateChoiceTargetPage: (pageId: String, choiceId: String, targetPageIndex: Int?) -> Unit,
    pageCount: Int // Количество страниц для выбора цели
) {
    // Состояние для диалога выбора целевой страницы
    var showTargetPagePickerDialog by remember { mutableStateOf(false) }
    var currentPageForChoiceSelection by remember { mutableStateOf<EditablePageDraft?>(null) }
    var currentChoiceForPageSelection by remember { mutableStateOf<EditableChoiceDraft?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // --- Блок Обложки и Мета-информации ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.story_editor_basic_info), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            // Поле для Названия
            OutlinedTextField(
                value = draft.title,
                onValueChange = onUpdateTitle,
                label = { Text(stringResource(R.string.story_editor_title)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Поле для Описания
            OutlinedTextField(
                value = draft.description ?: "",
                onValueChange = { onUpdateDescription(it) },
                label = { Text(stringResource(R.string.story_editor_description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))

            // TODO: Реализовать более удобный UI для тегов (чипсы, автодополнение)
            var tagsText by remember(draft.tags) { mutableStateOf(draft.tags.joinToString(", ")) }
            OutlinedTextField(
                value = tagsText,
                onValueChange = {
                    tagsText = it
                    // Преобразуем строку в список тегов (удаляем пустые)
                    onUpdateTags(it.split(",").map { tag -> tag.trim() }.filter { tag -> tag.isNotBlank() })
                },
                label = { Text(stringResource(R.string.story_editor_tags)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Блок Обложки - Теперь передаем local path и callback для Uri
            ImagePickerSection(
                title = stringResource(R.string.story_editor_cover_image),
                imageLocalPath = draft.coverImageLocalPath,
                onImagePicked = onSetCoverImage,
                onRemoveImage = { onSetCoverImage(null) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // --- Блок Страниц ---
            Text(text = stringResource(R.string.story_editor_pages_title), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }

        itemsIndexed(draft.pages, key = { _, page -> page.id }) { index, page ->
            PageEditor(
                page = page,
                pageIndex = index,
                pageCount = pageCount,
                onUpdateText = { text -> onUpdatePageText(page.id, text) },
                onSetImage = { uri -> onSetPageImage(page.id, uri) },
                onUpdateIsEnding = { isEnding -> onUpdatePageIsEnding(page.id, isEnding) },
                onAddChoice = { onAddChoice(page.id) },
                onRemoveChoice = { choiceId -> onRemoveChoice(page.id, choiceId) },
                onUpdateChoiceText = { choiceId, text -> onUpdateChoiceText(page.id, choiceId, text) },
                onSelectTargetPage = { choiceId ->
                    // Открываем диалог выбора целевой страницы для этого выбора
                    currentPageForChoiceSelection = page
                    currentChoiceForPageSelection = page.choices.find { it.id == choiceId }
                    showTargetPagePickerDialog = true
                },
                onRemovePage = { onRemovePage(page.id) },
                canRemove = draft.pages.size > 1 // Нельзя удалить последнюю страницу
                // TODO: Добавить кнопки для перемещения страниц onMovePage
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = onAddPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.story_editor_add_page))
            }
            Spacer(modifier = Modifier.height(32.dp)) // Отступ снизу
        }
        item {
            Spacer(modifier = Modifier.padding(bottom = navPadding.calculateBottomPadding()).windowInsetsPadding(
                WindowInsets.navigationBars))
        }
    }

    // Диалог выбора целевой страницы
    if (showTargetPagePickerDialog) {
        TargetPagePickerDialog(
            pages = draft.pages,
            onPageSelected = { selectedPageIndex ->
                currentChoiceForPageSelection?.let { choice ->
                    currentPageForChoiceSelection?.let { page ->
                        onUpdateChoiceTargetPage(page.id, choice.id, selectedPageIndex)
                    }
                }
                showTargetPagePickerDialog = false
                currentPageForChoiceSelection = null
                currentChoiceForPageSelection = null
            },
            onDismiss = {
                showTargetPagePickerDialog = false
                currentPageForChoiceSelection = null
                currentChoiceForPageSelection = null
            },
            // Текущее выбранное значение, если есть
            currentSelectionIndex = currentChoiceForPageSelection?.targetPageIndex
        )
    }
}

@Composable
fun PageEditor(
    page: EditablePageDraft,
    pageIndex: Int,
    pageCount: Int,
    onUpdateText: (String) -> Unit,
    onSetImage: (Uri?) -> Unit,
    onUpdateIsEnding: (Boolean) -> Unit,
    onAddChoice: () -> Unit,
    onRemoveChoice: (choiceId: String) -> Unit,
    onUpdateChoiceText: (choiceId: String, text: String) -> Unit,
    onSelectTargetPage: (choiceId: String) -> Unit,
    onRemovePage: () -> Unit,
    canRemove: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.story_editor_page_number, pageIndex + 1), style = MaterialTheme.typography.titleMedium)
            if (canRemove) {
                IconButton(onClick = onRemovePage) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_page), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Поле для Текста страницы
        OutlinedTextField(
            value = page.text,
            onValueChange = onUpdateText,
            label = { Text(stringResource(R.string.story_editor_page_text)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Блок Изображения страницы - передаем local path и callback для Uri
        ImagePickerSection(
            title = stringResource(R.string.story_editor_page_image),
            imageLocalPath = page.imageLocalPath, // Локальный путь
            onImagePicked = onSetImage, // Передаем колбэк, ожидающий Uri
            onRemoveImage = { onSetImage(null) } // Просто сбрасываем
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Checkbox "Концовка"
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = page.isEndingPage, onCheckedChange = onUpdateIsEnding)
            Text(text = stringResource(R.string.story_editor_is_ending_page), style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // --- Блок Выборов ---
        if (!page.isEndingPage) {
            Text(text = stringResource(R.string.story_editor_choices_title), style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                page.choices.forEach { choice ->
                    ChoiceEditor(
                        choice = choice,
                        pageCount = pageCount,
                        onUpdateText = { text -> onUpdateChoiceText(choice.id, text) },
                        onSelectTargetPage = { onSelectTargetPage(choice.id) },
                        onRemoveChoice = { onRemoveChoice(choice.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Button(
                    onClick = onAddChoice,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !page.isEndingPage // Нельзя добавить выбор на концовку
                ) {
                    Text(stringResource(R.string.story_editor_add_choice))
                }
            }
        }
    }
}

@Composable
fun ChoiceEditor(
    choice: EditableChoiceDraft,
    pageCount: Int,
    onUpdateText: (String) -> Unit,
    onSelectTargetPage: () -> Unit,
    onRemoveChoice: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Поле для Текста выбора
        OutlinedTextField(
            value = choice.text,
            onValueChange = onUpdateText,
            label = { Text(stringResource(R.string.story_editor_choice_text)) },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Кнопка выбора целевой страницы
        Button(onClick = onSelectTargetPage) {
            // Отображаем либо выбранный индекс, либо "Не выбрано"
            Text(text = if (choice.targetPageIndex != null) "-> ${choice.targetPageIndex!! + 1}" else stringResource(R.string.story_editor_select_page))
        }
        Spacer(modifier = Modifier.width(8.dp))

        // Кнопка удаления выбора
        IconButton(onClick = onRemoveChoice) {
            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.remove_choice), tint = MaterialTheme.colorScheme.error)
        }
    }
}


@Composable
fun ImagePickerSection(
    title: String,
    imageLocalPath: String?, // Теперь принимает локальный путь (String)
    onImagePicked: (Uri?) -> Unit, // Все еще принимает Uri? от лаунчера
    onRemoveImage: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        // Здесь просто передаем Uri обратно в вышестоящий Composable/ViewModel
        // ViewModel будет заниматься копированием в локальное хранилище
        onImagePicked(uri)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(4.dp))

        if (imageLocalPath != null) { // imageLocalPath для отображения
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Фиксированная высота для изображения
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
                    // Клик на изображение открывает picker для замены
                    .clickable {
                        launcher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
            ) {
                // Coil может загружать из абсолютного пути к файлу
                val painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageLocalPath) // Передаем локальный путь
                        .crossfade(true)
                        .build()
                )
                Image(
                    painter = painter,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Кнопка удаления изображения поверх
                IconButton(
                    onClick = onRemoveImage,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.remove_image), tint = Color.White)
                }
            }
        } else {
            // Кнопка для выбора изображения
            Button(
                onClick = { launcher.launch(androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.select_image))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.select_image))
            }
        }
    }
}

@Composable
fun TargetPagePickerDialog(
    pages: List<EditablePageDraft>,
    currentSelectionIndex: Int?,
    onPageSelected: (Int?) -> Unit, // Null для сброса выбора
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.story_editor_select_target_page_title)) },
        text = {
            LazyColumn {
                // Опция для сброса выбора
                item {
                    Text(
                        text = stringResource(R.string.story_editor_select_target_page_none),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (currentSelectionIndex == null) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPageSelected(null) }
                            .padding(vertical = 8.dp)
                    )
                    HorizontalDivider()
                }
                itemsIndexed(pages) { index, page ->
                    // Показываем номер страницы и первые символы текста или "Пустая страница"
                    val pageTitle = "${index + 1}. ${page.text.take(30).ifBlank { stringResource(R.string.story_editor_empty_page) }}..."
                    Text(
                        text = pageTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (currentSelectionIndex == index) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPageSelected(index) }
                            .padding(vertical = 8.dp)
                    )
                    HorizontalDivider()
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun PublishSuccessScreen(
    onViewStory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = "Успех", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.story_editor_publish_success_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.story_editor_publish_success_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onViewStory) {
            Text(stringResource(R.string.story_editor_view_published_story))
        }
    }
}
