package it.vfsfitvnm.vimusic.service

import androidx.media3.common.PlaybackException

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlayableFormatNotFoundException : PlaybackException(null, null, ERROR_CODE_REMOTE_ERROR)

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class UnplayableException : PlaybackException(null, null, ERROR_CODE_REMOTE_ERROR)

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class LoginRequiredException : PlaybackException(null, null, ERROR_CODE_REMOTE_ERROR)

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class VideoIdMismatchException : PlaybackException(null, null, ERROR_CODE_REMOTE_ERROR)