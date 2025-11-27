package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.Error
import com.example.core.ui.theme.White
import com.example.core.R
import com.example.core.ui.theme.ButtonColorDark
import com.example.core.ui.theme.ButtonColorSelected


@Composable
fun CustomGenderField(
    gender: String,
    onGenderSelected: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .width(304.dp)
                .padding(bottom = 4.dp),
        )

        Row(
            modifier = Modifier
                .width(304.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(6.dp)),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val options = listOf("M", "Ž")
            options.forEach { option ->
                val isSelected = gender == option

                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(32.dp)
                        .border(
                            width = if (isError) 1.dp else 0.dp,
                            color = if (isError) Error else Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Button(
                        onClick = { onGenderSelected(option) },
                        modifier = Modifier.fillMaxSize(),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) ButtonColorSelected else ButtonColorDark,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(option)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }


            if (isError && errorMessage != null) {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_error_hint),
                        contentDescription = "Prikaži grešku",
                        tint = Error
                    )
                }
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
                offset = DpOffset(x = (-50).dp, y = (-60).dp),
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
}

