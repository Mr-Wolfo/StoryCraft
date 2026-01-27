package com.wolfo.storycraft.presentation.ui.utils

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.backgroundBlur(radius: Float): Modifier {
    // Эффект доступен только на Android 12 (API 31) и выше


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        return this.graphicsLayer(
            compositingStrategy = CompositingStrategy.Offscreen,
            renderEffect = RenderEffect.createBlurEffect(
                    radius, // Горизонтальный радиус блюра
                    radius, // Вертикальный радиус блюра
                    Shader.TileMode.DECAL // Как обрабатывать края
                )
                .asComposeRenderEffect() // Преобразуем в Compose-совместимый эффект
        )
    } else {
        return this
    }
}