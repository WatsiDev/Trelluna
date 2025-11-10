package com.watsidev.kanbanboard.model.data.columns

import com.google.gson.annotations.SerializedName

data class Column(
    val id: Int,
    val name: String,

    // Mapea la clave JSON "project_id" a la variable "projectId" en Kotlin
    @SerializedName("project_id")
    val projectId: Int
)