package it.vfsfitvnm.vimusic.ui.screens.localplaylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.compose.reordering.draggedItem
import it.vfsfitvnm.compose.reordering.rememberReorderingState
import it.vfsfitvnm.compose.reordering.reorder
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.LocalMenuState
import it.vfsfitvnm.vimusic.models.PlaylistWithSongs
import it.vfsfitvnm.vimusic.models.Song
import it.vfsfitvnm.vimusic.query
import it.vfsfitvnm.vimusic.ui.components.PlaylistThumbnail
import it.vfsfitvnm.vimusic.ui.components.themed.InPlaylistMediaItemMenu
import it.vfsfitvnm.vimusic.ui.items.SongItem
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.px
import it.vfsfitvnm.vimusic.utils.asMediaItem
import it.vfsfitvnm.vimusic.utils.enqueue
import it.vfsfitvnm.vimusic.utils.forcePlayAtIndex
import it.vfsfitvnm.vimusic.utils.forcePlayFromBeginning

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun LocalPlaylistSongs(
    playlistId: Long,
    playlistWithSongs: PlaylistWithSongs?
) {
    val binder = LocalPlayerServiceBinder.current

    val menuState = LocalMenuState.current

    val lazyListState = rememberLazyListState()

    val reorderingState = rememberReorderingState(
        lazyListState = lazyListState,
        key = playlistWithSongs?.songs ?: emptyList<Any>(),
        onDragEnd = { fromIndex, toIndex ->
            query {
                Database.move(playlistId, fromIndex, toIndex)
            }
        },
        extraItemCount = 1
    )

    val thumbnailSizeDp = Dimensions.thumbnails.song
    val thumbnailSizePx = thumbnailSizeDp.px

    LazyColumn(
        state = reorderingState.lazyListState,
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item(key = "thumbnail") {
            Box(modifier = Modifier.widthIn(max = 400.dp)) {
                PlaylistThumbnail(playlistId = playlistId)

                if (playlistWithSongs?.songs?.isNotEmpty() == true) {
                    FloatingActionButton(
                        onClick = {
                            playlistWithSongs.songs.let { songs ->
                                if (songs.isNotEmpty()) {
                                    binder?.stopRadio()
                                    binder?.player?.forcePlayFromBeginning(
                                        songs.shuffled().map(Song::asMediaItem)
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
                            playlistWithSongs.songs
                                .map(Song::asMediaItem)
                                .let { mediaItems ->
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
        }

        item(key = "spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }

        itemsIndexed(
            items = playlistWithSongs?.songs ?: emptyList(),
            key = { _, song -> song.id },
            contentType = { _, song -> song },
        ) { index, song ->
            SongItem(
                song = song,
                thumbnailSizePx = thumbnailSizePx,
                modifier = Modifier
                    .combinedClickable(
                        onLongClick = {
                            menuState.display {
                                InPlaylistMediaItemMenu(
                                    playlistId = playlistId,
                                    positionInPlaylist = index,
                                    song = song,
                                    onDismiss = menuState::hide
                                )
                            }
                        },
                        onClick = {
                            playlistWithSongs?.songs
                                ?.map(Song::asMediaItem)
                                ?.let { mediaItems ->
                                    binder?.stopRadio()
                                    binder?.player?.forcePlayAtIndex(
                                        mediaItems,
                                        index
                                    )
                                }
                        }
                    )
                    .draggedItem(
                        reorderingState = reorderingState,
                        index = index
                    )
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .reorder(
                            reorderingState = reorderingState,
                            index = index
                        )
                        .size(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Reorder,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
