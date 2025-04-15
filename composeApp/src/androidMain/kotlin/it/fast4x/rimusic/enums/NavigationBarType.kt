package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import it.fast4x.rimusic.utils.navigationBarTypeKey
import it.fast4x.rimusic.utils.rememberPreference


enum class NavigationBarType {
    IconAndText,
    IconOnly;

    companion object {

        @Composable
        fun current(): NavigationBarType = rememberPreference( navigationBarTypeKey, NavigationBarType.IconAndText ).value
    }

    @Composable
    fun isCurrent(): Boolean = current() == this
}