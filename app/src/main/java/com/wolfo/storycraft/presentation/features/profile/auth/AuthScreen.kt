package com.wolfo.storycraft.presentation.features.profile.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn

@Composable
fun AuthScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxSize()) {
        // Параллакс-фон
        AsyncImage(
            model = "https://ephemeroloverdose.wordpress.com/wp-content/uploads/2015/06/bigstock-old-book-with-feather-pen-78743609.jpg?w=1296",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = scrollState.value * 0.3f }
        )

        // Затемнение с градиентом
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        startY = 0f,
                        endY = LocalConfiguration.current.screenHeightDp * 0.6f
                    )
                )
        )

        // Контент
        CustomScrollableColumn(
            scrollState = scrollState,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            // Заголовок
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Добро пожаловать",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                )

                Text(
                    text = "Создавайте уникальные истории и исследуйте миры",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colorScheme.onSurfaceVariant
                    )
                )
            }

            // Кнопки действий
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Войти")
                }

                OutlinedButton(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, colorScheme.outline),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Зарегистрироваться")
                }
            }
        }
    }
}