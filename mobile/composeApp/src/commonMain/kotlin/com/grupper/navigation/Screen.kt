package com.grupper.navigation

import kotlinx.serialization.Serializable

/**
 * Grupper Navigation Routes
 * Based on Designer Agent's navigation structure (DESIGN-003)
 *
 * Navigation Pattern: Single-activity, stack-based navigation
 * - Groups List is the home/root screen
 * - Detail screens use forward navigation (slide from right)
 * - Create/Edit screens use modal navigation (slide from bottom)
 */

/**
 * Type-safe navigation routes using Kotlin Serialization
 */
sealed interface Screen {

    /**
     * Groups List - Home screen
     * Shows all available groups
     */
    @Serializable
    data object GroupsList : Screen

    /**
     * Group Detail - Shows group info, tags, and posts
     * @param groupId The ID of the group to display
     */
    @Serializable
    data class GroupDetail(val groupId: Long) : Screen

    /**
     * Post Detail - Shows full post and comments
     * @param postId The ID of the post to display
     */
    @Serializable
    data class PostDetail(val postId: Long) : Screen

    /**
     * Create Group - Modal for creating a new group
     */
    @Serializable
    data object CreateGroup : Screen

    /**
     * Edit Group - Modal for editing an existing group
     * @param groupId The ID of the group to edit
     */
    @Serializable
    data class EditGroup(val groupId: Long) : Screen

    /**
     * Create Post - Modal for creating a new post in a group
     * @param groupId The ID of the group to create post in
     */
    @Serializable
    data class CreatePost(val groupId: Long) : Screen

    /**
     * Edit Post - Modal for editing an existing post
     * @param postId The ID of the post to edit
     */
    @Serializable
    data class EditPost(val postId: Long) : Screen

    /**
     * Manage Tags - Screen for managing group tags
     * @param groupId The ID of the group whose tags to manage
     */
    @Serializable
    data class ManageTags(val groupId: Long) : Screen
}

/**
 * Navigation actions that can be performed
 */
sealed interface NavigationAction {
    data object NavigateBack : NavigationAction
    data class NavigateTo(val screen: Screen) : NavigationAction
    data object PopToRoot : NavigationAction
}
