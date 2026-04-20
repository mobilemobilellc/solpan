# SolPan Architecture Deep Dive

## Overview

SolPan demonstrates a modern, clean Android architecture built on Jetpack Compose, combining single-activity pattern, reactive state management, and modular design. This document provides a comprehensive analysis of the architectural decisions and patterns used.

## High-Level Architecture

```
┌─────────────────────────────────────────────────────┐
│          SolPanActivity (Compose UI)                │
│  • enableEdgeToEdge() for immersive UI              │
│  • Theme application (Material 3 Expressive)        │
└──────────────┬──────────────────────────────────────┘
               │
        ┌──────▼──────────┐
        │  SolPanApp()    │
        │  Navigation     │
        └──────┬──────────┘
               │
     ┌─────────┴──────────┐
     ▼                    ▼
NavigationSuiteScaffold  NavDisplay
(Adaptive Layout)       (Navigation3)
```

## Modular Architecture

SolPan uses a multi-module structure following Android best practices:

### Dependency Graph

```
:app (Application)
├── :feature:optimizer
│   └── :core:model
│   └── :core:data
│   └── :core:analytics
├── :core:designsystem
├── :core:solar
└── :build-logic (Convention plugins)
```

### Module Responsibilities

| Module | Purpose | Key Classes |
|--------|---------|------------|
| `:app` | Main application, Activity, UI composition | SolPanActivity, SolPanApp |
| `:feature:optimizer` | Feature module with ViewModel and business logic | SolPanViewModel, SolPanUiState |
| `:core:model` | Pure data models (no dependencies) | TiltMode, LocationData, SolarPosition |
| `:core:data` | Repository implementations | DefaultLocationRepository, DataStoreUserPreferencesRepository |
| `:core:analytics` | Analytics abstraction | AnalyticsTracker, FirebaseAnalyticsTracker |
| `:core:designsystem` | Reusable UI components, theming | SolPanTheme, Material 3 components |
| `:core:solar` | Domain logic for solar calculations | SolarCalculator (wraps commons-suncalc) |
| `:build-logic` | Gradle convention plugins | AndroidApplicationConventionPlugin, AndroidLibraryConventionPlugin |

### Dependency Inversion

Each layer depends on abstractions, not concrete implementations:

```
SolPanViewModel
  └── depends on
      ├── MagneticDeclinationProvider (interface)
      ├── LocationRepository (interface)
      └── UserPreferencesRepository (interface)
```

Implementations are injected at composition time, enabling:
- Easy testing with fake implementations
- Swappable backends
- Clear separation of concerns

## State Management Pattern

### Reactive Architecture

SolPanViewModel uses Kotlin Flow to combine multiple data streams:

```kotlin
// ViewModel combines multiple reactive sources
val optimalPanelParameters: StateFlow<OptimalPanelParameters?> = combine(
    currentLocation,           // Location from GPS
    magneticDeclinationFlow,   // Calculated or fetched
    currentOrientation,        // From device sensors
    selectedTiltMode,          // User selection
) { location, declination, orientation, mode ->
    if (location != null) {
        calculateOptimalPanel(location, declination, orientation, mode)
    } else null
}.stateIn(viewModelScope, WhileSubscribed(5000), null)
```

**Why this pattern?**
- Reactive: Updates propagate automatically
- Testable: Each stream can be mocked independently
- Efficient: Uses WhileSubscribed(5000) for resource management
- Type-safe: No casting needed

### UI State Flow

```
User Interaction
    ↓
ViewModel collects state
    ↓
StateFlow emits updates
    ↓
Composable recomposes
    ↓
Screen updates
```

## Lifecycle Management

### Activity Lifecycle Handling

```kotlin
class SolPanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Modern full-screen UI
        setContent { SolPanTheme { SolPanApp(modifier = Modifier.fillMaxSize()) } }
    }
}
```

### ViewModel Lifecycle

- ViewModel survives configuration changes (Activity recreation)
- ViewModelScope tied to ViewModel lifecycle
- Automatic cleanup via `WhileSubscribed` scope
- No memory leaks from Composables holding ViewModels

### Fragment/Compose Integration

SolPan uses Navigation3 with Compose:
- RememberViewModelStoreNavEntryDecorator: Preserves ViewModel across navigation
- RememberSaveableStateHolderNavEntryDecorator: Preserves Compose state

## UI Architecture

### Material Design 3 Expressive

SolPan uses Material Design 3's Expressive system:

```kotlin
SolPanTheme {  // Provides colors, typography, shapes
    NavigationSuiteScaffold {  // Adaptive layout (phone/tablet)
        SolPanScreen()  // Main content
    }
}
```

### Adaptive Layout

NavigationSuiteScaffold automatically adapts:
- **Phone**: Bottom navigation bar
- **Tablet**: Lateral navigation rail
- **Foldable**: Responds to hinge position

```kotlin
NavigationSuiteScaffold(
    navigationSuiteItems = {
        TiltMode.entries.forEach { mode ->
            item(
                selected = currentScreen is SolPan && currentScreen.mode == mode,
                onClick = { /* navigate */ },
                icon = { Icon(mode.icon, ...) },
                label = { Text(stringResource(mode.titleRes)) },
            )
        }
    }
)
```

## Navigation Pattern

### Navigation3 with Serializable Routes

Type-safe navigation using kotlinx.serialization:

```kotlin
@Serializable
data class SolPan(val mode: TiltMode) : NavKey

@Serializable
data object AboutLibraries : NavKey
```

Benefits:
- Compile-time type safety
- State preservation across app kills
- No string-based routes (less error-prone)

## Data Flow Example: Location Update

```
User grants location permission
    ↓
DeviceLocationManager.startLocationUpdates()
    ↓
Google Play Services Fused Location Provider
    ↓
LocationRepository.locationFlow emits LocationData
    ↓
SolPanViewModel receives update
    ↓
combine() recalculates OptimalPanelParameters
    ↓
optimalPanelParameters.collect() in UI
    ↓
Composable recomposes with new data
    ↓
Screen shows updated panel orientation
```

## Testability Strategy

### Dependency Injection for Testing

```kotlin
// Production
val viewModel = SolPanViewModel(
    mode = TiltMode.REALTIME,
    userPreferencesRepository = preferencesRepo,
    locationRepository = locationRepo,
    analyticsTracker = FirebaseAnalyticsTracker(),
)

// Testing
val viewModel = SolPanViewModel(
    mode = TiltMode.REALTIME,
    userPreferencesRepository = FakeUserPreferencesRepository(),
    locationRepository = FakeLocationRepository(),
    analyticsTracker = FakeAnalyticsTracker(),
)
```

### Platform-Specific Abstraction

GeomagneticField is a native Android API that fails in unit tests:

```kotlin
// Production: Uses real GeomagneticField
class AndroidMagneticDeclinationProvider : MagneticDeclinationProvider {
    override fun getDeclination(...) = GeomagneticField(...).declination
}

// Testing: Returns fixed value
class FakeMagneticDeclinationProvider : MagneticDeclinationProvider {
    override fun getDeclination(...) = 10f
}
```

## Performance Considerations

### Startup Performance

1. **Cold start (TTID < 1.5s)**
   - enableEdgeToEdge() avoids window insets calculation
   - Baseline profiles pre-compile critical paths
   - ViewModel lazy initialization

2. **Recomposition Efficiency**
   - Composables are pure functions
   - Modifiers are immutable
   - State changes only trigger affected branches

3. **Memory Usage**
   - Location and sensor data not persisted
   - DataStore for user preferences only
   - Coroutines properly scoped

### Baseline Profiles

Located in `:baselineprofile` module:
- Pre-compiles startup path
- Reduces jank in critical user flows
- Generated via Chrome tracing

## Security & Privacy

### Sensitive Data Handling

- **Location**: Only accessed with explicit user permission
- **Sensors**: Processed locally, never logged
- **User Prefs**: Encrypted in DataStore
- **Analytics**: Configured to exclude PII

### Permissions Model

```kotlin
permissions.forEach { permission ->
    if (ActivityCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), REQUEST_CODE)
    }
}
```

## Future Extensibility

### Adding New Tilt Modes

1. Add variant to TiltMode enum
2. Add calculation logic to SolarCalculator
3. Add UI option to navigation
4. ViewModel automatically adapts

### Adding New Repositories

1. Implement Repository interface
2. Inject into ViewModel
3. Add to Compose remember { } block
4. Fully testable

### Adding New Analytics Events

1. Extend AnalyticsTracker interface
2. Implement in FirebaseAnalyticsTracker
3. Call from ViewModel or Composables
4. Abstracting makes it swappable

## Architecture Principles

1. **Single Responsibility**: Each module has one reason to change
2. **Dependency Inversion**: Depend on abstractions, not concretions
3. **Composition**: Build UI from small, reusable composables
4. **Reactivity**: Data flows drive UI updates
5. **Testability**: Architecture enables easy unit testing
6. **Accessibility**: Semantic descriptions throughout

## Comparison with Other Patterns

| Pattern | SolPan | Use Case |
|---------|--------|----------|
| MVI | ✓ | Uses ModelViewIntent-like patterns |
| MVVM | ✓ | ViewModel + StateFlow |
| Redux | ✗ | Too complex for single screen |
| MVP | ✗ | Compose replaces View layer |
| Clean Architecture | ✓ | Modular layers with clear contracts |

## Conclusion

SolPan's architecture demonstrates best practices for modern Android development:
- Modular structure for scalability
- Reactive patterns for responsive UI
- Dependency injection for testability
- Clean separation of concerns
- Modern Jetpack libraries
- Accessibility-first design

The architecture scales from a single-screen app to a full-featured application while maintaining code quality and testability throughout.
