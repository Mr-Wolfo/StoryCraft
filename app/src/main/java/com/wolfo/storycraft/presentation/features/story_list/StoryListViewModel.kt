package com.wolfo.storycraft.presentation.features.story_list

import android.util.Log
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.model.StoryQuery
import com.wolfo.storycraft.domain.usecase.story.GetStoriesStreamUseCase
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoader
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AppStatusBarUiState<out T> {
    object Idle : AppStatusBarUiState<Nothing>()
    object Loading : AppStatusBarUiState<Nothing>()
    data class Success(val message: String) : AppStatusBarUiState<Nothing>()
    data class Error<out T>(val error: DataError) : AppStatusBarUiState<T>()
}

sealed class StoryListUiState<out T> {
    object Idle : StoryListUiState<Nothing>()
    object Loading : StoryListUiState<Nothing>()
    data class Success(val data: List<StoryBaseInfo>) : StoryListUiState<List<StoryBaseInfo>>()
    data class Error<out T>(val error: DataError) : StoryListUiState<T>()
}

class StoryListViewModel(
    private val getStoriesStreamUseCase: GetStoriesStreamUseCase,
    private val nativeAdsLoader: NativeAdManager
) : ViewModel() {
    private val _uiState = MutableStateFlow<StoryListUiState<List<StoryBaseInfo>>>(StoryListUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _appStatusBarUiState = MutableStateFlow<AppStatusBarUiState<DataError>>(AppStatusBarUiState.Idle)
    val appStatusBarUiState = _appStatusBarUiState.asStateFlow()

    private val _mixedList = MutableStateFlow<List<ListItem>>(emptyList())
    val mixedList = _mixedList.asStateFlow()

    private var ads: MutableList<NativeAd> = mutableListOf()

    private var currentJob: Job? = null

    init {
        loadAds()
        loadStories()
    }

    fun loadAds() {
        viewModelScope.launch {
           nativeAdsLoader.loadAds(4) // Предзагрузка объявлений
        }
    }

    fun getAd() : NativeAd? {
        val nativeAd = nativeAdsLoader.getLoadedAd()
        if(nativeAd == null) loadAds()
        Log.d("StoryListViewModel", "Loaded: ${nativeAd?.adAssets?.title}")
        return nativeAd
    }

    fun mixList(data: List<StoryBaseInfo>): List<ListItem> {
        val mixedList = ListItem.createMixedList(
            stories = data,
        )

        val mixedListWithAds =
            mixedList.map { item ->
                if (item is ListItem.AdItem) {
                    val ad = getAd()
                    if (ad != null) {
                        Log.d("AD", "Ad loaded: ${ad.adAssets.title}")
                        item.copy(nativeAd = ad)
                    } else {
                        loadAds()
                        Log.d("AD", "No ad available")
                        item
                    }
                } else {
                    item
                }
            }

        return mixedListWithAds
    }

    private fun createMixedList(data: List<StoryBaseInfo>): List<ListItem> {
        val basicMixedList = ListItem.createMixedList(data)
        if (basicMixedList.none { it is ListItem.AdItem }) return basicMixedList

        return basicMixedList.map { item ->
            if (item is ListItem.AdItem) {
                // Не заменяем AdItem если нет доступных объявлений
                getAd()?.let { item.copy(nativeAd = it) } ?: item
            } else {
                item
            }
        }
    }


    fun loadStories(query: StoryQuery = StoryQuery()) {
        _uiState.value = StoryListUiState.Loading
        _appStatusBarUiState.value = AppStatusBarUiState.Loading

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            getStoriesStreamUseCase(forceRefresh = true, query)
                .collect { result ->
                    when (result) {
                        is ResultM.Success -> {
                            _mixedList.value = createMixedList(result.data)
                            _uiState.value = StoryListUiState.Success(result.data)
                            _appStatusBarUiState.value = AppStatusBarUiState.Idle
                        }
                        is ResultM.Failure -> {
                            if (result.cachedData != null) {
                                _uiState.value = StoryListUiState.Success(result.cachedData as List<StoryBaseInfo>)
                            }
                            _appStatusBarUiState.value = AppStatusBarUiState.Error(result.error)
                        }
                        ResultM.Loading -> {
                            _appStatusBarUiState.value = AppStatusBarUiState.Loading
                        }
                    }
                }
        }
    }

    fun destroy() {
        currentJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
        nativeAdsLoader.destroy()
    }
}