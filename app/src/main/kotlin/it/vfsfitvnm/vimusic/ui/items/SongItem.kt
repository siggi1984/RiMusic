package it.vfsfitvnm.vimusic.ui.items

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.vimusic.models.Song
import it.vfsfitvnm.vimusic.ui.styling.px
import it.vfsfitvnm.vimusic.utils.thumbnail

@Composable
fun SongItem(
    modifier: Modifier = Modifier,
    song: Innertube.SongItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItemContainer(
        modifier = modifier,
        title = song.info?.name ?: "",
        subtitle = song.authors?.joinToString(separator = "") { it.name ?: "" },
        onClick = onClick,
        onLongClick = onLongClick,
        thumbnail = {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = song.thumbnail?.size(maxWidth.px),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        },
        trailingContent = trailingContent
    )
}

@Composable
fun LocalSongItem(
    modifier: Modifier = Modifier,
    song: Song,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    thumbnailContent: @Composable (() -> Unit)? = null,
    onThumbnailContent: @Composable (BoxScope.() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItemContainer(
        modifier = modifier,
        title = song.title,
        subtitle = "${song.artistsText} • ${song.durationText}",
        onClick = onClick,
        onLongClick = onLongClick,
        thumbnail = {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (thumbnailContent == null) {
                    AsyncImage(
                        model = song.thumbnailUrl?.thumbnail(maxWidth.px),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium)
                    )

                    onThumbnailContent?.invoke(this)
                } else {
                    thumbnailContent()
                }
            }
        },
        trailingContent = trailingContent
    )
}

@Composable
fun MediaSongItem(
    modifier: Modifier = Modifier,
    song: MediaItem,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onThumbnailContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItemContainer(
        modifier = modifier,
        title = song.mediaMetadata.title.toString(),
        subtitle = if (song.mediaMetadata.extras?.getString("durationText") == null) {
            song.mediaMetadata.artist.toString()
        } else {
            "${song.mediaMetadata.artist} • ${song.mediaMetadata.extras?.getString("durationText")}"
        },
        onClick = onClick,
        onLongClick = onLongClick,
        thumbnail = {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = song.mediaMetadata.artworkUri.thumbnail(maxWidth.px),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                )

                onThumbnailContent?.invoke()
            }
        },
        trailingContent = trailingContent
    )
}