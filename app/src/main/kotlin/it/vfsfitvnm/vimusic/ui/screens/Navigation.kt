package it.vfsfitvnm.vimusic.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.enums.BuiltInPlaylist
import it.vfsfitvnm.vimusic.enums.SettingsSection
import it.vfsfitvnm.vimusic.models.SearchQuery
import it.vfsfitvnm.vimusic.ui.screens.album.AlbumScreen
import it.vfsfitvnm.vimusic.ui.screens.artist.ArtistScreen
import it.vfsfitvnm.vimusic.ui.screens.builtinplaylist.BuiltInPlaylistScreen
import it.vfsfitvnm.vimusic.ui.screens.home.HomeScreen
import it.vfsfitvnm.vimusic.ui.screens.localplaylist.LocalPlaylistScreen
import it.vfsfitvnm.vimusic.ui.screens.playlist.PlaylistScreen
import it.vfsfitvnm.vimusic.ui.screens.search.SearchResultScreen
import it.vfsfitvnm.vimusic.ui.screens.search.SearchScreen
import it.vfsfitvnm.vimusic.ui.screens.settings.SettingsPage
import it.vfsfitvnm.vimusic.ui.screens.settings.SettingsScreen
import it.vfsfitvnm.vimusic.utils.pauseSearchHistoryKey
import it.vfsfitvnm.vimusic.utils.preferences

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    player: @Composable () -> Unit = {}
) {
    @Composable
    fun PlayerScaffold(content: @Composable () -> Unit) {
        Scaffold(
            bottomBar = { player() }
        ) { paddingValues ->
            Surface(
                modifier = Modifier.padding(paddingValues),
                content = content
            )
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) + fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) + fadeOut() }
    ) {
        val navigateToAlbum =
            { browseId: String -> navController.navigate(route = "album/$browseId") }
        val navigateToArtist = { browseId: String -> navController.navigate("artist/$browseId") }
        val popDestination = {
            if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) navController.popBackStack()
        }

        composable(route = "home") {
            HomeScreen(
                navController = navController,
                player = player
            )
        }

        composable(
            route = "artist/{id}",
            arguments = listOf(
                navArgument(
                    name = "id",
                    builder = { type = NavType.StringType }
                )
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id") ?: ""

            PlayerScaffold {
                ArtistScreen(
                    browseId = id,
                    pop = popDestination,
                    onAlbumClick = navigateToAlbum
                )
            }
        }

        composable(
            route = "album/{id}",
            arguments = listOf(
                navArgument(
                    name = "id",
                    builder = { type = NavType.StringType }
                )
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id") ?: ""

            PlayerScaffold {
                AlbumScreen(
                    browseId = id,
                    pop = popDestination,
                    onAlbumClick = navigateToAlbum,
                    onGoToArtist = navigateToArtist
                )
            }
        }

        composable(
            route = "playlist/{id}",
            arguments = listOf(
                navArgument(
                    name = "id",
                    builder = { type = NavType.StringType }
                )
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id") ?: ""

            PlayerScaffold {
                PlaylistScreen(
                    browseId = id,
                    pop = popDestination,
                    onGoToAlbum = navigateToAlbum,
                    onGoToArtist = navigateToArtist
                )
            }
        }

        composable(route = "settings") {
            PlayerScaffold {
                SettingsScreen(
                    pop = popDestination,
                    onGoToSettingsPage = { index -> navController.navigate("settingsPage/$index") }
                )
            }
        }

        composable(
            route = "settingsPage/{index}",
            arguments = listOf(
                navArgument(
                    name = "index",
                    builder = { type = NavType.IntType }
                )
            )
        ) { navBackStackEntry ->
            val index = navBackStackEntry.arguments?.getInt("index") ?: 0

            PlayerScaffold {
                SettingsPage(
                    section = SettingsSection.entries[index],
                    pop = popDestination
                )
            }
        }

        composable(
            route = "search?text={text}",
            arguments = listOf(
                navArgument(
                    name = "text",
                    builder = {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            )
        ) { navBackStackEntry ->
            val context = LocalContext.current
            val text = navBackStackEntry.arguments?.getString("text") ?: ""

            PlayerScaffold {
                SearchScreen(
                    initialTextInput = text,
                    pop = popDestination,
                    onSearch = { query ->
                        navController.navigate(route = "searchResults/$query")

                        if (!context.preferences.getBoolean(pauseSearchHistoryKey, false)) {
                            it.vfsfitvnm.vimusic.query {
                                Database.insert(SearchQuery(query = query))
                            }
                        }
                    }
                )
            }
        }

        composable(
            route = "searchResults/{query}",
            arguments = listOf(
                navArgument(
                    name = "query",
                    builder = { type = NavType.StringType }
                )
            )
        ) { navBackStackEntry ->
            val query = navBackStackEntry.arguments?.getString("query") ?: ""

            PlayerScaffold {
                SearchResultScreen(
                    query = query,
                    pop = popDestination,
                    onAlbumClick = navigateToAlbum,
                    onArtistClick = navigateToArtist,
                    onPlaylistClick = { browseId -> navController.navigate("playlist/$browseId") }
                )
            }
        }

        composable(
            route = "builtInPlaylist/{index}",
            arguments = listOf(
                navArgument(
                    name = "index",
                    builder = { type = NavType.IntType }
                )
            )
        ) { navBackStackEntry ->
            val index = navBackStackEntry.arguments?.getInt("index") ?: 0

            PlayerScaffold {
                BuiltInPlaylistScreen(
                    builtInPlaylist = BuiltInPlaylist.entries[index],
                    pop = popDestination,
                    onGoToAlbum = navigateToAlbum,
                    onGoToArtist = navigateToArtist
                )
            }
        }

        composable(
            route = "localPlaylist/{id}",
            arguments = listOf(
                navArgument(
                    name = "id",
                    builder = { type = NavType.LongType }
                )
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getLong("id") ?: 0L

            PlayerScaffold {
                LocalPlaylistScreen(
                    playlistId = id,
                    pop = popDestination,
                    onGoToAlbum = navigateToAlbum,
                    onGoToArtist = navigateToArtist
                )
            }
        }
    }
}