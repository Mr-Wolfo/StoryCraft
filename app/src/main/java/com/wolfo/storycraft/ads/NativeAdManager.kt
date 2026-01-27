package com.wolfo.storycraft.ads

import android.content.Context
import android.util.Log
import com.wolfo.storycraft.presentation.ui.utils.UiUtils
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NativeAdManager(
    private val context: Context,
    private val adUnitId: String
) {

    private var nativeAdLoader: NativeAdLoader? = null

    private var isLoading = false

    private val loadedAds = mutableListOf<NativeAd>()

    private var lastAdUpdateTime: Long = 0
    private val adUpdateInterval = 60 * 1000L // 1 минута в миллисекундах

    private val _adsState = MutableStateFlow<AdsState>(AdsState.Empty)
    val adsState = _adsState.asStateFlow()

    sealed class AdsState {
        object Empty : AdsState()
        object Loading : AdsState()
        data class Loaded(val ads: List<NativeAd>, val size: Int) : AdsState()
        data class Error(val message: String) : AdsState()
    }


    init {
        Log.d("NativeAdManager", "Initialized with hash: ${this.hashCode()}")
    }

    fun loadAds(count: Int = 1, onLoaded: (NativeAd) -> Unit = {}, onFailed: () -> Unit = {}) : List<NativeAd> {
        if (!UiUtils.isNetworkAvailable(context)) return loadedAds

        if (isLoading || (loadedAds.size >= count && System.currentTimeMillis() - lastAdUpdateTime < adUpdateInterval)) return loadedAds

        isLoading = true

        loadedAds.clear()

        _adsState.value = AdsState.Loading

        val adLoader: NativeAdLoader = nativeAdLoader ?: NativeAdLoader(context).apply {
            setNativeAdLoadListener(object : NativeAdLoadListener  {
                override fun onAdLoaded(nativeAd: NativeAd) {
                    isLoading = false
                    loadedAds.add(nativeAd)

                    lastAdUpdateTime = System.currentTimeMillis()
                    Log.d("NativeAdManager", "CacheAds: ${loadedAds.size} ads")

                    Log.d("NativeAdManager", "Loaded: ${nativeAd.adAssets.title} ads")
                    _adsState.value = AdsState.Loaded(loadedAds, loadedAds.size)
                    onLoaded(nativeAd)
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    isLoading = false
                    Log.e("YandexAds", "Native ad failed: ${error.description}, ${error.code}, ${error.adUnitId}")
                    _adsState.value = AdsState.Error(error.description)
                    onFailed()
                }
            })
        }

        repeat(count) {
            adLoader.loadAd(NativeAdRequestConfiguration.Builder(adUnitId).build())
        }
        return loadedAds
    }

    fun updateAds() {
        loadedAds.clear()
        loadAds(4)
    }

    fun getLoadedAd(): NativeAd? {
        println(">>> AD MANAGER >>> Get Ad from cache")
        return loadedAds.removeFirstOrNull()
    }

    fun destroy() {
        nativeAdLoader = null
        loadedAds.clear()
    }
}