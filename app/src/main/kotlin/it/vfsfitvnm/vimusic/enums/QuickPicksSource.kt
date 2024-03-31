package it.vfsfitvnm.vimusic.enums

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import it.vfsfitvnm.vimusic.R

enum class QuickPicksSource(
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    Trending(
        resourceId = R.string.most_played,
        icon = Icons.AutoMirrored.Outlined.TrendingUp
    ),
    LastPlayed(
        resourceId = R.string.last_played,
        icon = Icons.Outlined.Schedule
    )
}