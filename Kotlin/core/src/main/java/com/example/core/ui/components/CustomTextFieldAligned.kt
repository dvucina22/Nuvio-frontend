package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White
import com.example.core.R
import com.example.core.ui.theme.BackgroundColorInput
import com.example.core.ui.theme.Black
import com.example.core.ui.theme.ButtonColorSelected
import com.example.core.ui.theme.ColorInput

@Composable
fun CustomTextFieldAligned(
    value: String,
    onValueChange: (String) -> Unit,
    labelColor: Color = Black,
    placeholder: String,
    label: String? = null,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (() -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
    ) {
        label?.let {
            Text(
                text = it,
                color = Black,
                style = textStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.Start
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(Black),
            textStyle = textStyle.copy(color = Black),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundColorInput.copy(alpha = 0.3f))
                        .border(
                            width = if (isError) 1.dp else 0.dp,
                            color = if (isError) Error else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle.copy(color = Black.copy(alpha = 0.7f))
                        )
                    }
                    innerTextField()

                    if (isPassword && onPasswordVisibilityChange != null) {
                        val icon = if (passwordVisible)
                            painterResource(id = R.drawable.ic_password_visibility_hide)
                        else
                            painterResource(id = R.drawable.ic_password_visibility_show)
                        IconButton(
                            onClick = onPasswordVisibilityChange,
                            modifier = Modifier.align(Alignment.CenterEnd).size(20.dp)
                        ) {
                            Icon(painter = icon, contentDescription = null, tint = Black)
                        }
                    }

                    if (isError && errorMessage != null) {
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier.align(Alignment.CenterEnd).size(20.dp)
                        ) {
                            Icon(painter = painterResource(id = R.drawable.ic_error_hint), contentDescription = "", tint = Error)
                        }
                    }
                }
            }
        )

        if (isError && errorMessage != null && expanded) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .background(Color(0xFF1A1F16))
                    .shadow(elevation = 8.dp, clip = true),
                offset = DpOffset(x = 0.dp, y = (-60).dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = errorMessage,
                        color = White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}
