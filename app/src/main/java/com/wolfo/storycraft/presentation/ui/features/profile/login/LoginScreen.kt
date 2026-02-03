package com.wolfo.storycraft.presentation.ui.features.profile.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.R
import com.wolfo.storycraft.domain.DataError
import com.wolfo.storycraft.presentation.common.BackgroundImage
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn
import com.wolfo.storycraft.presentation.common.ImmersiveBackground
import com.wolfo.storycraft.presentation.theme.spacing
import com.wolfo.storycraft.presentation.ui.components.AppButton
import com.wolfo.storycraft.presentation.ui.components.AppCard
import com.wolfo.storycraft.presentation.ui.components.AppTopBar
import com.wolfo.storycraft.presentation.ui.features.profile.auth.AuthFormViewModel
import com.wolfo.storycraft.presentation.ui.features.profile.auth.AuthUiState
import com.wolfo.storycraft.presentation.ui.features.profile.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navPadding: PaddingValues,
    viewModel: AuthViewModel = koinViewModel(),
    formViewModel: AuthFormViewModel = koinViewModel(),
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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

            // Кнопка Назад
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.Start)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Заголовок
            Column(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                Text(
                    text = "С возвращением",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(2f, 4f),
                            blurRadius = 8f
                        )
                    ),
                    color = Color.White
                )
                Text(
                    text = "Войдите, чтобы продолжить свои истории.",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.5f),
                            offset = Offset(2f, 4f),
                            blurRadius = 8f
                        )
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Основная карточка формы
            AppCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.large),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    // Поля ввода
                    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                        OutlinedTextField(
                            value = formViewModel.loginName.value,
                            onValueChange = { formViewModel.loginName.value = it },
                            label = { Text("Имя пользователя") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            singleLine = true,
                        )

                        OutlinedTextField(
                            value = formViewModel.loginPassword.value,
                            onValueChange = { formViewModel.loginPassword.value = it },
                            label = { Text("Пароль") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            singleLine = true,
                        )
                    }

                    // Обработка ошибок
                    if (uiState is AuthUiState.Error) {
                        val state = uiState as AuthUiState.Error
                        Text(
                            text = state.error.message ?: "Ошибка входа",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.extraSmall)
                        )
                    }

                    AppButton(
                        text = if (uiState is AuthUiState.Loading) "Загрузка..." else "Войти в аккаунт",
                        isPrimary = true,
                        onClick = {
                            viewModel.login(
                                formViewModel.loginName.value,
                                formViewModel.loginPassword.value
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Ссылка на регистрацию
            AppButton(
                text = "Ещё нет аккаунта? Создать",
                isPrimary = false,
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.small)
            )
        }
    }
}