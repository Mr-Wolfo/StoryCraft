package com.wolfo.storycraft.presentation.features.profile.register

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.wolfo.storycraft.presentation.common.CustomScrollableColumn
import com.wolfo.storycraft.presentation.common.GlassCard
import com.wolfo.storycraft.presentation.common.ImmersiveBackground
import com.wolfo.storycraft.presentation.features.profile.auth.AuthFormViewModel
import com.wolfo.storycraft.presentation.features.profile.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = koinViewModel<AuthViewModel>(),
    formViewModel: AuthFormViewModel = koinViewModel<AuthFormViewModel>(),
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        ImmersiveBackground(scrollState)

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Регистрация") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            CustomScrollableColumn(
                scrollState = scrollState,
                contentPadding = padding,
                modifier = Modifier.padding(24.dp)
            ) {
                // Поля формы
                GlassCard {
                    Column(modifier = Modifier.padding(20.dp)) {
                        OutlinedTextField(
                            value = formViewModel.registerName.value,
                            onValueChange = { formViewModel.registerName.value = it },
                            label = { Text("Имя пользователя") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = formViewModel.registerEmail.value,
                            onValueChange = { formViewModel.registerEmail.value = it },
                            label = { Text("Почта") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = formViewModel.registerPassword.value,
                            onValueChange = { formViewModel.registerPassword.value = it },
                            label = { Text("Пароль") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (uiState.error != null) {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp))
                        }

                        Button(
                            onClick = { viewModel.register(
                                formViewModel.registerName.value,
                                formViewModel.registerEmail.value,
                                formViewModel.registerPassword.value
                            ) },
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("Зарегистрироваться")
                            }
                        }
                    }
                }

                // Ссылка на логин
                TextButton(onClick = onNavigateToLogin) {
                    Text("Уже есть аккаунт? Войдите",
                        color = MaterialTheme.colorScheme.tertiaryContainer)
                }
            }
        }
    }
}