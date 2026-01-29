package com.grupper.data.repository

import com.grupper.data.model.Comment
import com.grupper.data.model.Group
import com.grupper.data.model.Post
import com.grupper.data.model.Tag
import com.grupper.data.model.TagInfo

/**
 * Mock data for UI development and testing
 * Will be replaced with real API calls when backend is ready
 */
object MockData {

    // All tags across all groups
    val tags = listOf(
        // Group 1 - Kotlin Developers
        Tag(1, 1, "General", "#7C3AED", "2024-12-30T10:00:00Z"),
        Tag(2, 1, "Question", "#14B8A6", "2024-12-30T10:00:00Z"),
        Tag(3, 1, "Tutorial", "#F59E0B", "2024-12-30T10:00:00Z"),
        Tag(4, 1, "News", "#EF4444", "2024-12-30T10:00:00Z"),
        // Group 2 - Android Dev Community
        Tag(5, 2, "Compose", "#7C3AED", "2024-11-30T10:00:00Z"),
        Tag(6, 2, "Architecture", "#14B8A6", "2024-11-30T10:00:00Z"),
        Tag(7, 2, "Help", "#F59E0B", "2024-11-30T10:00:00Z"),
        // Group 3-5 - Default tags
        Tag(103, 3, "General", "#7C3AED", "2024-12-15T10:00:00Z"),
        Tag(104, 3, "Discussion", "#14B8A6", "2024-12-15T10:00:00Z"),
        Tag(104, 4, "General", "#7C3AED", "2024-12-15T10:00:00Z"),
        Tag(105, 4, "Discussion", "#14B8A6", "2024-12-15T10:00:00Z"),
        Tag(105, 5, "General", "#7C3AED", "2024-12-15T10:00:00Z"),
        Tag(106, 5, "Discussion", "#14B8A6", "2024-12-15T10:00:00Z")
    )

    // All posts across all groups
    val posts = listOf(
        // Group 1 - Kotlin Developers
        Post(
            id = 1,
            groupId = 1,
            title = "Kotlin 2.0 is here! What's new?",
            content = "Let's discuss the new features in Kotlin 2.0 including the new K2 compiler...",
            authorName = "Sarah Chen",
            imageUrl = null,
            tag = TagInfo(4, "News", "#EF4444"),
            commentCount = 42,
            createdAt = "2025-01-21T10:00:00Z",
            updatedAt = "2025-01-21T10:00:00Z"
        ),
        Post(
            id = 2,
            groupId = 1,
            title = "Best practices for coroutines?",
            content = "I'm looking for advice on structuring coroutines in a large project...",
            authorName = "Mike Johnson",
            imageUrl = null,
            tag = TagInfo(2, "Question", "#14B8A6"),
            commentCount = 18,
            createdAt = "2025-01-20T10:00:00Z",
            updatedAt = "2025-01-22T10:00:00Z"
        ),
        Post(
            id = 3,
            groupId = 1,
            title = "Introduction to Kotlin Flow",
            content = "A comprehensive guide to using Kotlin Flow for reactive programming...",
            authorName = "Alex Rivera",
            imageUrl = null,
            tag = TagInfo(3, "Tutorial", "#F59E0B"),
            commentCount = 56,
            createdAt = "2025-01-18T10:00:00Z",
            updatedAt = "2025-01-18T10:00:00Z"
        ),
        // Group 2 - Android Dev Community
        Post(
            id = 4,
            groupId = 2,
            title = "Compose Navigation 2.8 changes",
            content = "The new type-safe navigation API is a game changer...",
            authorName = "Emily Zhang",
            imageUrl = null,
            tag = TagInfo(5, "Compose", "#7C3AED"),
            commentCount = 31,
            createdAt = "2025-01-22T10:00:00Z",
            updatedAt = "2025-01-22T10:00:00Z"
        ),
        Post(
            id = 5,
            groupId = 2,
            title = "Clean Architecture with Compose",
            content = "How I structure my Compose projects for maintainability...",
            authorName = "David Kim",
            imageUrl = null,
            tag = TagInfo(6, "Architecture", "#14B8A6"),
            commentCount = 67,
            createdAt = "2025-01-19T10:00:00Z",
            updatedAt = "2025-01-21T10:00:00Z"
        )
    )

    val groups = listOf(
        Group(
            id = 1,
            name = "Kotlin Developers",
            description = "A community for Kotlin enthusiasts. Share tips, ask questions, and discuss the latest features.",
            imageUrl = null,
            memberCount = 1248,
            postCount = 342,
            createdAt = "2024-12-25T10:00:00Z",
            updatedAt = "2025-01-22T14:30:00Z"
        ),
        Group(
            id = 2,
            name = "Android Dev Community",
            description = "Everything Android! From Jetpack Compose to architecture patterns.",
            imageUrl = null,
            memberCount = 3567,
            postCount = 892,
            createdAt = "2024-11-25T10:00:00Z",
            updatedAt = "2025-01-23T12:00:00Z"
        ),
        Group(
            id = 3,
            name = "iOS & Swift",
            description = "Swift programming and iOS development discussions.",
            imageUrl = null,
            memberCount = 2134,
            postCount = 567,
            createdAt = "2024-12-10T10:00:00Z",
            updatedAt = "2025-01-23T08:00:00Z"
        ),
        Group(
            id = 4,
            name = "Cross-Platform Mobile",
            description = "KMP, Flutter, React Native - discuss cross-platform development approaches.",
            imageUrl = null,
            memberCount = 876,
            postCount = 234,
            createdAt = "2025-01-03T10:00:00Z",
            updatedAt = "2025-01-23T10:00:00Z"
        ),
        Group(
            id = 5,
            name = "Backend Engineering",
            description = "Server-side development with Ktor, Spring, Node.js and more.",
            imageUrl = null,
            memberCount = 1567,
            postCount = 445,
            createdAt = "2024-10-25T10:00:00Z",
            updatedAt = "2025-01-23T20:00:00Z"
        )
    )

    fun getTagsForGroup(groupId: Long): List<Tag> {
        return when (groupId) {
            1L -> listOf(
                Tag(1, groupId, "General", "#7C3AED", "2024-12-30T10:00:00Z"),
                Tag(2, groupId, "Question", "#14B8A6", "2024-12-30T10:00:00Z"),
                Tag(3, groupId, "Tutorial", "#F59E0B", "2024-12-30T10:00:00Z"),
                Tag(4, groupId, "News", "#EF4444", "2024-12-30T10:00:00Z")
            )
            2L -> listOf(
                Tag(5, groupId, "Compose", "#7C3AED", "2024-11-30T10:00:00Z"),
                Tag(6, groupId, "Architecture", "#14B8A6", "2024-11-30T10:00:00Z"),
                Tag(7, groupId, "Help", "#F59E0B", "2024-11-30T10:00:00Z")
            )
            else -> listOf(
                Tag(100 + groupId, groupId, "General", "#7C3AED", "2024-12-15T10:00:00Z"),
                Tag(101 + groupId, groupId, "Discussion", "#14B8A6", "2024-12-15T10:00:00Z")
            )
        }
    }

    fun getPostsForGroup(groupId: Long): List<Post> {
        val tags = getTagsForGroup(groupId)
        return when (groupId) {
            1L -> listOf(
                Post(
                    id = 1,
                    groupId = groupId,
                    title = "Kotlin 2.0 is here! What's new?",
                    content = "Let's discuss the new features in Kotlin 2.0 including the new K2 compiler...",
                    authorName = "Sarah Chen",
                    imageUrl = null,
                    tag = tags.getOrNull(3)?.toTagInfo(),
                    commentCount = 42,
                    createdAt = "2025-01-21T10:00:00Z",
                    updatedAt = "2025-01-21T10:00:00Z"
                ),
                Post(
                    id = 2,
                    groupId = groupId,
                    title = "Best practices for coroutines?",
                    content = "I'm looking for advice on structuring coroutines in a large project...",
                    authorName = "Mike Johnson",
                    imageUrl = null,
                    tag = tags.getOrNull(1)?.toTagInfo(),
                    commentCount = 18,
                    createdAt = "2025-01-20T10:00:00Z",
                    updatedAt = "2025-01-22T10:00:00Z"
                ),
                Post(
                    id = 3,
                    groupId = groupId,
                    title = "Introduction to Kotlin Flow",
                    content = "A comprehensive guide to using Kotlin Flow for reactive programming...",
                    authorName = "Alex Rivera",
                    imageUrl = null,
                    tag = tags.getOrNull(2)?.toTagInfo(),
                    commentCount = 56,
                    createdAt = "2025-01-18T10:00:00Z",
                    updatedAt = "2025-01-18T10:00:00Z"
                )
            )
            2L -> listOf(
                Post(
                    id = 4,
                    groupId = groupId,
                    title = "Compose Navigation 2.8 changes",
                    content = "The new type-safe navigation API is a game changer...",
                    authorName = "Emily Zhang",
                    imageUrl = null,
                    tag = tags.getOrNull(0)?.toTagInfo(),
                    commentCount = 31,
                    createdAt = "2025-01-22T10:00:00Z",
                    updatedAt = "2025-01-22T10:00:00Z"
                ),
                Post(
                    id = 5,
                    groupId = groupId,
                    title = "Clean Architecture with Compose",
                    content = "How I structure my Compose projects for maintainability...",
                    authorName = "David Kim",
                    imageUrl = null,
                    tag = tags.getOrNull(1)?.toTagInfo(),
                    commentCount = 67,
                    createdAt = "2025-01-19T10:00:00Z",
                    updatedAt = "2025-01-21T10:00:00Z"
                )
            )
            else -> listOf(
                Post(
                    id = 100 + groupId,
                    groupId = groupId,
                    title = "Welcome to the group!",
                    content = "Feel free to introduce yourself and share what you're working on.",
                    authorName = "Admin",
                    imageUrl = null,
                    tag = tags.firstOrNull()?.toTagInfo(),
                    commentCount = 12,
                    createdAt = "2025-01-13T10:00:00Z",
                    updatedAt = "2025-01-13T10:00:00Z"
                )
            )
        }
    }

    fun getCommentsForPost(postId: Long): List<Comment> {
        return listOf(
            Comment(
                id = 1,
                postId = postId,
                parentId = null,
                authorName = "Jane Doe",
                content = "Great post! This is exactly what I was looking for.",
                createdAt = "2025-01-22T10:00:00Z",
                updatedAt = "2025-01-22T10:00:00Z",
                replies = listOf(
                    Comment(
                        id = 2,
                        postId = postId,
                        parentId = 1,
                        authorName = "John Smith",
                        content = "Agreed! Very helpful.",
                        createdAt = "2025-01-22T14:00:00Z",
                        updatedAt = "2025-01-22T14:00:00Z",
                        replies = emptyList()
                    )
                )
            ),
            Comment(
                id = 3,
                postId = postId,
                parentId = null,
                authorName = "Bob Wilson",
                content = "Thanks for sharing this. Would love to see more content like this!",
                createdAt = "2025-01-22T22:00:00Z",
                updatedAt = "2025-01-22T22:00:00Z",
                replies = emptyList()
            )
        )
    }

    private fun Tag.toTagInfo() = TagInfo(id, name, color)
}
