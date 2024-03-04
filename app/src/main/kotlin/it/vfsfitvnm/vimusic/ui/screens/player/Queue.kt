package it.vfsfitvnm.vimusic.ui.screens.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Reorder
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import com.valentinilk.shimmer.shimmer
import it.vfsfitvnm.compose.reordering.draggedItem
import it.vfsfitvnm.compose.reordering.rememberReorderingState
import it.vfsfitvnm.compose.reordering.reorder
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.LocalMenuState
import it.vfsfitvnm.vimusic.ui.components.MusicBars
import it.vfsfitvnm.vimusic.ui.components.themed.QueuedMediaItemMenu
import it.vfsfitvnm.vimusic.ui.items.ListItemPlaceholder
import it.vfsfitvnm.vimusic.ui.items.MediaSongItem
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.onOverlay
import it.vfsfitvnm.vimusic.utils.DisposableListener
import it.vfsfitvnm.vimusic.utils.queueLoopEnabledKey
import it.vfsfitvnm.vimusic.utils.rememberPreference
import it.vfsfitvnm.vimusic.utils.shouldBePlaying
import it.vfsfitvnm.vimusic.utils.shuffleQueue
import it.vfsfitvnm.vimusic.utils.windows
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Queue() {
    val binder = LocalPlayerServiceBinder.current

    binder?.player ?: return

    val player = binder.player

    var queueLoopEnabled by rememberPreference(queueLoopEnabledKey, defaultValue = false)

    val menuState = LocalMenuState.current

    var mediaItemIndex by remember {
        mutableIntStateOf(if (player.mediaItemCount == 0) -1 else player.currentMediaItemIndex)
    }

    var windows by remember {
        mutableStateOf(player.currentTimeline.windows)
    }

    var shouldBePlaying by remember {
        mutableStateOf(binder.player.shouldBePlaying)
    }

    player.DisposableListener {
        object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItemIndex =
                    if (player.mediaItemCount == 0) -1 else player.currentMediaItemIndex
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                windows = timeline.windows
                mediaItemIndex =
                    if (player.mediaItemCount == 0) -1 else player.currentMediaItemIndex
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                shouldBePlaying = binder.player.shouldBePlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                shouldBePlaying = binder.player.shouldBePlaying
            }
        }
    }

    val reorderingState = rememberReorderingState(
        lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = mediaItemIndex),
        key = windows,
        onDragEnd = player::moveMediaItem,
        extraItemCount = 0
    )

    val musicBarsTransition = updateTransition(targetState = mediaItemIndex, label = "")

    Column {
        LazyColumn(
            state = reorderingState.lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1F)
        ) {
            items(
                items = windows,
                key = { it.uid.hashCode() }
            ) { window ->
                val isPlayingThisMediaItem = mediaItemIndex == window.firstPeriodIndex

                MediaSongItem(
                    modifier = Modifier.draggedItem(
                        reorderingState = reorderingState,
                        index = window.firstPeriodIndex
                    ),
                    song = window.mediaItem,
                    onClick = {
                        if (isPlayingThisMediaItem) {
                            if (shouldBePlaying) {
                                player.pause()
                            } else {
                                player.play()
                            }
                        } else {
                            player.seekToDefaultPosition(window.firstPeriodIndex)
                            player.playWhenReady = true
                        }
                    },
                    onLongClick = {
                        menuState.display {
                            QueuedMediaItemMenu(
                                mediaItem = window.mediaItem,
                                indexInQueue = if (isPlayingThisMediaItem) null else window.firstPeriodIndex,
                                onDismiss = menuState::hide
                            )
                        }
                    },
                    onThumbnailContent = {
                        musicBarsTransition.AnimatedVisibility(
                            visible = { it == window.firstPeriodIndex },
                            enter = fadeIn(tween(800)),
                            exit = fadeOut(tween(800)),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        color = Color.Black.copy(alpha = 0.25F),
                                        shape = MaterialTheme.shapes.medium
                                    )
                            ) {
                                if (shouldBePlaying) {
                                    MusicBars(
                                        color = MaterialTheme.colorScheme.onOverlay,
                                        modifier = Modifier
                                            .height(24.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onOverlay
                                    )
                                }
                            }
                        }
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Reorder,
                            contentDescription = null,
                            modifier = Modifier
                                .reorder(
                                    reorderingState = reorderingState,
                                    index = window.firstPeriodIndex
                                )
                        )
                    }
                )
            }

            item {
                if (binder.isLoadingRadio) {
                    Column(
                        modifier = Modifier
                            .shimmer()
                    ) {
                        repeat(3) { index ->
                            ListItemPlaceholder(
                                modifier = Modifier.alpha(1f - index * 0.125f)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text =
                if (windows.size == 1) "1 ${stringResource(id = R.string.song).lowercase()}"
                else "${windows.size} ${stringResource(id = R.string.songs).lowercase()}",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 8.dp)
            )

            Row {
                IconButton(
                    onClick = {
                        reorderingState.coroutineScope.launch {
                            reorderingState.lazyListState.animateScrollToItem(0)
                        }.invokeOnCompletion {
                            player.shuffleQueue()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shuffle,
                        contentDescription = null
                    )
                }

                IconButton(onClick = { queueLoopEnabled = !queueLoopEnabled }) {
                    Icon(
                        imageVector = Icons.Outlined.Repeat,
                        contentDescription = null,
                        modifier = Modifier.alpha(if (queueLoopEnabled) 1F else Dimensions.lowOpacity)
                    )
                }
            }
        }
    }
}