package com.watsidev.kanbanboard.model.data.login

import com.watsidev.kanbanboard.model.data.User

data class LoginResponse(
    val message: String,
    val user: User
)