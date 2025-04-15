package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import it.fast4x.rimusic.utils.navigationBarPositionKey
import it.fast4x.rimusic.utils.rememberPreference

enum class NavigationBarPosition {
    Left,
    Right,
    Top,
    Bottom;

    companion object {

        @Composable
        fun current() = rememberPreference( navigationBarPositionKey, Bottom ).value
    }

    @Composable
    fun isCurrent(): Boolean = current() == this
}