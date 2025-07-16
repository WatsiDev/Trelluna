package com.watsidev.kanbanboard.ui.screens.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getColorByPosition(position: Int): Color {
    return when (position % 3) {
        1 -> MaterialTheme.colorScheme.primary   // 1, 4, 7
        2 -> MaterialTheme.colorScheme.secondary // 2, 5, 8
        0 -> MaterialTheme.colorScheme.tertiary  // 3, 6, 9
        else -> MaterialTheme.colorScheme.surface
    }
}
