import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.gradle.plugin-publish") version "1.1.0"
    `kotlin-dsl`    // gradle kotlin dsl, syntax allows gradle to manage version for compatibility
    signing         // gradle signing plugin
    groovy          // for testing with Spock
}

repositories {
    google()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.4.0")
    implementation("org.redundent:kotlin-xml-builder:1.8.0")
    implementation("com.squareup:kotlinpoet:1.12.0") {
        exclude(module = "kotlin-reflect")
    }

    testImplementation(platform("org.spockframework:spock-bom:2.3-groovy-3.0"))
    testImplementation("org.spockframework:spock-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.7"
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

group = "com.mermake"
version = "0.1.1"

gradlePlugin {
    plugins {
        register("localeResourceGeneratorPlugin") {
            id = "com.mermake.locale-resource-generator"
            displayName = "Locale Resource Generator"
            implementationClass = "com.mermake.locale_resource_generator.LocaleResourceGeneratorPlugin"
            description =
                "Automatically generates locale_config.xml for per-app language support on Android 13 and higher. " +
                        "Also generates map of supported locales/languages for use in a runtime language picker."
        }
    }
}

pluginBundle {
    website = "https://adamormsby.com"
    vcsUrl = "https://github.com/aormsby/android-locale-resource-generator"
    tags = listOf(
        "android",
        "android plugin",
        "android 13",
        "tiramisu",
        "localization",
        "internationalization",
        "locale config",
        "language support",
        "automation"
    )
}