package com.grupper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupper.data.api.GroupService
import com.grupper.data.model.Group
import kotlinx.coroutines.launch

/**
 * ViewModel for Create/Edit Group screen
 * Manages form state, validation, and save logic
 */
class CreateEditGroupViewModel(
    private val groupId: Long? = null
) : ViewModel() {

    var uiState by mutableStateOf(CreateEditGroupUiState())
        private set

    init {
        // If editing, load existing group data
        if (groupId != null) {
            loadGroup(groupId)
        }
    }

    /**
     * Load existing group for editing
     */
    private fun loadGroup(id: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                val group = GroupService.getGroupById(id)

                uiState = uiState.copy(
                    isLoading = false,
                    name = group.name,
                    description = group.description,
                    imageUrl = group.imageUrl,
                    isEditMode = true
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Failed to load group"
                )
            }
        }
    }

    /**
     * Update group name
     */
    fun updateName(name: String) {
        uiState = uiState.copy(
            name = name,
            nameError = validateName(name)
        )
    }

    /**
     * Update group description
     */
    fun updateDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    /**
     * Update image URL (when image is selected)
     */
    fun updateImageUrl(url: String?) {
        uiState = uiState.copy(imageUrl = url)
    }

    /**
     * Validate group name
     */
    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Name is required"
            name.length > MAX_NAME_LENGTH -> "Name must be $MAX_NAME_LENGTH characters or less"
            else -> null
        }
    }

    /**
     * Check if form is valid and can be saved
     */
    private fun isFormValid(): Boolean {
        return uiState.name.isNotBlank() &&
                uiState.name.length <= MAX_NAME_LENGTH &&
                uiState.description.isNotBlank()
    }

    /**
     * Check if form has unsaved changes
     */
    fun hasUnsavedChanges(): Boolean {
        return uiState.name.isNotBlank() || uiState.description.isNotBlank()
    }

    /**
     * Save group (create or update)
     */
    fun saveGroup(onSuccess: (Long) -> Unit) {
        if (!isFormValid()) {
            // Trigger validation errors
            uiState = uiState.copy(nameError = validateName(uiState.name))
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null)

            try {
                val savedGroup = if (groupId != null) {
                    // Update existing group
                    GroupService.updateGroup(
                        id = groupId,
                        name = uiState.name,
                        description = uiState.description,
                        imageUrl = uiState.imageUrl
                    )
                } else {
                    // Create new group
                    GroupService.createGroup(
                        name = uiState.name,
                        description = uiState.description,
                        imageUrl = uiState.imageUrl
                    )
                }

                uiState = uiState.copy(isSaving = false)
                onSuccess(savedGroup.id)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    error = "Failed to save group: ${e.message}"
                )
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    companion object {
        const val MAX_NAME_LENGTH = 50
    }
}

/**
 * UI state for Create/Edit Group screen
 */
data class CreateEditGroupUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val name: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val nameError: String? = null,
    val error: String? = null
) {
    val canSave: Boolean get() = name.isNotBlank() &&
                                  description.isNotBlank() &&
                                  nameError == null &&
                                  !isSaving
}
