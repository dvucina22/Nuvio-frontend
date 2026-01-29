package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.R
import com.example.core.ui.theme.White

@Composable
fun SelectedImagesRow(
    images: List<String>,
    onRemoveImage: (String) -> Unit
){
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .horizontalScroll(scrollState)
            .padding(vertical = 8.dp)
    ) {
        images.forEach { imageUrl ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = imageUrl.takeIf { it.isNotBlank() } ?: R.drawable.random_laptop,
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        placeholder = null,
                        error = null
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(18.dp)
                        .background(color = Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(5.dp))
                        .align(Alignment.TopEnd)
                        .clickable { onRemoveImage(imageUrl) },
                ){
                    IconButton(
                        onClick = { onRemoveImage(imageUrl) },
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = White
                        )
                    }
                }

            }
        }
    }
}