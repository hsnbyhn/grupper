package com.grupper.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupper.ui.components.EmptyStateView
import com.grupper.ui.components.GroupCard
import com.grupper.ui.components.LoadingScreen
import com.grupper.ui.components.NetworkErrorView
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing
import com.grupper.viewmodel.GroupsListViewModel

/**
 * Groups List Screen - Home screen
 * Based on Designer Agent's design (DESIGN-004)
 *
 * Features:
 * - List of groups as cards
 * - Pull to refresh
 * - FAB for creating groups
 * - Loading, empty, and error states
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListScreen(
    onGroupClick: (Long) -> Unit,
    onCreateGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupsListViewModel = viewModel { GroupsListViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Groups",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshGroups() }) {
                        Icon(
                            imageVector = GrupperIcons.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && !uiState.hasError && !uiState.isEmpty) {
                FloatingActionButton(
                    onClick = onCreateGroupClick,
                    shape = GrupperShapes.Large,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = GrupperIcons.Add,
                        contentDescription = "Create group"
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
                    LoadingScreen(message = "Loading groups...")
                }

                // Error state
                uiState.hasError -> {
                    NetworkErrorView(
                        onRetry = { viewModel.loadGroups() }
                    )
                }

                // Empty state
                uiState.isEmpty -> {
                    EmptyStateView(
                        title = "No groups yet",
                        message = "Create your first group to start discussions",
                        actionLabel = "Create Group",
                        onAction = onCreateGroupClick
                    )
                }

                // Content state with pull to refresh
                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.refreshGroups() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = GrupperSpacing.Default,
                                vertical = GrupperSpacing.Small
                            ),
                            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
                        ) {
                            items(
                                items = uiState.groups,
                                key = { it.id }
                            ) { group ->
                                GroupCard(
                                    group = group,
                                    onClick = { onGroupClick(group.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
