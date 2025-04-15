package it.fast4x.rimusic.utils

import it.fast4x.rimusic.R
import it.fast4x.rimusic.enums.QueueLoopType

fun setQueueLoopState(currentState: QueueLoopType): QueueLoopType {
    val newState =
        when (currentState) {
            QueueLoopType.Default -> QueueLoopType.RepeatOne
            QueueLoopType.RepeatOne -> QueueLoopType.RepeatAll
            else -> QueueLoopType.Default
        }

    return newState
}

fun getIconQueueLoopState(currentState: QueueLoopType): Int {
    val current =
        when (currentState) {
            QueueLoopType.Default -> R.drawable.repeat
            QueueLoopType.RepeatOne -> R.drawable.repeatone
            else -> R.drawable.infinite
        }

    return current
}

