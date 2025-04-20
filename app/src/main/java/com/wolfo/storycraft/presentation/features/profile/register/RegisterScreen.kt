package com.wolfo.storycraft.presentation.features.profile.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wolfo.storycraft.presentation.features.profile.auth.AuthFormViewModel
import com.wolfo.storycraft.presentation.features.profile.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = koinViewModel<AuthViewModel>(),
    authFormViewModel: AuthFormViewModel = koinViewModel<AuthFormViewModel>()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = authFormViewModel.registerName.value,
            onValueChange = { authFormViewModel.registerName.value = it }
        )
        TextField(
            value = authFormViewModel.registerEmail.value,
            onValueChange = { authFormViewModel.registerEmail.value = it }
        )
        TextField(
            value = authFormViewModel.registerPassword.value,
            onValueChange = { authFormViewModel.registerPassword.value = it }
        )

        Button(onClick = {
            authViewModel.register(
                authFormViewModel.registerName.value,
                authFormViewModel.registerEmail.value,
                authFormViewModel.registerPassword.value
            )
        }) {
            Text("Register")
        }
    }
}