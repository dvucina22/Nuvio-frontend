package com.example.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.core.R
import com.example.core.ui.theme.BackgroundNavDark
import com.example.core.ui.theme.Error

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
    errorMessage: String? = null,
    getItemColor: ((T) -> Color?)? = null,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded && enabled) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "arrow_rotation"
    )

    var triggeredWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onBackground,
                style = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            val selectedColor = value?.let { v -> getItemColor?.invoke(v) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                    triggeredWidth = with(density) {
                        coordinates.size.width.toDp()
                        }
                    }
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (enabled) MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.3f)
                    )
                    .border(
                        width = if (isError) 1.dp else 0.dp,
                        color = if (isError) Error else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = enabled
                    ) {
                        expanded = true
                    }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedColor != null && value != null) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(selectedColor)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }

                Text(
                    text = value?.let { itemLabel(it) } ?: placeholder,
                    style = textStyle.copy(
                        color = when {
                            !enabled -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            value == null ->  MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            else ->  MaterialTheme.colorScheme.onBackground
                        }
                    ),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    tint = if (enabled)  MaterialTheme.colorScheme.onBackground else  MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(arrowRotation)
                )
            }

            DropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(triggeredWidth)
                    .clip(RoundedCornerShape(8.dp))
                    .background( MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            ) {
                items.forEachIndexed { index, item ->
                    val color = getItemColor?.invoke(item)

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (color != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.size(8.dp))
                                }
                                Text(
                                    text = itemLabel(item),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = textStyle
                                )
                            }
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        colors = MenuDefaults.itemColors(
                            textColor =  MaterialTheme.colorScheme.onBackground
                        )
                    )

                    if (index < items.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(BackgroundNavDark.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }

        if (isError && errorMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
