package it.vfsfitvnm.vimusic.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.graphics.vector.ImageVector

enum class QuickPicksSource(val icon: ImageVector) {
    Trending(icon = Icons.AutoMirrored.Outlined.TrendingUp),
    LastPlayed(icon = Icons.Outlined.Schedule)
}