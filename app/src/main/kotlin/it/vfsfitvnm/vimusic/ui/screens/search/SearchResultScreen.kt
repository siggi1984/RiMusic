package it.vfsfitvnm.vimusic.ui.screens.search

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.innertube.models.bodies.ContinuationBody
import it.vfsfitvnm.innertube.models.bodies.SearchBody
import it.vfsfitvnm.innertube.requests.searchPage
import it.vfsfitvnm.innertube.utils.from
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.LocalMenuState
import it.vfsfitvnm.vimusic.models.Section
import it.vfsfitvnm.vimusic.ui.components.ChipScaffold
import it.vfsfitvnm.vimusic.ui.components.themed.NonQueuedMediaItemMenu
import it.vfsfitvnm.vimusic.ui.items.AlbumItem
import it.vfsfitvnm.vimusic.ui.items.ArtistItem
import it.vfsfitvnm.vimusic.ui.items.ItemPlaceholder
import it.vfsfitvnm.vimusic.ui.items.ListItemPlaceholder
import it.vfsfitvnm.vimusic.ui.items.PlaylistItem
import it.vfsfitvnm.vimusic.ui.items.SongItem
import it.vfsfitvnm.vimusic.ui.items.VideoItem
import it.vfsfitvnm.vimusic.utils.asMediaItem
import it.vfsfitvnm.vimusic.utils.forcePlay
import it.vfsfitvnm.vimusic.utils.rememberPreference
import it.vfsfitvnm.vimusic.utils.searchResultScreenTabIndexKey

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun SearchResultScreen(
    query: String,
    pop: () -> Unit,
    onAlbumClick: (String) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (String) -> Unit
) {
    val emptyItemsText = stringResource(id = R.string.no_results_found)
    val (tabIndex, onTabIndexChanges) = rememberPreference(searchResultScreenTabIndexKey, 0)
    val sections = listOf(
        Section(stringResource(id = R.string.songs), Icons.Outlined.MusicNote),
        Section(stringResource(id = R.string.albums), Icons.Outlined.Album),
        Section(stringResource(id = R.string.artists), Icons.Outlined.Person),
        Section(stringResource(id = R.string.videos), Icons.Outlined.Movie),
        Section(stringResource(id = R.string.playlists), Icons.AutoMirrored.Outlined.QueueMusic),
        Section(stringResource(id = R.string.featured), Icons.AutoMirrored.Outlined.QueueMusic)
    )

    ChipScaffold(
        topIconButtonId = Icons.Outlined.Search,
        onTopIconButtonClick = pop,
        sectionTitle = query,
        tabIndex = tabIndex,
        onTabChanged = onTabIndexChanges,
        tabColumnContent = sections
    ) { index ->
        when (index) {
            0 -> {
                val binder = LocalPlayerServiceBinder.current
                val menuState = LocalMenuState.current

                ItemsPage(
                    tag = "searchResults/$query/songs",
                    itemsPageProvider = { continuation ->
                        if (continuation == null) {
                            Innertube.searchPage(
                                body = SearchBody(
                                    query = query,
                                    params = Innertube.SearchFilter.Song.value
                                ),
                                fromMusicShelfRendererContent = Innertube.SongItem.Companion::from
                            )
                        } else {
                            Innertube.searchPage(
                                body = ContinuationBody(continuation = continuation),
                                fromMusicShelfRendererContent = Innertube.SongItem.Companion::from
                            )
                        }
                    },
                    emptyItemsText = emptyItemsText,
                    itemContent = { song ->
                        SongItem(
                            song = song,
                            onClick = {
                                binder?.stopRadio()
                                binder?.player?.forcePlay(song.asMediaItem)
                                binder?.setupRadio(song.info?.endpoint)
                            },
                            onLongClick = {
                                menuState.display {
                                    NonQueuedMediaItemMenu(
                                        onDismiss = menuState::hide,
                                        mediaItem = song.asMediaItem,
                                        onGoToAlbum = onAlbumClick,
                                        onGoToArtist = onArtistClick
                                    )
                                }
                            }
                        )
                    },
                    itemPlaceholderContent = {
                        ListItemPlaceholder()
                    }
                )
            }

            1 -> {
                ItemsPage(
                    tag = "searchResults/$query/albums",
                    itemsPageProvider = { continuation ->
                        if (continuation == null) {
                            Innertube.searchPage(
                                body = SearchBody(
                                    query = query,
                                    params = Innertube.SearchFilter.Album.value
                                ),
                                fromMusicShelfRendererContent = Innertube.AlbumItem::from
                            )
                        } else {
                            Innertube.searchPage(
                                body = ContinuationBody(continuation = continuation),
                                fromMusicShelfRendererContent = Innertube.AlbumItem::from
                            )
                        }
                    },
                    emptyItemsText = emptyItemsText,
                    itemContent = { album ->
                        AlbumItem(
                            album = album,
                            onClick = { onAlbumClick(album.key) }
                        )

                    },
                    itemPlaceholderContent = {
                        ItemPlaceholder()
                    }
                )
            }

            2 -> {
                ItemsPage(
                    tag = "searchResults/$query/artists",
                    itemsPageProvider = { continuation ->
                        if (continuation == null) {
                            Innertube.searchPage(
                                body = SearchBody(
                                    query = query,
                                    params = Innertube.SearchFilter.Artist.value
                                ),
                                fromMusicShelfRendererContent = Innertube.ArtistItem::from
                            )
                        } else {
                            Innertube.searchPage(
                                body = ContinuationBody(continuation = continuation),
                                fromMusicShelfRendererContent = Innertube.ArtistItem::from
                            )
                        }
                    },
                    emptyItemsText = emptyItemsText,
                    itemContent = { artist ->
                        ArtistItem(
                            artist = artist,
                            onClick = { onArtistClick(artist.key) }
                        )
                    },
                    itemPlaceholderContent = {
                        ItemPlaceholder(shape = CircleShape)
                    }
                )
            }

            3 -> {
                val binder = LocalPlayerServiceBinder.current
                val menuState = LocalMenuState.current

                ItemsPage(
                    tag = "searchResults/$query/videos",
                    itemsPageProvider = { continuation ->
                        if (continuation == null) {
                            Innertube.searchPage(
                                body = SearchBody(
                                    query = query,
                                    params = Innertube.SearchFilter.Video.value
                                ),
                                fromMusicShelfRendererContent = Innertube.VideoItem::from
                            )
                        } else {
                            Innertube.searchPage(
                                body = ContinuationBody(continuation = continuation),
                                fromMusicShelfRendererContent = Innertube.VideoItem::from
                            )
                        }
                    },
                    emptyItemsText = emptyItemsText,
                    itemContent = { video ->
                        VideoItem(
                            video = video,
                            onClick = {
                                binder?.stopRadio()
                                binder?.player?.forcePlay(video.asMediaItem)
                                binder?.setupRadio(video.info?.endpoint)
                            },
                            onLongClick = {
                                menuState.display {
                                    NonQueuedMediaItemMenu(
                                        mediaItem = video.asMediaItem,
                                        onDismiss = menuState::hide,
                                        onGoToAlbum = onAlbumClick,
                                        onGoToArtist = onArtistClick
                                    )
                                }
                            }
                        )
                    },
                    itemPlaceholderContent = {
                        ListItemPlaceholder(
                            thumbnailHeight = 64.dp,
                            thumbnailAspectRatio = 16F / 9F
                        )
                    }
                )
            }

            4, 5 -> {
                ItemsPage(
                    tag = "searchResults/$query/${if (index == 4) "playlists" else "featured"}",
                    itemsPageProvider = { continuation ->
                        if (continuation == null) {
                            val filter =
                                if (index == 4) Innertube.SearchFilter.CommunityPlaylist else Innertube.SearchFilter.FeaturedPlaylist

                            Innertube.searchPage(
                                body = SearchBody(query = query, params = filter.value),
                                fromMusicShelfRendererContent = Innertube.PlaylistItem::from
                            )
                        } else {
                            Innertube.searchPage(
                                body = ContinuationBody(continuation = continuation),
                                fromMusicShelfRendererContent = Innertube.PlaylistItem::from
                            )
                        }
                    },
                    emptyItemsText = emptyItemsText,
                    itemContent = { playlist ->
                        PlaylistItem(
                            playlist = playlist,
                            onClick = { onPlaylistClick(playlist.key) }
                        )
                    },
                    itemPlaceholderContent = {
                        ItemPlaceholder()
                    }
                )
            }
        }
    }
}