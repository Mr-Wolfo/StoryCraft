package com.wolfo.storycraft.presentation.ui.features.profile.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolfo.storycraft.R
import com.wolfo.storycraft.presentation.common.BackgroundImage
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.components.AppButton

@Composable
fun AuthWelcomeScreen(
    navPadding: PaddingValues,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(
            modifier = Modifier.blur(8.dp),
            painter = painterResource(id = R.drawable.auth_background)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = navPadding.calculateBottomPadding())
                .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.Bottom
        ) {

            Spacer(modifier = Modifier.weight(1f))

            // Лого (по центру свободного места)
            Image(
                painter = painterResource(R.drawable.storycraft_hero),
                contentDescription = "StoryCraft Logo",
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(MaterialTheme.shapes.extraLarge)
            )

            Spacer(modifier = Modifier.weight(1f))

            AppCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .padding(MaterialTheme.spacing.large),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                ) {

                    // Вступительная чушь
                    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                        Text(
                            text = "StoryCraft",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Создавайте уникальные истории и исследуйте новые миры.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 26.sp
                        )
                    }

                    // Блок кнопок
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                    ) {
                        // Главная кнопка - Зарегистрироваться
                        AppButton(
                            text = "Зарегистрироваться",
                            onClick = onNavigateToRegister,
                            isPrimary = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Второстепенная кнопка - Войти
                        AppButton(
                            text = "У меня уже есть аккаунт",
                            onClick = onNavigateToLogin,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Text(
                        text = "Продолжая, вы принимаете условия использования",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}