package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.core.R

@Composable
fun ProductImageCarousel(
    images: List<String>
) {
    if (images.isEmpty()) return

    val pagerState = rememberPagerState {
        images.size
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
        ) { page ->
            AsyncImage(
                model = images[page].takeIf { it.isNotBlank() } ?: R.drawable.random_laptop,
                contentDescription = "Product Image",
                modifier = Modifier
                    .width(415.dp)
                    .height(240.dp)
                    .clip(RoundedCornerShape(2.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                placeholder = null,
                error = null
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (index in images.indices) {
                val color = if (pagerState.currentPage == index) Color.White else Color.Gray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}
