/*
 * Copyright 2025 MobileMobile LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://androidx.dev/snapshots/builds/13786634/artifacts/repository")
        }
    }
}
dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://androidx.dev/snapshots/builds/13786634/artifacts/repository") }
  }
}

rootProject.name = "SolPan"

include(":app")

plugins {
  id("org.gradle.toolchains.foojay-resolver") version "1.0.0"
  id("com.gradle.develocity") version("4.1")
}

develocity {
  buildScan {
    capture {
      fileFingerprints.set(true)
    }
//    publishing.onlyIf { false }
  }
}

@Suppress("UnstableApiUsage")
toolchainManagement {
  jvm {
    javaRepositories {
      repository("foojay") {
        resolverClass.set(org.gradle.toolchains.foojay.FoojayToolchainResolver::class.java)
      }
    }
  }
}
