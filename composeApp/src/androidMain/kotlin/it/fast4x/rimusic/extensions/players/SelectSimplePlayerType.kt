package it.fast4x.rimusic.extensions.players

import it.fast4x.rimusic.enums.AudioQualityFormat
import it.fast4x.rimusic.enums.StreamingPlayerType
import it.fast4x.rimusic.extensions.players.models.PlaybackData
import it.fast4x.rimusic.getStreamingPlayerType
import it.fast4x.rimusic.models.Format
import it.fast4x.rimusic.utils.isAtLeastAndroid8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun SelectSimplePlayerType(
    mediaId: String,
    playedFormat: Format?,
    audioQualityFormat: AudioQualityFormat
): Result<PlaybackData> {
    return runBlocking(Dispatchers.IO) {
        when (getStreamingPlayerType()) {
            StreamingPlayerType.Default -> {
                SimplePlayer.playerResponseForPlayback(
                    mediaId,
                    playedFormat = playedFormat,
                    audioQuality = audioQualityFormat
                )
            }

            StreamingPlayerType.Next -> {
                SimplePlayer.playerResponseForPlaybackWithPotoken(
                    mediaId,
                    playedFormat = playedFormat,
                    audioQuality = audioQualityFormat,
                )
            }

            StreamingPlayerType.Advanced -> {
                if (isAtLeastAndroid8) {
                    SimplePlayer.playerResponseForPlaybackWithWebPotoken(
                        mediaId,
                        playedFormat = playedFormat,
                        audioQuality = audioQualityFormat,
                    )
                } else {
                    SimplePlayer.playerResponseForPlaybackWithPotoken(
                        mediaId,
                        playedFormat = playedFormat,
                        audioQuality = audioQualityFormat,
                    )
                }
            }
        }
    }
}