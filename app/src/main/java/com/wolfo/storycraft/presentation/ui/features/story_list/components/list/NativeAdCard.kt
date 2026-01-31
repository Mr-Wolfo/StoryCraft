package com.wolfo.storycraft.presentation.ui.features.story_list.components.list

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wolfo.storycraft.ads.NativeAdEventLogger
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.template.NativeBannerView

@Composable
fun NativeAdCard(
    nativeAd: NativeAd,
    modifier: Modifier = Modifier,
    onAdClicked: () -> Unit = {}
) {
    val colorText = MaterialTheme.colorScheme.onPrimaryContainer.toArgb()

    AppCard {
        AndroidView(
            factory = { ctx ->
                NativeBannerView(ctx).apply {
                    nativeAd.setNativeAdEventListener(NativeAdEventLogger())
                    setBackgroundColor(Color.TRANSPARENT)
                    iconView.setBackgroundColor(Color.TRANSPARENT)
                    titleView.setTextColor(colorText)
                    bodyView.setTextColor(colorText)
                    domainView.setTextColor(colorText)
                    setAd(nativeAd)
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}