package com.watsidev.kanbanboard.model.data.register

import com.watsidev.kanbanboard.model.data.User

data class UserCreatedResponse(
    val message: String,
    val user: User
)