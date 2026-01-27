package com.grupper.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import com.grupper.ui.theme.GrupperIcons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.grupper.ui.components.GrupperCard
import com.grupper.ui.components.GrupperPrimaryButton
import com.grupper.ui.components.GrupperSecondaryButton
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing

/**
 * Placeholder screens for navigation testing
 * These will be replaced with actual implementations in later tasks
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListPlaceholder(
    onGroupClick: (Long) -> Unit,
    onCreateGroupClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Groups", style = MaterialTheme.typography.headlineMedium) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateGroupClick,
                shape = GrupperShapes.Large,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(GrupperIcons.Add, contentDescription = "Create group")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(GrupperSpacing.Default),
            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
        ) {
            Text(
                text = "Groups List Screen",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Tap a card to navigate to Group Detail",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(GrupperSpacing.Default))

            // Sample group cards
            repeat(3) { index ->
                GrupperCard(
                    onClick = { onGroupClick(index.toLong() + 1) }
                ) {
                    Text(
                        text = "Sample Group ${index + 1}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tap to view details",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailPlaceholder(
    groupId: Long,
    onBackClick: () -> Unit,
    onPostClick: (Long) -> Unit,
    onCreatePostClick: () -> Unit,
    onManageTagsClick: () -> Unit,
    onEditGroupClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Group $groupId") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(GrupperIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreatePostClick,
                shape = GrupperShapes.Large,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(GrupperIcons.Add, contentDescription = "Create post")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(GrupperSpacing.Default),
            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
        ) {
            Text(
                text = "Group Detail Screen",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Group ID: $groupId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(GrupperSpacing.Small))

            Row(horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)) {
                GrupperSecondaryButton(
                    text = "Manage Tags",
                    onClick = onManageTagsClick
                )
                GrupperSecondaryButton(
                    text = "Edit Group",
                    onClick = onEditGroupClick
                )
            }

            Spacer(modifier = Modifier.height(GrupperSpacing.Default))

            // Sample post cards
            repeat(3) { index ->
                GrupperCard(
                    onClick = { onPostClick(index.toLong() + 1) }
                ) {
                    Text(
                        text = "Sample Post ${index + 1}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tap to view post details",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailPlaceholder(
    postId: Long,
    onBackClick: () -> Unit,
    onEditPostClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(GrupperIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(GrupperSpacing.Default),
            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
        ) {
            Text(
                text = "Post Detail Screen",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Post ID: $postId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(GrupperSpacing.Small))

            GrupperSecondaryButton(
                text = "Edit Post",
                onClick = onEditPostClick
            )

            Spacer(modifier = Modifier.height(GrupperSpacing.Default))

            GrupperCard {
                Text(
                    text = "Post content goes here...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Text(
                text = "Comments",
                style = MaterialTheme.typography.titleMedium
            )

            repeat(2) { index ->
                GrupperCard {
                    Text(
                        text = "Comment ${index + 1}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditGroupPlaceholder(
    groupId: Long?,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val isEditing = groupId != null
    val title = if (isEditing) "Edit Group" else "Create Group"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(GrupperIcons.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(GrupperSpacing.Default),
            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Default)
        ) {
            Text(
                text = "$title Screen (Modal)",
                style = MaterialTheme.typography.titleLarge
            )
            if (isEditing) {
                Text(
                    text = "Editing Group ID: $groupId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            GrupperPrimaryButton(
                text = if (isEditing) "Save Changes" else "Create Group",
                onClick = onSaveSuccess,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditPostPlaceholder(
    groupId: Long?,
    postId: Long?,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val isEditing = postId != null
    val title = if (isEditing) "Edit Post" else "Create Post"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(GrupperIcons.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(GrupperSpacing.Default),
            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Default)
        ) {
            Text(
                text = "$title Screen (Modal)",
                style = MaterialTheme.typography.titleLarge
            )
            if (isEditing) {
                Text(
                    text = "Editing Post ID: $postId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (groupId != null) {
                Text(
                    text = "Creating post in Group ID: $groupId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            GrupperPrimaryButton(
                text = if (isEditing) "Save Changes" else "Post",
                onClick = onSaveSuccess,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTagsPlaceholder(
    groupId: Long,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Tags") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(GrupperIcons.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(GrupperSpacing.Default),
            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
        ) {
            Text(
                text = "Manage Tags Screen",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Group ID: $groupId",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(GrupperSpacing.Default))

            Text(
                text = "Tag management UI will go here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
