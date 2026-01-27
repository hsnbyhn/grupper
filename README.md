# Grupper

A user-friendly group discussion mobile application - simpler and more intuitive than Reddit or Discord.

## 🏗️ Project Structure (Monorepo)

```
grupper/
├── backend/          # Kotlin + Ktor REST API
├── mobile/           # Kotlin Multiplatform + Compose Multiplatform (Android & iOS)
├── .github/          # CI/CD workflows
└── CLAUDE.md         # Multi-agent development system documentation
```

## 🚀 Tech Stack

### Backend
- **Language**: Kotlin
- **Framework**: Ktor 2.3+
- **Database**: PostgreSQL (Exposed ORM)
- **Architecture**: RESTful API

### Mobile
- **Framework**: Kotlin Multiplatform (KMP)
- **UI**: Compose Multiplatform (shared UI for Android & iOS)
- **Navigation**: androidx.navigation-compose 2.9.1
- **Networking**: Ktor Client
- **Dependency Injection**: Koin

## 🎯 Phase 1 Features (No Authentication)

- **Groups**: Browse and create public groups
- **Tags**: Custom tags per group (admin-created)
- **Posts**: Create posts with title, content, image, tag, and author name
- **Comments**: Nested/threaded comments on posts
- **Filtering**: Filter posts by tags
- **Sorting**: Posts by newest, oldest, or most commented

## 🛠️ Development Setup

### Prerequisites
- **JDK 21** (required for both backend and mobile)
- **Android SDK** (for Android development)
- **Xcode** (for iOS development, macOS only)
- **Gradle 8.10** (included via wrapper)

### Backend Setup

```bash
cd backend
./gradlew build
./gradlew run
# Backend runs at http://localhost:8080
```

### Mobile Setup

#### Android
```bash
cd mobile
export JAVA_HOME=/path/to/jdk21  # Or set in gradle.properties
./gradlew :composeApp:assembleDebug
# Or open in Android Studio and run
```

#### iOS
```bash
cd mobile
./gradlew :composeApp:compileKotlinIosSimulatorArm64
# Then open iosApp/iosApp.xcodeproj in Xcode and run
```

## 🔄 CI/CD

GitHub Actions workflows:
- **Backend CI**: Builds and tests on every push to `backend/`
- **Mobile Android CI**: Builds APK on every push to `mobile/`
- **Mobile iOS CI**: Builds iOS framework (requires macOS runner)

## 📱 Current Status

### Completed (Mobile)
- ✅ Theme system (colors, typography, shapes)
- ✅ Reusable UI components (buttons, cards, text fields, etc.)
- ✅ Navigation architecture with animations
- ✅ Groups List screen with mock data
- ✅ API client setup (Ktor)
- ✅ Data models (Group, Post, Tag, Comment)

### Completed (Backend)
- ✅ Ktor project setup
- ✅ PostgreSQL database connection
- ✅ Database tables (Groups, Tags, Posts, Comments)
- ✅ Groups repository with CRUD operations

### In Progress
- 🚧 Group Detail screen (next)
- 🚧 Backend API routes

## 🎨 Design System

- **Primary Color**: Purple (#7C3AED) - Creative, community-focused
- **Secondary Color**: Teal (#14B8A6) - Calming contrast
- **Tertiary Color**: Amber (#F59E0B) - Tags and highlights
- **Typography**: Inter (Android) / SF Pro (iOS)
- **Spacing**: 8dp base grid
- **Border Radius**: Small (8dp), Medium (12dp), Large (16dp)

## 📚 Architecture

### Backend
- RESTful API design
- Repository pattern for data access
- Exposed DSL for type-safe SQL queries

### Mobile
- MVVM architecture
- Unidirectional data flow (StateFlow)
- Compose navigation with type-safe routes
- Platform-specific code only where needed (expect/actual)

## 🤝 Contributing

This project uses a multi-agent development system. See `CLAUDE.md` for details.

## 📄 License

[Add your license here]

## 🔗 Links

- [Design System Mockup](design-system-mockup.html)
- [Backend API Documentation](backend/README.md) *(coming soon)*
- [Mobile App Documentation](mobile/README.md) *(coming soon)*
