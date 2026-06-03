package com.islate.silentpass.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BrandBlue = Color(0xFF409EFF)
val AppBackground = Color(0xFFF7F9FC)
val SoftBlue = Color(0xFFEAF5FF)
val SoftWarning = Color(0xFFFFF4E5)
val Warning = Color(0xFFB56A00)
val CompactButtonPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp)

@Composable
fun SilentCallTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BrandBlue,
            secondary = Color(0xFF496580),
            background = AppBackground,
            surface = Color.White,
            onSurface = Color(0xFF172033),
            onSurfaceVariant = Color(0xFF667085),
            outlineVariant = Color(0xFFE1E7F0)
        ),
        typography = CompactTypography,
        content = {
            Surface(content = content)
        }
    )
}

private val CompactTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
