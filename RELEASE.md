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

Merging the release PR is the whole release. Everything after it is automated and ends with the build live on Google Play once Play's review passes.

## The pipeline

1. Commits land on `main` in [Conventional Commits](https://www.conventionalcommits.org/) form.
2. `release-please.yml` keeps one open release PR, `chore: release main`, that bumps `.release-please-manifest.json`, `appVersionName` in `gradle.properties` and `CHANGELOG.md`.
3. Merging that PR makes release-please tag `vX.Y.Z` and publish a GitHub Release with the changelog.
4. The tag starts `release.yml`, which:
   - writes the Play "What's new" text from the version's `CHANGELOG.md` section,
   - builds the signed AAB and APK with `versionCode` set to the workflow's run number,
   - attaches both to the GitHub Release and attests their provenance,
   - publishes the AAB to Google Play with [Gradle Play Publisher](https://github.com/Triple-T/gradle-play-publisher) as a completed release, so it reaches all users after review with no Console step.

release-please runs with a GitHub App token rather than `GITHUB_TOKEN`. That is what lets the release PR run CI and the tag push start `release.yml`; events made with `GITHUB_TOKEN` start no workflows.

## Commit types

| Type | Version bump | In the changelog | In Play's "What's new" |
|---|---|---|---|
| `feat:` | minor | Features | yes |
| `fix:` | patch | Bug Fixes | yes |
| `perf:` | patch | Performance | yes |
| `docs:`, `ci:`, `chore:` | patch | own sections | no |
| `BREAKING CHANGE:` footer | major | | |

Play's text is the `feat`, `fix` and `perf` subjects of that version, without links, deduplicated and cut at a line boundary to Play's 500-character limit (`.github/scripts/play-release-notes.sh`). A release with none of them ships "Bug fixes and improvements." Subjects are user-facing text, so write them for users.

## Tracks

The tag name picks the Play track:

| Tag contains | Track |
|---|---|
| `-alpha` | alpha |
| `-beta` | beta |
| `-test.` | internal |
| anything else | production |

## Dry run

A pull request never runs `release.yml`, so test a change to it with a dry run on its branch:

```bash
gh workflow run release.yml --ref <branch> -f dry_run=true
```

It builds the signed artifacts and uploads to a Play edit that is never committed. No GitHub Release, attestation or Play release is created. A dispatch defaults to a dry run, and a real release only ever runs from a `v*` tag.

## Setup

Secrets and variables the pipeline reads:

| Name | Kind | Used for |
|---|---|---|
| `RELEASE_APP_CLIENT_ID` | variable | GitHub App that release-please acts as |
| `RELEASE_APP_PRIVATE_KEY` | secret | that app's private key |
| `GOOGLE_PLAY_JSON_KEY` | secret | Play service account JSON, needs release rights on production |
| `SIGNING_KEYSTORE_BASE64`, `SIGNING_KEY_ALIAS`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_PASSWORD` | secrets | upload key |
| `GOOGLE_SERVICES_JSON_BASE64` | secret | Firebase config, needed by every build |
| `GRADLE_CACHE_ENCRYPTION_KEY` | secret | Gradle configuration cache |

The GitHub App needs Contents, Pull requests and Issues read and write on this repository, and must be installed on it.

The Play listing (title, descriptions, graphics) is edited in the Play Console, not in this repository.
