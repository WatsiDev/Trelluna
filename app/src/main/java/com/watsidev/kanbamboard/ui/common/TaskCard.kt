package com.watsidev.kanbamboard.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watsidev.kanbamboard.R

@Composable
fun TaskCard(
    title: String,
    description: String,
    comments: String,
    checked: String,
    chipText: String,
    chipTextColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            chipText,
            color = chipTextColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 3.dp, horizontal = 8.dp)
        )
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(vertical = 4.dp)
        )
        Text(
            description,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(vertical = 4.dp)
        )
        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)
        ) {
            UsersCard(
                modifier = Modifier
                    .height(40.dp)
                    .weight(1f)
            )
            Icon(
                Icons.Rounded.Sms,
                contentDescription = "Message Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                comments,
                fontSize = 16.sp
            )
            Icon(
                Icons.Rounded.CheckCircle,
                contentDescription = "Check Circle Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                checked,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun UsersCard(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
    ) {
        Image(
            painterResource(R.drawable.user_avatar),
            contentDescription = "User Image",
        )
        Image(
            painterResource(R.drawable.user_avatar),
            contentDescription = "User Image",
        )
    }
}