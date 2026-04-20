# Changelog

All notable changes to SolPan are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2025-06-01

### Added
- Initial production release of SolPan
- Real-time solar panel orientation optimization with 5 tilt modes (REALTIME, SUMMER, WINTER, SPRING_AUTUMN, YEAR_ROUND)
- Magnetic declination correction using Google Play Services
- Location-aware sun position calculations using commons-suncalc
- Material 3 Expressive design theme with light/dark mode support
- Orientation tracking via device accelerometer + magnetometer with low-pass filtering
- Comprehensive API documentation via Dokka and GitHub Pages
- End-to-end instrumentation tests covering all tilt modes and critical flows
- CI/CD pipeline with detekt static analysis, spotless formatting, and automated releases
- Support for 29 locales with accessibility considerations
- Firebase analytics and crash reporting integration

[Unreleased]: https://github.com/mobilemobilellc/solpan/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/mobilemobilellc/solpan/releases/tag/v1.0.0
