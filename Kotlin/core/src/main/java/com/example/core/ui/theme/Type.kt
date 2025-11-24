package com.example.core.ui.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import okhttp3.internal.notify
import com.example.core.R

val Montserrat = FontFamily(
    Font(R.font.montserrat_bold, weight = FontWeight.Bold),
    Font(R.font.montserrat_semibold, weight = FontWeight.SemiBold),
)
val MontserratRegular = FontFamily(
    Font(R.font.montserrat_regular, weight = FontWeight.Normal)
)
val MontserratAlternates = FontFamily(
    Font(R.font.montserrat_alternates_regular, weight = FontWeight.Normal)
)
val AppTypography = Typography(
    labelSmall = TextStyle(
        fontFamily = MontserratRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MontserratAlternates,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp,
        letterSpacing = 0.5.sp
    ),
    titleMedium = TextStyle(
        fontFamily = MontserratRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontFamily = MontserratRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        letterSpacing = 0.5.sp
    ),
)