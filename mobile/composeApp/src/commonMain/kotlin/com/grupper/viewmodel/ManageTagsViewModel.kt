package com.grupper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupper.data.api.TagService
import com.grupper.data.model.Tag
import kotlinx.coroutines.launch

/**
 * ViewModel for Manage Tags screen
 * Manages tag list, create/edit/delete operations
 */
class ManageTagsViewModel(
    private val groupId: Long
) : ViewModel() {

    var uiState by mutableStateOf(ManageTagsUiState())
        private set

    init {
        loadTags()
    }

    /**
     * Load tags for the group
     */
    fun loadTags() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                val tags = TagService.getTagsByGroupId(groupId)
                uiState = uiState.copy(
                    isLoading = false,
                    tags = tags
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Failed to load tags"
                )
            }
        }
    }

    /**
     * Start creating a new tag
     */
    fun startCreate() {
        uiState = uiState.copy(
            isCreating = true,
            editingTagId = null,
            editName = "",
            editColor = PRESET_COLORS.first()
        )
    }

    /**
     * Start editing an existing tag
     */
    fun startEdit(tag: Tag) {
        uiState = uiState.copy(
            isCreating = false,
            editingTagId = tag.id,
            editName = tag.name,
            editColor = tag.color
        )
    }

    /**
     * Cancel create/edit
     */
    fun cancelEdit() {
        uiState = uiState.copy(
            isCreating = false,
            editingTagId = null,
            editName = "",
            editColor = PRESET_COLORS.first()
        )
    }

    /**
     * Update edit name
     */
    fun updateEditName(name: String) {
        uiState = uiState.copy(editName = name)
    }

    /**
     * Update edit color
     */
    fun updateEditColor(color: String) {
        uiState = uiState.copy(editColor = color)
    }

    /**
     * Save tag (create or update)
     */
    fun saveTag() {
        if (uiState.editName.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)

            try {
                if (uiState.editingTagId != null) {
                    // Update existing tag
                    TagService.updateTag(
                        tagId = uiState.editingTagId!!,
                        name = uiState.editName,
                        color = uiState.editColor
                    )
                } else {
                    // Create new tag
                    TagService.createTag(
                        groupId = groupId,
                        name = uiState.editName,
                        color = uiState.editColor
                    )
                }

                loadTags()
                cancelEdit()

                uiState = uiState.copy(isSaving = false)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    error = "Failed to save tag"
                )
            }
        }
    }

    /**
     * Delete tag
     */
    fun deleteTag(tagId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)

            try {
                TagService.deleteTag(tagId)
                loadTags()

                uiState = uiState.copy(isSaving = false)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    error = "Failed to delete tag"
                )
            }
        }
    }

    companion object {
        // Preset colors (8 options from design specs)
        val PRESET_COLORS = listOf(
            "#EF4444", // Red
            "#F97316", // Orange
            "#F59E0B", // Amber
            "#22C55E", // Green
            "#14B8A6", // Teal
            "#3B82F6", // Blue
            "#8B5CF6", // Purple
            "#EC4899"  // Pink
        )
    }
}

/**
 * UI state for Manage Tags screen
 */
data class ManageTagsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val tags: List<Tag> = emptyList(),
    val isCreating: Boolean = false,
    val editingTagId: Long? = null,
    val editName: String = "",
    val editColor: String = ManageTagsViewModel.PRESET_COLORS.first(),
    val error: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && tags.isEmpty()
    val canSave: Boolean get() = editName.isNotBlank() && !isSaving
}
