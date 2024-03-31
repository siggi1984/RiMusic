package it.vfsfitvnm.vimusic.ui.screens.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material.icons.outlined.Language
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.enums.QuickPicksSource
import it.vfsfitvnm.vimusic.utils.isAtLeastAndroid12
import it.vfsfitvnm.vimusic.utils.isAtLeastAndroid13
import it.vfsfitvnm.vimusic.utils.quickPicksSourceKey
import it.vfsfitvnm.vimusic.utils.rememberPreference
import it.vfsfitvnm.vimusic.utils.toast

@Composable
fun GeneralSettings() {
    val context = LocalContext.current
    var quickPicksSource by rememberPreference(quickPicksSourceKey, QuickPicksSource.Trending)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        EnumValueSelectorSettingsEntry(
            title = stringResource(id = R.string.quick_picks_source),
            selectedValue = quickPicksSource,
            onValueSelected = { quickPicksSource = it },
            icon = Icons.AutoMirrored.Outlined.List,
            valueText = { context.getString(it.resourceId) }
        )

        if (isAtLeastAndroid13) {
            val intent = Intent(
                Settings.ACTION_APP_LOCALE_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )

            SettingsEntry(
                title = stringResource(id = R.string.app_language),
                text = stringResource(id = R.string.configure_app_language),
                icon = Icons.Outlined.Language,
                onClick = {
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        context.toast("Couldn't find app language settings, please configure them manually")
                    }
                }
            )
        }

        if (isAtLeastAndroid12) {
            val intent = Intent(
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )

            SettingsEntry(
                title = stringResource(id = R.string.open_supported_links_by_default),
                text = stringResource(id = R.string.configure_supported_links),
                icon = Icons.Outlined.AddLink,
                onClick = {
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        context.toast("Couldn't find supported links settings, please configure them manually")
                    }
                }
            )

            SettingsInformation(text = stringResource(id = R.string.configure_supported_links_information))
        }
    }
}