# SolPan Project Context

SolPan is a modern Android application designed to provide essential information about the sun's position and path. It is built with a focus on clean architecture, testability, and a polished user experience across different device form factors.

## Project Overview

- **Purpose:** Help users understand the sun's movement and optimize solar panel orientation.
- **Main Technologies:** Kotlin, Jetpack Compose (Material 3 Expressive), Navigation 3, Coroutines, Flow, DataStore.
- **Architecture:**
    - **Single-Screen Focused:** Uses `NavigationSuiteScaffold` for top-level navigation (switching modes) and `NavDisplay` for screen content.
    - **ViewModel + StateFlow:** UI state is centralized in `SolPanViewModel` and exposed via `StateFlow`.
    - **Clean Abstractions:** Analytics are decoupled via the `AnalyticsTracker` interface.
    - **Adaptive Layout:** Utilizes a staggered grid that adjusts column counts for phone and tablet screens.
    - **Solar Logic:** Core calculations are handled in the `app.mobilemobile.solpan.solar` package.

## Building and Running

The project uses Gradle with Version Catalogs (`gradle/libs.versions.toml`).

- **Assemble Release APK:** `./gradlew assembleRelease`
- **Run Unit Tests:** `./gradlew :app:testDebugUnitTest`
- **Format Code:** `./gradlew app:spotlessApply`
- **Static Analysis (Detekt):** `./gradlew app:detekt`
- **Screenshot Testing:**
    - **Update Reference Images:** `./gradlew :app:updateDebugScreenshotTest`
    - **Validate Changes:** `./gradlew :app:validateDebugScreenshotTest`

## Development Conventions

- **State Management:** Always use `StateFlow` in the `ViewModel` to expose UI state. Keep composables stateless where possible.
- **Code Quality:**
    - Adhere to the formatting rules enforced by **Spotless** and **ktlint**.
    - Ensure all code passes **Detekt** static analysis.
- **Testing:**
    - Write unit tests for business logic in `ViewModel` and utility classes.
    - Use **Screenshot Tests** for UI components to prevent visual regressions.
- **Architecture:** Maintain the separation of concerns by using interfaces for external services (e.g., analytics, location) to facilitate testing and mockability.
- **Resources:** String resources are localized across many languages (see `app/src/main/res/values-*/strings.xml`).

## Custom Agent Tooling

This workspace is equipped with specialized agents and skills to accelerate Android development:

- **Skill: `android-cli`**: Orchestrates Android development tasks including project creation, deployment, SDK management, and environment diagnostics using the `android` command-line tool.
- **Skill: `android-dev-workflow`**: Provides expert guidance and automated workflows for Gradle tasks, UI testing, and device interactions, integrated with the `android` CLI. Activate with `activate_skill(name="android-dev-workflow")`.
- **Agent: `android-expert`**: A specialized sub-agent for advanced architecture, refactoring, and Jetpack Compose expertise, utilizing `android` CLI for UI inspection and documentation. Invoke with `@android-expert`.

## Key Packages

- `app.mobilemobile.solpan.ui`: Contains all Compose UI code, including screens, components, and themes.
- `app.mobilemobile.solpan.solar`: Logic for sun position and altitude calculations.
- `app.mobilemobile.solpan.data`: Data repositories and models (Location, User Preferences).
- `app.mobilemobile.solpan.analytics`: Analytics abstractions and implementations.
- `app.mobilemobile.solpan.orientation`: Sensors and orientation-related logic.
