package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.core.R
import com.example.core.ui.theme.Black
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White

@Composable
fun CustomDescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String? = null,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    isError: Boolean = false,
    errorMessage: String? = null,
    minLines: Int = 1,
    maxLines: Int = 20,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.Sentences,
        imeAction = ImeAction.Default
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var expanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
    ) {
        label?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onBackground,
                style = textStyle,
                modifier = Modifier.padding(bottom = 4.dp),
                textAlign = TextAlign.Start
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = false,
                cursorBrush = SolidColor(Black),
                textStyle = textStyle.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    lineHeight = textStyle.fontSize * 1.2f
                ),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                maxLines = maxLines,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = (minLines * 24).dp, max = (maxLines * 24).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.7f))
                    .border(
                        width = if (isError) 1.dp else 0.dp,
                        color = if (isError) Error else if (isFocused) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .onFocusChanged { isFocused = it.isFocused }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = textStyle.copy(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                            )
                        }
                        innerTextField()
                    }

                    if (isError && errorMessage != null) {
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_error_hint),
                                contentDescription = "Prikaži grešku",
                                tint = Error
                            )
                        }
                    }

                    if (isError && errorMessage != null) {
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                                .background(Color(0xFF1A1F16))
                                .shadow(elevation = 8.dp, clip = true),
                            offset = DpOffset(x = (-50).dp, y = (-60).dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = errorMessage,
                                    color = White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}