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
    val route: String,
    @StringRes val resourceId: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        resourceId = R.string.home,
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    data object Songs : Screen(
        route = "songs",
        resourceId = R.string.songs,
        unselectedIcon = Icons.Outlined.MusicNote,
        selectedIcon = Icons.Filled.MusicNote
    )

    data object Artists : Screen(
        route = "artists",
        resourceId = R.string.artists,
        unselectedIcon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )

    data object Albums : Screen(
        route = "albums",
        resourceId = R.string.albums,
        unselectedIcon = Icons.Outlined.Album,
        selectedIcon = Icons.Filled.Album
    )

    data object Playlists : Screen(
        route = "playlists",
        resourceId = R.string.playlists,
        unselectedIcon = Icons.AutoMirrored.Outlined.QueueMusic,
        selectedIcon = Icons.AutoMirrored.Filled.QueueMusic
    )
}