package com.grupper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.grupper.viewmodel.ManageTagsViewModel

/**
 * Manage Tags Screen
 * Full-screen modal for managing group tags (create, edit, delete)
 *
 * Features:
 * - List of existing tags with color preview
 * - Add tag button (in app bar)
 * - Inline editing with color picker
 * - Delete confirmation
 * - 8 preset colors to choose from
 *
 * Based on DESIGN-007 specifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTagsScreen(
    groupId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageTagsViewModel = viewModel { ManageTagsViewModel(groupId) }
) {
    val uiState = viewModel.uiState
    var tagToDelete by remember { mutableStateOf<Tag?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Tags") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = GrupperIcons.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Add tag button
                    if (!uiState.isCreating && uiState.editingTagId == null) {
                        IconButton(onClick = { viewModel.startCreate() }) {
                            Icon(
                                imageVector = GrupperIcons.Add,
                                contentDescription = "Add tag"
                            )
                        }
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
            when {
                uiState.isLoading -> {
                    LoadingScreen(message = "Loading tags...")
                }

                uiState.isEmpty && !uiState.isCreating -> {
                    EmptyStateView(
                        title = "No tags yet",
                        message = "Tap + to create your first tag",
                        actionLabel = "Add Tag",
                        onAction = { viewModel.startCreate() }
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(GrupperSpacing.Default),
                        verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
                    ) {
                        // Create form at top (if creating)
                        if (uiState.isCreating) {
                            item {
                                TagEditForm(
                                    name = uiState.editName,
                                    color = uiState.editColor,
                                    onNameChange = { viewModel.updateEditName(it) },
                                    onColorChange = { viewModel.updateEditColor(it) },
                                    onSave = { viewModel.saveTag() },
                                    onCancel = { viewModel.cancelEdit() },
                                    isSaving = uiState.isSaving,
                                    canSave = uiState.canSave,
                                    isCreate = true
                                )
                            }
                        }

                        // Tag list
                        items(uiState.tags) { tag ->
                            if (uiState.editingTagId == tag.id) {
                                // Show edit form
                                TagEditForm(
                                    name = uiState.editName,
                                    color = uiState.editColor,
                                    onNameChange = { viewModel.updateEditName(it) },
                                    onColorChange = { viewModel.updateEditColor(it) },
                                    onSave = { viewModel.saveTag() },
                                    onCancel = { viewModel.cancelEdit() },
                                    isSaving = uiState.isSaving,
                                    canSave = uiState.canSave,
                                    isCreate = false
                                )
                            } else {
                                // Show tag row
                                TagRow(
                                    tag = tag,
                                    onEdit = { viewModel.startEdit(tag) },
                                    onDelete = { tagToDelete = tag },
                                    enabled = !uiState.isCreating && uiState.editingTagId == null
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (tagToDelete != null) {
        AlertDialog(
            onDismissRequest = { tagToDelete = null },
            title = { Text("Delete tag?") },
            text = { Text("Are you sure you want to delete \"${tagToDelete?.name}\"? Posts with this tag will not be deleted.") },
            confirmButton = {
                GrupperPrimaryButton(
                    text = "Delete",
                    onClick = {
                        viewModel.deleteTag(tagToDelete!!.id)
                        tagToDelete = null
                    }
                )
            },
            dismissButton = {
                GrupperTextButton(
                    text = "Cancel",
                    onClick = { tagToDelete = null }
                )
            }
        )
    }
}

/**
 * Tag row component showing color and name with edit/delete actions
 */
@Composable
private fun TagRow(
    tag: Tag,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    GrupperCard(
        onClick = if (enabled) onEdit else null,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GrupperSpacing.Default),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color dot + name
            Row(
                horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Default),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Color dot
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(parseColor(tag.color)))
                )

                // Name
                Text(
                    text = tag.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Delete button
            if (enabled) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = GrupperIcons.Close,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Tag edit form component (for create or edit)
 */
@Composable
private fun TagEditForm(
    name: String,
    color: String,
    onNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isSaving: Boolean,
    canSave: Boolean,
    isCreate: Boolean,
    modifier: Modifier = Modifier
) {
    GrupperCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GrupperSpacing.Default),
            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Default)
        ) {
            // Title
            Text(
                text = if (isCreate) "Create Tag" else "Edit Tag",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Name field
            GrupperTextField(
                value = name,
                onValueChange = onNameChange,
                label = "Tag Name",
                placeholder = "e.g., Discussion",
                modifier = Modifier.fillMaxWidth()
            )

            // Color picker
            Column {
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(GrupperSpacing.Small))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
                ) {
                    items(ManageTagsViewModel.PRESET_COLORS) { presetColor ->
                        ColorOption(
                            color = presetColor,
                            isSelected = color == presetColor,
                            onClick = { onColorChange(presetColor) }
                        )
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GrupperTextButton(
                    text = "Cancel",
                    onClick = onCancel,
                    enabled = !isSaving
                )

                Spacer(modifier = Modifier.width(GrupperSpacing.Small))

                GrupperPrimaryButton(
                    text = if (isCreate) "Create" else "Save",
                    onClick = onSave,
                    isLoading = isSaving,
                    enabled = canSave
                )
            }
        }
    }
}

/**
 * Color option component for color picker
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
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
    )
}

/**
 * Parse color hex string to Color (cross-platform)
 */
private fun parseColor(colorHex: String): Long {
    val hex = colorHex.removePrefix("#")
    return ("FF" + hex).toLong(16)
}
