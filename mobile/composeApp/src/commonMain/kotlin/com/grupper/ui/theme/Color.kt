package com.grupper.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Grupper Color Palette
 * Based on Designer Agent's design system (DESIGN-001)
 *
 * Primary: Warm purple for creativity and community
 * Secondary: Calming teal for supporting elements
 * Tertiary: Warm amber for accents and highlights
 */

// Primary Colors - Purple
val Primary = Color(0xFF7C3AED)           // Violet 600 - Main actions, primary buttons
val OnPrimary = Color(0xFFFFFFFF)          // White - Text/icons on primary
val PrimaryContainer = Color(0xFFEDE9FE)   // Violet 100 - Subtle primary backgrounds
val OnPrimaryContainer = Color(0xFF5B21B6) // Violet 800 - Text on primary container

// Secondary Colors - Teal
val Secondary = Color(0xFF14B8A6)          // Teal 500 - Supporting elements
val OnSecondary = Color(0xFFFFFFFF)        // White - Text/icons on secondary
val SecondaryContainer = Color(0xFFCCFBF1) // Teal 100 - Subtle secondary backgrounds
val OnSecondaryContainer = Color(0xFF0F766E) // Teal 700 - Text on secondary container

// Tertiary Colors - Amber (for tags, accents)
val Tertiary = Color(0xFFF59E0B)           // Amber 500 - Accents, highlights, tags
val OnTertiary = Color(0xFFFFFFFF)         // White - Text/icons on tertiary
val TertiaryContainer = Color(0xFFFEF3C7)  // Amber 100 - Subtle tertiary backgrounds
val OnTertiaryContainer = Color(0xFFB45309) // Amber 700 - Text on tertiary container

// Surface Colors
val Surface = Color(0xFFFFFFFF)            // White - Default background for cards, sheets
val OnSurface = Color(0xFF1F2937)          // Gray 800 - Primary text color
val SurfaceVariant = Color(0xFFF3F4F6)     // Gray 100 - Alternative surface (backgrounds)
val OnSurfaceVariant = Color(0xFF6B7280)   // Gray 500 - Secondary text color

// Background Colors
val Background = Color(0xFFF9FAFB)         // Gray 50 - App background
val OnBackground = Color(0xFF1F2937)       // Gray 800 - Text on background

// Outline Colors
val Outline = Color(0xFFD1D5DB)            // Gray 300 - Borders, dividers
val OutlineVariant = Color(0xFFE5E7EB)     // Gray 200 - Subtle borders

// Error Colors
val Error = Color(0xFFEF4444)              // Red 500
val OnError = Color(0xFFFFFFFF)            // White
val ErrorContainer = Color(0xFFFEE2E2)     // Red 100
val OnErrorContainer = Color(0xFF991B1B)   // Red 800

// Inverse Colors (for snackbars, etc.)
val InverseSurface = Color(0xFF1F2937)     // Gray 800
val InverseOnSurface = Color(0xFFF9FAFB)   // Gray 50
val InversePrimary = Color(0xFFA78BFA)     // Violet 400

// Scrim
val Scrim = Color(0xFF000000)              // Black for modal overlays
