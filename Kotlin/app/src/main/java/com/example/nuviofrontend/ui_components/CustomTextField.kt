package com.example.nuviofrontend.ui_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nuviofrontend.R
import com.example.nuviofrontend.ui.theme.BackgroundColorInput
import com.example.nuviofrontend.ui.theme.ColorInput

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String? = null,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (() -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall
) {
    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label?.let {
            Text(
                text = it,
                color = Color.White,
                style = textStyle,
                modifier = Modifier.width(304.dp).padding(bottom = 4.dp),
                textAlign = TextAlign.Start
            )
        }

        BasicTextField(
            modifier = Modifier
                .width(304.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    BackgroundColorInput.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                ),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(Color.White),
            textStyle = textStyle.copy(color = Color.White),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) Text(
                            placeholder,
                            style = textStyle.copy(
                                color = ColorInput.copy(alpha = 0.7f)
                            )
                        )
                        innerTextField()
                    }

                    if (isPassword && onPasswordVisibilityChange != null) {
                        val icon = if (passwordVisible)
                            painterResource(id = R.drawable.ic_password_visibility_hide)
                        else
                            painterResource(id = R.drawable.ic_password_visibility_show)

                        IconButton(
                            onClick = onPasswordVisibilityChange,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(
                                painter = icon,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        )
    }
}