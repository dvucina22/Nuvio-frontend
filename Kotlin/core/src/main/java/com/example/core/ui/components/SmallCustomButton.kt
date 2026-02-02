package com.example.core.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.theme.AccentColor

@Composable
fun SmallCustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Int? = null,
    height: Int = 32,
    containerColor: Color = AccentColor
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(height.dp)
            .then(
                if (width != null) Modifier.width(width.dp) else Modifier
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 8.dp,
            vertical = 4.dp
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.displaySmall
        )
    }
}