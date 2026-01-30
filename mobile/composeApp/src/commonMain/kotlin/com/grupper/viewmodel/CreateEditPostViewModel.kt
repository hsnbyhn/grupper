package com.grupper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupper.data.api.PostService
import com.grupper.data.api.TagService
import com.grupper.data.model.Post
import com.grupper.data.model.Tag
import kotlinx.coroutines.launch

/**
 * ViewModel for Create/Edit Post screen
 * Manages form state, validation, tag selection, and save logic
 */
class CreateEditPostViewModel(
    private val groupId: Long,
    private val postId: Long? = null
) : ViewModel() {

    var uiState by mutableStateOf(CreateEditPostUiState())
        private set

    init {
        loadGroupTags()

        // If editing, load existing post data
        if (postId != null) {
            loadPost(postId)
        }
    }

    /**
     * Load tags for the group
     */
    private fun loadGroupTags() {
        viewModelScope.launch {
            try {
                val tags = TagService.getTagsByGroupId(groupId)
                uiState = uiState.copy(availableTags = tags)
            } catch (e: Exception) {
                uiState = uiState.copy(error = "Failed to load tags")
            }
        }
    }

    /**
     * Load existing post for editing
     */
    private fun loadPost(id: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)

            try {
                val post = PostService.getPostById(id)
                val selectedTag = uiState.availableTags.find { it.id == post.tag?.id }

                uiState = uiState.copy(
                    isLoading = false,
                    title = post.title,
                    content = post.content,
                    authorName = post.authorName,
                    imageUrl = post.imageUrl,
                    selectedTag = selectedTag,
                    isEditMode = true
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Failed to load post"
                )
            }
        }
    }

    /**
     * Update post title
     */
    fun updateTitle(title: String) {
        uiState = uiState.copy(
            title = title,
            titleError = validateTitle(title)
        )
    }

    /**
     * Update post content
     */
    fun updateContent(content: String) {
        uiState = uiState.copy(content = content)
    }

    /**
     * Update author name
     */
    fun updateAuthorName(name: String) {
        uiState = uiState.copy(authorName = name)
    }

    /**
     * Select a tag
     */
    fun selectTag(tag: Tag?) {
        uiState = uiState.copy(selectedTag = tag)
    }

    /**
     * Update image URL (when image is selected)
     */
    fun updateImageUrl(url: String?) {
        uiState = uiState.copy(imageUrl = url)
    }

    /**
     * Validate post title
     */
    private fun validateTitle(title: String): String? {
        return when {
            title.isBlank() -> "Title is required"
            title.length > MAX_TITLE_LENGTH -> "Title must be $MAX_TITLE_LENGTH characters or less"
            else -> null
        }
    }

    /**
     * Check if form is valid and can be saved
     */
    private fun isFormValid(): Boolean {
        return uiState.title.isNotBlank() &&
                uiState.title.length <= MAX_TITLE_LENGTH &&
                uiState.content.isNotBlank() &&
                uiState.selectedTag != null &&
                uiState.authorName.isNotBlank()
    }

    /**
     * Check if form has unsaved changes
     */
    fun hasUnsavedChanges(): Boolean {
        return uiState.title.isNotBlank() ||
               uiState.content.isNotBlank() ||
               uiState.authorName.isNotBlank()
    }

    /**
     * Save post (create or update)
     */
    fun savePost(onSuccess: (Long) -> Unit) {
        if (!isFormValid()) {
            // Trigger validation errors
            uiState = uiState.copy(titleError = validateTitle(uiState.title))
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, error = null)

            try {
                val savedPost = if (postId != null) {
                    // Update existing post
                    PostService.updatePost(
                        id = postId,
                        title = uiState.title,
                        content = uiState.content,
                        imageUrl = uiState.imageUrl
                    )
                } else {
                    // Create new post
                    PostService.createPost(
                        groupId = groupId,
                        tagId = uiState.selectedTag!!.id,
                        title = uiState.title,
                        content = uiState.content,
                        authorName = uiState.authorName,
                        imageUrl = uiState.imageUrl
                    )
                }

                uiState = uiState.copy(isSaving = false)
                onSuccess(savedPost.id)
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    error = "Failed to save post: ${e.message}"
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
        const val MAX_TITLE_LENGTH = 100
    }
}

/**
 * UI state for Create/Edit Post screen
 */
data class CreateEditPostUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val title: String = "",
    val content: String = "",
    val authorName: String = "",
    val imageUrl: String? = null,
    val selectedTag: Tag? = null,
    val availableTags: List<Tag> = emptyList(),
    val titleError: String? = null,
    val error: String? = null
) {
    val canSave: Boolean get() = title.isNotBlank() &&
                                  content.isNotBlank() &&
                                  authorName.isNotBlank() &&
                                  selectedTag != null &&
                                  titleError == null &&
                                  !isSaving
}
