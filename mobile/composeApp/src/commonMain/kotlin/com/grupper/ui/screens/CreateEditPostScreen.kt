package com.grupper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupper.data.model.Tag
import com.grupper.ui.components.*
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing
import com.grupper.viewmodel.CreateEditPostViewModel

/**
 * Create/Edit Post Screen
 * Full-screen modal form for creating posts within a group or editing existing posts
 *
 * Features:
 * - Title field with character counter (max 100)
 * - Content field (multiline, unlimited)
 * - Image picker (placeholder for MVP)
 * - Tag selection (bottom sheet with group tags)
 * - Author name field (required for Phase 1)
 * - Form validation
 * - Save/Cancel actions
 * - Discard changes confirmation
 *
 * Based on DESIGN-008 specifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditPostScreen(
    groupId: Long,
    postId: Long? = null,
    onBackClick: () -> Unit,
    onSaveSuccess: (postId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateEditPostViewModel = viewModel { CreateEditPostViewModel(groupId, postId) }
) {
    val uiState = viewModel.uiState
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showTagSelector by remember { mutableStateOf(false) }

    // Handle back press with unsaved changes
    val handleBack = {
        if (viewModel.hasUnsavedChanges() && !uiState.isSaving) {
            showDiscardDialog = true
        } else {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditMode) "Edit Post" else "Create Post")
                },
                navigationIcon = {
                    IconButton(onClick = handleBack) {
                        Icon(
                            imageVector = GrupperIcons.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LoadingScreen(message = "Loading post...")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(GrupperSpacing.Default)
                ) {
                    // Title field
                    GrupperTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = "Title",
                        placeholder = "e.g., Best practices for Kotlin coroutines",
                        errorMessage = uiState.titleError,
                        maxLength = CreateEditPostViewModel.MAX_TITLE_LENGTH,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(GrupperSpacing.Default))

                    // Content field
                    GrupperMultilineTextField(
                        value = uiState.content,
                        onValueChange = { viewModel.updateContent(it) },
                        label = "Content",
                        placeholder = "Share your thoughts, questions, or knowledge...",
                        minLines = 6,
                        maxLines = 12,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(GrupperSpacing.Default))

                    // Image picker placeholder
                    ImagePickerPlaceholder(
                        imageUrl = uiState.imageUrl,
                        onImageClick = {
                            // TODO: Open image picker when implementing image upload
                        }
                    )

                    Spacer(modifier = Modifier.height(GrupperSpacing.Default))

                    // Tag selection field
                    TagSelectorField(
                        selectedTag = uiState.selectedTag,
                        onClick = { showTagSelector = true }
                    )

                    Spacer(modifier = Modifier.height(GrupperSpacing.Default))

                    // Author name field
                    GrupperTextField(
                        value = uiState.authorName,
                        onValueChange = { viewModel.updateAuthorName(it) },
                        label = "Your Name",
                        placeholder = "e.g., Sarah",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(GrupperSpacing.Large))

                    // Save button
                    GrupperPrimaryButton(
                        text = if (uiState.isEditMode) "Save Changes" else "Post",
                        onClick = {
                            viewModel.savePost { postId ->
                                onSaveSuccess(postId)
                            }
                        },
                        isLoading = uiState.isSaving,
                        enabled = uiState.canSave,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Error message
                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(GrupperSpacing.Default))
                        Text(
                            text = uiState.error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Bottom spacing
                    Spacer(modifier = Modifier.height(GrupperSpacing.XXLarge))
                }
            }
        }
    }

    // Tag selector bottom sheet
    if (showTagSelector) {
        TagSelectorBottomSheet(
            tags = uiState.availableTags,
            selectedTag = uiState.selectedTag,
            onTagSelected = { tag ->
                viewModel.selectTag(tag)
                showTagSelector = false
            },
            onDismiss = { showTagSelector = false }
        )
    }

    // Discard changes dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to leave?") },
            confirmButton = {
                GrupperPrimaryButton(
                    text = "Discard",
                    onClick = {
                        showDiscardDialog = false
                        onBackClick()
                    }
                )
            },
            dismissButton = {
                GrupperTextButton(
                    text = "Keep Editing",
                    onClick = { showDiscardDialog = false }
                )
            }
        )
    }
}

/**
 * Image picker placeholder component
 */
@Composable
private fun ImagePickerPlaceholder(
    imageUrl: String?,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(GrupperShapes.Medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = GrupperShapes.Medium
            )
            .clickable(onClick = onImageClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = GrupperIcons.Info,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(GrupperSpacing.Small))
                Text(
                    text = "Image selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = GrupperIcons.Add,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(GrupperSpacing.Small))
                Text(
                    text = "Add image (optional)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Tag selector field - tappable field showing selected tag
 */
@Composable
private fun TagSelectorField(
    selectedTag: Tag?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tag *",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(GrupperShapes.Medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = GrupperShapes.Medium
                )
                .clickable(onClick = onClick)
                .padding(horizontal = GrupperSpacing.Default),
            contentAlignment = Alignment.CenterStart
        ) {
            if (selectedTag != null) {
                TagChip(
                    name = selectedTag.name,
                    color = Color(parseColor(selectedTag.color)),
                    isSelected = true,
                    onClick = onClick
                )
            } else {
                Text(
                    text = "Select a tag...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Tag selector bottom sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagSelectorBottomSheet(
    tags: List<Tag>,
    selectedTag: Tag?,
    onTagSelected: (Tag) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = GrupperShapes.BottomSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GrupperSpacing.Default)
        ) {
            Text(
                text = "Select Tag",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = GrupperSpacing.Default)
            )

            if (tags.isEmpty()) {
                EmptyStateView(
                    title = "No tags yet",
                    message = "This group doesn't have any tags yet.",
                    modifier = Modifier.height(200.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
                ) {
                    items(tags) { tag ->
                        TagChip(
                            name = tag.name,
                            color = Color(parseColor(tag.color)),
                            isSelected = tag.id == selectedTag?.id,
                            onClick = { onTagSelected(tag) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(GrupperSpacing.XLarge))
        }
    }
}

/**
 * Parse color hex string to Color (cross-platform)
 */
private fun parseColor(colorHex: String): Long {
    val hex = colorHex.removePrefix("#")
    return ("FF" + hex).toLong(16)
}
