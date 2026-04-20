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

# Release Process

SolPan uses automated semantic versioning and release management via [release-please](https://github.com/googleapis/release-please) and GitHub Actions. This ensures consistent versioning, changelog generation, and streamlined releases.

## Overview

The release workflow operates on two levels:

1. **Automated Release PR Creation** — `release-please` monitors commits to `main`, detects conventional commits, and opens a Release PR with bumped version numbers and generated changelog
2. **Automated Tag & Release Creation** — When a Release PR is merged, `release-please` creates a git tag, which triggers the `release.yml` workflow to build, sign, and upload release artifacts to GitHub Releases and Google Play

## Conventional Commits

All commits to `main` must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Commit Types

- **`feat:`** New feature → **MINOR** version bump (e.g., 1.0.0 → 1.1.0)
- **`fix:`** Bug fix → **PATCH** version bump (e.g., 1.0.0 → 1.0.1)
- **`perf:`** Performance improvement → **PATCH** version bump
- **`docs:`** Documentation only → **PATCH** version bump
- **`ci:`** CI/CD changes → **PATCH** version bump
- **`chore:`** Maintenance, dependencies → **PATCH** version bump (hidden in changelog)
- **`BREAKING CHANGE:`** footer → **MAJOR** version bump (e.g., 1.0.0 → 2.0.0)

### Examples

**Feature commit:**
```
feat: add summer tilt mode optimization

- Implements seasonal latitude adjustment based on Earth's axial tilt
- Adds preview functions for all tilt modes
```

**Bug fix:**
```
fix: correct magnetic declination calculation

Fixes incorrect azimuth offset in high-latitude regions.
```

**Breaking change:**
```
feat: refactor SolarCalculator API

BREAKING CHANGE: calculateSolarPosition() now requires altitude parameter
```

**Chore (hidden from changelog):**
```
chore: upgrade kotlin to 2.0.0
```

## Automated Workflow

### Step 1: Merge Conventional Commits to `main`

```bash
git checkout main
git pull origin main

# Create feature branch
git checkout -b feat/new-feature

# Make changes...

# Commit with conventional format
git commit -m "feat: add new feature

Detailed description of changes."

# Push and create pull request
git push origin feat/new-feature
```

### Step 2: Release PR Created (Automatic)

When commits are merged to `main`, the `release-please` workflow (`.github/workflows/release-please.yml`) automatically:

1. **Analyzes commits** since last release using conventional commits format
2. **Determines version bump** (MAJOR/MINOR/PATCH) based on commit types
3. **Updates version files**:
   - `gradle.properties` (appVersionName, appVersionCode)
   - `version.properties` (versionName, versionCode)
4. **Generates CHANGELOG.md** with new version entries organized by type
5. **Opens Release PR** with all changes

**Example Release PR:**
- Title: `chore(release): v1.1.0`
- Description: Lists all changes, new version, updated files
- Status: Ready to merge

### Step 3: Merge Release PR

Review the Release PR to verify:
- ✅ Version bump is correct
- ✅ Changelog entries are accurate
- ✅ All changes look good

Then merge the PR:

```bash
# Click "Merge pull request" on GitHub
# or via GitHub CLI:
gh pr merge <pr-number> --merge
```

### Step 4: Automated Release Creation (Automatic)

Once the Release PR is merged, `release-please` automatically:

1. **Creates git tag** (e.g., `v1.1.0`) pointing to release commit
2. **Triggers `release.yml` workflow** which:
   - Builds signed AAB and APK artifacts
   - Generates release notes from changelog
   - Creates GitHub Release with artifacts attached
   - Uploads AAB to Google Play Store
   - Attaches build provenance attestation

**Result:**
- ✅ New GitHub Release published at https://github.com/mobilemobilellc/solpan/releases/tag/v1.1.0
- ✅ Artifacts available for download (AAB, APK)
- ✅ New version uploaded to Google Play Store

## Manual Release (Emergency Only)

If automated workflow fails and manual intervention is needed:

### Trigger Release Workflow Manually

```bash
# Via GitHub CLI
gh workflow run release.yml --ref main

# Or via GitHub UI:
# 1. Go to Actions → Create Release
# 2. Click "Run workflow" with ref=main
```

### Manual Tag Creation (Last Resort)

Only if release-please is broken and GitHub Actions is down:

```bash
# Update version files manually
echo "appVersionName=1.1.0" >> gradle.properties
echo "appVersionCode=2" >> gradle.properties

# Commit and tag
git add gradle.properties
git commit -m "chore(release): v1.1.0"
git tag -a v1.1.0 -m "Release v1.1.0"
git push origin main v1.1.0
```

## Version Strategy

SolPan follows [Semantic Versioning](https://semver.org/):

- **MAJOR** (X.0.0) — Breaking API changes, incompatible user-facing changes
- **MINOR** (0.X.0) — New features, backward compatible
- **PATCH** (0.0.X) — Bug fixes and maintenance

### Version File Format

**gradle.properties:**
```properties
appVersionName=1.0.0  # Semantic version string (shown to users)
appVersionCode=1      # Integer code (incremented for each release, used by Play Store)
```

**version.properties:**
```properties
versionName=1.0.0
versionCode=1
```

Both are automatically updated by `release-please` and kept in sync.

## Changelog Format

The `CHANGELOG.md` is automatically generated by `release-please` and includes:

- **Sections** for each commit type (Features, Bug Fixes, Performance, etc.)
- **Links** to commits and PRs on GitHub
- **Version headers** with release dates
- **Comparison links** (e.g., "Unreleased" → "v1.0.0")

Example:

```markdown
## [1.1.0] - 2025-06-15

### Features
- feat: add summer tilt mode optimization ([#42](https://github.com/mobilemobilellc/solpan/pull/42))
- feat: implement magnetic declination auto-detection ([#40](https://github.com/mobilemobilellc/solpan/pull/40))

### Bug Fixes
- fix: correct azimuth calculation in southern hemisphere ([#39](https://github.com/mobilemobilellc/solpan/pull/39))

### Performance
- perf: reduce magnetometer polling frequency ([#38](https://github.com/mobilemobilellc/solpan/pull/38))

## [1.0.0] - 2025-06-01
```

## Pre-release Versions

For alpha/beta testing:

1. **Push commit with pre-release tag:**
   ```bash
   git tag -a v1.1.0-alpha.1 -m "v1.1.0-alpha.1"
   git push origin v1.1.0-alpha.1
   ```

2. **Automated workflow will:**
   - Create GitHub Release marked as pre-release
   - Upload to Google Play Track: **Internal**
   - Tag draft as α/β indicator

## Troubleshooting

### Release PR didn't open

**Cause:** No conventional commits since last release, or commits don't match format.

**Solution:**
```bash
# Verify commits are properly formatted
git log --oneline main~10..main

# Check .release-please-config.json is valid JSON
cat .release-please-config.json | python3 -m json.tool
```

### Version mismatch between files

**Cause:** Manual changes or partial updates

**Solution:**
```bash
# Release-please will fix on next run; or manually sync:
grep "appVersionName" gradle.properties
grep "versionName" version.properties
# Ensure they match
```

### GitHub Actions failed

**Cause:** Missing secrets or SDK download failures

**Solution:**
- Check workflow logs at Actions tab
- Verify secrets: GOOGLE_SERVICES_JSON_BASE64, SIGNING_KEYSTORE_BASE64, GOOGLE_PLAY_JSON_KEY
- Check Android SDK availability in runner

## References

- [release-please Documentation](https://github.com/googleapis/release-please)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
