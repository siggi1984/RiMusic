package it.vfsfitvnm.vimusic.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SortByAlpha
import androidx.compose.ui.graphics.vector.ImageVector

enum class ArtistSortBy(val icon: ImageVector) {
    Name(icon = Icons.Outlined.SortByAlpha),
    DateAdded(icon = Icons.Outlined.Schedule)
}
