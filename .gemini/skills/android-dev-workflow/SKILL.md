# Android Development Workflow

This skill provides specialized procedures for Android development within the SolPan project, leveraging both project-specific Gradle tasks and the global `android` CLI for efficient development.

## Core Workflows

### 1. Code Quality & Formatting
Always run these before committing:
- **Format code**: `./gradlew app:spotlessApply`
- **Linting**: `./gradlew app:detekt`
- **Dry-run check**: `./gradlew app:spotlessCheck app:detekt`

### 2. Testing & UI Validation
- **Unit Tests**: `./gradlew :app:testDebugUnitTest`
- **Screenshot Tests**:
    - **Update References**: `./gradlew :app:updateDebugScreenshotTest` (Run this after intentional UI changes)
    - **Validate**: `./gradlew :app:validateDebugScreenshotTest`
- **Instrumentation Tests**: `./gradlew :app:connectedDebugAndroidTest`
- **UI Inspection**: Use `android layout --pretty` to inspect the view hierarchy and `android layout --diff` to track changes.

### 3. Device & UI Interactions
- **Capture Screenshot**: `android screen capture -o screen.png`
- **Annotated Screenshot**: `android screen capture --annotate -o screen.png` (Useful for finding UI elements)
- **Resolve UI Element**: `android screen resolve --screen screen.png --string "#3"` (Returns coordinates for a label)
- **Record Video**: `adb shell screenrecord /sdcard/demo.mp4` (Stop with Ctrl+C, then pull)
- **Check Device Logcat**: `adb logcat -d | grep "SolPan"`
- **Clear App Data**: `adb shell pm clear app.mobilemobile.solpan`
- **Check Permissions**: `adb shell dumpsys package app.mobilemobile.solpan | grep "permission"`

### 4. Environment & SDK Management
- **List Installed SDKs**: `android sdk list`
- **Install SDK Package**: `android sdk install "platforms;android-34"`
- **Manage Emulators**: `android emulator list`, `android emulator start <name>`, `android emulator stop <name>`
- **Environment Info**: `android info`

### 5. Deployment & Profiling
- **Deploy App**: `android run` (Installs and launches the application)
- **Generate Baseline Profile**: `./gradlew :app:generateBaselineProfile`
- **Build Release APK**: `./gradlew assembleRelease` (Ensure `ANDROID_KEYSTORE_PATH` etc. are set if signing)

## Expert Guidance
- **Documentation**: Use `android docs search <keywords>` to find high-quality Android development articles and API guides.
- **Material 3 Expressive**: When adding new UI components, refer to `app/src/main/java/app/mobilemobile/solpan/ui/components/`. Use `MaterialTheme.colorScheme` and `MaterialTheme.typography` consistently.
- **Navigation 3**: Follow the pattern in `SolPanApp.kt`. Define screens as `NavKey` objects and use `entryProvider`.
- **StateFlow**: Expose state from ViewModels using `StateFlow`. Use `whileSubscribed(5000)` for efficiency.
- **Localization**: When adding strings, check `app/src/main/res/values/strings.xml` and ensure keys are descriptive.
