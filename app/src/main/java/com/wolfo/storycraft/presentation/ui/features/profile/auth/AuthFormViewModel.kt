package com.wolfo.storycraft.presentation.ui.features.profile.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthFormViewModel: ViewModel() {
    val loginName = mutableStateOf("")
    val loginPassword = mutableStateOf("")

    val registerName = mutableStateOf("")
    val registerEmail = mutableStateOf("")
    val registerPassword = mutableStateOf("")
}