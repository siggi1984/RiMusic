package it.vfsfitvnm.vimusic.ui.screens.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.ui.styling.Dimensions

@ExperimentalAnimationApi
@Composable
fun About() {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.social),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        SettingsEntry(
            title = stringResource(id = R.string.github),
            text = stringResource(id = R.string.view_source_code),
            onClick = {
                uriHandler.openUri("https://github.com/DanielSevillano/music-you")
            }
        )

        Spacer(modifier = Modifier.height(Dimensions.spacer))

        Text(
            text = stringResource(id = R.string.troubleshooting),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        SettingsEntry(
            title = stringResource(id = R.string.create_issue),
            text = stringResource(id = R.string.redirected_to_github),
            onClick = {
                uriHandler.openUri("https://github.com/DanielSevillano/music-you/issues/new/choose")
            }
        )
    }
}
