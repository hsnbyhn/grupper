package com.grupper.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.grupper.ui.screens.GroupsListScreen
import com.grupper.ui.screens.GroupDetailScreen
import com.grupper.ui.screens.PostDetailScreen
import com.grupper.ui.screens.CreateEditGroupScreen

/**
 * Grupper Navigation Graph
 * Based on Designer Agent's navigation structure (DESIGN-003)
 *
 * Transition animations:
 * - Forward: Slide from right (300ms)
 * - Back: Slide from left (300ms)
 * - Modals: Slide from bottom with fade
 */

private const val ANIMATION_DURATION = 300

@Composable
fun GrupperNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.GroupsList
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ANIMATION_DURATION)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(ANIMATION_DURATION)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(ANIMATION_DURATION)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(ANIMATION_DURATION)
            )
        }
    ) {
        // Groups List - Home screen
        composable<Screen.GroupsList> {
            GroupsListScreen(
                onGroupClick = { groupId ->
                    navController.navigate(Screen.GroupDetail(groupId))
                },
                onCreateGroupClick = {
                    navController.navigate(Screen.CreateGroup)
                }
            )
        }

        // Group Detail
        composable<Screen.GroupDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.GroupDetail>()
            GroupDetailScreen(
                groupId = route.groupId,
                onBackClick = { navController.popBackStack() },
                onPostClick = { postId ->
                    navController.navigate(Screen.PostDetail(postId))
                },
                onCreatePostClick = {
                    navController.navigate(Screen.CreatePost(route.groupId))
                }
            )
        }

        // Post Detail
        composable<Screen.PostDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PostDetail>()
            PostDetailScreen(
                postId = route.postId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Create Group (Modal)
        composable<Screen.CreateGroup>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            },
            popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) {
            CreateEditGroupScreen(
                groupId = null,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Edit Group (Modal)
        composable<Screen.EditGroup>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            },
            popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.EditGroup>()
            CreateEditGroupScreen(
                groupId = route.groupId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Create Post (Modal)
        composable<Screen.CreatePost>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            },
            popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.CreatePost>()
            CreateEditPostPlaceholder(
                groupId = route.groupId,
                postId = null,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Edit Post (Modal)
        composable<Screen.EditPost>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            },
            popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.EditPost>()
            CreateEditPostPlaceholder(
                groupId = null,
                postId = route.postId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        // Manage Tags
        composable<Screen.ManageTags> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ManageTags>()
            ManageTagsPlaceholder(
                groupId = route.groupId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
