package com.example.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import com.example.core.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun ProfileHeader(
    displayName: String,
    displayEmail: String,
    profilePictureUrl: String = ""
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profilePictureUrl.ifEmpty { R.drawable.logo_light_icon },
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (profilePictureUrl.isNotEmpty()) Color.Transparent else Color(0xFF5A676A),
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.logo_light_icon),
            error = painterResource(id = R.drawable.logo_light_icon)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = displayName,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = displayEmail,
            color = Color(0xFF9AA4A6),
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}