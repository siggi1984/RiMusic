package it.vfsfitvnm.vimusic.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector

enum class AlbumSortBy(val icon: ImageVector) {
    Title(icon = Icons.Outlined.SortByAlpha),
    Year(icon = Icons.Outlined.CalendarMonth),
    DateAdded(icon = Icons.Outlined.Schedule)
}
