package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RatingStars(
    rating: Double,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    maxStars: Int = 5
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(maxStars) { index ->
            val filled = index < rating
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (filled) Color(0xFFFFC107) else Color(0xFFD1D5DB),
                modifier = Modifier.size(size)
            )
        }
    }
}
