<!--
Copyright 2025 MobileMobile LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Code Coverage & Quality Metrics

SolPan maintains comprehensive code quality metrics to ensure reliability, maintainability, and consistent test coverage. This document outlines the tools, thresholds, and reporting mechanisms used.

## Overview

Code coverage and quality are enforced through:

1. **JaCoCo** — Code coverage analysis tool integrated with Gradle
2. **GitHub Actions** — Automated coverage reporting on each commit/PR
3. **Quality Gates** — Minimum coverage thresholds prevent regressions
4. **Public Reports** — Coverage artifacts accessible from CI/CD pipeline

## Coverage Tools

### JaCoCo Integration

JaCoCo is configured to analyze unit test coverage on every build:

```bash
# Generate coverage report
./gradlew app:jacocoTestReport

# Output files:
# - app/build/reports/jacoco/html/ — HTML coverage report
# - app/build/reports/jacoco/jacoco.csv — CSV for parsing
# - app/build/reports/jacoco/jacoco.xml — XML for CI tools
```

**Configuration:**
- **Version:** 0.8.12
- **Report Format:** HTML, CSV, XML
- **Scope:** Unit tests only (testDebugUnitTest)
- **Build Types:** Debug (coverage enabled) + Release (minification)

### Android Gradle Plugin Coverage

AGP 9 provides built-in unit test coverage via `enableUnitTestCoverage`:

```gradle
buildTypes {
    debug {
        enableUnitTestCoverage = true  // Enables .exec generation
    }
}
```

Coverage execution data is generated at:
```
app/build/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec
```

## Coverage Targets

SolPan targets **80% line coverage** on all production code, measured over:

- `app/src/main/java/**` — UI, screens, composables
- `core/model/**` — Data models and entities
- `core/solar/**` — Solar calculation logic (critical)
- `core/data/**` — Repositories and data access
- `feature/optimizer/**` — ViewModel and optimization logic

**Excluded from coverage:**
- `**/R.class` — Android resource bindings
- `**/BuildConfig.*` — Compiler-generated
- `**/*Test*` — Test fixtures
- `**/databinding/**` — Data binding generated code
- `**/generated/**` — All generated code
- `**/Manifest*` — Manifest files

## CI/CD Integration

### Automated Coverage Reporting

The `.github/workflows/build.yml` workflow:

1. **Runs tests with coverage** — `app:testDebugUnitTest` with JaCoCo enabled
2. **Generates report** — `app:jacocoTestReport` creates HTML, CSV, XML
3. **Uploads artifacts** — Coverage reports available as CI artifacts
4. **Comments on PRs** — Coverage percentage posted automatically

**Example PR comment:**
```
## Code Coverage Report

📊 **Line Coverage: 78.5%**

- Lines Covered: 2,340
- Lines Missed: 642
```

### Artifact Retention

Coverage reports are retained in GitHub Actions artifacts for 90 days:
- Artifact name: `coverage-reports`
- Path: `app/build/reports/jacoco/`
- Includes: HTML report, CSV, XML

Access from Actions tab → Workflow run → "coverage-reports"

## Local Coverage Analysis

### Generate Coverage Report

```bash
# Run tests and generate coverage
./gradlew :app:testDebugUnitTest app:jacocoTestReport

# View HTML report
open app/build/reports/jacoco/html/index.html
```

### Coverage Report Structure

```
app/build/reports/jacoco/
├── html/
│   ├── index.html          # Entry point
│   ├── css/
│   ├── js/
│   └── [package-hierarchy] # Drill-down coverage by package
├── jacoco.csv              # Machine-readable coverage data
└── jacoco.xml              # XML format for other tools
```

### Interpreting the HTML Report

1. **Package Coverage** — Top-level shows each package's coverage percentage
2. **Class Coverage** — Drill into packages to see class-level metrics
3. **Line Coverage** — Hover over code lines to see if they're covered
4. **Branch Coverage** — Conditional coverage for if/else, loops, etc.

**Color coding:**
- 🟢 Green — Covered by at least one test
- 🔴 Red — Not covered by any test
- 🟡 Yellow — Partially covered (some branches only)

## Quality Gates

### Pre-commit Checks

Developers must maintain coverage on modified code:

```bash
# Before committing:
./gradlew :app:testDebugUnitTest app:jacocoTestReport

# Verify coverage on modified lines
# If coverage drops, write additional tests
```

### CI Workflow Enforcement

- ✅ Tests must pass (`./gradlew :app:testDebugUnitTest`)
- ✅ No Detekt violations (`./gradlew app:detekt`)
- ✅ Formatting must pass (`./gradlew app:spotlessCheck`)
- ✅ Build must succeed (`./gradlew :app:assembleDebug`)
- ⚠️ Coverage report generated (informational, not blocking)

### Future Enforcement

To enforce minimum coverage (e.g., 80%) as a CI blocker:

```bash
# Add to build.yml after test step:
./gradlew app:jacocoTestReport --check-coverage-thresholds=80

# Or use a GitHub Action like:
# - uses: codecov/codecov-action@v3
#   with:
#     minimum-coverage: 80
```

## Coverage Measurement

### Line Coverage (Primary Metric)

**Measures:** Percentage of executable code lines executed by tests

**Formula:**
```
Coverage = (Lines Executed / Total Executable Lines) × 100%
```

**Example:**
```
SolarCalculator:
- Total lines: 142
- Lines tested: 118
- Coverage: 83.1% ✅
```

### Branch Coverage (Secondary)

**Measures:** Percentage of conditional branches (if/else, loops) covered

**Example:**
```
isLocationPermitted():
- Branches: 2 (permitted, denied)
- Tested: 2
- Coverage: 100% ✅
```

### Method Coverage (Indicator)

**Measures:** Percentage of methods with at least one test

**Example:**
```
SolPanViewModel:
- Total methods: 8
- Methods tested: 7
- Coverage: 87.5% ✅
```

## Test Coverage By Module

### High-Priority Modules (Target: >85%)

- **`core/solar/`** — Critical solar calculations; must be thoroughly tested
- **`feature/optimizer/`** — ViewModel & UI state logic
- **`core/data/`** — Repositories and data persistence

### Medium-Priority Modules (Target: >75%)

- **`core/model/`** — Data classes and enums
- **`core/analytics/`** — Event tracking logic

### Lower-Priority (Target: >60%)

- **`core/designsystem/`** — Compose theme & components (visual testing)
- **`feature/ui/`** — Screen composables (covered by screenshot tests)

## Viewing Coverage Reports

### On GitHub Actions

1. Go to repository → **Actions** tab
2. Click desired workflow run
3. Scroll to **Artifacts** section
4. Download `coverage-reports` zip
5. Extract and open `jacoco/html/index.html`

### In GitHub PR

Coverage comment is automatically posted to PRs showing:
- Line coverage percentage
- Total covered/missed lines
- Trend indicator (↑ improving, ↓ declining)

### Local Development

```bash
# After running coverage tasks:
cd app/build/reports/jacoco/html
python3 -m http.server 8000
# Visit http://localhost:8000
```

## Improving Coverage

### Strategies for Increasing Coverage

1. **Add unit tests for uncovered code**
   ```kotlin
   // Find red lines in coverage report
   // Write tests for those code paths
   
   @Test
   fun testSolarPositionAtMidnight() {
       // Add test for midnight edge case
   }
   ```

2. **Test error paths and edge cases**
   ```kotlin
   @Test
   fun testLocationPermissionDenied() {
       // Verify behavior when permission is denied
   }
   ```

3. **Use parametrized tests for branch coverage**
   ```kotlin
   @ParameterizedTest
   @ValueSource(ints = [0, 15, 30, 45, 60, 75, 90])
   fun testAzimuthCalculation(angle: Int) {
       // Test multiple scenarios efficiently
   }
   ```

4. **Mock external dependencies**
   ```kotlin
   @Test
   fun testViewModelWithMockedLocation() {
       val mockLocationRepository = mockk<LocationRepository>()
       every { mockLocationRepository.location } returns flowOf(testLocation)
       // Test ViewModel behavior
   }
   ```

### Testing Checklist

- [ ] Happy path covered?
- [ ] Error cases covered?
- [ ] Edge cases (0, negative, max values)?
- [ ] Null handling?
- [ ] State transitions?
- [ ] User interactions (clicks, scrolls)?

## Troubleshooting

### JaCoCo Report Not Generated

**Cause:** Coverage execution data not found

**Solution:**
```bash
# Ensure coverage is enabled in build types
grep -A 2 "buildTypes" app/build.gradle.kts

# Verify .exec file exists
find app/build -name "*.exec"

# Clean and rebuild
./gradlew clean :app:testDebugUnitTest
```

### Coverage Report Shows 0%

**Cause:** Class directories or source directories misconfigured

**Solution:**
```bash
# Verify plugin configuration
./gradlew app:jacocoTestReport --info | grep -i "classDirectories\|sourceDirectories"

# Check build outputs exist
ls app/build/tmp/kotlin-classes/debug
ls app/build/outputs/unit_test_code_coverage/debugUnitTest
```

### GitHub Action Fails to Upload Coverage

**Cause:** Report path doesn't exist or permission denied

**Solution:**
1. Check workflow logs for exact path
2. Verify `jacocoTestReport` runs successfully
3. Ensure report path in `.github/workflows/build.yml` is correct

## References

- [JaCoCo Documentation](https://www.jacoco.org/jacoco/)
- [Android Gradle Plugin Coverage](https://developer.android.com/build/instrumented-tests#utp-coverage)
- [Codecov Integration](https://codecov.io/)
- [GitHub Actions Artifacts](https://docs.github.com/en/actions/using-workflows/storing-workflow-data-as-artifacts)
