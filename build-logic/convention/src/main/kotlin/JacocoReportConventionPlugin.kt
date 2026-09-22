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
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

class JacocoReportConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            extensions.configure(JacocoPluginExtension::class.java) {
                toolVersion = "0.8.12"
            }

            tasks.register<JacocoReport>("jacocoTestReport") {
                dependsOn("testDebugUnitTest")

                reports {
                    html.required.set(true)
                    html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
                    csv.required.set(true)
                    csv.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacoco.csv"))
                    xml.required.set(true)
                    xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacoco.xml"))
                }

                val excludes =
                    setOf(
                        "**/R.class",
                        "**/R${'$'}*.class",
                        "**/BuildConfig.*",
                        "**/Manifest*.*",
                        "**/*Test*.*",
                        "**/databinding/**",
                        "**/generated/**",
                    )

                // The unit tests live in :app but exercise code that #95 moved out into the
                // core and feature modules, so the report has to span every module or it
                // measures the wrong classes.
                val modules = rootProject.subprojects.filter { it.name != "baselineprofile" }

                classDirectories.setFrom(
                    modules.flatMap { module ->
                        listOf(
                            "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes",
                            "intermediates/javac/debug/compileDebugJavaWithJavac/classes",
                        ).map { path ->
                            module.layout.buildDirectory.dir(path).map {
                                fileTree(it) { setExcludes(excludes) }
                            }
                        }
                    },
                )

                sourceDirectories.setFrom(
                    modules.flatMap { module ->
                        listOf("src/main/java", "src/main/kotlin").map { module.file(it) }
                    },
                )

                executionData.setFrom(
                    modules.map { module ->
                        module.layout.buildDirectory
                            .dir("outputs/unit_test_code_coverage/debugUnitTest")
                            .map { fileTree(it) { setIncludes(setOf("*.exec")) } }
                    },
                )
            }
        }
    }
}
