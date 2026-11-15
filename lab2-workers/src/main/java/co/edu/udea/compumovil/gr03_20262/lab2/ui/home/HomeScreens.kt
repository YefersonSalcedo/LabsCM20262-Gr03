/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.edu.udea.compumovil.gr03_20262.lab2.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import co.edu.udea.compumovil.gr03_20262.lab2.R
import co.edu.udea.compumovil.gr03_20262.lab2.data.Result
import co.edu.udea.compumovil.gr03_20262.lab2.data.posts.PostsRepository
import co.edu.udea.compumovil.gr03_20262.lab2.data.posts.impl.BlockingFakePostsRepository
import co.edu.udea.compumovil.gr03_20262.lab2.deeplink.util.DeepLinkPattern
import co.edu.udea.compumovil.gr03_20262.lab2.model.Post
import co.edu.udea.compumovil.gr03_20262.lab2.model.PostsFeed
import co.edu.udea.compumovil.gr03_20262.lab2.ui.components.JetnewsSnackbarHost
import co.edu.udea.compumovil.gr03_20262.lab2.ui.navigation.ListDetailScene
import co.edu.udea.compumovil.gr03_20262.lab2.ui.theme.JetnewsTheme
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

val HomeDeepLinkPattern = DeepLinkPattern(
    HomeKey.serializer(),
    uriPattern = "https://developer.android.com/jetnews".toUri(),
)

fun EntryProviderScope<NavKey>.homeEntry(
    postsRepository: PostsRepository,
    isExpandedScreen: () -> Boolean,
    openDrawer: () -> Unit,
    navigateToPost: (String) -> Unit,
) {
    entry<HomeKey>(
        metadata = ListDetailScene.list(
            ListDetailScene.ListConfiguration(
                modifier = Modifier.width(334.dp),
                detailPlaceholder = {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                stringResource(R.string.home_detail_placeholder),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                },
            ),
        ),
    ) {
        val homeViewModel: HomeViewModel =
            viewModel(factory = HomeViewModel.provideFactory(postsRepository))

        HomeScreen(
            homeViewModel = homeViewModel,
            isExpandedScreen = isExpandedScreen(),
            openDrawer = openDrawer,
            navigateToPost = navigateToPost,
        )
    }
}


@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    navigateToPost: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    // UiState of the HomeScreen
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        homeViewModel.refreshPosts()
    }

    HomeFeedScreen(
        uiState = uiState,
        showTopAppBar = !isExpandedScreen,
        onToggleFavorite = { homeViewModel.toggleFavourite(it) },
        onSelectPost = { navigateToPost(it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        openDrawer = openDrawer,
        homeListLazyListState = rememberLazyListState(),
        snackbarHostState = snackbarHostState,
    )
}


@Composable
fun HomeFeedScreen(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    HomeScreenWithList(
        uiState = uiState,
        showTopAppBar = showTopAppBar,
        onRefreshPosts = onRefreshPosts,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) { hasPostsUiState, contentPadding, contentModifier ->
        PostList(
            postsFeed = hasPostsUiState.postsFeed,
            favorites = hasPostsUiState.favorites,
            onPostTapped = onSelectPost,
            onToggleFavorite = onToggleFavorite,
            contentPadding = contentPadding,
            modifier = contentModifier,
            state = homeListLazyListState,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenWithList(
    uiState: HomeUiState,
    showTopAppBar: Boolean,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    hasPostsContent: @Composable (
        uiState: HomeUiState.HasPosts,
        contentPadding: PaddingValues,
        modifier: Modifier,
    ) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    Scaffold(
        snackbarHost = { JetnewsSnackbarHost(hostState = snackbarHostState) },
        topBar = {
            HomeTopAppBar(
                openDrawer = openDrawer,
                onSyncCompleted = onRefreshPosts,
                showNavigationIcon = showTopAppBar,
                topAppBarState = topAppBarState,
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        val contentModifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)

        LoadingContent(
            modifier = Modifier.padding(innerPadding),
            empty = when (uiState) {
                is HomeUiState.HasPosts -> false
                is HomeUiState.NoPosts -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshPosts,
            content = {
                when (uiState) {
                    is HomeUiState.HasPosts ->
                        hasPostsContent(uiState, innerPadding, contentModifier)

                    is HomeUiState.NoPosts -> {
                        if (uiState.errorMessages.isEmpty()) {
                            // if there are no posts, and no error, let the user refresh manually
                            TextButton(
                                onClick = onRefreshPosts,
                                modifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    stringResource(id = R.string.home_tap_to_load_content),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        } else {
                            // there's currently an error showing, don't show any content
                            Box(
                                contentModifier
                                    .padding(innerPadding)
                                    .fillMaxSize(),
                            ) { /* empty screen */ }
                        }
                    }
                }
            },
        )
    }

    // Process one error message at a time and show them as Snackbars in the UI
    if (uiState.errorMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage = remember(uiState) { uiState.errorMessages[0] }

        // Get the text to show on the message from resources
        val errorMessageText: String = stringResource(errorMessage.messageId)
        val retryMessageText = stringResource(id = R.string.retry)

        // Use latest callbacks without restarting the effect
        val onRefreshPostsState by rememberUpdatedState(onRefreshPosts)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        // Show one snackbar at a time; restart if inputs change
        LaunchedEffect(errorMessageText, retryMessageText, snackbarHostState) {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText,
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (empty) {
        emptyContent()
    } else {
        val refreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = loading,
            onRefresh = onRefresh,
            content = { content() },
            state = refreshState,
            indicator = {
                Indicator(
                    modifier = modifier
                        .align(Alignment.TopCenter)
                        .padding(),
                    isRefreshing = loading,
                    state = refreshState,
                )
            },
        )
    }
}


@Composable
private fun PostList(
    postsFeed: PostsFeed,
    favorites: Set<String>,
    onPostTapped: (postId: String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state,
    ) {
        item { PostListTopSection(postsFeed.highlightedPost, onPostTapped) }
        if (postsFeed.recommendedPosts.isNotEmpty()) {
            item {
                PostListSimpleSection(
                    postsFeed.recommendedPosts,
                    onPostTapped,
                    favorites,
                    onToggleFavorite,
                )
            }
        }
        if (postsFeed.popularPosts.isNotEmpty()) {
            item {
                PostListPopularSection(
                    postsFeed.popularPosts, onPostTapped,
                )
            }
        }
        if (postsFeed.recentPosts.isNotEmpty()) {
            item { PostListHistorySection(postsFeed.recentPosts, onPostTapped) }
        }
    }
}


@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
    ) {
        CircularProgressIndicator()
    }
}


@Composable
private fun PostListTopSection(post: Post, navigateToPost: (String) -> Unit) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.home_top_section_title),
        style = MaterialTheme.typography.titleMedium,
    )
    PostCardTop(
        post = post,
        modifier = Modifier.clickable(onClick = { navigateToPost(post.id) }),
    )
    PostListDivider()
}


@Composable
private fun PostListSimpleSection(
    posts: List<Post>,
    navigateToPost: (String) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
) {
    Column {
        posts.forEach { post ->
            PostCardSimple(
                post = post,
                navigateToPost = navigateToPost,
                isFavorite = favorites.contains(post.id),
                onToggleFavorite = { onToggleFavorite(post.id) },
            )
            PostListDivider()
        }
    }
}


@Composable
private fun PostListPopularSection(posts: List<Post>, navigateToPost: (String) -> Unit) {
    Column {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.home_popular_section_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .height(IntrinsicSize.Max)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (post in posts) {
                PostCardPopular(
                    post,
                    navigateToPost,
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        PostListDivider()
    }
}


@Composable
private fun PostListHistorySection(posts: List<Post>, navigateToPost: (String) -> Unit) {
    Column {
        posts.forEach { post ->
            PostCardHistory(post, navigateToPost)
            PostListDivider()
        }
    }
}


@Composable
private fun PostListDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    openDrawer: () -> Unit,
    onSyncCompleted: () -> Unit,
    showNavigationIcon: Boolean,
    modifier: Modifier = Modifier,
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState),
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                maxLines = 1,
            )
        },
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(onClick = openDrawer) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_jetnews_logo,
                        ),
                        contentDescription = stringResource(
                            R.string.cd_open_navigation_drawer,
                        ),
                    )
                }
            }
        },
        actions = {
            SyncNowButton(
                onSyncCompleted = onSyncCompleted,
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}

@Preview("Home list drawer screen")
@Preview("Home list drawer screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Preview("Home list drawer screen (big font)", fontScale = 1.5f)
@Composable
fun PreviewHomeListDrawerScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
            ),
            showTopAppBar = false,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview("Home list navrail screen", device = Devices.NEXUS_7_2013)
@Preview(
    "Home list navrail screen (dark)",
    uiMode = UI_MODE_NIGHT_YES,
    device = Devices.NEXUS_7_2013,
)
@Preview("Home list navrail screen (big font)", fontScale = 1.5f, device = Devices.NEXUS_7_2013)
@Composable
fun PreviewHomeListNavRailScreen() {
    val postsFeed = runBlocking {
        (BlockingFakePostsRepository().getPostsFeed() as Result.Success).data
    }
    JetnewsTheme {
        HomeFeedScreen(
            uiState = HomeUiState.HasPosts(
                postsFeed = postsFeed,
                favorites = emptySet(),
                isLoading = false,
                errorMessages = emptyList(),
            ),
            showTopAppBar = true,
            onToggleFavorite = {},
            onSelectPost = {},
            onRefreshPosts = {},
            onErrorDismiss = {},
            openDrawer = {},
            homeListLazyListState = rememberLazyListState(),
            snackbarHostState = SnackbarHostState(),
        )
    }
}
