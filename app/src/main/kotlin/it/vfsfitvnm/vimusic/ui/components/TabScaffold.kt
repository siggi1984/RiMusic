package it.vfsfitvnm.vimusic.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import it.vfsfitvnm.vimusic.models.Section

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun TabScaffold(
    topIconButtonId: ImageVector,
    onTopIconButtonClick: () -> Unit,
    sectionTitle: String? = null,
    appBarActions: @Composable (() -> Unit)? = null,
    tabIndex: Int,
    onTabChanged: (Int) -> Unit,
    tabColumnContent: List<Section>,
    content: @Composable (AnimatedVisibilityScope.(Int) -> Unit)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = sectionTitle ?: tabColumnContent[tabIndex].title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onTopIconButtonClick) {
                            Icon(
                                imageVector = topIconButtonId,
                                contentDescription = null
                            )
                        }
                    },
                    actions = { appBarActions?.invoke() },
                    scrollBehavior = scrollBehavior
                )

                if (tabColumnContent.size > 1) {
                    TabGroup(
                        tabIndex = tabIndex,
                        onTabIndexChanged = onTabChanged,
                        content = tabColumnContent
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = tabIndex,
                transitionSpec = {
                    val slideDirection = when (targetState > initialState) {
                        true -> AnimatedContentTransitionScope.SlideDirection.Left
                        false -> AnimatedContentTransitionScope.SlideDirection.Right
                    }

                    slideIntoContainer(slideDirection) togetherWith slideOutOfContainer(
                        slideDirection
                    )
                },
                content = content,
                label = "tabs"
            )
        }
    }
}