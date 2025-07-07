package com.watsidev.kanbanboard.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskTitle(
    backgroundColor: Color,
    badgeColor: Color,
    badgeText: String,
    text: String,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(100))
            .fillMaxWidth()
            .height(56.dp)
            .background(backgroundColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.width(4.dp))
        Text(
            text = badgeText,
            color = badgeColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 15.dp, vertical = 2.dp),
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.background,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier
                .weight(1f)
        )
        IconButton(
            onClick = { onAddClick() },
        ) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = "Add Task",
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}