package it.vfsfitvnm.vimusic.ui.screens.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.compose.persist.persist
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.enums.AlbumSortBy
import it.vfsfitvnm.vimusic.enums.SortOrder
import it.vfsfitvnm.vimusic.models.Album
import it.vfsfitvnm.vimusic.ui.items.AlbumItem
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.px
import it.vfsfitvnm.vimusic.utils.albumSortByKey
import it.vfsfitvnm.vimusic.utils.albumSortOrderKey
import it.vfsfitvnm.vimusic.utils.rememberPreference

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun HomeAlbums(
    onAlbumClick: (Album) -> Unit
) {
    var sortBy by rememberPreference(albumSortByKey, AlbumSortBy.Title)
    var sortOrder by rememberPreference(albumSortOrderKey, SortOrder.Ascending)

    var items by persist<List<Album>>(tag = "home/albums", emptyList())

    LaunchedEffect(sortBy, sortOrder) {
        Database.albums(sortBy, sortOrder).collect { items = it }
    }

    val thumbnailSizeDp = Dimensions.thumbnails.song * 2
    val thumbnailSizePx = thumbnailSizeDp.px

    val sortOrderIconRotation by animateFloatAsState(
        targetValue = if (sortOrder == SortOrder.Ascending) 0f else 180f,
        label = "rotation"
    )

    var isSorting by rememberSaveable {
        mutableStateOf(false)
    }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(key = "header") {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { isSorting = true }
                ) {
                    Text(
                        text = when (sortBy) {
                            AlbumSortBy.Title -> stringResource(id = R.string.title)
                            AlbumSortBy.Year -> stringResource(id = R.string.year)
                            AlbumSortBy.DateAdded -> stringResource(id = R.string.date_added)
                        }
                    )
                }

                IconButton(
                    onClick = { sortOrder = !sortOrder },
                    modifier = Modifier.graphicsLayer { rotationZ = sortOrderIconRotation }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1F))

                Text(
                    text =
                    if (items.size == 1) "1 ${stringResource(id = R.string.album).lowercase()}"
                    else "${items.size} ${stringResource(id = R.string.albums).lowercase()}",
                    style = MaterialTheme.typography.labelLarge
                )

                DropdownMenu(
                    expanded = isSorting,
                    onDismissRequest = { isSorting = false }
                ) {
                    AlbumSortBy.entries.forEach { entry ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = when (entry) {
                                        AlbumSortBy.Title -> stringResource(id = R.string.title)
                                        AlbumSortBy.Year -> stringResource(id = R.string.year)
                                        AlbumSortBy.DateAdded -> stringResource(id = R.string.date_added)
                                    }
                                )
                            },
                            onClick = {
                                isSorting = false
                                sortBy = entry
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = entry.icon,
                                    contentDescription = entry.name
                                )
                            },
                            trailingIcon = {
                                RadioButton(
                                    selected = sortBy == entry,
                                    onClick = { sortBy = entry }
                                )
                            }
                        )
                    }
                }
            }
        }

        items(
            items = items,
            key = Album::id
        ) { album ->
            AlbumItem(
                album = album,
                thumbnailSizePx = thumbnailSizePx,
                thumbnailSizeDp = thumbnailSizeDp,
                modifier = Modifier
                    .clickable(onClick = { onAlbumClick(album) })
                    .animateItemPlacement()
            )
        }
    }
}