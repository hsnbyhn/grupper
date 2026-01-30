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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewModelScope
import com.grupper.data.api.TagService
import com.grupper.data.model.Tag
import com.grupper.ui.components.*
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing
import com.grupper.viewmodel.CreateEditGroupViewModel
import com.grupper.viewmodel.CreateEditPostViewModel
import kotlinx.coroutines.launch

/**
 * Create/Edit Group Screen
 * Full-screen modal form for creating new groups or editing existing ones
 *
 * Features:
 * - Image picker (placeholder for MVP)
 * - Name field with character counter (max 50)
 * - Description field (multiline)
 * - Form validation
 * - Save/Cancel actions
 * - Loading states
 * - Discard changes confirmation
 *
 * Based on DESIGN-005 specifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditGroupScreen(
    groupId: Long? = null,
    onBackClick: () -> Unit,
    onSaveSuccess: (groupId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateEditGroupViewModel = viewModel { CreateEditGroupViewModel(groupId) }
) {
    val uiState = viewModel.uiState
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showAddTagsSheet by remember { mutableStateOf(false) }
    var createdGroupId by remember { mutableStateOf<Long?>(null) }
    var createdGroupName by remember { mutableStateOf("") }

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
                    Text(if (uiState.isEditMode) "Edit Group" else "Create Group")
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
                LoadingScreen(message = "Loading group...")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(GrupperSpacing.Default)
                ) {
                    // Image picker placeholder
                    ImagePickerPlaceholder(
                        imageUrl = uiState.imageUrl,
                        onImageClick = {
                            // TODO: Open image picker when implementing image upload
                        }
                    )

                    Spacer(modifier = Modifier.height(GrupperSpacing.Large))

                    // Name field
                    GrupperTextField(
                        value = uiState.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = "Group Name",
                        placeholder = "e.g., Kotlin Developers",
                        errorMessage = uiState.nameError,
                        maxLength = CreateEditGroupViewModel.MAX_NAME_LENGTH,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(GrupperSpacing.Default))

                    // Description field
                    GrupperMultilineTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = "Description",
                        placeholder = "Tell people what this group is about...",
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(GrupperSpacing.Large))

                    // Save button
                    GrupperPrimaryButton(
                        text = if (uiState.isEditMode) "Save Changes" else "Create Group",
                        onClick = {
                            viewModel.saveGroup { groupId ->
                                if (uiState.isEditMode) {
                                    // For edits, go directly to success
                                    onSaveSuccess(groupId)
                                } else {
                                    // For new groups, show tag setup sheet
                                    createdGroupId = groupId
                                    createdGroupName = uiState.name
                                    showAddTagsSheet = true
                                }
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

    // Add tags bottom sheet (shown after creating new group)
    if (showAddTagsSheet && createdGroupId != null) {
        AddTagsBottomSheet(
            groupId = createdGroupId!!,
            groupName = createdGroupName,
            onDone = {
                showAddTagsSheet = false
                onSaveSuccess(createdGroupId!!)
            },
            onSkip = {
                showAddTagsSheet = false
                onSaveSuccess(createdGroupId!!)
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
 * Shows either selected image or placeholder with instructions
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
            .height(180.dp)
            .clip(GrupperShapes.Medium)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = GrupperShapes.Medium
            )
            .clickable(onClick = onImageClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            // TODO: Show actual image when image loading is implemented
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = GrupperIcons.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
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
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(GrupperSpacing.Default)
            ) {
                Icon(
                    imageVector = GrupperIcons.Add,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(GrupperSpacing.Small))
                Text(
                    text = "Add group image",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "(Optional)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Add Tags Bottom Sheet
 * Shown after creating a new group to allow adding essential tags
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTagsBottomSheet(
    groupId: Long,
    groupName: String,
    onDone: () -> Unit,
    onSkip: () -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(CreateEditPostViewModel.PRESET_COLORS.first()) }
    var createdTags by remember { mutableStateOf<List<Tag>>(emptyList()) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onSkip,
        shape = GrupperShapes.BottomSheet
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GrupperSpacing.Default)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add tags to $groupName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                GrupperTextButton(
                    text = "Skip",
                    onClick = onSkip
                )
            }

            Spacer(modifier = Modifier.height(GrupperSpacing.Small))

            Text(
                text = "Create tags to organize posts in your group",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(GrupperSpacing.Default))

            // Created tags list
            if (createdTags.isNotEmpty()) {
                Text(
                    text = "Created tags (${createdTags.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = GrupperSpacing.Small)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Small),
                    modifier = Modifier.padding(bottom = GrupperSpacing.Default)
                ) {
                    items(createdTags) { tag ->
                        TagChip(
                            name = tag.name,
                            color = Color(parseColor(tag.color)),
                            isSelected = true,
                            onClick = { }
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = GrupperSpacing.Default)
                )
            }

            // Tag creation form
            Text(
                text = if (createdTags.isEmpty()) "Create your first tag" else "Add another tag",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = GrupperSpacing.Small)
            )

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
                text = "Color",
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

            // Error message
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(GrupperSpacing.Small))
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(GrupperSpacing.Large))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Default)
            ) {
                // Add Tag button
                GrupperSecondaryButton(
                    text = if (createdTags.isEmpty()) "Add Tag" else "Add Another",
                    onClick = {
                        scope.launch {
                            isSaving = true
                            errorMessage = null
                            try {
                                val newTag = TagService.createTag(
                                    groupId = groupId,
                                    name = tagName,
                                    color = selectedColor
                                )
                                createdTags = createdTags + newTag
                                // Clear form for next tag
                                tagName = ""
                                selectedColor = CreateEditPostViewModel.PRESET_COLORS.first()
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Failed to create tag"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    isLoading = isSaving,
                    enabled = tagName.isNotBlank() && !isSaving,
                    modifier = Modifier.weight(1f)
                )

                // Done button
                GrupperPrimaryButton(
                    text = "Done",
                    onClick = onDone,
                    enabled = !isSaving,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(GrupperSpacing.XLarge))
        }
    }
}

/**
 * Color option circle for color picker
 */
@Composable
private fun ColorOption(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
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
