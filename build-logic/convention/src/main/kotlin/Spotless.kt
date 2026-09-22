/*
 * Copyright 2025 MobileMobile LLC
 */

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * Applies spotless with one shared configuration, so every module is formatted to the same bar
 * rather than only `:app`. Paths resolve against the root so they do not depend on how deeply a
 * module is nested.
 */
internal fun Project.configureSpotless() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    val ktlintVersion = libs.findVersion("ktlint").get().toString()
    val composeRulesVersion = libs.findVersion("ktlintCompose").get().toString()

    with(pluginManager) {
        apply("com.diffplug.spotless")
    }

    val editorConfig = rootProject.file("configs/spotless/.editorconfig")
    val copyright = rootProject.file("configs/spotless/copyright.kt")
    val composeRuleSet = listOf("io.nlopez.compose.rules:ktlint:$composeRulesVersion")

    extensions.configure<SpotlessExtension> {
        kotlin {
            target("src/*/kotlin/**/*.kt", "src/*/java/**/*.kt")
            ktfmt()
            ktlint(ktlintVersion).setEditorConfigPath(editorConfig).customRuleSets(composeRuleSet)
            licenseHeaderFile(copyright)
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktfmt()
            ktlint(ktlintVersion).setEditorConfigPath(editorConfig).customRuleSets(composeRuleSet)
        }
    }
}
