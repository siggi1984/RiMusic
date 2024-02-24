package it.vfsfitvnm.vimusic.ui.screens.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import it.vfsfitvnm.compose.persist.persistList
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.enums.ArtistSortBy
import it.vfsfitvnm.vimusic.enums.SortOrder
import it.vfsfitvnm.vimusic.models.Artist
import it.vfsfitvnm.vimusic.ui.items.ArtistItem
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.px
import it.vfsfitvnm.vimusic.utils.artistSortByKey
import it.vfsfitvnm.vimusic.utils.artistSortOrderKey
import it.vfsfitvnm.vimusic.utils.rememberPreference

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun HomeArtistList(
    onArtistClick: (Artist) -> Unit
) {
    var sortBy by rememberPreference(artistSortByKey, ArtistSortBy.Name)
    var sortOrder by rememberPreference(artistSortOrderKey, SortOrder.Ascending)

    var items by persistList<Artist>("home/artists")

    LaunchedEffect(sortBy, sortOrder) {
        Database.artists(sortBy, sortOrder).collect { items = it }
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

    LazyVerticalGrid(
        columns = GridCells.Adaptive(Dimensions.thumbnails.song * 2 + Dimensions.itemsVerticalPadding * 2),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(Dimensions.itemsVerticalPadding * 2),
        horizontalArrangement = Arrangement.spacedBy(
            space = Dimensions.itemsVerticalPadding * 2,
            alignment = Alignment.CenterHorizontally
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        item(
            key = "header",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { isSorting = true }
                ) {
                    Text(
                        text = when (sortBy) {
                            ArtistSortBy.Name -> stringResource(id = R.string.name)
                            ArtistSortBy.DateAdded -> stringResource(id = R.string.date_added)
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
                    if (items.size == 1) "1 ${stringResource(id = R.string.artist).lowercase()}"
                    else "${items.size} ${stringResource(id = R.string.artists).lowercase()}",
                    style = MaterialTheme.typography.labelLarge
                )

                DropdownMenu(
                    expanded = isSorting,
                    onDismissRequest = { isSorting = false }
                ) {
                    ArtistSortBy.entries.forEach { entry ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = when (entry) {
                                        ArtistSortBy.Name -> stringResource(id = R.string.name)
                                        ArtistSortBy.DateAdded -> stringResource(id = R.string.date_added)
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

        items(items = items, key = Artist::id) { artist ->
            ArtistItem(
                artist = artist,
                thumbnailSizePx = thumbnailSizePx,
                thumbnailSizeDp = thumbnailSizeDp,
                alternative = true,
                modifier = Modifier
                    .clickable(onClick = { onArtistClick(artist) })
                    .animateItemPlacement()
            )
        }
    }
}
