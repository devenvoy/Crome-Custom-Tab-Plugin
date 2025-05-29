package com.demo.hybrid

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class UseTheme(
    val primaty: Color = Color(0xFFFFFF)
)

val LocalTheme = compositionLocalOf<UseTheme>{
    error("No theme provided")
}