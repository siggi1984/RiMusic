package it.vfsfitvnm.vimusic.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import it.vfsfitvnm.compose.persist.PersistMapCleanup
import it.vfsfitvnm.compose.routing.RouteHandler
import it.vfsfitvnm.compose.routing.defaultStacking
import it.vfsfitvnm.compose.routing.defaultStill
import it.vfsfitvnm.compose.routing.defaultUnstacking
import it.vfsfitvnm.compose.routing.isStacking
import it.vfsfitvnm.compose.routing.isUnknown
import it.vfsfitvnm.compose.routing.isUnstacking
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.MainSection
import it.vfsfitvnm.vimusic.models.SearchQuery
import it.vfsfitvnm.vimusic.query
import it.vfsfitvnm.vimusic.ui.screens.albumRoute
import it.vfsfitvnm.vimusic.ui.screens.artistRoute
import it.vfsfitvnm.vimusic.ui.screens.builtInPlaylistRoute
import it.vfsfitvnm.vimusic.ui.screens.builtinplaylist.BuiltInPlaylistScreen
import it.vfsfitvnm.vimusic.ui.screens.globalRoutes
import it.vfsfitvnm.vimusic.ui.screens.localPlaylistRoute
import it.vfsfitvnm.vimusic.ui.screens.localplaylist.LocalPlaylistScreen
import it.vfsfitvnm.vimusic.ui.screens.playlistRoute
import it.vfsfitvnm.vimusic.ui.screens.search.SearchResultScreen
import it.vfsfitvnm.vimusic.ui.screens.search.SearchScreen
import it.vfsfitvnm.vimusic.ui.screens.searchResultRoute
import it.vfsfitvnm.vimusic.ui.screens.searchRoute
import it.vfsfitvnm.vimusic.ui.screens.settings.SettingsScreen
import it.vfsfitvnm.vimusic.ui.screens.settingsRoute
import it.vfsfitvnm.vimusic.utils.homeScreenTabIndexKey
import it.vfsfitvnm.vimusic.utils.pauseSearchHistoryKey
import it.vfsfitvnm.vimusic.utils.preferences
import it.vfsfitvnm.vimusic.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun HomeScreen() {
    val saveableStateHolder = rememberSaveableStateHolder()

    PersistMapCleanup("home/")

    RouteHandler(
        listenToGlobalEmitter = true,
        transitionSpec = {
            when {
                isStacking -> defaultStacking
                isUnstacking -> defaultUnstacking
                isUnknown -> when {
                    initialState.route == searchRoute && targetState.route == searchResultRoute -> defaultStacking
                    initialState.route == searchResultRoute && targetState.route == searchRoute -> defaultUnstacking
                    else -> defaultStill
                }

                else -> defaultStill
            }
        }
    ) {
        globalRoutes()

        settingsRoute {
            SettingsScreen()
        }

        localPlaylistRoute { playlistId ->
            LocalPlaylistScreen(
                playlistId = playlistId ?: error("playlistId cannot be null")
            )
        }

        builtInPlaylistRoute { builtInPlaylist ->
            BuiltInPlaylistScreen(
                builtInPlaylist = builtInPlaylist
            )
        }

        searchResultRoute { query ->
            SearchResultScreen(
                query = query,
                onSearchAgain = {
                    searchRoute(query)
                }
            )
        }

        searchRoute { initialTextInput ->
            val context = LocalContext.current

            SearchScreen(
                initialTextInput = initialTextInput
            ) { query ->
                pop()
                searchResultRoute(query)

                if (!context.preferences.getBoolean(pauseSearchHistoryKey, false)) {
                    query {
                        Database.insert(SearchQuery(query = query))
                    }
                }
            }
        }

        host {
            val (tabIndex, onTabChanged) = rememberPreference(
                homeScreenTabIndexKey,
                defaultValue = 0
            )

            val sections = listOf(
                MainSection(
                    stringResource(id = R.string.home),
                    Icons.Outlined.Home,
                    Icons.Filled.Home
                ),
                MainSection(
                    stringResource(id = R.string.songs),
                    Icons.Outlined.MusicNote,
                    Icons.Filled.MusicNote
                ),
                MainSection(
                    stringResource(id = R.string.artists),
                    Icons.Outlined.Person,
                    Icons.Filled.Person
                ),
                MainSection(
                    stringResource(id = R.string.albums),
                    Icons.Outlined.Album,
                    Icons.Filled.Album
                ),
                MainSection(
                    stringResource(id = R.string.playlists),
                    Icons.AutoMirrored.Outlined.QueueMusic,
                    Icons.AutoMirrored.Filled.QueueMusic
                )
            )

            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = sections[tabIndex].title)
                        },
                        actions = {
                            IconButton(onClick = { searchRoute("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = stringResource(id = R.string.search)
                                )
                            }

                            IconButton(onClick = { settingsRoute() }) {
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
                    NavigationBar {
                        sections.forEachIndexed { index, section ->
                            val selected = tabIndex == index

                            NavigationBarItem(
                                selected = selected,
                                onClick = { onTabChanged(index) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) section.selectedIcon else section.unselectedIcon,
                                        contentDescription = section.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = section.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
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
                        targetState = tabIndex,
                        transitionSpec = {
                            val slideDirection = when (targetState > initialState) {
                                true -> AnimatedContentTransitionScope.SlideDirection.Left
                                false -> AnimatedContentTransitionScope.SlideDirection.Right
                            }

                            val animationSpec = spring(
                                dampingRatio = 0.9f,
                                stiffness = Spring.StiffnessLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            )

                            slideIntoContainer(slideDirection, animationSpec) togetherWith
                                    slideOutOfContainer(slideDirection, animationSpec)
                        },
                        label = "navigation"
                    ) { currentTabIndex ->
                        saveableStateHolder.SaveableStateProvider(key = currentTabIndex) {
                            when (currentTabIndex) {
                                0 -> {
                                    QuickPicks(
                                        onAlbumClick = { albumRoute(it) },
                                        onArtistClick = { artistRoute(it) },
                                        onPlaylistClick = { playlistRoute(it) }
                                    )
                                }

                                1 -> HomeSongs()

                                2 -> HomeArtistList(
                                    onArtistClick = { artistRoute(it.id) }
                                )

                                3 -> HomeAlbums(
                                    onAlbumClick = { albumRoute(it.id) }
                                )

                                4 -> HomePlaylists(
                                    onBuiltInPlaylist = { builtInPlaylistRoute(it) },
                                    onPlaylistClick = { localPlaylistRoute(it.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
