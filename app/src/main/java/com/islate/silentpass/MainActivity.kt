package com.islate.silentpass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.islate.silentpass.ui.ContactRingApp
import com.islate.silentpass.ui.theme.AppBackground
import com.islate.silentpass.ui.theme.SilentCallTheme

class MainActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = AppBackground.toArgb()
        window.navigationBarColor = AppBackground.toArgb()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            SilentCallTheme {
                ContactRingApp()
            }
        }
    }
}
