package it.vfsfitvnm.vimusic.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import it.vfsfitvnm.vimusic.R

sealed class Screen(
    @StringRes val resourceId: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : Screen(
        resourceId = R.string.home,
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    data object Songs : Screen(
        resourceId = R.string.songs,
        unselectedIcon = Icons.Outlined.MusicNote,
        selectedIcon = Icons.Filled.MusicNote
    )

    data object Artists : Screen(
        resourceId = R.string.artists,
        unselectedIcon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )

    data object Albums : Screen(
        resourceId = R.string.albums,
        unselectedIcon = Icons.Outlined.Album,
        selectedIcon = Icons.Filled.Album
    )

    data object Playlists : Screen(
        resourceId = R.string.playlists,
        unselectedIcon = Icons.AutoMirrored.Outlined.QueueMusic,
        selectedIcon = Icons.AutoMirrored.Filled.QueueMusic
    )
}