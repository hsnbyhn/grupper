package com.grupper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupper.data.model.Group
import com.grupper.data.model.PostSortOrder
import com.grupper.data.model.Tag
import com.grupper.ui.components.EmptyStateView
import com.grupper.ui.components.LoadingScreen
import com.grupper.ui.components.NetworkErrorView
import com.grupper.ui.components.PostCard
import com.grupper.ui.components.TagChip
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing
import com.grupper.viewmodel.GroupDetailViewModel

/**
 * Group Detail Screen
 * Based on Designer Agent's design (DESIGN-006)
 *
 * Features:
 * - Sticky group header
 * - Horizontal tag filtering
 * - Posts list with sorting
 * - Pull to refresh
 * - FAB for create post
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: Long,
    onBackClick: () -> Unit,
    onPostClick: (Long) -> Unit,
    onCreatePostClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupDetailViewModel = viewModel { GroupDetailViewModel(groupId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.group?.name ?: "Group",
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = GrupperIcons.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Sort dropdown
                    Box {
                        TextButton(onClick = { showSortMenu = true }) {
                            Text(
                                text = when (uiState.sortOrder) {
                                    PostSortOrder.NEWEST -> "Newest"
                                    PostSortOrder.OLDEST -> "Oldest"
                                    PostSortOrder.MOST_COMMENTED -> "Most Commented"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = GrupperIcons.ArrowBack, // TODO: Need ArrowDown icon
                                contentDescription = "Sort",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Newest") },
                                onClick = {
                                    viewModel.changeSortOrder(PostSortOrder.NEWEST)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Oldest") },
                                onClick = {
                                    viewModel.changeSortOrder(PostSortOrder.OLDEST)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Most Commented") },
                                onClick = {
                                    viewModel.changeSortOrder(PostSortOrder.MOST_COMMENTED)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && !uiState.hasError) {
                FloatingActionButton(
                    onClick = onCreatePostClick,
                    shape = GrupperShapes.Large,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = GrupperIcons.Add,
                        contentDescription = "Create post"
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // Loading state
                uiState.isLoading -> {
                    LoadingScreen(message = "Loading group...")
                }

                // Error state
                uiState.hasError -> {
                    NetworkErrorView(
                        onRetry = { viewModel.loadGroupDetail() }
                    )
                }

                // Content state
                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.refreshGroup() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                        ) {
                            // Group header
                            item {
                                uiState.group?.let { group ->
                                    GroupHeader(group = group)
                                }
                            }

                            // Tags row (sticky-like by being in header)
                            item {
                                TagsRow(
                                    tags = uiState.tags,
                                    selectedTagId = uiState.selectedTagId,
                                    onTagClick = { tagId -> viewModel.selectTag(tagId) }
                                )
                            }

                            // Empty state
                            if (uiState.isEmpty) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        EmptyStateView(
                                            title = if (uiState.hasNoPostsWithTag) {
                                                "No posts with this tag"
                                            } else {
                                                "No posts yet"
                                            },
                                            message = if (uiState.hasNoPostsWithTag) {
                                                "Try selecting a different tag or create the first post"
                                            } else {
                                                "Be the first to start a discussion"
                                            },
                                            actionLabel = "Create Post",
                                            onAction = onCreatePostClick
                                        )
                                    }
                                }
                            } else {
                                // Posts list
                                items(
                                    items = uiState.filteredPosts,
                                    key = { it.id }
                                ) { post ->
                                    PostCard(
                                        post = post,
                                        onClick = { onPostClick(post.id) },
                                        modifier = Modifier.padding(
                                            horizontal = GrupperSpacing.Default,
                                            vertical = GrupperSpacing.Small
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Group Header Component
 * Shows group image, name, description, and stats
 */
@Composable
private fun GroupHeader(
    group: Group,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(GrupperSpacing.Default)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Default)
        ) {
            // Group avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getInitials(group.name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Group info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(GrupperSpacing.XSmall))

                Text(
                    text = group.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(GrupperSpacing.Small))

                // Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Medium)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.XSmall)
                    ) {
                        Icon(
                            imageVector = GrupperIcons.Person,
                            contentDescription = null,
                            modifier = Modifier.size(GrupperSpacing.IconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatCount(group.memberCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.XSmall)
                    ) {
                        Icon(
                            imageVector = GrupperIcons.Message,
                            contentDescription = null,
                            modifier = Modifier.size(GrupperSpacing.IconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${formatCount(group.postCount)} posts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tags Row Component
 * Horizontal scrollable list of tag chips for filtering
 */
@Composable
private fun TagsRow(
    tags: List<Tag>,
    selectedTagId: Long?,
    onTagClick: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = GrupperSpacing.Small),
        contentPadding = PaddingValues(horizontal = GrupperSpacing.Default),
        horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
    ) {
        // "All" chip
        item {
            TagChip(
                name = "All",
                color = MaterialTheme.colorScheme.primary,
                isSelected = selectedTagId == null,
                onClick = { onTagClick(null) }
            )
        }

        // Tag chips
        items(tags, key = { it.id }) { tag ->
            TagChip(
                name = tag.name,
                color = Color(parseColor(tag.color)),
                isSelected = selectedTagId == tag.id,
                onClick = { onTagClick(tag.id) }
            )
        }
    }
}

/**
 * Parse color hex string to Long for Color constructor
 */
private fun parseColor(colorHex: String): Long {
    val hex = colorHex.removePrefix("#")
    return ("FF" + hex).toLong(16)
}

/**
 * Get initials from group name (up to 2 characters)
 */
private fun getInitials(name: String): String {
    return name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "G" }
}

/**
 * Format count with K/M suffixes
 */
private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}
