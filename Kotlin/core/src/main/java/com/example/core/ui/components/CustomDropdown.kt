package com.example.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.core.R
import com.example.core.ui.theme.BackgroundColorInput
import com.example.core.ui.theme.ColorInput
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White

@Composable
fun <T> CustomDropdown(
    label: String? = null,
    value: T?,
    items: List<T>,
    itemLabel: @Composable (T) -> String,
    placeholder: String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "arrow_rotation"
    )

    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label?.let {
            Text(
                text = it,
                color = White,
                style = textStyle,
                modifier = Modifier
                    .width(304.dp)
                    .padding(bottom = 4.dp)
            )
        }

        Box(modifier = Modifier.width(304.dp)) {

            Row(
                modifier = Modifier
                    .width(304.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        BackgroundColorInput.copy(alpha = 0.3f)
                    )
                    .border(
                        width = if (isError) 1.dp else 0.dp,
                        color = if (isError) Error else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value?.let { itemLabel(it) } ?: placeholder,
                    style = textStyle.copy(
                        color = if (value == null)
                            ColorInput.copy(alpha = 0.7f)
                        else White
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(arrowRotation)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(304.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.85f))
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = itemLabel(item),
                                color = Color.Black,
                                style = textStyle
                            )
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 4.dp, vertical = 0.dp),
                        colors = MenuDefaults.itemColors(
                            textColor = Color.Black
                        )
                    )

                    if (index < items.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.Black.copy(alpha = 0.1f))
                        )
                    }
                }
            }
        }

        if (isError && errorMessage != null) {
            Row(
                modifier = Modifier
                    .width(304.dp)
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error_hint),
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(end = 4.dp)
                )
                Text(
                    text = errorMessage,
                    color = Error,
                    style = textStyle
                )
            }
        }
    }
}
