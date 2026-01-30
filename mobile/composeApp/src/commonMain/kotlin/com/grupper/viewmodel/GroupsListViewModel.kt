package com.grupper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupper.data.api.GroupService
import com.grupper.data.model.Group
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Groups List Screen
 * Manages state for loading, displaying, and refreshing groups
 */
class GroupsListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsListUiState())
    val uiState: StateFlow<GroupsListUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val groups = GroupService.getAllGroups()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        groups = groups,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load groups"
                    )
                }
            }
        }
    }

    fun refreshGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                val groups = GroupService.getAllGroups()

                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        groups = groups,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        error = e.message ?: "Failed to refresh groups"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI State for Groups List Screen
 */
data class GroupsListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val groups: List<Group> = emptyList(),
    val error: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && groups.isEmpty() && error == null
    val hasError: Boolean get() = error != null && !isLoading
}
