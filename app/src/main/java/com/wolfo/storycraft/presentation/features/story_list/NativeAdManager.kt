package com.wolfo.storycraft.presentation.features.story_list

import android.content.Context
import android.util.Log
import com.wolfo.storycraft.presentation.common.isNetworkAvailable
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdException
import com.yandex.mobile.ads.nativeads.NativeAdImageLoadingListener
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoader
import java.util.logging.Logger

class NativeAdManager(
    private val context: Context,
    private val adUnitId: String
) {

    private var nativeAdLoader: NativeAdLoader? = null

    private var isLoading = false

    private val loadedAds = mutableListOf<NativeAd>()

    private var lastAdUpdateTime: Long = 0
    private val adUpdateInterval = 60 * 1000L // 1 минута в миллисекундах

    init {
        Log.d("NativeAdManager", "Initialized with hash: ${this.hashCode()}")
    }

    fun loadAds(count: Int = 1, onLoaded: (NativeAd) -> Unit = {}, onFailed: () -> Unit = {}) : List<NativeAd> {
        if (!isNetworkAvailable(context)) return loadedAds

        if (isLoading || (loadedAds.size >= count && System.currentTimeMillis() - lastAdUpdateTime < adUpdateInterval)) return loadedAds

        isLoading = true

        loadedAds.clear()

        val adLoader: NativeAdLoader = nativeAdLoader ?: NativeAdLoader(context).apply {
            setNativeAdLoadListener(object : NativeAdLoadListener  {
                override fun onAdLoaded(nativeAd: NativeAd) {
                    isLoading = false
                    loadedAds.add(nativeAd)

                    lastAdUpdateTime = System.currentTimeMillis()
                    Log.d("NativeAdManager", "CacheAds: ${loadedAds.size} ads")

                    Log.d("NativeAdManager", "Loaded: ${nativeAd.adAssets.title} ads")

                    onLoaded(nativeAd)
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    isLoading = false
                    Log.e("YandexAds", "Native ad failed: ${error.description}, ${error.code}, ${error.adUnitId}")
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