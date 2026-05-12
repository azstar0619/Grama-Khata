package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.AmountAdvance
import com.example.myapplication.ui.theme.AmountDue
import com.example.myapplication.ui.theme.AmountNeutral
import com.example.myapplication.ui.theme.avatarColorFor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerAvatar(name: String, size: Dp = 48.dp) {
    val color = remember(name) { avatarColorFor(name) }
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            color = Color.White,
            fontSize = (size.value * 0.42f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AmountText(
    amount: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    val color = when {
        amount > 0 -> AmountDue
        amount < 0 -> AmountAdvance
        else -> AmountNeutral
    }
    Text(
        text = "₹${"%,.0f".format(kotlin.math.abs(amount))}",
        color = color,
        style = style,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

fun formatTimestamp(timestamp: Long): String {
    val msgCal = Calendar.getInstance().apply { timeInMillis = timestamp }
    val now = Calendar.getInstance()
    return when {
        msgCal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) &&
        msgCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) ->
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        msgCal.get(Calendar.YEAR) == now.get(Calendar.YEAR) ->
            SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
        else ->
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

fun daysSince(timestamp: Long): Long =
    (System.currentTimeMillis() - timestamp) / (1000L * 60 * 60 * 24)
