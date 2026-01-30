package com.grupper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupper.data.api.GroupService
import com.grupper.data.api.PostService
import com.grupper.data.api.TagService
import com.grupper.data.model.Group
import com.grupper.data.model.Post
import com.grupper.data.model.PostSortOrder
import com.grupper.data.model.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Group Detail Screen
 * Manages state for group info, tags, posts with filtering and sorting
 */
class GroupDetailViewModel(
    private val groupId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    init {
        loadGroupDetail()
    }

    fun loadGroupDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val group = GroupService.getGroupById(groupId)
                val tags = TagService.getTagsByGroupId(groupId)
                val allPosts = PostService.getPostsByGroupId(
                    groupId = groupId,
                    sortOrder = PostSortOrder.NEWEST
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        group = group,
                        tags = tags,
                        allPosts = allPosts,
                        filteredPosts = allPosts,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load group"
                    )
                }
            }
        }
    }

    fun refreshGroup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                val group = GroupService.getGroupById(groupId)
                val tags = TagService.getTagsByGroupId(groupId)
                val allPosts = PostService.getPostsByGroupId(
                    groupId = groupId,
                    tagId = _uiState.value.selectedTagId,
                    sortOrder = _uiState.value.sortOrder
                )

                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        group = group,
                        tags = tags,
                        allPosts = allPosts,
                        filteredPosts = allPosts,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        error = e.message ?: "Failed to refresh"
                    )
                }
            }
        }
    }

    fun selectTag(tagId: Long?) {
        val newSelectedTagId = if (_uiState.value.selectedTagId == tagId) null else tagId
        _uiState.update { it.copy(selectedTagId = newSelectedTagId) }
        loadPosts()
    }

    fun changeSortOrder(sortOrder: PostSortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
        loadPosts()
    }

    private fun loadPosts() {
        viewModelScope.launch {
            try {
                val posts = PostService.getPostsByGroupId(
                    groupId = groupId,
                    tagId = _uiState.value.selectedTagId,
                    sortOrder = _uiState.value.sortOrder
                )

                _uiState.update {
                    it.copy(
                        allPosts = posts,
                        filteredPosts = posts
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Failed to load posts")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for Group Detail Screen
 */
data class GroupDetailUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val group: Group? = null,
    val tags: List<Tag> = emptyList(),
    val allPosts: List<Post> = emptyList(),
    val filteredPosts: List<Post> = emptyList(),
    val selectedTagId: Long? = null,
    val sortOrder: PostSortOrder = PostSortOrder.NEWEST,
    val error: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && filteredPosts.isEmpty() && error == null
    val hasError: Boolean get() = error != null && !isLoading
    val hasNoPostsWithTag: Boolean get() = isEmpty && selectedTagId != null
}
