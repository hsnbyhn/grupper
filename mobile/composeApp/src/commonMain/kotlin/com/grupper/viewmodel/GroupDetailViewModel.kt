package com.grupper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupper.data.model.Group
import com.grupper.data.model.Post
import com.grupper.data.model.PostSortOrder
import com.grupper.data.model.Tag
import com.grupper.data.repository.MockData
import kotlinx.coroutines.delay
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
                // Simulate network delay
                delay(600)

                // Using mock data - will be replaced with API call
                val group = MockData.groups.find { it.id == groupId }
                    ?: throw IllegalArgumentException("Group not found")

                val tags = MockData.tags.filter { it.groupId == groupId }
                val allPosts = MockData.posts.filter { it.groupId == groupId }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        group = group,
                        tags = tags,
                        allPosts = allPosts,
                        filteredPosts = allPosts.sortedByDescending { post -> post.createdAt },
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
                delay(500)

                val group = MockData.groups.find { it.id == groupId }
                    ?: throw IllegalArgumentException("Group not found")

                val tags = MockData.tags.filter { it.groupId == groupId }
                val allPosts = MockData.posts.filter { it.groupId == groupId }

                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        group = group,
                        tags = tags,
                        allPosts = allPosts,
                        filteredPosts = applyFiltersAndSort(allPosts, it.selectedTagId, it.sortOrder),
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
        _uiState.update { state ->
            val newSelectedTagId = if (state.selectedTagId == tagId) null else tagId
            state.copy(
                selectedTagId = newSelectedTagId,
                filteredPosts = applyFiltersAndSort(state.allPosts, newSelectedTagId, state.sortOrder)
            )
        }
    }

    fun changeSortOrder(sortOrder: PostSortOrder) {
        _uiState.update { state ->
            state.copy(
                sortOrder = sortOrder,
                filteredPosts = applyFiltersAndSort(state.allPosts, state.selectedTagId, sortOrder)
            )
        }
    }

    private fun applyFiltersAndSort(
        posts: List<Post>,
        selectedTagId: Long?,
        sortOrder: PostSortOrder
    ): List<Post> {
        // Filter by tag
        val filtered = if (selectedTagId != null) {
            posts.filter { it.tag?.id == selectedTagId }
        } else {
            posts
        }

        // Sort
        return when (sortOrder) {
            PostSortOrder.NEWEST -> filtered.sortedByDescending { it.createdAt }
            PostSortOrder.OLDEST -> filtered.sortedBy { it.createdAt }
            PostSortOrder.MOST_COMMENTED -> filtered.sortedByDescending { it.commentCount }
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
