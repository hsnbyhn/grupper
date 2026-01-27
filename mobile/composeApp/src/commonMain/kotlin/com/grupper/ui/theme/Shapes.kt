package com.grupper.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Grupper Shape System
 * Based on Designer Agent's design system (DESIGN-001/DESIGN-002)
 *
 * Shape sizes:
 * - Small (8dp): Tags, chips, small buttons
 * - Medium (12dp): Cards, buttons, text fields
 * - Large (16dp): FAB, dialogs, bottom sheets
 * - Full: Circular elements (avatars)
 */

object GrupperShapes {
    // Small - for chips, tags, small elements
    val Small = RoundedCornerShape(8.dp)

    // Medium - for cards, buttons, text fields
    val Medium = RoundedCornerShape(12.dp)

    // Large - for FAB, dialogs, modals
    val Large = RoundedCornerShape(16.dp)

    // Bottom Sheet - rounded top corners only
    val BottomSheet = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // Full - for circular elements (avatars, round buttons)
    val Full = RoundedCornerShape(50)
}

/**
 * Spacing/Dimension constants
 * Based on 8dp grid system with 4dp for fine-tuning
 */
object GrupperSpacing {
    val XXSmall = 2.dp
    val XSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Default = 16.dp
    val Large = 24.dp
    val XLarge = 32.dp
    val XXLarge = 48.dp

    // Touch targets
    val MinTouchTarget = 48.dp

    // Icon sizes
    val IconSmall = 16.dp
    val IconDefault = 24.dp
    val IconLarge = 32.dp
    val IconXLarge = 48.dp
    val IconHero = 64.dp

    // Component heights
    val ButtonHeight = 48.dp
    val TextFieldHeight = 56.dp
    val AppBarHeight = 56.dp
    val FABSize = 56.dp
    val CardMinHeight = 96.dp
}

/**
 * Elevation levels
 * Minimal elevation for modern, flat design
 */
object GrupperElevation {
    val Level0 = 0.dp    // Base surface
    val Level1 = 1.dp    // Cards, buttons (default)
    val Level2 = 2.dp    // FAB, app bar when scrolled
    val Level3 = 4.dp    // Modals, dialogs, menus
}
