package com.wolfo.storycraft.presentation.features.story_list

import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.nativeads.NativeAdEventListener

class NativeAdEventLogger : NativeAdEventListener {

    override fun onAdClicked() {
        println("Native ad clicked")
    }

    override fun onLeftApplication() {
        println("Left application")
    }

    override fun onReturnedToApplication() {
        println("Returned to application")
    }

    override fun onImpression(data: ImpressionData?) {
        println("Impression: ${data?.rawData}")
    }
}
