package com.watsidev.kanbanboard.model.data.columns

import com.google.gson.annotations.SerializedName

data class CreateColumnRequest(
    val name: String,

    @SerializedName("project_id")
    val projectId: Int
)

data class UpdateColumnRequest(
    val name: String
)

data class MessageResponse(
    val message: String
)