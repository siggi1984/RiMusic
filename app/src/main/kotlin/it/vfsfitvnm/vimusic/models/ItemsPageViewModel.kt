package it.vfsfitvnm.vimusic.models

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.innertube.utils.plus

class ItemsPageViewModel<T : Innertube.Item> : ViewModel() {
    var itemsMap: MutableMap<String, Innertube.ItemsPage<T>?> = mutableStateMapOf()

    fun setItems(
        tag: String,
        items: Innertube.ItemsPage<T>?
    ) {
        if (!itemsMap.containsKey(tag)) itemsMap[tag] = items
        else itemsMap[tag] += items!!
    }
}