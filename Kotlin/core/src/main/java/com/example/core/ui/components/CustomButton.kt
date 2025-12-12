
package com.example.core.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.ButtonColorDark

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Int = 250,
    height: Int = 45,
    containerColor: Color = AccentColor,
    iconRes: Int? = null,
    iconSize: Int = 20
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(height.dp)
            .width(width.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            iconRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize.dp)
                        .padding(end = 1.dp)
                )
            }

            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
