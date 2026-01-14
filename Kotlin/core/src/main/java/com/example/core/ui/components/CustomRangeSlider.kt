package com.example.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.theme.AccentColor
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    minGap: Float = 100f,
    modifier: Modifier = Modifier
) {
    val trackColor = Color(0xFFD1D5D7)
    val activeTrackColor = Color(0xFF5A656A)
    val thumbColor = Color(0xFF5A656A)

    Column(modifier = modifier) {
        Text(
            text = "${value.start.roundToInt()} - ${value.endInclusive.roundToInt()}",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        RangeSlider(
            value = value,
            onValueChange = { newRange ->
                val adjustedRange = if (newRange.endInclusive - newRange.start < minGap) {
                    if (newRange.start != value.start) {
                        newRange.start..(newRange.start + minGap).coerceAtMost(valueRange.endInclusive)
                    } else {
                        (newRange.endInclusive - minGap).coerceAtLeast(valueRange.start)..newRange.endInclusive
                    }
                } else {
                    newRange
                }
                onValueChange(adjustedRange)
            },
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = AccentColor,
                activeTrackColor = AccentColor,
                inactiveTrackColor = trackColor
            )
        )
    }
}