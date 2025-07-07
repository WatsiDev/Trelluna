package com.watsidev.kanbanboard.model.data.register

data class NewUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

