package com.example.mypeople.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mypeople.R

// Crea la familia de fuentes usando los archivos locales
val roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
)

// Configura la tipografía en tu tema
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)