package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.AccentColor
import com.example.core.ui.theme.ButtonColorDark


@Composable
fun CustomGenderField(
    gender: String,
    onGenderSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(start = 32.dp, bottom = 4.dp, top = 4.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        val options = listOf("M", "Ž")

        options.forEach { option ->
            val isSelected = gender == option

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onGenderSelected(option) }
            ) {

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) AccentColor else ButtonColorDark
                        )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = option,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

