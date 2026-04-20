# Contributing to SolPan

We're thrilled that you're interested in contributing to SolPan! Your help is welcome and appreciated. This document outlines the process for contributing to the project.

## How to Contribute

There are many ways to contribute, from reporting bugs to submitting code changes. Here are some guidelines to help you get started.

### Reporting Bugs

If you find a bug, please create an issue in our [GitHub Issues](https://github.com/mobilemobilellc/solpan/issues) tracker. When you are creating a bug report, please include as many details as possible:

- A clear and descriptive title.
- A detailed description of the problem, including steps to reproduce it.
- The version of the app you're using.
- Information about your device and Android version.
- Any relevant logs or screenshots.

### Suggesting Enhancements

If you have an idea for a new feature or an improvement to an existing one, please create an issue in our [GitHub Issues](https://github.com/mobilemobilellc/solpan/issues) tracker. Please provide:

- A clear and descriptive title.
- A detailed description of the proposed feature and why it would be valuable.
- Any mockups or examples that might help illustrate your idea.

### Pull Request Process

We welcome pull requests! If you'd like to contribute code, please follow these steps:

1.  **Fork the repository** to your own GitHub account.
2.  **Create a new branch** for your changes (`git checkout -b feature/your-feature-name` or `bugfix/issue-description`).
3.  **Make your changes** to the code. Ensure your code adheres to the project's existing style and formatting.
4.  **Run `./gradlew app:spotlessApply`** to format your code.
5.  **Run the full CI check locally** before submitting:
    ```bash
    # Static analysis + formatting check + unit tests
    ./gradlew app:detekt app:spotlessCheck :app:testDebugUnitTest

    # Verify screenshots haven't unexpectedly changed
    ./gradlew :app:validateDebugScreenshotTest
    ```
    If you changed any UI components, regenerate the reference images and commit them:
    ```bash
    ./gradlew :app:updateDebugScreenshotTest
    ```
6.  **Test your changes** to ensure they work as expected and do not introduce any new bugs. Run all existing unit and instrumentation tests.
6.  **Commit your changes** with a clear and concise commit message.
7.  **Push your branch** to your fork.
8.  **Create a pull request** from your fork to the main SolPan repository. Please provide a clear description of the changes you've made.

We will review your pull request as soon as possible and provide feedback. Thank you for your contribution!

## Project Structure

SolPan has been modernized with a multi-module architecture using Android Gradle Plugin 9.0:

### Modules

- **`:app`** - Main application module with UI and entry point
- **`:feature:optimizer`** - Feature module containing SolPan ViewModel and business logic
- **`:core:model`** - Core data models (TiltMode, LocationData, OptimalPanelParameters, etc.)
- **`:core:data`** - Repository implementations and data layer
- **`:core:analytics`** - Analytics abstraction and Firebase integration
- **`:core:designsystem`** - Reusable UI components, themes, and design tokens
- **`:core:solar`** - Solar calculation logic (SolarCalculator)
- **`:build-logic`** - Gradle convention plugins for shared build configuration

### Build System

The project uses:
- **AGP 9.0** with new DSL interfaces and built-in Kotlin support
- **Gradle 9.1.0+** for compatibility with AGP 9
- **Kotlin 2.3.20+** for full AGP 9 support
- **Convention plugins** in `build-logic/` for consistent module configuration

## Development Commands

### Building

```bash
# Assemble debug APK
./gradlew :app:assembleDebug

# Assemble release APK
./gradlew :app:assembleRelease
```

### Testing

```bash
# Run all unit tests
./gradlew :app:testDebugUnitTest

# Run a specific test class
./gradlew :app:testDebugUnitTest --tests "app.mobilemobile.solpan.SolPanViewModelTest"
```

### Code Quality

```bash
# Format code with Spotless (ktlint + ktfmt)
./gradlew app:spotlessApply

# Check formatting without applying
./gradlew app:spotlessCheck

# Run Detekt static analysis
./gradlew app:detekt

# Full CI check (same as CI pipeline)
./gradlew app:detekt app:spotlessCheck :app:testDebugUnitTest :app:assembleDebug --parallel
```

### Screenshot Tests

```bash
# Generate/update screenshot reference images
./gradlew :app:updateDebugScreenshotTest

# Validate screenshots against references
./gradlew :app:validateDebugScreenshotTest
```

## Architecture Highlights

### State Management

- **ViewModel + StateFlow**: `SolPanViewModel` exposes UI state as immutable `StateFlow`
- **Reactive**: All data flows (location, orientation, location preferences) are combined reactively
- **No DI Framework**: Dependencies are wired via constructor injection and Compose's `remember {}`

### Testing Strategy

- **Unit Tests**: Test business logic in ViewModel, repositories, and utilities
- **Mocking**: Use Fake implementations (e.g., `FakeMagneticDeclinationProvider`) for testable abstractions
- **Dependency Extraction**: Platform-specific code (like `GeomagneticField`) is abstracted behind interfaces

### Code Quality Standards

- **Apache 2.0 License Headers**: Required on all Kotlin and Gradle files
- **ktlint with Compose Rules**: Official Kotlin style with no star imports
- **Detekt**: Static analysis with baseline configuration (max issues: 0)
- **Format on Save**: Configure IDE to run `spotlessApply` on save for convenience

## Key Classes and Packages

- `app.mobilemobile.solpan.optimizer.SolPanViewModel` - Main state container
- `app.mobilemobile.solpan.solar.SolarCalculator` - Sun position calculations
- `app.mobilemobile.solpan.orientation.DeviceOrientationController` - Sensor fusion (accelerometer + magnetometer)
- `app.mobilemobile.solpan.location.DeviceLocationManager` - GPS location updates
- `app.mobilemobile.solpan.ui.screen.SolPanScreen` - Main UI composable with Navigation
- `app.mobilemobile.solpan.designsystem.theme.SolPanTheme` - Material 3 Expressive theming

## Questions?

If you have any questions or need help, feel free to:
- Open a GitHub Discussion
- Check existing Issues for similar questions
- Review the project README for architectural diagrams
