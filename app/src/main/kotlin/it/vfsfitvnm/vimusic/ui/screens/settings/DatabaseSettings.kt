package it.vfsfitvnm.vimusic.ui.screens.settings

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.internal
import it.vfsfitvnm.vimusic.path
import it.vfsfitvnm.vimusic.query
import it.vfsfitvnm.vimusic.service.PlayerService
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.utils.intent
import it.vfsfitvnm.vimusic.utils.toast
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.system.exitProcess

@ExperimentalAnimationApi
@Composable
fun DatabaseSettings() {
    val context = LocalContext.current

    val eventsCount by remember {
        Database.eventsCount().distinctUntilChanged()
    }.collectAsState(initial = 0)

    val backupLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.sqlite3")) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            query {
                Database.checkpoint()

                context.applicationContext.contentResolver.openOutputStream(uri)
                    ?.use { outputStream ->
                        FileInputStream(internal.path).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
            }
        }

    val restoreLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

            query {
                Database.checkpoint()
                internal.close()

                context.applicationContext.contentResolver.openInputStream(uri)
                    ?.use { inputStream ->
                        FileOutputStream(internal.path).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                context.stopService(context.intent<PlayerService>())
                exitProcess(0)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.cleanup),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        SettingsEntry(
            title = stringResource(id = R.string.reset_quick_picks),
            text = if (eventsCount > 0) {
                stringResource(id = R.string.delete_playback_events, eventsCount)
            } else {
                stringResource(id = R.string.quick_picks_cleared)
            },
            onClick = { query(Database::clearEvents) },
            isEnabled = eventsCount > 0
        )

        Spacer(modifier = Modifier.height(Dimensions.spacer))

        Text(
            text = stringResource(id = R.string.backup),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        SettingsEntry(
            title = stringResource(id = R.string.backup),
            text = stringResource(id = R.string.backup_description),
            onClick = {
                @SuppressLint("SimpleDateFormat")
                val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")

                try {
                    backupLauncher.launch("musicyou_${dateFormat.format(Date())}.db")
                } catch (e: ActivityNotFoundException) {
                    context.toast("Couldn't find an application to create documents")
                }
            }
        )

        SettingsInformation(text = stringResource(id = R.string.backup_information))

        Spacer(modifier = Modifier.height(Dimensions.spacer))

        Text(
            text = stringResource(id = R.string.restore),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        SettingsEntry(
            title = stringResource(id = R.string.restore),
            text = stringResource(id = R.string.restore_description),
            onClick = {
                try {
                    restoreLauncher.launch(
                        arrayOf(
                            "application/vnd.sqlite3",
                            "application/x-sqlite3",
                            "application/octet-stream"
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    context.toast("Couldn't find an application to open documents")
                }
            }
        )

        SettingsInformation(text = stringResource(id = R.string.restore_information))
    }
}
