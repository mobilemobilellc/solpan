/*
 * Copyright 2025 MobileMobile LLC
 */

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Applies detekt with one shared configuration, so every module is held to the same bar rather
 * than only `:app`. Config and baseline resolve against the root so they do not depend on how
 * deeply a module is nested.
 */
internal fun Project.configureDetekt() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    with(pluginManager) { apply("io.gitlab.arturbosch.detekt") }

    extensions.configure<DetektExtension> {
        config.setFrom(rootProject.file("configs/detekt/detekt.yml"))
        // No baseline. Findings get fixed or the rule gets turned off with a reason.
        ignoredBuildTypes = listOf("release")
        enableCompilerPlugin.set(true)
    }

    dependencies {
        add("detektPlugins", libs.findLibrary("detekt-formatting").get())
        add("detektPlugins", libs.findLibrary("detekt-compose-rules").get())
    }
}
