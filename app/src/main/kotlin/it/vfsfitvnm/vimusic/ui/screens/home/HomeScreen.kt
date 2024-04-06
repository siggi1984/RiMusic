package it.vfsfitvnm.vimusic.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.Screen
import it.vfsfitvnm.vimusic.utils.homeScreenTabIndexKey
import it.vfsfitvnm.vimusic.utils.rememberPreference

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun HomeScreen(
    navController: NavController,
    player: @Composable () -> Unit = {}
) {
    val (screenIndex, onScreenChanged) = rememberPreference(homeScreenTabIndexKey, defaultValue = 0)
    val screens = listOf(
        Screen.Home,
        Screen.Songs,
        Screen.Artists,
        Screen.Albums,
        Screen.Playlists
    )
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = screens[screenIndex].resourceId))
                },
                actions = {
                    IconButton(onClick = { navController.navigate(route = "search") }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }

                    IconButton(onClick = { navController.navigate(route = "settings") }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.settings)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Column {
                player()

                NavigationBar {
                    screens.forEachIndexed { index, screen ->
                        val selected = screenIndex == index

                        NavigationBarItem(
                            selected = selected,
                            onClick = { onScreenChanged(index) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = stringResource(id = screen.resourceId)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = screen.resourceId),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = screenIndex,
                label = "home"
            ) { index ->
                when (index) {
                    0 -> QuickPicks(
                        onAlbumClick = { browseId -> navController.navigate(route = "album/$browseId") },
                        onArtistClick = { browseId -> navController.navigate(route = "artist/$browseId") },
                        onPlaylistClick = { browseId -> navController.navigate(route = "playlist/$browseId") }
                    )

                    1 -> HomeSongs(
                        onGoToAlbum = { browseId -> navController.navigate(route = "album/$browseId") },
                        onGoToArtist = { browseId -> navController.navigate(route = "artist/$browseId") }
                    )

                    2 -> HomeArtistList(
                        onArtistClick = { artist -> navController.navigate(route = "artist/${artist.id}") }
                    )

                    3 -> HomeAlbums(
                        onAlbumClick = { album -> navController.navigate(route = "album/${album.id}") }
                    )

                    4 -> HomePlaylists(
                        onBuiltInPlaylist = { playlistIndex -> navController.navigate(route = "builtInPlaylist/$playlistIndex") },
                        onPlaylistClick = { playlist -> navController.navigate(route = "localPlaylist/${playlist.id}") }
                    )
                }
            }
        }
    }
}