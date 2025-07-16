package com.watsidev.kanbanboard.model.data.tasks

data class TaskResponse(
    val id: Int,
    val title: String,
    val description: String,
    val priority: String,
    val column_id: Int,
    val created_by: Int,
    val created_at: String,
    val updated_at: String
)
