package it.vfsfitvnm.vimusic.ui.screens.search

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.compose.persist.persist
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.innertube.utils.plus
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.ui.components.ShimmerHost
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ExperimentalAnimationApi
@Composable
inline fun <T : Innertube.Item> ItemsPage(
    tag: String,
    crossinline itemContent: @Composable LazyGridItemScope.(T) -> Unit,
    noinline itemPlaceholderContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    initialPlaceholderCount: Int = 16,
    continuationPlaceholderCount: Int = 6,
    emptyItemsText: String = stringResource(id = R.string.no_items_found),
    noinline itemsPageProvider: (suspend (String?) -> Result<Innertube.ItemsPage<T>?>?)? = null,
) {
    val updatedItemsPageProvider by rememberUpdatedState(itemsPageProvider)
    val lazyGridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    var itemsPage by persist<Innertube.ItemsPage<T>?>(tag)

    LaunchedEffect(lazyGridState, updatedItemsPageProvider) {
        val currentItemsPageProvider = updatedItemsPageProvider ?: return@LaunchedEffect

        snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo.any { it.key == "loading0" } }
            .collect { shouldLoadMore ->
                if (!shouldLoadMore) return@collect

                withContext(Dispatchers.IO) {
                    currentItemsPageProvider(itemsPage?.continuation)
                }?.onSuccess {
                    if (it == null) {
                        if (itemsPage == null) {
                            itemsPage = Innertube.ItemsPage(items = null, continuation = null)
                        }
                    } else {
                        itemsPage += it
                        if (itemsPage == it) coroutineScope.launch {
                            lazyGridState.scrollToItem(index = 0)
                        }
                    }
                }
            }
    }

    val listLayout = tag.contains("songs") || tag.contains("videos")

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Adaptive(
            minSize = if (listLayout) 350.dp else 150.dp
        ),
        contentPadding = PaddingValues(
            start = if (listLayout) 0.dp else 8.dp,
            top = 8.dp,
            end = if (listLayout) 0.dp else 8.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(if (listLayout) 0.dp else 4.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = itemsPage?.items ?: emptyList(),
            key = Innertube.Item::key,
            itemContent = itemContent
        )

        if (itemsPage != null && itemsPage?.items.isNullOrEmpty()) {
            item(
                key = "empty",
                span = { GridItemSpan(maxCurrentLineSpan) }
            ) {
                Text(
                    text = emptyItemsText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                        .fillMaxWidth()
                        .alpha(Dimensions.mediumOpacity)
                )
            }
        }

        if (itemsPage == null || itemsPage?.continuation != null) {
            val isFirstLoad = itemsPage?.items.isNullOrEmpty()

            items(
                count = if (isFirstLoad) initialPlaceholderCount else continuationPlaceholderCount,
                key = { "loading$it" }
            ) {
                ShimmerHost {
                    itemPlaceholderContent()
                }
            }
        }
    }
}