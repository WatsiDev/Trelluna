package com.watsidev.kanbanboard.ui.screens.login

import com.watsidev.kanbanboard.model.data.User

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val user: User? = null
)
