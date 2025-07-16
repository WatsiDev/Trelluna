package com.watsidev.kanbanboard.model.data.tasks

data class NewTaskRequest(
    val title: String,
    val description: String,
    val priority: String,
    val column_id: String,
    val created_by: String
)
