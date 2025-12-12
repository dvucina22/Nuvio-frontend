package com.example.core.ui.components.banner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.core.ui.theme.AccentColor

data class BannerData(
    val id: Int,
    val imageUrl: String,
    val title: String,
    val subtitle: String,
    val buttonText: String = "Shop Now",
    val onButtonClick: () -> Unit = {}
)

@Composable
fun BannerComponent(
    banner: BannerData,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable { banner.onButtonClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(banner.imageUrl),
            contentDescription = banner.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = banner.title,
                    color = Color.Black,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = banner.subtitle,
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = banner.onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 0.dp),
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Text(
                    text = banner.buttonText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}

