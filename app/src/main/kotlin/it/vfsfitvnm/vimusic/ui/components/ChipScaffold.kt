package it.vfsfitvnm.vimusic.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import it.vfsfitvnm.vimusic.models.Section

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun ChipScaffold(
    topIconButtonId: ImageVector,
    onTopIconButtonClick: () -> Unit,
    sectionTitle: String? = null,
    tabIndex: Int,
    onTabChanged: (Int) -> Unit,
    tabColumnContent: List<Section>,
    content: @Composable (AnimatedVisibilityScope.(Int) -> Unit)
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                    }
                )

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabColumnContent.forEachIndexed { index, section ->
                        FilterChip(
                            selected = index == tabIndex,
                            onClick = { onTabChanged(index) },
                            label = {
                                Text(text = section.title)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = section.icon,
                                    contentDescription = section.title
                                )
                            }
                        )
                    }
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

                    val animationSpec = spring(
                        dampingRatio = 0.9f,
                        stiffness = Spring.StiffnessLow,
                        visibilityThreshold = IntOffset.VisibilityThreshold
                    )

                    slideIntoContainer(slideDirection, animationSpec) togetherWith
                            slideOutOfContainer(slideDirection, animationSpec)
                },
                content = content,
                label = ""
            )
        }
    }
}
