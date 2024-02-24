package it.vfsfitvnm.vimusic.ui.screens.playlist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.LocalMenuState
import it.vfsfitvnm.vimusic.ui.components.ShimmerHost
import it.vfsfitvnm.vimusic.ui.components.themed.NonQueuedMediaItemMenu
import it.vfsfitvnm.vimusic.ui.components.themed.adaptiveThumbnailContent
import it.vfsfitvnm.vimusic.ui.items.SongItem
import it.vfsfitvnm.vimusic.ui.items.SongItemPlaceholder
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.px
import it.vfsfitvnm.vimusic.utils.asMediaItem
import it.vfsfitvnm.vimusic.utils.enqueue
import it.vfsfitvnm.vimusic.utils.forcePlayAtIndex
import it.vfsfitvnm.vimusic.utils.forcePlayFromBeginning

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun PlaylistSongList(
    playlistPage: Innertube.PlaylistOrAlbumPage?
) {
    val binder = LocalPlayerServiceBinder.current
    val menuState = LocalMenuState.current

    val songThumbnailSizeDp = Dimensions.thumbnails.song
    val songThumbnailSizePx = songThumbnailSizeDp.px

    val thumbnailContent =
        adaptiveThumbnailContent(playlistPage == null, playlistPage?.thumbnail?.url)

    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "thumbnail") {
            Box(modifier = Modifier.widthIn(max = 400.dp)) {
                thumbnailContent()

                FloatingActionButton(
                    onClick = {
                        playlistPage?.songsPage?.items?.let { songs ->
                            if (songs.isNotEmpty()) {
                                binder?.stopRadio()
                                binder?.player?.forcePlayFromBeginning(
                                    songs.shuffled().map(Innertube.SongItem::asMediaItem)
                                )
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shuffle,
                        contentDescription = stringResource(id = R.string.shuffle)
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        playlistPage?.songsPage?.items?.map(Innertube.SongItem::asMediaItem)
                            ?.let { mediaItems ->
                                binder?.player?.enqueue(mediaItems)
                            }
                    },
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.PlaylistPlay,
                        contentDescription = stringResource(id = R.string.enqueue)
                    )
                }
            }
        }

        item(key = "spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(items = playlistPage?.songsPage?.items ?: emptyList()) { index, song ->
            SongItem(
                song = song,
                thumbnailSizePx = songThumbnailSizePx,
                modifier = Modifier
                    .combinedClickable(
                        onLongClick = {
                            menuState.display {
                                NonQueuedMediaItemMenu(
                                    onDismiss = menuState::hide,
                                    mediaItem = song.asMediaItem,
                                )
                            }
                        },
                        onClick = {
                            playlistPage?.songsPage?.items?.map(Innertube.SongItem::asMediaItem)
                                ?.let { mediaItems ->
                                    binder?.stopRadio()
                                    binder?.player?.forcePlayAtIndex(mediaItems, index)
                                }
                        }
                    )
            )
        }

        if (playlistPage == null) {
            item(key = "loading") {
                ShimmerHost(
                    modifier = Modifier
                        .fillParentMaxSize()
                ) {
                    repeat(4) {
                        SongItemPlaceholder(thumbnailSizeDp = songThumbnailSizeDp)
                    }
                }
            }
        }
    }
}
