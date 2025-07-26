package com.wolfo.storycraft.presentation.features.story_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wolfo.storycraft.ads.NativeAdManager
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.domain.ResultM
import com.wolfo.storycraft.domain.model.story.StoryBaseInfo
import com.wolfo.storycraft.domain.model.StoryQuery
import com.wolfo.storycraft.domain.usecase.story.GetStoriesStreamUseCase
import com.yandex.mobile.ads.nativeads.NativeAd
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
        setupStateObservers()
    }

    private fun setupStateObservers() {
        viewModelScope.launch {
            // Комбинируем состояния рекламы и историй
            combine(
                nativeAdsLoader.adsState,
                uiState
            ) { adsState, storiesState ->
                adsState to storiesState
            }.collect { (adsState, storiesState) ->
                Log.d(">>> Story List VM <<<", "Combined state update. Ads: ${adsState.javaClass.simpleName}, Stories: ${storiesState.javaClass.simpleName}")

                when (storiesState) {
                    is StoryListUiState.Success -> {
                        when (adsState) {
                            is NativeAdManager.AdsState.Loaded -> {
                                Log.d(">>> Story List VM <<<", "Updating mixed list with ${adsState.ads.size} ads")
                                updateMixedList(storiesState.data, adsState.ads)
                            }
                            NativeAdManager.AdsState.Empty -> {
                                Log.d(">>> Story List VM <<<", "No ads available yet")
                                updateMixedList(storiesState.data, emptyList())
                            }
                            else -> {
                                updateMixedList(storiesState.data, emptyList())
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }


    private fun updateMixedList(data: List<StoryBaseInfo>, ads: List<NativeAd>) {
        _mixedList.value = createMixedList(data, ads)
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

    private fun createMixedList(data: List<StoryBaseInfo>, ads: List<NativeAd>): List<ListItem> {
        val basicMixedList = ListItem.createMixedList(data)
        val mutableAdsList = ads.toMutableList()
        if (basicMixedList.none { it is ListItem.AdItem }) return basicMixedList

        return basicMixedList.map { item ->
            if (item is ListItem.AdItem) {
                mutableAdsList.removeFirstOrNull()?.let { item.copy(nativeAd = it) } ?: item
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
                            Log.d(">>> Story List VM <<<", "Success load stories")
                            _uiState.value = StoryListUiState.Success(result.data)
                            _appStatusBarUiState.value = AppStatusBarUiState.Idle
                        }
                        is ResultM.Failure -> {
                            Log.d(">>> Story List VM <<<", "Error load stories")
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