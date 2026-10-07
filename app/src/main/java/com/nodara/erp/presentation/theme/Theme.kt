package com.nodara.erp.presentation.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NodaraSignature,
    onPrimary = NodaraPaper,
    primaryContainer = NodaraSignature.copy(alpha = 0.15f),
    onPrimaryContainer = NodaraSignature,
    secondary = NodaraMoss,
    onSecondary = NodaraPaper,
    secondaryContainer = NodaraMoss.copy(alpha = 0.15f),
    onSecondaryContainer = NodaraMoss,
    background = NodaraMist,
    onBackground = NodaraInk,
    surface = NodaraPaper,
    onSurface = NodaraInk,
    surfaceVariant = NodaraMist,
    onSurfaceVariant = NodaraSlate,
    outline = NodaraLine,
    error = NodaraDanger,
    onError = NodaraPaper
)

private val DarkColorScheme = darkColorScheme(
    primary = NodaraSignature,
    onPrimary = NodaraInk,
    primaryContainer = NodaraSignature.copy(alpha = 0.2f),
    onPrimaryContainer = NodaraSignature,
    secondary = NodaraMoss,
    onSecondary = NodaraPaper,
    background = NodaraInk,
    onBackground = NodaraMist,
    surface = NodaraSlate,
    onSurface = NodaraMist,
    outline = NodaraLine.copy(alpha = 0.3f),
    error = NodaraDanger
)

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun NodaraERPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            activity?.window?.let { window ->
                window.statusBarColor = NodaraInk.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
