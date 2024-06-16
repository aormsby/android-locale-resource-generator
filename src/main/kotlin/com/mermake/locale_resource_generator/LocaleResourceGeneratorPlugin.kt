package com.mermake.locale_resource_generator

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import com.mermake.locale_resource_generator.tasks.GenerateLocaleConfigTask
import com.mermake.locale_resource_generator.tasks.GenerateSupportedLocalesTask
import com.mermake.locale_resource_generator.tasks.SoakConfiguredLocalesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class LocaleResourceGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType<AppPlugin> {
            val extension = project.extensions.getByType<ApplicationAndroidComponentsExtension>()
            extension.configure(project)
        }
    }
}

private fun ApplicationAndroidComponentsExtension.configure(project: Project) {
    // it seems odd to get this extension, but I don't know another way to update the source sets at this time
    val appExtension = project.extensions.getByType<AppExtension>()
    val intermediatesOutputDir = project.layout.buildDirectory.dir("intermediates/locale-resource-generator")
    val genDirName = "localeResources"
    val sourceCodePackage = "com.mermake.locale_resources"
    val sourceOutputDir = project.layout.buildDirectory.dir("generated/source/$genDirName")
    val resourceOutputDir = project.layout.buildDirectory.dir("generated/res/locale_resources")
    var rawResourceConfig = mutableSetOf<String>()

    finalizeDsl { extension ->
        rawResourceConfig = extension.defaultConfig.resourceConfigurations.toMutableSet()
    }

    onVariants { variant ->
        // prevents variants overwriting stored config data
        var variantResourceConfig = rawResourceConfig.toMutableSet()

        // Step 1 - if pseudo-locales disabled for variant, remove from 'resourceConfig' set
        if (!variant.pseudoLocalesEnabled.getOrElse(false)) {
            variantResourceConfig = variantResourceConfig.subtract(setOf("en-rXA", "ar-rXB")).toMutableSet()
        }

        // Step 2 - generate intermediate list of supported locales
        val languageListTaskProvider =
            project.tasks.register<SoakConfiguredLocalesTask>("soakConfiguredLocales${variant.name.capitalized()}") {
                resourceConfigInput.set(variantResourceConfig)
                languageTagListOutput.set(intermediatesOutputDir.get().file("${variant.name}/soaked_locale_list.txt"))
            }

        // Step 3 - generate locale_config.xml from intermediate locale list
        val generateLocaleConfigTaskProvider =
            project.tasks.register<GenerateLocaleConfigTask>("generateLocaleConfig${variant.name.capitalized()}") {
                languageListInput.set(languageListTaskProvider.flatMap { it.languageTagListOutput })
                localeConfigOutput.set(resourceOutputDir.get().file("${variant.name}/xml/locale_config.xml"))
            }

        // Step 4 - generate SupportedLocales data class from intermediate locale list
        val generateSupportedLocalesTaskProvider =
            project.tasks.register<GenerateSupportedLocalesTask>("generateSupportedLocales${variant.name.capitalized()}") {
                languageListInput.set(languageListTaskProvider.flatMap { it.languageTagListOutput })
                outputDir.set(sourceOutputDir.get().dir(variant.name))
                packageName.set(sourceCodePackage)
            }

        // Step 5 - add source sets for generated files
        appExtension.sourceSets.named(variant.name) {
            // note: 'res' is Android resources while 'resources' is Java-style resources - need to use the Android one!
            res.srcDir(resourceOutputDir.get().dir(variant.name))
            java.srcDir(generateSupportedLocalesTaskProvider)
        }

        // Step 6 - after configuration is complete, set task dependencies based on variant
        project.afterEvaluate {
            project.tasks.named("pre${variant.name.capitalized()}Build") {
                dependsOn(generateLocaleConfigTaskProvider)
                dependsOn(generateSupportedLocalesTaskProvider)
            }
        }
    }
}