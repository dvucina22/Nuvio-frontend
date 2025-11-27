package com.example.core.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.White
import com.example.core.ui.theme.ButtonColorDark
import com.example.core.ui.theme.ButtonColorSelected


@Composable
fun CustomGenderField(
    gender: String,
    onGenderSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            val options = listOf("M", "Ž")
            options.forEach { option ->
                val isSelected = gender == option

                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(32.dp)
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
        }
    }
}


