package it.vfsfitvnm.vimusic.ui.screens.localplaylist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import it.vfsfitvnm.compose.persist.PersistMapCleanup
import it.vfsfitvnm.compose.persist.persist
import it.vfsfitvnm.compose.routing.RouteHandler
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.innertube.models.bodies.BrowseBody
import it.vfsfitvnm.innertube.requests.playlistPage
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.models.PlaylistWithSongs
import it.vfsfitvnm.vimusic.models.SongPlaylistMap
import it.vfsfitvnm.vimusic.query
import it.vfsfitvnm.vimusic.transaction
import it.vfsfitvnm.vimusic.ui.components.themed.ConfirmationDialog
import it.vfsfitvnm.vimusic.ui.components.themed.TextFieldDialog
import it.vfsfitvnm.vimusic.ui.screens.globalRoutes
import it.vfsfitvnm.vimusic.utils.asMediaItem
import it.vfsfitvnm.vimusic.utils.completed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun LocalPlaylistScreen(playlistId: Long) {
    PersistMapCleanup(tagPrefix = "localPlaylist/$playlistId/")

    RouteHandler(listenToGlobalEmitter = true) {
        globalRoutes()

        host {
            var playlistWithSongs by persist<PlaylistWithSongs?>("localPlaylist/$playlistId/playlistWithSongs")
            var isRenaming by rememberSaveable {
                mutableStateOf(false)
            }
            var isDeleting by rememberSaveable {
                mutableStateOf(false)
            }

            LaunchedEffect(Unit) {
                Database.playlistWithSongs(playlistId).filterNotNull()
                    .collect { playlistWithSongs = it }
            }

            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = playlistWithSongs?.playlist?.name ?: "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = pop) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            if (!playlistWithSongs?.playlist?.browseId.isNullOrEmpty()) {
                                IconButton(
                                    onClick = {
                                        playlistWithSongs?.playlist?.browseId?.let { browseId ->
                                            transaction {
                                                runBlocking(Dispatchers.IO) {
                                                    withContext(Dispatchers.IO) {
                                                        Innertube.playlistPage(
                                                            BrowseBody(browseId = browseId)
                                                        )
                                                            ?.completed()
                                                    }
                                                }?.getOrNull()?.let { remotePlaylist ->
                                                    Database.clearPlaylist(playlistId)

                                                    remotePlaylist.songsPage
                                                        ?.items
                                                        ?.map(Innertube.SongItem::asMediaItem)
                                                        ?.onEach(Database::insert)
                                                        ?.mapIndexed { position, mediaItem ->
                                                            SongPlaylistMap(
                                                                songId = mediaItem.mediaId,
                                                                playlistId = playlistId,
                                                                position = position
                                                            )
                                                        }?.let(Database::insertSongPlaylistMaps)
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Sync,
                                        contentDescription = null
                                    )
                                }
                            }

                            IconButton(onClick = { isRenaming = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null
                                )
                            }

                            IconButton(onClick = { isDeleting = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { paddingValues ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LocalPlaylistSongs(
                        playlistId = playlistId,
                        playlistWithSongs = playlistWithSongs
                    )

                    if (isRenaming) {
                        TextFieldDialog(
                            title = stringResource(id = R.string.rename_playlist),
                            hintText = stringResource(id = R.string.playlist_name_hint),
                            initialTextInput = playlistWithSongs?.playlist?.name ?: "",
                            onDismiss = { isRenaming = false },
                            onDone = { text ->
                                query {
                                    playlistWithSongs?.playlist?.copy(name = text)
                                        ?.let(Database::update)
                                }
                            }
                        )
                    }

                    if (isDeleting) {
                        ConfirmationDialog(
                            title = stringResource(id = R.string.delete_playlist_dialog),
                            onDismiss = { isDeleting = false },
                            onConfirm = {
                                query {
                                    playlistWithSongs?.playlist?.let(Database::delete)
                                }
                                pop
                            }
                        )
                    }
                }
            }
        }
    }
}