package com.wolfo.storycraft.presentation.ui.features.story_editor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.PublishChoice
import com.wolfo.storycraft.domain.model.PublishContent
import com.wolfo.storycraft.domain.model.PublishPage
import com.wolfo.storycraft.domain.model.draft.DraftChoice
import com.wolfo.storycraft.domain.model.draft.DraftContent
import com.wolfo.storycraft.domain.model.draft.DraftPage
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.usecase.story.DeleteStoryDraftUseCase
import com.wolfo.storycraft.domain.usecase.story.GetDraftStoriesUseCase
import com.wolfo.storycraft.domain.usecase.story.GetStoryDraftUseCase
import com.wolfo.storycraft.domain.usecase.story.PublishStoryUseCase
import com.wolfo.storycraft.domain.usecase.story.SaveStoryDraftUseCase
import com.wolfo.storycraft.domain.usecase.user.GetCurrentUserIdUseCase
import com.wolfo.storycraft.presentation.ui.features.story_editor.models.EditableChoiceDraft
import com.wolfo.storycraft.presentation.ui.features.story_editor.models.EditablePageDraft
import com.wolfo.storycraft.presentation.ui.features.story_editor.models.EditableStoryDraft
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Duration.Companion.seconds

// Параметры навигации
const val STORY_ID_ARG = "storyId"

// Состояния UI и Status Bar - остаются как есть.
sealed class StoryEditorUiState {
    data object CheckingAuthentication : StoryEditorUiState()
    data object NotAuthenticated : StoryEditorUiState() // Пользователь не авторизован
    data object LoadingDrafts : StoryEditorUiState() // Загрузка списка черновиков
    data class ShowingDraftList(val drafts: List<StoryBaseInfo>) : StoryEditorUiState() // Отображение списка черновиков (Domain model)
    data object CreatingNewDraft : StoryEditorUiState() // Создание нового пустого черновика
    data class LoadingDraft(val draftId: String) : StoryEditorUiState() // Загрузка конкретного черновика
    data class Editing(val draft: EditableStoryDraft) : StoryEditorUiState() // Редактирование черновика (UI model)
    data class PublishSuccess(val storyId: String) : StoryEditorUiState() // История успешно опубликована (Domain model ID)
    data class PublishError(val error: DataError) : StoryEditorUiState() // Ошибка при публикации (Domain model)
    data class Error(val error: DataError) : StoryEditorUiState() // Общая ошибка (Domain model)
}

sealed class StoryEditorStatusBarState {
    data object Idle : StoryEditorStatusBarState()
    data object SavingDraft : StoryEditorStatusBarState()
    data object Publishing : StoryEditorStatusBarState()
    data class SaveSuccess(val message: String) : StoryEditorStatusBarState()
    data class SaveError(val error: DataError) : StoryEditorStatusBarState()
    data class PublishError(val error: DataError) : StoryEditorStatusBarState() // Дублируется для ясности статуса
    data class Error(val error: DataError) : StoryEditorStatusBarState() // Общая ошибка в статусе
}

class StoryEditorViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase, // Use Case: Domain Layer -> String?
    // Use Cases для черновиков: Domain Layer (Repository) -> Domain/UI Models
    private val getDraftStoriesUseCase: GetDraftStoriesUseCase, // Flow<ResultM<List<StoryBaseInfo>>>
    private val getStoryDraftUseCase: GetStoryDraftUseCase, // ResultM<DraftContent>
    private val saveStoryDraftUseCase: SaveStoryDraftUseCase, // Accepts DraftContent -> ResultM<Unit>
    private val deleteStoryDraftUseCase: DeleteStoryDraftUseCase, // Accepts String -> ResultM<Unit>
    // Use Case для публикации: Domain Layer (Repository) -> Domain Models
    private val publishStoryUseCase: PublishStoryUseCase, // Accepts PublishContent -> ResultM<StoryFull>
    // Инжектируем Context для Android-специфичных операций (Uri -> File)
    private val appContext: Context
) : ViewModel() {

    // Состояния UI и Status Bar - остаются как есть
    private val _uiState = MutableStateFlow<StoryEditorUiState>(StoryEditorUiState.CheckingAuthentication)
    val uiState = _uiState.asStateFlow()

    private val _statusBarState = MutableStateFlow<StoryEditorStatusBarState>(StoryEditorStatusBarState.Idle)
    val statusBarState = _statusBarState.asStateFlow()

    // UI модель для редактирования - остается как есть
    private val _editableStoryDraft = MutableStateFlow<EditableStoryDraft?>(null)
    val editableStoryDraft: StateFlow<EditableStoryDraft?> = _editableStoryDraft.asStateFlow()

    // Уникальный ID пользователя, если авторизован
    private var currentUserId: String? = null

    // Job для автосохранения - остается как есть
    private var autoSaveJob: Job? = null

    // Флоу для отслеживания изменений в черновике - остается как есть
    private val draftChangesFlow = MutableSharedFlow<Unit>()

    // Список временных файлов, созданных для публикации, требующих очистки
    private val tempFilesToCleanup: MutableList<File> = mutableListOf()


    init {
        checkAuthentication()
        setupAutoSave()
    }

    fun retryEditor() {
        checkAuthentication()
    }

    // checkAuthentication - остается как есть, вызывает Use Case для получения userId
    private fun checkAuthentication() {
        viewModelScope.launch {
            _uiState.value = StoryEditorUiState.CheckingAuthentication
            currentUserId = getCurrentUserIdUseCase() // Use Case возвращает String?
            if (currentUserId == null) {
                _uiState.value = StoryEditorUiState.NotAuthenticated
            } else {
                val storyId = savedStateHandle.get<String>(STORY_ID_ARG)
                if (storyId != null) {
                    loadDraft(storyId)
                } else {
                    loadDraftsList(currentUserId!!)
                }
            }
        }
    }

    // --- Автосохранение ---
    private fun setupAutoSave() {
        autoSaveJob = viewModelScope.launch {
            // Слушаем изменения с debounce, чтобы не сохранять на каждый чих
            draftChangesFlow
                .debounce(5.seconds) // Задержка перед сохранением после последнего изменения
                .collect {
                    _editableStoryDraft.value?.let { draft ->
                        // Проверяем, что черновик не пустой (хотя бы заголовок)
                        if (draft.title.isNotBlank() || draft.pages.any { it.text.isNotBlank() || it.imageLocalPath != null || it.choices.isNotEmpty() }) {
                            saveDraft(draft)
                        }
                    }
                }
        }
    }

    // loadDraftsList - остается как есть, работает с Domain StoryBaseInfo из Use Case
    private fun loadDraftsList(userId: String) {
        viewModelScope.launch {
            _uiState.value = StoryEditorUiState.LoadingDrafts
            // Use Case возвращает Flow<ResultM<List<StoryBaseInfo>>> (Domain model)
            getDraftStoriesUseCase().collect { result ->
                when (result) {
                    is ResultM.Success -> {
                        _uiState.value = StoryEditorUiState.ShowingDraftList(result.data) // Domain StoryBaseInfo -> UI State
                        _statusBarState.value = StoryEditorStatusBarState.Idle // Скрываем статус загрузки
                    }
                    is ResultM.Failure -> {
                        // Если есть кэшированные данные, показываем их
                        if (result.cachedData is List<*>) {
                            // Убедимся, что cachedData - это List<StoryBaseInfo>
                            _uiState.value = StoryEditorUiState.ShowingDraftList(result.cachedData as List<StoryBaseInfo>)
                        } else {
                            _uiState.value = StoryEditorUiState.Error(result.error)
                        }
                        _statusBarState.value = StoryEditorStatusBarState.Error(result.error) // Показываем ошибку в статус баре
                    }
                    ResultM.Loading -> {
                        _statusBarState.value = StoryEditorStatusBarState.Publishing // Loading state
                    }
                }
            }
        }
    }

    // loadDraft - ОБНОВЛЕНО: Use Case возвращает DraftContent, VM маппит в EditableStoryDraft
    fun loadDraft(draftId: String) {
        viewModelScope.launch {
            _uiState.value = StoryEditorUiState.LoadingDraft(draftId)
            _statusBarState.value = StoryEditorStatusBarState.Publishing // Loading state
            // Use Case возвращает ResultM<DraftContent> (Domain model)
            when (val result = getStoryDraftUseCase(draftId)) {
                is ResultM.Success -> {
                    // Маппим Domain DraftContent -> UI EditableStoryDraft с помощью хелпера VM
                    val editableDraft = result.data.toEditableDraft()
                    _editableStoryDraft.value = editableDraft
                    _uiState.value = StoryEditorUiState.Editing(editableDraft)
                    _statusBarState.value = StoryEditorStatusBarState.Idle
                }
                is ResultM.Failure -> {
                    _uiState.value = StoryEditorUiState.Error(result.error)
                    _statusBarState.value = StoryEditorStatusBarState.Error(result.error)
                }
                ResultM.Loading -> {} // Not expected from suspend function
            }
        }
    }

    // createNewDraft - остается как есть
    fun createNewDraft() {
        if (currentUserId == null) {
            _uiState.value = StoryEditorUiState.NotAuthenticated
            return
        }
        viewModelScope.launch {
            _uiState.value = StoryEditorUiState.CreatingNewDraft
            val newDraft = EditableStoryDraft() // Создаем пустой черновик (UI model)
            _editableStoryDraft.value = newDraft
            _uiState.value = StoryEditorUiState.Editing(newDraft)
            // Сразу пытаемся сохранить
            saveDraft(newDraft) // userId будет получен в Use Case
        }
    }


    // Сигнал о том, что данные черновика изменились
    private fun notifyDraftChanged() {
        viewModelScope.launch {
            draftChangesFlow.emit(Unit)
        }
    }

    // saveDraft - ОБНОВЛЕНО: VM маппит EditableStoryDraft в DraftContent, вызывает Use Case с DraftContent
    fun saveDraft(draftToSave: EditableStoryDraft? = _editableStoryDraft.value) {
        // Use Case SaveStoryDraftUseCase теперь сам получает userId
        // Проверка currentUserId==null уже есть в Use Case, но можно оставить и здесь для раннего UI-отклика
        if (draftToSave == null) {
            // Ничего сохранять
            return
        }

        viewModelScope.launch {
            _statusBarState.value = StoryEditorStatusBarState.SavingDraft

            // Маппим UI EditableStoryDraft -> Domain DraftContent с помощью хелпера VM
            val draftContent = draftToSave.toDraftContent(currentUserId!!) // userId уже есть в VM

            // Вызываем Use Case с Domain моделью DraftContent
            when (val result = saveStoryDraftUseCase(draftContent)) { // Передаем Domain модель
                is ResultM.Success -> {
                    _statusBarState.value = StoryEditorStatusBarState.SaveSuccess(appContext.getString(R.string.draft_saved_success))
                }
                is ResultM.Failure -> {
                    _statusBarState.value = StoryEditorStatusBarState.SaveError(result.error)
                }
                ResultM.Loading -> {} // Не ожидается
            }
            // Скрываем сообщение через некоторое время
            delay(3.seconds)
            if (_statusBarState.value is StoryEditorStatusBarState.SaveSuccess || _statusBarState.value is StoryEditorStatusBarState.SaveError) {
                _statusBarState.value = StoryEditorStatusBarState.Idle
            }
        }
    }

    // --- Публикация ---
    // publishStory - ОБНОВЛЕНО: VM выполняет Uri -> File, маппит в PublishContent, вызывает Use Case с PublishContent
    fun publishStory() {
        val draft = _editableStoryDraft.value
        // Use Case PublishStoryUseCase не требует userId, если он не нужен для валидации на сервере
        // Проверка currentUserId == null уже есть в Use Case GetCurrentUserIdUseCase, вызываемом в репозитории/usecase.
        // Можно добавить раннюю проверку здесь, если неавторизованный пользователь вообще не должен видеть кнопку "Опубликовать".

        if (draft == null) {
            // Нет черновика для публикации
            _statusBarState.value = StoryEditorStatusBarState.Error(DataError.Validation.GENERAL(appContext.getString(R.string.error_no_draft_to_publish)))
            return
        }

        // 1. Клиентская валидация UI модели перед публикацией
        val validationErrors = draft.validateForPublish() // Метод валидации в EditableStoryDraft (UI)
        if (validationErrors.isNotEmpty()) {
            _statusBarState.value = StoryEditorStatusBarState.PublishError(DataError.Validation.UI(validationErrors.joinToString("\n")))
            return
        }

        viewModelScope.launch {
            _statusBarState.value = StoryEditorStatusBarState.Publishing

            // 2. Конвертация Uri -> File для изображений в ViewModel (требует Context)
            // Это место, где мы работаем с Android-специфичными Uri и создаем File.
            val publishContent = draft.toPublishContent(appContext)



            // Добавляем созданные временные файлы в список для последующей очистки
            publishContent.coverImageFile?.let { tempFilesToCleanup.add(it) }
            tempFilesToCleanup.addAll(publishContent.pages.mapNotNull { it.imageFile })


            // 3. Вызываем Use Case для публикации
            // Use Case PublishStoryUseCase принимает Domain модель PublishContent
            when (val result = publishStoryUseCase(publishContent)) { // Передаем Domain модель
                is ResultM.Success -> {
                    _statusBarState.value = StoryEditorStatusBarState.Idle // Скрываем статус
                    _uiState.value = StoryEditorUiState.PublishSuccess(result.data.id) // Переходим в состояние успеха публикации (Domain StoryFull ID)

                    // Успех публикации -> Use Case уже удалил черновик в БД.
                    // Очищаем временные файлы, созданные в VM.
                    onCleared()
                }
                is ResultM.Failure -> {
                    _statusBarState.value = StoryEditorStatusBarState.PublishError(result.error)
                    // Ошибка публикации -> Черновик не удален.
                    // Очищаем временные файлы, созданные в VM.
                    cleanupTempFiles() // Очищаем только после попытки публикации
                }
                ResultM.Loading -> {}
            }
        }
    }

    // --- Методы для редактирования черновика ---
    // Эти методы меняют состояние _editableStoryDraft и вызывают notifyDraftChanged()


    fun updateTitle(title: String) {
        _editableStoryDraft.update { draft ->
            draft?.copy(title = title) // Создаем новую копию Draft
        }
        notifyDraftChanged()
    }

    fun updateDescription(description: String) {
        _editableStoryDraft.update { draft ->
            draft?.copy(description = description) // Создаем новую копию Draft
        }
        notifyDraftChanged()
    }

    fun updateTags(tags: List<String>) {
        _editableStoryDraft.update { draft ->
            draft?.copy(tags = tags) // Создаем новую копию Draft
        }
        notifyDraftChanged()
    }

    fun setCoverImage(imageLocalPath: String?) { // Принимает String?
        _editableStoryDraft.update { draft ->
            draft?.copy(coverImageLocalPath = imageLocalPath) // Обновляем локальный путь
        }
        notifyDraftChanged()
    }

    fun addPage() {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                // Создаем новый список, добавляем новую страницу, создаем новую копию Draft
                currentDraft.copy(pages = currentDraft.pages.toMutableList().apply { add(
                    EditablePageDraft()
                ) })
            }
        }
        notifyDraftChanged()
    }

    fun removePage(pageId: String) {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                // Проверяем, что удаляем не последнюю страницу
                if (currentDraft.pages.size > 1) {
                    val removedPageIndex = currentDraft.pages.indexOfFirst { it.id == pageId }
                    if (removedPageIndex == -1) {
                        // Страница не найдена, возвращаем текущий черновик без изменений
                        return@update currentDraft
                    }

                    // Создаем новый список страниц без удаленной
                    val updatedPagesList = currentDraft.pages.filter { it.id != pageId }.toMutableList()

                    // Теперь проходимся по НОВОМУ списку страниц и обновляем таргеты выборов,
                    // создавая новые копии Choice и Page, если нужно.
                    val finalPages = updatedPagesList.map { page ->
                        val updatedChoices = page.choices.map { choice ->
                            val targetIndex = choice.targetPageIndex
                            if (targetIndex != null) {
                                // Находим старый DraftItemId целевой страницы (используем старый список до удаления)
                                val targetPageId = currentDraft.pages.getOrNull(targetIndex)?.id
                                if (targetPageId != null) {
                                    // Находим новый индекс этой целевой страницы в обновленном списке
                                    val newTargetIndex = updatedPagesList.indexOfFirst { it.id == targetPageId }
                                    if (newTargetIndex != targetIndex) {
                                        // Если индекс изменился, создаем новую копию Choice
                                        choice.copy(targetPageIndex = if (newTargetIndex != -1) newTargetIndex else null) // null если страница почему-то не нашлась в новом списке
                                    } else {
                                        choice // Индекс не изменился, оставляем старый выбор
                                    }
                                } else {
                                    // Если целевая страница по старому индексу не нашлась в исходном списке (что странно), сбрасываем таргет
                                    choice.copy(targetPageIndex = null)
                                }
                            } else {
                                choice // Таргет был null, не меняем
                            }
                        }.toMutableList() // Создаем новый список Choices для этой страницы

                        // Создаем новую копию Page только если choices изменились
                        if (updatedChoices !== page.choices) {
                            page.copy(choices = updatedChoices)
                        } else {
                            page // Choices не менялись, оставляем старую страницу
                        }

                    }.toMutableList() // Создаем финальный новый список Pages

                    // Создаем новую копию Draft с финальным списком Pages, только если список страниц или их содержимое изменилось
                    if (finalPages !== currentDraft.pages) {
                        currentDraft.copy(pages = finalPages)
                    } else {
                        currentDraft // Ничего не изменилось
                    }


                } else {
                    // Нельзя удалить последнюю страницу - показываем ошибку
                    _statusBarState.value = StoryEditorStatusBarState.Error(DataError.Validation.GENERAL("Нельзя удалить последнюю страницу"))
                    viewModelScope.launch {
                        delay(3.seconds)
                        if (_statusBarState.value is StoryEditorStatusBarState.Error) {
                            _statusBarState.value = StoryEditorStatusBarState.Idle
                        }
                    }
                    currentDraft // Возвращаем текущий черновик без изменений
                }
            }
        }
        notifyDraftChanged()
    }

    fun movePage(fromIndex: Int, toIndex: Int) {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                if (fromIndex !in 0 until currentDraft.pages.size || toIndex !in 0 until currentDraft.pages.size || fromIndex == toIndex) {
                    return@update currentDraft // Некорректные индексы или нет перемещения
                }

                // Создаем новый список страниц с перемещенным элементом
                val movedPagesList = currentDraft.pages.toMutableList().apply {
                    val pageToMove = removeAt(fromIndex)
                    add(toIndex, pageToMove)
                }

                // Обновляем таргеты выборов в НОВОМ списке страниц, создавая новые копии объектов
                val finalPages = movedPagesList.map { page ->
                    val updatedChoices = page.choices.map { choice ->
                        val targetIndex = choice.targetPageIndex
                        if (targetIndex != null) {
                            // Находим ID целевой страницы по ее СТАРОМУ индексу в ИСХОДНОМ списке страниц
                            val targetPageId = currentDraft.pages.getOrNull(targetIndex)?.id
                            if (targetPageId != null) {
                                // Находим НОВЫЙ индекс этой целевой страницы в ПЕРЕМЕЩЕННОМ списке страниц
                                val newTargetIndex = movedPagesList.indexOfFirst { it.id == targetPageId }
                                if (newTargetIndex != targetIndex) {
                                    // Если индекс изменился, создаем новую копию Choice
                                    choice.copy(targetPageIndex = if (newTargetIndex != -1) newTargetIndex else null) // null если страница почему-то не нашлась
                                } else {
                                    choice // Индекс не изменился, оставляем старый выбор
                                }
                            } else {
                                // Если целевая страница не нашлась по старому индексу, сбрасываем таргет
                                choice.copy(targetPageIndex = null)
                            }
                        } else {
                            choice // Таргет был null, не меняем
                        }
                    }.toMutableList() // Новый список Choices для этой страницы

                    // Создаем новую копию Page только если choices изменились
                    if (updatedChoices !== page.choices) {
                        page.copy(choices = updatedChoices)
                    } else {
                        page // Choices не менялись, оставляем старую страницу
                    }

                }.toMutableList() // Создаем финальный новый список Pages


                // Создаем новую копию Draft с финальным списком Pages (ссылка на список точно поменялась из-за перемещения)
                currentDraft.copy(pages = finalPages)

            }
        }
        // TODO: Добавить UI для перемещения страниц и вызывать этот метод
        notifyDraftChanged()
    }

    fun updatePageText(pageId: String, text: String) {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                // Создаем новый список страниц, обновляя нужную страницу (создавая ее копию)
                val updatedPages = currentDraft.pages.map { page ->
                    if (page.id == pageId) {
                        page.copy(text = text) // Создаем новую копию Page с новым текстом
                    } else {
                        page // Оставляем остальные страницы как есть
                    }
                }.toMutableList() // Создаем новый список MutableList

                // Создаем новую копию Draft с новым списком страниц
                currentDraft.copy(pages = updatedPages)
            }
        }
        notifyDraftChanged()
    }

    fun setPageImage(pageId: String, imageLocalPath: String?) { // Принимает String?
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                val updatedPages = currentDraft.pages.map { page ->
                    if (page.id == pageId) {
                        page.copy(imageLocalPath = imageLocalPath) // Обновляем локальный путь
                    } else {
                        page
                    }
                }.toMutableList()
                currentDraft.copy(pages = updatedPages)
            }
        }
        notifyDraftChanged()
    }

    fun updatePageIsEnding(pageId: String, isEnding: Boolean) {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                val updatedPages = currentDraft.pages.map { page ->
                    if (page.id == pageId) {
                        // Если страница становится концовкой, сбрасываем все выборы
                        val updatedChoices = if (isEnding) mutableListOf() else page.choices
                        page.copy(isEndingPage = isEnding, choices = updatedChoices) // Создаем новую копию Page
                    } else {
                        page
                    }
                }.toMutableList()
                currentDraft.copy(pages = updatedPages)
            }
        }
        notifyDraftChanged()
    }

    fun addChoice(pageId: String) {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                val updatedPages = currentDraft.pages.map { page ->
                    if (page.id == pageId) {
                        // Создаем новую копию списка choices и добавляем новый выбор, создавая новую копию Page
                        page.copy(choices = page.choices.toMutableList().apply { add(
                            EditableChoiceDraft()
                        ) })
                    } else {
                        page
                    }
                }.toMutableList()
                currentDraft.copy(pages = updatedPages)
            }
        }
        notifyDraftChanged()
    }

    fun removeChoice(pageId: String, choiceId: String) {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                val updatedPages = currentDraft.pages.map { page ->
                    if (page.id == pageId) {
                        // Создаем новую копию списка choices без удаленного выбора, создавая новую копию Page
                        page.copy(choices = page.choices.filter { it.id != choiceId }.toMutableList())
                    } else {
                        page
                    }
                }.toMutableList()
                currentDraft.copy(pages = updatedPages)
            }
        }
        notifyDraftChanged()
    }

    fun updateChoiceText(pageId: String, choiceId: String, text: String) {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                val updatedPages = currentDraft.pages.map { page ->
                    if (page.id == pageId) {
                        val updatedChoices = page.choices.map { choice ->
                            if (choice.id == choiceId) {
                                choice.copy(text = text)
                            } else {
                                choice
                            }
                        }.toMutableList()
                        page.copy(choices = updatedChoices)
                    } else {
                        page
                    }
                }.toMutableList()
                currentDraft.copy(pages = updatedPages)
            }
        }
        notifyDraftChanged()
    }

    fun updateChoiceTargetPage(pageId: String, choiceId: String, targetPageIndex: Int?) {
        _editableStoryDraft.update { draft ->
            draft?.let { currentDraft ->
                val updatedPages = currentDraft.pages.map { page ->
                    if (page.id == pageId) {
                        val updatedChoices = page.choices.map { choice ->
                            if (choice.id == choiceId) {
                                choice.copy(targetPageIndex = targetPageIndex)
                            } else {
                                choice
                            }
                        }.toMutableList()
                        page.copy(choices = updatedChoices)
                    } else {
                        page
                    }
                }.toMutableList()
                currentDraft.copy(pages = updatedPages)
            }
        }
        notifyDraftChanged()
    }

    // --- Очистка ---
    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel() // Отменяем автосохранение
        cleanupTempFiles() // Очищаем временные файлы при уничтожении VM
    }

    // Метод для очистки временных файлов, созданных в VM
    private fun cleanupTempFiles() {
        tempFilesToCleanup.forEach { file ->
            try {
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                // Лог ошибки, но не падать
                e.printStackTrace()
            }
        }
        tempFilesToCleanup.clear()
    }

    fun dismissStatusBar() {
        _statusBarState.value = StoryEditorStatusBarState.Idle
    }


    // --- ViewModel Private Mapping Helpers (UI <-> Domain) ---

    private fun EditableStoryDraft.toDraftContent(userId: String): DraftContent {
        return DraftContent(
            id = this.id,
            userId = userId,
            title = this.title,
            description = this.description,
            tags = this.tags,
            coverImagePath = this.coverImageLocalPath,
            pages = this.pages.mapIndexed { index, page -> page.toDraftPage(index) },
            lastSavedTimestamp = System.currentTimeMillis()
        )
    }

    private fun EditablePageDraft.toDraftPage(pageOrder: Int): DraftPage {
        return DraftPage(
            id = this.id,
            text = this.text,
            imagePath = this.imageLocalPath,
            isEndingPage = this.isEndingPage,
            choices = this.choices.map { it.toDraftChoice() },
            pageOrder = pageOrder
        )
    }

    private fun EditableChoiceDraft.toDraftChoice(): DraftChoice {
        return DraftChoice(
            id = this.id,
            text = this.text,
            targetPageIndex = this.targetPageIndex
        )
    }

    private fun DraftContent.toEditableDraft(): EditableStoryDraft {
        return EditableStoryDraft(
            id = this.id,
            title = this.title,
            description = this.description,
            tags = this.tags,
            coverImageLocalPath = this.coverImagePath,
            pages = this.pages.sortedBy { it.pageOrder }.map { it.toEditableDraft() }
                .toMutableList()
        )
    }

    private fun DraftPage.toEditableDraft(): EditablePageDraft {
        return EditablePageDraft(
            id = this.id,
            text = this.text,
            imageLocalPath = this.imagePath,
            isEndingPage = this.isEndingPage,
            choices = this.choices.map { it.toEditableDraft() }.toMutableList()
        )
    }

    private fun DraftChoice.toEditableDraft(): EditableChoiceDraft {
        return EditableChoiceDraft(
            id = this.id, // Клиентский ID
            text = this.text,
            targetPageIndex = this.targetPageIndex
        )
    }

    private fun EditableStoryDraft.toPublishContent(context: Context): PublishContent {
        val coverFile = this.coverImageLocalPath?.let { File(it) }
        val pageDataAndFiles = this.pages.map { page ->
            Pair(page, page.imageLocalPath?.let { File(it) })
        }

        return PublishContent(
            id = this.id,
            title = this.title,
            description = this.description,
            tags = this.tags,
            coverImageFile = coverFile,
            pages = pageDataAndFiles.map { (page, file) -> page.toPublishPage(file) }
        )
    }

    private fun EditablePageDraft.toPublishPage(file: File?): PublishPage {
        return PublishPage(
            text = this.text,
            imageFile = file, // Pass File
            isEndingPage = this.isEndingPage,
            choices = this.choices.map { it.toPublishChoice() }
        )
    }

    private fun EditableChoiceDraft.toPublishChoice(): PublishChoice {
        return PublishChoice(
            text = this.text,
            targetPageIndex = this.targetPageIndex
        )
    }

    private fun Context.uriToFile(uri: Uri, filenamePrefix: String): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            // Создаем временный файл в кеше приложения
            val tempFile = File(cacheDir, "$filenamePrefix${System.currentTimeMillis()}.${getFileExtension(uri)}")
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            // Do NOT deleteOnExit if VM manages cleanup via tempFilesToCleanup list
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            throw DataError.Unknown(appContext.getString(R.string.error_processing_image_file, filenamePrefix, e.localizedMessage), e) // Custom error
        }
    }

    fun copyUriToInternalStorage(uri: Uri, filenamePrefix: String): String? {
        return try {
            appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                val extension = appContext.contentResolver.getType(uri)?.split("/")?.lastOrNull() ?: "jpg"
                val tempFile = File(appContext.filesDir, "$filenamePrefix.${System.currentTimeMillis()}.$extension") // filesDir
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                tempFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw DataError.Unknown(appContext.getString(R.string.error_processing_image_file, filenamePrefix, e.localizedMessage), e)
        }
    }

    private fun Context.getFileExtension(uri: Uri): String {
        return contentResolver.getType(uri)?.split("/")?.lastOrNull() ?: "tmp" // Use MIME type for extension
    }
}

private fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
    while (true) {
        val current = value
        val next = function(current)
        if (compareAndSet(current, next)) {
            return
        }
    }
}