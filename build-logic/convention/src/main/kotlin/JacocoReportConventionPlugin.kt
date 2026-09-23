/*
 * Copyright 2025 MobileMobile LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.FileCollection
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

/** Line coverage floor. A ratchet against regression, not a target. See ROADMAP.md. */
private const val MINIMUM_LINE_COVERAGE = "0.12"

class JacocoReportConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            extensions.configure(JacocoPluginExtension::class.java) {
                toolVersion = libs.findVersion("jacoco").get().toString()
            }

            tasks.register<JacocoReport>("jacocoTestReport") {
                // Every module's tests feed this report, so every module's test task has to have
                // run first. Depending only on this project's would race the others.
                dependsOn(coveredModules().map { "${it.path}:testDebugUnitTest" })

                reports {
                    html.required.set(true)
                    html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
                    csv.required.set(true)
                    csv.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacoco.csv"))
                    xml.required.set(true)
                    xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacoco.xml"))
                }

                classDirectories.setFrom(coverageClassDirectories())
                sourceDirectories.setFrom(coverageSourceDirectories())
                executionData.setFrom(coverageExecutionData())
            }

            tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
                dependsOn("jacocoTestReport")

                classDirectories.setFrom(coverageClassDirectories())
                sourceDirectories.setFrom(coverageSourceDirectories())
                executionData.setFrom(coverageExecutionData())

                violationRules {
                    rule {
                        limit {
                            counter = "LINE"
                            value = "COVEREDRATIO"
                            minimum = MINIMUM_LINE_COVERAGE.toBigDecimal()
                        }
                    }
                }
            }
        }
    }
}

/**
 * The modules whose tests and classes belong in the aggregate report: real modules only, so the
 * empty `:core` and `:feature` container projects are skipped, and not the benchmark module.
 */
private fun Project.coveredModules() =
    rootProject.subprojects.filter { it.buildFile.exists() && it.name != "baselineprofile" }

private val coverageExcludes =
    setOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/databinding/**",
        "**/generated/**",
    )

/**
 * The tests in one module cover code in others, so both tasks read every module. AGP 9 emits
 * compiled classes under `intermediates`, not the `tmp/kotlin-classes` path older setups used.
 */
private fun Project.coverageClassDirectories(): FileCollection =
    files(
        coveredModules().flatMap { module ->
            listOf(
                    "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes",
                    "intermediates/javac/debug/compileDebugJavaWithJavac/classes",
                )
                .map { path ->
                    module.layout.buildDirectory.dir(path).map {
                        fileTree(it) { setExcludes(coverageExcludes) }
                    }
                }
        },
    )

private fun Project.coverageSourceDirectories(): FileCollection =
    files(
        coveredModules().flatMap { module ->
            listOf("src/main/java", "src/main/kotlin").map { module.file(it) }
        },
    )

private fun Project.coverageExecutionData(): FileCollection =
    files(
        coveredModules().map { module ->
            module.layout.buildDirectory
                .dir("outputs/unit_test_code_coverage/debugUnitTest")
                .map { fileTree(it) { setIncludes(setOf("*.exec")) } }
        },
    )
