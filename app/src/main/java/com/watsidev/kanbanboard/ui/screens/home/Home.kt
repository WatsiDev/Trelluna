package com.watsidev.kanbanboard.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watsidev.kanbanboard.model.data.columns.ColumnResponse
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.TaskCard
import com.watsidev.kanbanboard.ui.common.TaskTitle

@Composable
fun HomeScreen(
    onCreateNewColumn: () -> Unit,
    createTask:(Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderTab(
            onCreateNewColumn = { onCreateNewColumn() },
            createTask = { createTask(it) }
        )
    }
}

@Composable
fun HeaderTab(
    onCreateNewColumn: () -> Unit,
    createTask: (Int) -> Unit
) {
    val selectedTab = remember { mutableStateOf(1) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            "Project Name",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier
                .padding(10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { },
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Search Icon",
                )
            }
            ButtonKanban(
                text = "Share",
                icon = Icons.Default.Share,
                onClick = { /* Handle share action */ },
                modifier = Modifier
            )
            IconButton(
                onClick = { /* Handle settings action */ },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
            ) {
                Icon(
                    Icons.Outlined.FileUpload,
                    contentDescription = "Export Icon",
                )
            }
            IconButton(
                onClick = { onCreateNewColumn() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Add Column Icon",
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        TabRow(selectedTabIndex = selectedTab.value) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.value == index,
                    onClick = { selectedTab.value = index },
                    text = { Text(text = tab.title) },
                )
            }
        }
        when (selectedTab.value) {
            0 -> ByStatus()
            1 -> ByTotalTasks(createTask = { createTask(it) })
            2 -> TasksDue()
        }
    }
}

@Composable
fun ByStatus(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = "By Status",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ByTotalTasks(
    modifier: Modifier = Modifier,
    createTask: (Int) -> Unit,
    viewModel: ColumnListViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .padding(vertical = 18.dp, horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (state.isLoading) {
            item {
                CircularProgressIndicator()
            }
        }

        state.errorMessage?.let { error ->
            item {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }
        }

        items(state.columns) { column ->
            this@LazyColumn.item(key = "column-${column.id}") {
                ColumnWithTasksItem(
                    column = column,
                    onAddTaskClick = createTask
                )
            }
        }
    }
}



@Composable
fun ColumnWithTasksItem(
    column: ColumnResponse,
    onAddTaskClick: (Int) -> Unit
) {
    val viewModel: TaskListViewModel = viewModel(key = "tasks_for_column_${column.id}")
    val state by viewModel.uiState.collectAsState()

    // Cargar tareas solo la primera vez o si cambia la columna
    LaunchedEffect(column.id) {
        viewModel.fetchTasks(column.id)
    }

    val color = getColorByPosition(column.position)

    Column(modifier = Modifier.fillMaxWidth()) {
        TaskTitle(
            backgroundColor = color,
            badgeColor = color,
            badgeText = "${state.tasks.size}",
            text = column.name,
            onAddClick = { onAddTaskClick(column.id) }
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state.errorMessage != null) {
            Text("Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
        } else {
            state.tasks.forEach { task ->
                TaskCard(
                    title = task.title,
                    description = task.description,
                    comments = "", // completa si tienes info
                    checked = "",  // completa si tienes info
                    chipText = task.priority,
                    chipTextColor = color,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}



@Composable
fun TasksDue(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = "Tasks Due",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}