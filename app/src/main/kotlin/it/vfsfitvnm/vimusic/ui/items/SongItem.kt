package it.vfsfitvnm.vimusic.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.vimusic.models.Song
import it.vfsfitvnm.vimusic.ui.components.themed.TextPlaceholder
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.shimmer
import it.vfsfitvnm.vimusic.utils.thumbnail

@Composable
fun SongItem(
    song: Innertube.SongItem,
    thumbnailSizePx: Int,
    modifier: Modifier = Modifier
) {
    SongItem(
        thumbnailUrl = song.thumbnail?.size(thumbnailSizePx),
        title = song.info?.name,
        authors = song.authors?.joinToString("") { it.name ?: "" },
        duration = song.durationText,
        modifier = modifier,
    )
}

@Composable
fun SongItem(
    song: MediaItem,
    thumbnailSizePx: Int,
    modifier: Modifier = Modifier,
    onThumbnailContent: @Composable (BoxScope.() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    SongItem(
        thumbnailUrl = song.mediaMetadata.artworkUri.thumbnail(thumbnailSizePx)?.toString(),
        title = song.mediaMetadata.title.toString(),
        authors = song.mediaMetadata.artist.toString(),
        duration = song.mediaMetadata.extras?.getString("durationText"),
        modifier = modifier,
        onThumbnailContent = onThumbnailContent,
        trailingContent = trailingContent,
    )
}

@Composable
fun SongItem(
    song: Song,
    thumbnailSizePx: Int,
    modifier: Modifier = Modifier,
    onThumbnailContent: @Composable (BoxScope.() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    SongItem(
        thumbnailUrl = song.thumbnailUrl?.thumbnail(thumbnailSizePx),
        title = song.title,
        authors = song.artistsText,
        duration = song.durationText,
        modifier = modifier,
        onThumbnailContent = onThumbnailContent,
        trailingContent = trailingContent,
    )
}

@Composable
fun SongItem(
    thumbnailUrl: String?,
    title: String?,
    authors: String?,
    duration: String?,
    modifier: Modifier = Modifier,
    onThumbnailContent: @Composable (BoxScope.() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    SongItem(
        thumbnailContent = {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(Dimensions.thumbnailShape)
                    .fillMaxSize()
            )

            onThumbnailContent?.invoke(this)
        },
        title = title,
        authors = authors,
        duration = duration,
        modifier = modifier,
        trailingContent = trailingContent
    )
}

@Composable
fun SongItem(
    thumbnailContent: @Composable (BoxScope.() -> Unit),
    title: String?,
    authors: String?,
    duration: String?,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        headlineContent = {
            Text(
                text = title ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        supportingContent = {
            Text(
                text = authors ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                thumbnailContent()
            }
        },
        trailingContent = {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                if (trailingContent != null) trailingContent()

                if (duration != null) Text(text = duration)
            }
        }
    )
}

@Composable
fun SongItemPlaceholder(
    thumbnailSizeDp: Dp,
    modifier: Modifier = Modifier
) {
    ItemContainer(
        alternative = false,
        thumbnailSizeDp = thumbnailSizeDp,
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.shimmer,
                    shape = Dimensions.thumbnailShape
                )
                .size(thumbnailSizeDp)
        )

        ItemInfoContainer {
            TextPlaceholder()
            TextPlaceholder()
        }
    }
}
