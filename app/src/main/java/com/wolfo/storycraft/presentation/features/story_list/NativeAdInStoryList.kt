package com.wolfo.storycraft.presentation.features.story_list

import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wolfo.storycraft.presentation.common.GlassCard
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.template.NativeBannerView

@Composable
fun NativeAdCard(
    nativeAd: NativeAd,
    modifier: Modifier = Modifier,
    onAdClicked: () -> Unit = {}
) {

    val colorText = MaterialTheme.colorScheme.secondary.value.toInt()

    GlassCard {
        AndroidView(
            factory = { ctx ->
                NativeBannerView(ctx).apply {
                    nativeAd.setNativeAdEventListener(NativeAdEventLogger())
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    iconView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    titleView.setTextColor(colorText)
                    setAd(nativeAd)
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}