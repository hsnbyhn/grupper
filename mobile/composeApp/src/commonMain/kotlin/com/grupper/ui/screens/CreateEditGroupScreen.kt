package com.grupper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupper.ui.components.*
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing
import com.grupper.viewmodel.CreateEditGroupViewModel

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
                                onSaveSuccess(groupId)
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
