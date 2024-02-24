package it.vfsfitvnm.vimusic.ui.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.vimusic.ui.components.themed.TextPlaceholder
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.onOverlay
import it.vfsfitvnm.vimusic.ui.styling.overlay
import it.vfsfitvnm.vimusic.ui.styling.shimmer

@Composable
fun VideoItem(
    video: Innertube.VideoItem,
    thumbnailHeightDp: Dp,
    thumbnailWidthDp: Dp,
    modifier: Modifier = Modifier
) {
    VideoItem(
        thumbnailUrl = video.thumbnail?.url,
        duration = video.durationText,
        title = video.info?.name,
        uploader = video.authors?.joinToString("") { it.name ?: "" },
        views = video.viewsText,
        thumbnailHeightDp = thumbnailHeightDp,
        thumbnailWidthDp = thumbnailWidthDp,
        modifier = modifier
    )
}

@Composable
fun VideoItem(
    thumbnailUrl: String?,
    duration: String?,
    title: String?,
    uploader: String?,
    views: String?,
    thumbnailHeightDp: Dp,
    thumbnailWidthDp: Dp,
    modifier: Modifier = Modifier
) {
    ItemContainer(
        alternative = false,
        thumbnailSizeDp = 0.dp,
        modifier = modifier
    ) {
        Box {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(Dimensions.thumbnailShape)
                    .size(width = thumbnailWidthDp, height = thumbnailHeightDp)
            )

            duration?.let {
                Text(
                    text = duration,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onOverlay,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(all = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.overlay,
                            shape = RoundedCornerShape(2.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        ItemInfoContainer {
            Text(
                text = title ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = uploader ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .alpha(Dimensions.mediumOpacity)
            )

            views?.let {
                Text(
                    text = views,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .alpha(Dimensions.mediumOpacity)
                )
            }
        }
    }
}

@Composable
fun VideoItemPlaceholder(
    thumbnailHeightDp: Dp,
    thumbnailWidthDp: Dp,
    modifier: Modifier = Modifier
) {
    ItemContainer(
        alternative = false,
        thumbnailSizeDp = 0.dp,
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.shimmer,
                    shape = Dimensions.thumbnailShape
                )
                .size(width = thumbnailWidthDp, height = thumbnailHeightDp)
        )

        ItemInfoContainer {
            TextPlaceholder()
            TextPlaceholder()
            TextPlaceholder(
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
    }
}
