package com.desarrolloaplicaciones1.patitasperdidas.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.desarrolloaplicaciones1.patitasperdidas.R

val Urbanist = FontFamily(
    Font(R.font.urbanist_regular, FontWeight.Normal),
    Font(R.font.urbanist_medium, FontWeight.Medium),
    Font(R.font.urbanist_semi_bold, FontWeight.SemiBold),
    Font(R.font.urbanist_bold, FontWeight.Bold)
)

val HuellitasTypography = Typography(
    displayLarge  = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Bold,    fontSize = 32.sp),
    displayMedium = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Bold,    fontSize = 28.sp),
    headlineLarge = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Bold,    fontSize = 24.sp),
    headlineMedium= TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,fontSize = 20.sp),
    titleLarge    = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,fontSize = 18.sp),
    titleMedium   = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Medium,  fontSize = 16.sp),
    titleSmall    = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Medium,  fontSize = 14.sp),
    bodyLarge     = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Normal,  fontSize = 16.sp),
    bodyMedium    = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Normal,  fontSize = 14.sp),
    bodySmall     = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Normal,  fontSize = 12.sp),
    labelLarge    = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.SemiBold,fontSize = 14.sp),
    labelMedium   = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Medium,  fontSize = 12.sp),
    labelSmall    = TextStyle(fontFamily = Urbanist, fontWeight = FontWeight.Medium,  fontSize = 11.sp),
)