package com.watsidev.kanbanboard.ui.screens.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.watsidev.kanbanboard.ui.common.TaskCard
import com.watsidev.kanbanboard.ui.common.TaskTitle

@Composable
fun ProjectsScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Your Projects"
        )
        TaskTitle(
            backgroundColor = MaterialTheme.colorScheme.primary,
            badgeColor = MaterialTheme.colorScheme.primary,
            badgeText = "1",
            text = "Projects",
            onAddClick = { /* Handle add task click */ }
        )
        TaskCard(
            title = "Project Title",
            description = "Project Name",
            comments = "12",
            checked = "45",
            chipText = "Tasks",
            chipTextColor = MaterialTheme.colorScheme.primary
        )
    }
}