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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watsidev.kanbanboard.ui.common.ButtonKanban
import com.watsidev.kanbanboard.ui.common.TaskCard
import com.watsidev.kanbanboard.ui.common.TaskTitle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderTab()
    }
}

@Composable
fun HeaderTab() {
    val selectedTab = remember { mutableStateOf(1) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            "Kanban Board",
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
                    contentDescription = "Upload Icon",
                )
            }
            IconButton(
                onClick = { /* Handle settings action */ },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Upload Icon",
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
            1 -> ByTotalTasks()
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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(vertical = 18.dp, horizontal = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            TaskTitle(
                backgroundColor = MaterialTheme.colorScheme.primary,
                badgeColor = MaterialTheme.colorScheme.primary,
                badgeText = "25",
                text = "In Progress",
                onAddClick = { /* Handle add task click */ }
            )
        }
        item {
            TaskCard(
                title = "UI/UX Design in the age of AI",
                description = "Designing user interfaces and experiences with AI tools.",
                comments = "987",
                checked = "21,8K",
                chipText = "Important",
                chipTextColor = MaterialTheme.colorScheme.primary,
            )
        }
        item {
            TaskCard(
                title = "Landing page for Azunyan senpai",
                description = "Creating a landing page for Azunyan senpai's fan club.",
                comments = "123",
                checked = "1,2K",
                chipText = "Not than important",
                chipTextColor = MaterialTheme.colorScheme.error,
            )
        }
        item {
            TaskTitle(
                backgroundColor = MaterialTheme.colorScheme.secondary,
                badgeColor = MaterialTheme.colorScheme.secondary,
                badgeText = "10",
                text = "Reviewed",
                onAddClick = { /* Handle add task click */ }
            )
        }
        item {
            TaskTitle(
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                badgeColor = MaterialTheme.colorScheme.tertiary,
                badgeText = "5",
                text = "Completed",
                onAddClick = { /* Handle add task click */ }
            )
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