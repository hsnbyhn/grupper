package com.grupper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
 * Tag sheet mode - selection or creation
 */
private enum class TagSheetMode {
    SELECTION,  // Showing tag list
    CREATION    // Showing tag creation form
}

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
    var tagSheetMode by remember { mutableStateOf(TagSheetMode.SELECTION) }

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
            mode = tagSheetMode,
            tags = uiState.availableTags,
            selectedTag = uiState.selectedTag,
            onTagSelected = { tag ->
                viewModel.selectTag(tag)
                showTagSelector = false
                tagSheetMode = TagSheetMode.SELECTION // Reset for next time
            },
            onCreateNew = { tagSheetMode = TagSheetMode.CREATION },
            onBackToSelection = { tagSheetMode = TagSheetMode.SELECTION },
            onCreateTag = { name, color ->
                viewModel.createTagInline(
                    name = name,
                    color = color,
                    onSuccess = {
                        // Show selection briefly then dismiss
                        tagSheetMode = TagSheetMode.SELECTION
                        showTagSelector = false
                    },
                    onError = { error ->
                        // Error will be shown in creation form
                    }
                )
            },
            isSaving = uiState.isSaving,
            onDismiss = {
                showTagSelector = false
                tagSheetMode = TagSheetMode.SELECTION // Reset for next time
            }
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
 * Tag selector bottom sheet with inline creation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TagSelectorBottomSheet(
    mode: TagSheetMode,
    tags: List<Tag>,
    selectedTag: Tag?,
    onTagSelected: (Tag) -> Unit,
    onCreateNew: () -> Unit,
    onBackToSelection: () -> Unit,
    onCreateTag: (name: String, color: String) -> Unit,
    isSaving: Boolean,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = GrupperShapes.BottomSheet
    ) {
        when (mode) {
            TagSheetMode.SELECTION -> TagSelectionContent(
                tags = tags,
                selectedTag = selectedTag,
                onTagSelected = onTagSelected,
                onCreateNew = onCreateNew
            )
            TagSheetMode.CREATION -> TagCreationContent(
                onBack = onBackToSelection,
                onCreate = onCreateTag,
                isSaving = isSaving,
                onCancel = onDismiss
            )
        }
    }
}

/**
 * Tag selection content (list of tags + create button)
 */
@Composable
private fun TagSelectionContent(
    tags: List<Tag>,
    selectedTag: Tag?,
    onTagSelected: (Tag) -> Unit,
    onCreateNew: () -> Unit
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
                message = "Create your first tag to start organizing posts.",
                modifier = Modifier.height(200.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
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

            Spacer(modifier = Modifier.height(GrupperSpacing.Default))

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(vertical = GrupperSpacing.Default)
            )
        }

        // Create new tag button
        GrupperSecondaryButton(
            text = "Create new tag",
            onClick = onCreateNew,
            leadingIcon = GrupperIcons.Add,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(GrupperSpacing.XLarge))
    }
}

/**
 * Tag creation content (form to create new tag)
 */
@Composable
private fun TagCreationContent(
    onBack: () -> Unit,
    onCreate: (name: String, color: String) -> Unit,
    isSaving: Boolean,
    onCancel: () -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(CreateEditPostViewModel.PRESET_COLORS.first()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(GrupperSpacing.Default)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = GrupperIcons.ArrowBack,
                    contentDescription = "Back to tag selection"
                )
            }
            Text(
                text = "Create new tag",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(GrupperSpacing.Default))

        // Tag name field
        GrupperTextField(
            value = tagName,
            onValueChange = {
                if (it.length <= 30) {
                    tagName = it
                    errorMessage = null
                }
            },
            label = "Tag name",
            placeholder = "e.g., Question, Tutorial, Discussion",
            maxLength = 30,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(GrupperSpacing.Default))

        // Color picker
        Text(
            text = "Choose color",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = GrupperSpacing.Small)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
        ) {
            items(CreateEditPostViewModel.PRESET_COLORS) { presetColor ->
                ColorOption(
                    color = presetColor,
                    isSelected = selectedColor == presetColor,
                    onClick = { selectedColor = presetColor }
                )
            }
        }

        Spacer(modifier = Modifier.height(GrupperSpacing.Default))

        // Preview
        Text(
            text = "Preview",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = GrupperSpacing.Small)
        )

        if (tagName.isNotBlank()) {
            TagChip(
                name = tagName,
                color = Color(parseColor(selectedColor)),
                isSelected = true,
                onClick = { }
            )
        } else {
            Text(
                text = "Enter a tag name to see preview",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(GrupperSpacing.Large))

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = GrupperSpacing.Default)
            )
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Default)
        ) {
            GrupperTextButton(
                text = "Cancel",
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            )
            GrupperPrimaryButton(
                text = "Create",
                onClick = { onCreate(tagName, selectedColor) },
                isLoading = isSaving,
                enabled = tagName.isNotBlank() && !isSaving,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(GrupperSpacing.XLarge))
    }
}

/**
 * Color option circle for color picker
 */
@Composable
private fun ColorOption(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(parseColor(color)))
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = GrupperIcons.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
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
