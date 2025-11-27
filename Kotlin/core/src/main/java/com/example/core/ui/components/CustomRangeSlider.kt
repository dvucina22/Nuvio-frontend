package com.example.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.theme.White
import kotlin.math.roundToInt

@Composable
fun CustomRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    var sliderPosition by remember { mutableStateOf(value) }
    var trackWidth by remember { mutableFloatStateOf(0f) }

    var startThumbDragging by remember { mutableStateOf(false) }
    var endThumbDragging by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val trackColor = Color(0xFFD1D5D7)
    val activeTrackColor = Color(0xFF5A656A)
    val thumbColor = Color(0xFF5A656A)

    Column {
        Text(
            text = "€${sliderPosition.start.roundToInt()} - €${sliderPosition.endInclusive.roundToInt()}",
            color = Color(0xFF2A2A2A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .onGloballyPositioned { coordinates ->
                    trackWidth = coordinates.size.width.toFloat()
                }
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.Center)
            ) {
                val startX = ((sliderPosition.start - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * size.width
                val endX = ((sliderPosition.endInclusive - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * size.width

                drawLine(
                    color = trackColor,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = size.height
                )

                drawLine(
                    color = activeTrackColor,
                    start = Offset(startX, size.height / 2),
                    end = Offset(endX, size.height / 2),
                    strokeWidth = size.height
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.Center)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = { offset ->
                                if (trackWidth == 0f) return@detectHorizontalDragGestures

                                val startX = ((sliderPosition.start - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth
                                val endX = ((sliderPosition.endInclusive - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth

                                startThumbDragging = kotlin.math.abs(offset.x - startX) < 50f
                                endThumbDragging = kotlin.math.abs(offset.x - endX) < 50f
                            },
                            onDragEnd = {
                                startThumbDragging = false
                                endThumbDragging = false
                                onValueChange(sliderPosition)
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                if (trackWidth == 0f) return@detectHorizontalDragGestures

                                val valuePerPixel = (valueRange.endInclusive - valueRange.start) / trackWidth

                                if (startThumbDragging) {
                                    val newStart = (sliderPosition.start + dragAmount * valuePerPixel)
                                        .coerceIn(valueRange.start, sliderPosition.endInclusive - 100f)
                                    sliderPosition = newStart..sliderPosition.endInclusive
                                } else if (endThumbDragging) {
                                    val newEnd = (sliderPosition.endInclusive + dragAmount * valuePerPixel)
                                        .coerceIn(sliderPosition.start + 100f, valueRange.endInclusive)
                                    sliderPosition = sliderPosition.start..newEnd
                                }
                            }
                        )
                    }
            ) {
                if (trackWidth > 0f) {
                    val startX = ((sliderPosition.start - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth
                    val endX = ((sliderPosition.endInclusive - valueRange.start) / (valueRange.endInclusive - valueRange.start)) * trackWidth

                    with(density) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .offset(x = startX.toDp() - 10.dp, y = 10.dp)
                                .clip(CircleShape)
                                .background(thumbColor)
                                .border(3.dp, White, CircleShape)
                        )

                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .offset(x = endX.toDp() - 10.dp, y = 10.dp)
                                .clip(CircleShape)
                                .background(thumbColor)
                                .border(3.dp, White, CircleShape)
                        )
                    }
                }
            }
        }
    }
}