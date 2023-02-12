import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    `kotlin-dsl`    // gradle manages version for compatibility
    id("com.gradle.plugin-publish") version "1.1.0"
    signing
}

group = "com.mermake"
version = "0.1"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.4.0")
    implementation("org.redundent:kotlin-xml-builder:1.8.0")
    implementation("com.squareup:kotlinpoet:1.12.0") {
        exclude(module = "kotlin-reflect")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.7"
    kotlinOptions.jvmTarget = "11"
}

gradlePlugin {
    plugins {
        register("localeResourceGeneratorPlugin") {
            id = "com.mermake.locale-resource-generator"
            displayName = "Locale Resource Generator"
            description = "Automatically generates locale_config.xml for per-app language support on Android 13 and higher. " +
                    "Also generates map of supported locales/languages for use in a runtime language picker."
            implementationClass = "com.mermake.locale_resource_generator.LocaleResourceGeneratorPlugin"
        }
    }
}

pluginBundle {
    // TODO: update with mermake group info when available (date very much tbd)
    website = "https://adamormsby.com"
    vcsUrl = "https://github.com/aormsby/android-locale-resource-generator"
    tags = listOf("android", "android 13", "localization", "locale config", "language support", "automation")
}