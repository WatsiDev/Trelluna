package com.watsidev.kanbanboard.ui.screens.signUp

data class SignUpUiState(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val registrationCompleted: Boolean = false
)