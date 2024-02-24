package it.vfsfitvnm.vimusic.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import it.vfsfitvnm.vimusic.models.Section

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun TabGroup(
    tabIndex: Int,
    crossinline onTabIndexChanged: (Int) -> Unit,
    content: List<Section>
) {
    PrimaryScrollableTabRow(
        selectedTabIndex = tabIndex
    ) {
        content.forEachIndexed { index, section ->
            LeadingIconTab(
                selected = index == tabIndex,
                onClick = { onTabIndexChanged(index) },
                text = { Text(text = section.title) },
                icon = {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = section.title
                    )
                },
                unselectedContentColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}