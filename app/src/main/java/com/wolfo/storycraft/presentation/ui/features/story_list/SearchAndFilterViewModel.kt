package com.wolfo.storycraft.presentation.ui.features.story_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.model.StoryQuery
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class SearchAndFilterViewModel : ViewModel() {
    // Состояние поиска
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Параметры сортировки
    private val _sortBy = MutableStateFlow("published_time")
    val sortBy = _sortBy.asStateFlow()

    private val _sortOrder = MutableStateFlow("desc")
    val sortOrder = _sortOrder.asStateFlow()

    // Фильтры
    private val _authorFilter = MutableStateFlow<String?>(null)
    val authorFilter = _authorFilter.asStateFlow()

    private val _tagsFilter = MutableStateFlow<List<String>?>(null)
    val tagsFilter = _tagsFilter.asStateFlow()

    // Видимость панели фильтров
    private val _filtersVisible = MutableStateFlow(false)
    val filtersVisible = _filtersVisible.asStateFlow()

    // События изменений для дебаунса
    private val _searchChanged = MutableSharedFlow<Unit>()

    init {
        viewModelScope.launch {
            _searchChanged
                .debounce(500) // Дебаунс 500мс
                .collect { buildQuery() }
        }
    }

    fun toggleFilters() {
        _filtersVisible.value = !_filtersVisible.value
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch { _searchChanged.emit(Unit) }
    }

    fun updateSortBy(sort: String) {
        _sortBy.value = sort
        buildQuery()
    }

    fun updateSortOrder(order: String) {
        _sortOrder.value = order
        buildQuery()
    }

    fun updateAuthorFilter(author: String?) {
        _authorFilter.value = author
        buildQuery()
    }

    fun updateTagsFilter(tags: List<String>?) {
        _tagsFilter.value = tags
        buildQuery()
    }

    // Собираем текущий Query и отправляем в SharedFlow
    private val _currentQuery = MutableStateFlow(StoryQuery())
    val currentQuery = _currentQuery.asStateFlow()

    private fun buildQuery() {
        _currentQuery.value = StoryQuery(
            sortBy = _sortBy.value,
            sortOrder = _sortOrder.value,
            searchQuery = _searchQuery.value.takeIf { it.isNotBlank() },
            authorUsername = _authorFilter.value.takeIf { (it?.length ?: 0) >= 3 },
            tagNames = _tagsFilter.value
        )
    }
}