# Changelog

All notable changes to SolPan are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.3](https://github.com/mobilemobilellc/solpan/compare/v1.2.2...v1.2.3) (2026-09-23)


### Bug Fixes

* don't crash when the settings file can't be read ([#173](https://github.com/mobilemobilellc/solpan/issues/173)) ([5cf016b](https://github.com/mobilemobilellc/solpan/commit/5cf016b05b85ee81be388c9b51d225bb5f0f0963))
* keep guidance right when the screen is in landscape ([#176](https://github.com/mobilemobilellc/solpan/issues/176)) ([c562d46](https://github.com/mobilemobilellc/solpan/commit/c562d46574d0ba79f6c3646b4fefd0ee5558bf63))
* point realtime mode at the sun, and say when it has set ([#171](https://github.com/mobilemobilellc/solpan/issues/171)) ([3a5b167](https://github.com/mobilemobilellc/solpan/commit/3a5b167079f67a9d496573cf49f2330324e84cda))
* point the turn and tilt instructions the right way ([#182](https://github.com/mobilemobilellc/solpan/issues/182)) ([c684c1d](https://github.com/mobilemobilellc/solpan/commit/c684c1d461b847dca69eb7146db77ffc04fbbc65))
* stop using location and sensors while the app is in the background ([#175](https://github.com/mobilemobilellc/solpan/issues/175)) ([21b7e3f](https://github.com/mobilemobilellc/solpan/commit/21b7e3f139b074a15c3c4b2668e2c694e354d2b9))
* work with approximate location ([#172](https://github.com/mobilemobilellc/solpan/issues/172)) ([3e3f564](https://github.com/mobilemobilellc/solpan/commit/3e3f564350118c3c3225cbd7b369427a179cae35))


### Chores

* **deps:** update androidx ([#174](https://github.com/mobilemobilellc/solpan/issues/174)) ([5d56c15](https://github.com/mobilemobilellc/solpan/commit/5d56c15ce966942fa5f3861d15910f446f9f2a35))
* **deps:** update androidx.compose:compose-bom-alpha to v2026.09.01 ([#179](https://github.com/mobilemobilellc/solpan/issues/179)) ([0ff420d](https://github.com/mobilemobilellc/solpan/commit/0ff420d50b5959b3d34e3b1d09762b9a6e302d71))

## [1.2.2](https://github.com/mobilemobilellc/solpan/compare/v1.2.1...v1.2.2) (2026-09-23)


### Bug Fixes

* drop orientation readings that are not finite ([#170](https://github.com/mobilemobilellc/solpan/issues/170)) ([3ddfb3a](https://github.com/mobilemobilellc/solpan/commit/3ddfb3a4ffb9f7923f0058319885d5203577c71e))


### CI/CD

* let [@claude](https://github.com/claude) respond on issues and pull requests ([#167](https://github.com/mobilemobilellc/solpan/issues/167)) ([58bd48c](https://github.com/mobilemobilellc/solpan/commit/58bd48c82f823ea0815c394703a08e4a9a0b3b9f))


### Chores

* **deps:** update androidx.compose.material3:material3 to v1.5.0-alpha29 ([#110](https://github.com/mobilemobilellc/solpan/issues/110)) ([663e30e](https://github.com/mobilemobilellc/solpan/commit/663e30edff094bffeaa308e82fe6910343e56bbe))
* **deps:** update androidx.compose.material3:material3-adaptive-navigation-suite to v1.5.0-alpha29 ([#169](https://github.com/mobilemobilellc/solpan/issues/169)) ([9d57d89](https://github.com/mobilemobilellc/solpan/commit/9d57d89c11ee0a42200a5c6091fdb00e0099fb20))

## [1.2.1](https://github.com/mobilemobilellc/solpan/compare/v1.2.0...v1.2.1) (2026-09-23)


### Bug Fixes

* start the orientation sensor listener again ([#165](https://github.com/mobilemobilellc/solpan/issues/165)) ([bb5c0a4](https://github.com/mobilemobilellc/solpan/commit/bb5c0a4248228e1ba36d9dc529ffc77a9fe6fc37))


### CI/CD

* publish to Play with Gradle Play Publisher and release via a GitHub App ([#164](https://github.com/mobilemobilellc/solpan/issues/164)) ([d42aceb](https://github.com/mobilemobilellc/solpan/commit/d42aceb9371f3967514eeef9192cce7f5d3c9552))

## [1.2.0](https://github.com/mobilemobilellc/solpan/compare/v1.1.2...v1.2.0) (2026-09-23)


### Features

* restore Firebase Analytics and Crashlytics ([#162](https://github.com/mobilemobilellc/solpan/issues/162)) ([d8836cc](https://github.com/mobilemobilellc/solpan/commit/d8836cc9c107dbd552f7f97aa0eee89cd5a84ee7))

## [1.1.2](https://github.com/mobilemobilellc/solpan/compare/v1.1.1...v1.1.2) (2026-09-23)


### CI/CD

* pass the release versionCode as a separate argument again ([#160](https://github.com/mobilemobilellc/solpan/issues/160)) ([54ba5aa](https://github.com/mobilemobilellc/solpan/commit/54ba5aaa4d8e938cf61a2c91353d06062e392c82))

## [1.1.1](https://github.com/mobilemobilellc/solpan/compare/v1.1.0...v1.1.1) (2026-09-23)


### CI/CD

* drop the release-please component so merged release PRs get tagged ([#156](https://github.com/mobilemobilellc/solpan/issues/156)) ([24ea6b8](https://github.com/mobilemobilellc/solpan/commit/24ea6b89261281632e78132f1f144089a536bc06))
* keep the release versionCode above what Play already holds ([#159](https://github.com/mobilemobilellc/solpan/issues/159)) ([41f305c](https://github.com/mobilemobilellc/solpan/commit/41f305cf8fa39459675cda85da90d22784c1b1a4))
* let release-please dispatch the release build ([#158](https://github.com/mobilemobilellc/solpan/issues/158)) ([4530eb6](https://github.com/mobilemobilellc/solpan/commit/4530eb6ac95a81d3f2574a099ef40d1adfdd3b4a))

## [1.1.0](https://github.com/mobilemobilellc/solpan/compare/v1.0.0...v1.1.0) (2026-09-23)


### Features

* **.github/workflows:** set status to draft ([#13](https://github.com/mobilemobilellc/solpan/issues/13)) ([742714a](https://github.com/mobilemobilellc/solpan/commit/742714a028215562722343b7c797b272c478c6ec))
* add Danish, Finnish, and Dutch language support ([#45](https://github.com/mobilemobilellc/solpan/issues/45)) ([437a786](https://github.com/mobilemobilellc/solpan/commit/437a786395ec1b05bee6220630a7f221d8a6cebf))
* Add per-app language preferences ([0db4c03](https://github.com/mobilemobilellc/solpan/commit/0db4c03a40942708c81f302bb83f777433a6a317))
* Add per-app language preferences ([a5cda14](https://github.com/mobilemobilellc/solpan/commit/a5cda14d45755c8a2334e92f17f71f3edad45c55))
* **ci:** add merge queue workflow ([45ede0e](https://github.com/mobilemobilellc/solpan/commit/45ede0e93d6d33912d573de13f3948b8caa7b9f1))
* **ci:** add merge queue workflow ([56b08e2](https://github.com/mobilemobilellc/solpan/commit/56b08e2961c5141e7984a0abd953fe30c09e0176))
* **i18n:** add locale filters to app/build.gradle.kts ([23653c6](https://github.com/mobilemobilellc/solpan/commit/23653c60cab1f9d23936d5eec3b853e8a7c9a81b))
* **i18n:** add locale filters to app/build.gradle.kts ([3fa0f6f](https://github.com/mobilemobilellc/solpan/commit/3fa0f6f6bf6e2413ea7e2c4341b7160262dd421d))
* **i18n:** Add Spanish (Latin America) and Chinese (Simplified) translations ([939976e](https://github.com/mobilemobilellc/solpan/commit/939976e3a2d94d28d3931cc5bdd1bd860904591e))
* modernization sweep — AGP 9, automated releases, API docs, coverage ([#95](https://github.com/mobilemobilellc/solpan/issues/95)) ([4344233](https://github.com/mobilemobilellc/solpan/commit/4344233a6464449fdf46020933db9c827edf7d5b))
* refactor navigation and ViewModel initialization ([#2](https://github.com/mobilemobilellc/solpan/issues/2)) ([b118bd2](https://github.com/mobilemobilellc/solpan/commit/b118bd240d2d9fe9fc8ff7688ed50d6ac713a834))
* **theme:** Update launcher icon and add Play Store assets ([#8](https://github.com/mobilemobilellc/solpan/issues/8)) ([e15be45](https://github.com/mobilemobilellc/solpan/commit/e15be45cff4daba2906528b695824f353c791ef5))
* **translations:** add localized strings for 28 languages ([b43f740](https://github.com/mobilemobilellc/solpan/commit/b43f7400f6803ea2765294e230cdab3163ebd728))
* **translations:** add localized strings for 28 languages ([7238715](https://github.com/mobilemobilellc/solpan/commit/7238715b3785534040890c2a268436a59ba94b72))


### Bug Fixes

* **build:** make the coverage report actually produce a report ([#146](https://github.com/mobilemobilellc/solpan/issues/146)) ([b5d17ef](https://github.com/mobilemobilellc/solpan/commit/b5d17ef8647297581469ed28ca3c594e807f9389))
* **ci:** correct apostrophe in release template and enhance Google Play upload ([#10](https://github.com/mobilemobilellc/solpan/issues/10)) ([4b1642b](https://github.com/mobilemobilellc/solpan/commit/4b1642bd79824d398bcc167ac8ee0fd84526581c))
* **ci:** give release-please a step id and drop the placeholder sha ([#137](https://github.com/mobilemobilellc/solpan/issues/137)) ([da24873](https://github.com/mobilemobilellc/solpan/commit/da24873625ed40aff6c166dda3654d909cf28952))
* **ci:** security-event write perms ([f4066bb](https://github.com/mobilemobilellc/solpan/commit/f4066bb661b47717261b4b1491d6d8a9dfb2d7d3))
* clear every detekt finding and drop the baseline ([#153](https://github.com/mobilemobilellc/solpan/issues/153)) ([dfb5178](https://github.com/mobilemobilellc/solpan/commit/dfb5178ea7efdd2e0dfb9d20c0f78661e9e277cf))
* **deps:** downgrade AboutLibraries to v12.2.4 ([#58](https://github.com/mobilemobilellc/solpan/issues/58)) ([e5f6e86](https://github.com/mobilemobilellc/solpan/commit/e5f6e862e2e6d37dde3f43c2c97a26f1b29f3e0b))
* **renovate:** correct `matchNewVersion` regex ([#53](https://github.com/mobilemobilellc/solpan/issues/53)) ([a6018e6](https://github.com/mobilemobilellc/solpan/commit/a6018e696351622efc2af9d8f9542f69ded18e71))
* **SolPanViewModel:** ensure correct ViewModel class in Factory ([#59](https://github.com/mobilemobilellc/solpan/issues/59)) ([6513048](https://github.com/mobilemobilellc/solpan/commit/6513048ec658a71568dd398b786a3511c2986fc5))
* **typo:** repo label in changelog config ([#12](https://github.com/mobilemobilellc/solpan/issues/12)) ([2c570a5](https://github.com/mobilemobilellc/solpan/commit/2c570a5ca56d350466529970492d60b67a552d94))


### Documentation

* add privacy policy ([#6](https://github.com/mobilemobilellc/solpan/issues/6)) ([7485cd0](https://github.com/mobilemobilellc/solpan/commit/7485cd0461b2042921bc466cc4f2fe38a7e5cffc))
* catch up with detekt, the test move and the coverage gate ([#154](https://github.com/mobilemobilellc/solpan/issues/154)) ([68f1965](https://github.com/mobilemobilellc/solpan/commit/68f1965aab73d858407365a8747a32d063522ed3))
* consolidate, and say what the project actually does ([#148](https://github.com/mobilemobilellc/solpan/issues/148)) ([869d88d](https://github.com/mobilemobilellc/solpan/commit/869d88d469789168f49fd951842922978ab15e14))
* correct the screenshot claim, and drop the unused ktlint alias ([#149](https://github.com/mobilemobilellc/solpan/issues/149)) ([d587fa4](https://github.com/mobilemobilellc/solpan/commit/d587fa49196814b2335a2d6aa600e572a879c375))


### CI/CD

* add attestations and id-token permissions to workflows ([3bb2951](https://github.com/mobilemobilellc/solpan/commit/3bb2951ff67edddb6cd5c203e0f9b367d2c17ccf))
* Add Detekt for static analysis ([#50](https://github.com/mobilemobilellc/solpan/issues/50)) ([f4e9e70](https://github.com/mobilemobilellc/solpan/commit/f4e9e70d021a901577bfc276acc013137a752bb2))
* Add SLSA provenance attestation ([48a0bcc](https://github.com/mobilemobilellc/solpan/commit/48a0bccc1e438cfcadebaee8380febdbb0767fd1))
* Add SLSA provenance attestation ([0d72193](https://github.com/mobilemobilellc/solpan/commit/0d721935e891069e12e28ed140d2702794977b6c))
* add write permission for checks ([57183ff](https://github.com/mobilemobilellc/solpan/commit/57183ff910dd0175aad1a0e87ef31e3dfe7a39f8))
* add write permission for checks ([5e97a81](https://github.com/mobilemobilellc/solpan/commit/5e97a81d3b3c79523ea30488aafe305d3c810b72))
* combine gradle tasks in github workflows ([01f1477](https://github.com/mobilemobilellc/solpan/commit/01f147778c104040534ff2172aebc14a6bd84a26))
* Configure Gradle cache encryption and remove redundant JDK setup ([49463dc](https://github.com/mobilemobilellc/solpan/commit/49463dc0dcdd5fc66f03e5bf2f258e2e8c5333d0))
* Configure Gradle cache encryption and remove redundant JDK setup ([4da9a40](https://github.com/mobilemobilellc/solpan/commit/4da9a407fd29eda1ec6901c1c47c5f4dfb973df7))
* configure Java separately and add dependency submission ([376593d](https://github.com/mobilemobilellc/solpan/commit/376593df2524fa9342046375fc43dbd943d2670e))
* configure Java separately and add dependency submission ([196261e](https://github.com/mobilemobilellc/solpan/commit/196261e922416d8db933f18ac6fd295506e428c5))
* configure test reporting and changelog generation ([e7eed1d](https://github.com/mobilemobilellc/solpan/commit/e7eed1d602f6695c529fb2202390956f9d5d5801))
* configure test reporting and changelog generation ([eb79e90](https://github.com/mobilemobilellc/solpan/commit/eb79e90d41d2b9b7470b35f855f2732ffd371a72))
* configure test reporting and changelog generation ([d682403](https://github.com/mobilemobilellc/solpan/commit/d682403b27ab80f9df28bb82673845d55c51f86e))
* configure test reporting and changelog generation ([fdd5da6](https://github.com/mobilemobilellc/solpan/commit/fdd5da688274d14b9ba95ee3a8f8150fb348b055))
* configure test reporting and changelog generation [#25](https://github.com/mobilemobilellc/solpan/issues/25) ([d682403](https://github.com/mobilemobilellc/solpan/commit/d682403b27ab80f9df28bb82673845d55c51f86e))
* dispatch the release build on the new tag, not main ([#155](https://github.com/mobilemobilellc/solpan/issues/155)) ([c8a8c1b](https://github.com/mobilemobilellc/solpan/commit/c8a8c1b7d4b016c3913f927a2149911e14f604bc))
* enhance release workflow and build configuration ([#3](https://github.com/mobilemobilellc/solpan/issues/3)) ([3b421cd](https://github.com/mobilemobilellc/solpan/commit/3b421cd022a5bc31e13c8e91b23ff72bd86d23ec))
* Fix merge queue concurrency ([4e6dcd5](https://github.com/mobilemobilellc/solpan/commit/4e6dcd50c0937a1f7d65b84537094e822e6d4547))
* publish api docs to github pages ([#136](https://github.com/mobilemobilellc/solpan/issues/136)) ([21c4ea7](https://github.com/mobilemobilellc/solpan/commit/21c4ea7ee3e8598df64bf8411074b6153645c730))
* **release:** add new labels for features in release notes ([#11](https://github.com/mobilemobilellc/solpan/issues/11)) ([c1cbab3](https://github.com/mobilemobilellc/solpan/commit/c1cbab3e25e25af3e50899d305f06608f39c45bf))
* **release:** broaden tag matching for release workflow ([#4](https://github.com/mobilemobilellc/solpan/issues/4)) ([ab07067](https://github.com/mobilemobilellc/solpan/commit/ab070672ae23aa7f88457ef88e2cfc6187c0d112))
* **release:** ensure google play upload runs after github release ([#7](https://github.com/mobilemobilellc/solpan/issues/7)) ([9abe6e6](https://github.com/mobilemobilellc/solpan/commit/9abe6e66e4555f83287708fe0f3d1d000867a29b))
* **release:** mark GitHub releases as draft and ignore pre-releases in changelog ([#46](https://github.com/mobilemobilellc/solpan/issues/46)) ([7aa04f8](https://github.com/mobilemobilellc/solpan/commit/7aa04f816e95d5315e7a43d2c21caa094fa9ea3d))
* run spotless repo-wide in the merge queue too ([#147](https://github.com/mobilemobilellc/solpan/issues/147)) ([a68b882](https://github.com/mobilemobilellc/solpan/commit/a68b8828c6e23a11864559059f60aaf163c725b9))
* run the workflows on jdk 25 ([#135](https://github.com/mobilemobilellc/solpan/issues/135)) ([e09dbdb](https://github.com/mobilemobilellc/solpan/commit/e09dbdb6c588acd0fa3517f8862d4e2d5354bf9e))
* stop main runs cancelling each other and drop the fake metrics ([#140](https://github.com/mobilemobilellc/solpan/issues/140)) ([f12a360](https://github.com/mobilemobilellc/solpan/commit/f12a3609b4e22b24d2f35e6a602835afae9f0c85))
* stop pushing generated docs to main so the build can pass ([#117](https://github.com/mobilemobilellc/solpan/issues/117)) ([401fcf8](https://github.com/mobilemobilellc/solpan/commit/401fcf8b0281e8c87bdcd851d09d528ad660b147))


### Chores

* Add GitHub Actions for CI/CD and configure Develocity ([#1](https://github.com/mobilemobilellc/solpan/issues/1)) ([4213ced](https://github.com/mobilemobilellc/solpan/commit/4213ced41f0f0119c043c1b841827781c351ab03))
* **build:** enable gradle build caching, use default changelog config ([#14](https://github.com/mobilemobilellc/solpan/issues/14)) ([f532b03](https://github.com/mobilemobilellc/solpan/commit/f532b03a38d0e85c805636bb6d43bc87ffa271c9))
* **build:** update release build path for attestation ([6215684](https://github.com/mobilemobilellc/solpan/commit/6215684233dc3480e1d164d767b07332cadf02ac))
* **build:** update release build path for attestation ([6504d6b](https://github.com/mobilemobilellc/solpan/commit/6504d6b782753086db87466774fd3d9a1a11add7))
* **config:** migrate config renovate.json ([#57](https://github.com/mobilemobilellc/solpan/issues/57)) ([e60a966](https://github.com/mobilemobilellc/solpan/commit/e60a966aea565478e95e8ec6895a2f993ffba6fe))
* Configure Renovate ([bb106bf](https://github.com/mobilemobilellc/solpan/commit/bb106bfd6f5a35aa1e252ea9f0a8d9900f9e0e20))
* **dependencies:** Merge pull request [#16](https://github.com/mobilemobilellc/solpan/issues/16) from mobilemobilellc/renovate/configure ([bb106bf](https://github.com/mobilemobilellc/solpan/commit/bb106bfd6f5a35aa1e252ea9f0a8d9900f9e0e20))
* **deps:** Configure Renovate to automerge passing PRs ([#56](https://github.com/mobilemobilellc/solpan/issues/56)) ([ba3c0cd](https://github.com/mobilemobilellc/solpan/commit/ba3c0cd5df1a9f29b83b21e007885f1b51302ee6))
* **deps:** disable automerge for unstable pre-releases ([#51](https://github.com/mobilemobilellc/solpan/issues/51)) ([023c1ad](https://github.com/mobilemobilellc/solpan/commit/023c1ad572d5edcb51aa3c794815b8608c2c93db))
* **deps:** migrate to dokka 2.2.0 ([#126](https://github.com/mobilemobilellc/solpan/issues/126)) ([e9c60d5](https://github.com/mobilemobilellc/solpan/commit/e9c60d5edec20b80b1abafa07c78af499fd66d15))
* **deps:** move spotless onto ktlint 1.8.0 and the catalog ([#127](https://github.com/mobilemobilellc/solpan/issues/127)) ([a7206fb](https://github.com/mobilemobilellc/solpan/commit/a7206fb1bafa0f3e0343d1a19125bc8915a2a61f))
* **deps:** remove config ([#54](https://github.com/mobilemobilellc/solpan/issues/54)) ([291b97f](https://github.com/mobilemobilellc/solpan/commit/291b97f7cbaef631320df5fda9c7b1096d61df22))
* **deps:** update aboutlibraries to v14.2.1 ([#113](https://github.com/mobilemobilellc/solpan/issues/113)) ([f8d4e5e](https://github.com/mobilemobilellc/solpan/commit/f8d4e5e17bc1604559e89cd98781a3295b605b46))
* **deps:** update aboutlibraries to v15 ([#144](https://github.com/mobilemobilellc/solpan/issues/144)) ([248ac15](https://github.com/mobilemobilellc/solpan/commit/248ac15b18e17527db2078626af16d8f67bc450b))
* **deps:** update actions/attest-build-provenance action to v3 ([#82](https://github.com/mobilemobilellc/solpan/issues/82)) ([674de4e](https://github.com/mobilemobilellc/solpan/commit/674de4e1fc61645b5aa5ab5bf48f722b1c2e66cc))
* **deps:** update actions/attest-build-provenance action to v4 ([#98](https://github.com/mobilemobilellc/solpan/issues/98)) ([9086b0e](https://github.com/mobilemobilellc/solpan/commit/9086b0ef40c4938f227afad3ffeb3e3d26ae8a79))
* **deps:** update actions/checkout action to v5 ([#70](https://github.com/mobilemobilellc/solpan/issues/70)) ([8e98495](https://github.com/mobilemobilellc/solpan/commit/8e9849572fe490a61dd2dda3c0ddc933c4770820))
* **deps:** update actions/checkout action to v6 ([#99](https://github.com/mobilemobilellc/solpan/issues/99)) ([9472e27](https://github.com/mobilemobilellc/solpan/commit/9472e2782a98d839e7add8a436db756babc98322))
* **deps:** update actions/github-script action to v9 ([#100](https://github.com/mobilemobilellc/solpan/issues/100)) ([051abca](https://github.com/mobilemobilellc/solpan/commit/051abcaee8751ee95320626f5190919d308fbdf6))
* **deps:** update actions/setup-java action to v5 ([#75](https://github.com/mobilemobilellc/solpan/issues/75)) ([ff324b6](https://github.com/mobilemobilellc/solpan/commit/ff324b6c3c92faf738beecd63f73cd87ea1c379b))
* **deps:** update actions/upload-artifact action to v7 ([#101](https://github.com/mobilemobilellc/solpan/issues/101)) ([4841602](https://github.com/mobilemobilellc/solpan/commit/484160268eefef9086163204515c553c87056221))
* **deps:** update actions/upload-pages-artifact action to v5 ([#145](https://github.com/mobilemobilellc/solpan/issues/145)) ([7d8b308](https://github.com/mobilemobilellc/solpan/commit/7d8b3085547328a82a0a094912e7750c1ce1b33b))
* **deps:** update androidx ([#71](https://github.com/mobilemobilellc/solpan/issues/71)) ([2497122](https://github.com/mobilemobilellc/solpan/commit/249712276204566e0aa7b89ea4328353ecfdc0d1))
* **deps:** update androidx and compile against SDK 37.1 ([#123](https://github.com/mobilemobilellc/solpan/issues/123)) ([dcf1877](https://github.com/mobilemobilellc/solpan/commit/dcf18770f984785b29102dd747086bc582d95c7e))
* **deps:** update androidx to v1.4.1 ([#79](https://github.com/mobilemobilellc/solpan/issues/79)) ([36ff5ba](https://github.com/mobilemobilellc/solpan/commit/36ff5ba525d206a277c6eaf98d1216c73c428622))
* **deps:** update androidx.compose.material3:material3 to v1.4.0-snapshot ([d7c9036](https://github.com/mobilemobilellc/solpan/commit/d7c9036134cfcbb5a566da5c49f2f3610bfcd97f))
* **deps:** update androidx.compose.material3:material3 to v1.4.0-snapshot ([be30316](https://github.com/mobilemobilellc/solpan/commit/be303165315e320f4e3ac5f0e145f3d996c863e2))
* **deps:** update androidx.compose.material3:material3 to v1.5.0-snapshot ([#61](https://github.com/mobilemobilellc/solpan/issues/61)) ([4741297](https://github.com/mobilemobilellc/solpan/commit/4741297a890fa273bfc2490caae7ef4bb5b816f3))
* **deps:** update androidx.lifecycle:lifecycle-viewmodel-navigation3 to v1.0.0-snapshot ([3ff9c60](https://github.com/mobilemobilellc/solpan/commit/3ff9c60c989c19064fb908f52528440227beda25))
* **deps:** update androidx.lifecycle:lifecycle-viewmodel-navigation3 to v1.0.0-snapshot ([6a14a85](https://github.com/mobilemobilellc/solpan/commit/6a14a855ef53e0deda2d6530d3cd42905c8de456))
* **deps:** update com.android.application to v8.12.0 ([#62](https://github.com/mobilemobilellc/solpan/issues/62)) ([c984612](https://github.com/mobilemobilellc/solpan/commit/c984612ed0688d2cfc50a0ca84b91692dc4163a2))
* **deps:** update com.android.application to v8.12.1 ([#73](https://github.com/mobilemobilellc/solpan/issues/73)) ([232fa67](https://github.com/mobilemobilellc/solpan/commit/232fa674a031c7d3bfdfacefc55c6ba1c0f0746e))
* **deps:** update com.android.application to v8.13.1 ([#80](https://github.com/mobilemobilellc/solpan/issues/80)) ([def1d63](https://github.com/mobilemobilellc/solpan/commit/def1d6348f4086243f97a3be2b9ed19404325f7e))
* **deps:** update com.diffplug.spotless to v7.2.1 ([f81021d](https://github.com/mobilemobilellc/solpan/commit/f81021d6632fca6af4e4e7e1a38d954ad1d1e841))
* **deps:** update com.diffplug.spotless to v7.2.1 ([2807a56](https://github.com/mobilemobilellc/solpan/commit/2807a562c1ac3eff08b9943cac882f387a85d9a8))
* **deps:** update com.google.android.gms:play-services-location to v21.4.0 ([#119](https://github.com/mobilemobilellc/solpan/issues/119)) ([e106dd8](https://github.com/mobilemobilellc/solpan/commit/e106dd8db3cc1fd4fee0cce6ce2e3bb6553d5cf2))
* **deps:** update com.google.firebase:firebase-bom to v34 ([b97cc0e](https://github.com/mobilemobilellc/solpan/commit/b97cc0e64c092e382f99e5c7c93d62dfa6f53ad7))
* **deps:** update com.google.firebase:firebase-bom to v34 ([577c257](https://github.com/mobilemobilellc/solpan/commit/577c2579e6617bfda719f15029da6f0000762441))
* **deps:** update com.google.firebase:firebase-bom to v34.1.0 ([#69](https://github.com/mobilemobilellc/solpan/issues/69)) ([e2c23b0](https://github.com/mobilemobilellc/solpan/commit/e2c23b082d788d56296ae1369bf375e0e62c5321))
* **deps:** update com.google.firebase:firebase-bom to v34.19.0 ([#120](https://github.com/mobilemobilellc/solpan/issues/120)) ([27a702a](https://github.com/mobilemobilellc/solpan/commit/27a702a6c9771dfe4e5239c4abc47daaf921d6bc))
* **deps:** update com.google.firebase:firebase-bom to v34.6.0 ([#81](https://github.com/mobilemobilellc/solpan/issues/81)) ([b48c310](https://github.com/mobilemobilellc/solpan/commit/b48c310e94d9fd832e7266d533f57a778c0089c9))
* **deps:** update com.google.firebase.crashlytics to v3.0.5 ([35e7972](https://github.com/mobilemobilellc/solpan/commit/35e7972e5ba9117670b6aa3448ac123efad760d3))
* **deps:** update com.google.firebase.crashlytics to v3.0.5 ([e7e04bf](https://github.com/mobilemobilellc/solpan/commit/e7e04bfbf64cc93dc52fbeb4aa3565d72fecc74d))
* **deps:** update com.google.firebase.crashlytics to v3.0.6 ([#67](https://github.com/mobilemobilellc/solpan/issues/67)) ([b2bc21b](https://github.com/mobilemobilellc/solpan/commit/b2bc21b3b19575b9f53b976ae1f5d519ccbecb56))
* **deps:** update com.google.firebase.crashlytics to v3.0.8 ([#115](https://github.com/mobilemobilellc/solpan/issues/115)) ([f944969](https://github.com/mobilemobilellc/solpan/commit/f9449698ce6ef0bd7b9d75dae1ef5f2257548c4d))
* **deps:** update com.google.firebase.firebase-perf to v2 ([85ec871](https://github.com/mobilemobilellc/solpan/commit/85ec87142e8ca29c8febb073b7ec255f18b36fff))
* **deps:** update com.google.firebase.firebase-perf to v2 ([6320275](https://github.com/mobilemobilellc/solpan/commit/6320275ce1ada3e1901045d1d8bccf02e3499a27))
* **deps:** update com.google.firebase.firebase-perf to v2.0.1 ([#68](https://github.com/mobilemobilellc/solpan/issues/68)) ([24ddfa2](https://github.com/mobilemobilellc/solpan/commit/24ddfa2c0d3f39cbfcabbab980efbca6a8e3a0a9))
* **deps:** update com.google.gms.google-services to v4.5.0 ([#124](https://github.com/mobilemobilellc/solpan/issues/124)) ([d1198c7](https://github.com/mobilemobilellc/solpan/commit/d1198c7ea64908862f2879c4fb4b62a7ff857a19))
* **deps:** update develocity, spotless, checkout and setup-java ([#134](https://github.com/mobilemobilellc/solpan/issues/134)) ([e705a3c](https://github.com/mobilemobilellc/solpan/commit/e705a3c297922fc211f138c61ad7261839264f24))
* **deps:** update firebase, play-services, screenshot and two actions ([#121](https://github.com/mobilemobilellc/solpan/issues/121)) ([6c01a07](https://github.com/mobilemobilellc/solpan/commit/6c01a072a969ca445145e04c43ca4cab839e533e))
* **deps:** update github/codeql-action action to v4 ([#102](https://github.com/mobilemobilellc/solpan/issues/102)) ([8b0f371](https://github.com/mobilemobilellc/solpan/commit/8b0f371de50effa95db91e4a4fc98ae4c90a6c56))
* **deps:** update gradle to v9 ([#66](https://github.com/mobilemobilellc/solpan/issues/66)) ([0890a5c](https://github.com/mobilemobilellc/solpan/commit/0890a5c5964c36bd101e24e7b6df0c50d0c0baea))
* **deps:** update gradle, agp and kotlin together ([#118](https://github.com/mobilemobilellc/solpan/issues/118)) ([54b7d39](https://github.com/mobilemobilellc/solpan/commit/54b7d39d1fbd99095a83b0fb7929f275a33a3ede))
* **deps:** update gradle/actions action to v4 ([f80e125](https://github.com/mobilemobilellc/solpan/commit/f80e12568841ef93d13717a1ce9b80eab0cebbc6))
* **deps:** update gradle/actions action to v4 ([47ed910](https://github.com/mobilemobilellc/solpan/commit/47ed910f8ea10b0260b69aa2ca1bc04f9de37f14))
* **deps:** update gradle/actions action to v5 ([#88](https://github.com/mobilemobilellc/solpan/issues/88)) ([3710900](https://github.com/mobilemobilellc/solpan/commit/37109004faca07045a5743591630c08417e1494a))
* **deps:** update gradle/actions action to v6 ([#103](https://github.com/mobilemobilellc/solpan/issues/103)) ([bff27c2](https://github.com/mobilemobilellc/solpan/commit/bff27c2eaf9e907313be0d51f6d8ca15781622d1))
* **deps:** update io.nlopez.compose.rules to 0.6.6 for detekt ([#122](https://github.com/mobilemobilellc/solpan/issues/122)) ([c123d64](https://github.com/mobilemobilellc/solpan/commit/c123d6400bae7fb6c1c074885fe4897520a85e1c))
* **deps:** update io.nlopez.compose.rules:ktlint to v0.4.24 ([55c0fb2](https://github.com/mobilemobilellc/solpan/commit/55c0fb28714527fc037e14151023c3c45c21fcdc))
* **deps:** update io.nlopez.compose.rules:ktlint to v0.4.24 ([1b15d06](https://github.com/mobilemobilellc/solpan/commit/1b15d06caf1901baa96a9032c5b0fe8a0cc29385))
* **deps:** update io.nlopez.compose.rules:ktlint to v0.4.25 ([c2dc65d](https://github.com/mobilemobilellc/solpan/commit/c2dc65d1ed2973d05f5f7179521754cbea7564e4))
* **deps:** update io.nlopez.compose.rules:ktlint to v0.4.25 ([f5e2f86](https://github.com/mobilemobilellc/solpan/commit/f5e2f86dd71230de71d13a40b29c40745e465d8d))
* **deps:** update io.nlopez.compose.rules:ktlint to v0.4.26 ([939d680](https://github.com/mobilemobilellc/solpan/commit/939d680b2de35e938914e92b1ccfc1a702362f0b))
* **deps:** update io.nlopez.compose.rules:ktlint to v0.4.26 ([c225016](https://github.com/mobilemobilellc/solpan/commit/c22501648b0667aa99c3bbcbc907190777fe20a1))
* **deps:** update io.nlopez.compose.rules:ktlint to v0.4.26 ([#55](https://github.com/mobilemobilellc/solpan/issues/55)) ([61cf10e](https://github.com/mobilemobilellc/solpan/commit/61cf10e50eecdb9f591f1900cc1ef04a09011386))
* **deps:** update io.nlopez.compose.rules:ktlint to v0.4.27 ([#63](https://github.com/mobilemobilellc/solpan/issues/63)) ([16ad1de](https://github.com/mobilemobilellc/solpan/commit/16ad1dedd66a2ae9bc07a1f77db58eb03fc32f63))
* **deps:** update kotlin & agp to v2.2.10 ([#72](https://github.com/mobilemobilellc/solpan/issues/72)) ([60f19b4](https://github.com/mobilemobilellc/solpan/commit/60f19b41db6fbfb3e9e5696daf3818022dc5a1b3))
* **deps:** update kotlin & agp to v2.2.21 ([#83](https://github.com/mobilemobilellc/solpan/issues/83)) ([3b374b7](https://github.com/mobilemobilellc/solpan/commit/3b374b75993b176e4f1a84f1a2bf2dd570132949))
* **deps:** update mikepenz/action-junit-report action to v6 ([#105](https://github.com/mobilemobilellc/solpan/issues/105)) ([5a3d738](https://github.com/mobilemobilellc/solpan/commit/5a3d7389b59b0388a1cdac42260dca597fa98294))
* **deps:** update mikepenz/release-changelog-builder-action action to v5 ([3f26e0d](https://github.com/mobilemobilellc/solpan/commit/3f26e0d9bea5374d4c53af32c1b0923185a0cf14))
* **deps:** update mikepenz/release-changelog-builder-action action to v5 ([6859c93](https://github.com/mobilemobilellc/solpan/commit/6859c93337a6447b98d63ec8d80b6de6f6a44262))
* **deps:** update mikepenz/release-changelog-builder-action action to v6 ([#106](https://github.com/mobilemobilellc/solpan/issues/106)) ([b917288](https://github.com/mobilemobilellc/solpan/commit/b91728853ad395a3130899c8a83343f2b1ef3b19))
* **deps:** update nav3core to v1.0.0-snapshot ([d00d35e](https://github.com/mobilemobilellc/solpan/commit/d00d35e530a13206f376c9a4fd391c69ba3612c2))
* **deps:** update nav3core to v1.0.0-snapshot ([10608f9](https://github.com/mobilemobilellc/solpan/commit/10608f933108c52b6cf7ca1e395428fe4da728e2))
* **deps:** update org.jetbrains.kotlin:kotlin-gradle-plugin to v2.4.20 [security] ([#114](https://github.com/mobilemobilellc/solpan/issues/114)) ([088ebc4](https://github.com/mobilemobilellc/solpan/commit/088ebc4784eda8a57532d3645f7ea7a80c132920))
* **deps:** update org.jetbrains.kotlinx:kotlinx-datetime to v0.7.1-0.6.x-compat ([#96](https://github.com/mobilemobilellc/solpan/issues/96)) ([2498628](https://github.com/mobilemobilellc/solpan/commit/2498628f22d2096e92cfce6222ba5c7444b2854c))
* **deps:** update plugin com.gradle.common-custom-user-data-gradle-plugin to v2.6.0 ([#84](https://github.com/mobilemobilellc/solpan/issues/84)) ([f6624b6](https://github.com/mobilemobilellc/solpan/commit/f6624b6e3b16d9fe890db40e7e39766bdd899934))
* **deps:** update plugin com.gradle.common-custom-user-data-gradle-plugin to v2.8.0 ([#125](https://github.com/mobilemobilellc/solpan/issues/125)) ([b89561f](https://github.com/mobilemobilellc/solpan/commit/b89561f056c8265fd234a6415f90a9991669e4e3))
* **deps:** update plugin com.gradle.develocity to v4.1.1 ([#74](https://github.com/mobilemobilellc/solpan/issues/74)) ([dbf3630](https://github.com/mobilemobilellc/solpan/commit/dbf3630ae48f4c6811f687960524769ea800bc92))
* **deps:** update plugin com.gradle.develocity to v4.2.2 ([#85](https://github.com/mobilemobilellc/solpan/issues/85)) ([58320b1](https://github.com/mobilemobilellc/solpan/commit/58320b13ed20bd74fab68f552de439ff847658b9))
* **deps:** update plugin com.gradle.develocity to v4.4.1 ([#97](https://github.com/mobilemobilellc/solpan/issues/97)) ([8efbb33](https://github.com/mobilemobilellc/solpan/commit/8efbb331b1361f1d5da4e8ca90ca055ecb9f2324))
* **deps:** update the pages actions ([#142](https://github.com/mobilemobilellc/solpan/issues/142)) ([902e3d8](https://github.com/mobilemobilellc/solpan/commit/902e3d82aede28d9880fe70dc9e455daafb899bb))
* **docs:** Update README.md ([#9](https://github.com/mobilemobilellc/solpan/issues/9)) ([13b8028](https://github.com/mobilemobilellc/solpan/commit/13b80286677a998676bc68395fff961fbf7104e6))
* **formatting:** update spotless configuration ([00b9b8c](https://github.com/mobilemobilellc/solpan/commit/00b9b8c2880abfda29a144e2eb684bfdebd4bae6))
* **formatting:** update spotless configuration ([c0f8fbe](https://github.com/mobilemobilellc/solpan/commit/c0f8fbe73d0b4a7190c8bff622a41969f10564b6))
* **renovate:** configure automerge and group dependencies ([#47](https://github.com/mobilemobilellc/solpan/issues/47)) ([55f06c1](https://github.com/mobilemobilellc/solpan/commit/55f06c12a17da9fe3b9afe426bb705f4df398a8f))
* update develocity Gradle plugin versions ([7836798](https://github.com/mobilemobilellc/solpan/commit/7836798ed525193d73bfacf8f180fec8214dd616))
* update Firebase Analytics import ([df10b93](https://github.com/mobilemobilellc/solpan/commit/df10b93d4c99b07e1a3c86d913b4be21e2df7354))
* update Gradle plugin versions ([a9769a8](https://github.com/mobilemobilellc/solpan/commit/a9769a8f09841b9cba128ec1c01e8dcb215c641f))
* update Proguard rules for kotlinx.serialization ([#60](https://github.com/mobilemobilellc/solpan/issues/60)) ([cedf656](https://github.com/mobilemobilellc/solpan/commit/cedf656f4cdb0cf13e1a8d9af48af46bc97d912c))
* update release configuration and include extra files in release process ([#104](https://github.com/mobilemobilellc/solpan/issues/104)) ([09acb15](https://github.com/mobilemobilellc/solpan/commit/09acb15f320ee6d7c895a34097496e5fb2cc0e3f))

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
