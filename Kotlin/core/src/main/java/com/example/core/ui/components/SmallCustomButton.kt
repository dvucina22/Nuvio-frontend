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
import com.example.core.ui.theme.ButtonColorDark

@Composable
fun SmallCustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Int? = null,
    height: Int = 32,
    containerColor: Color = ButtonColorDark
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(height.dp)
            .then(
                if (width != null) Modifier.width(width.dp) else Modifier
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}