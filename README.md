# Android Locale Resource Generator

A Gradle plugin for Android projects to automate locale configuration and provide supporting source/resource files.
Built with Android Gradle Plugin (AGP) 7.5.0

## Functionality

- Generates `locale-config.xml` file for Android [per-app language settings](https://developer.android.com/guide/topics/resources/app-languages)
- Generates `SupportedLocales` Kotlin class for in-app access to your configured locales
- Includes pseudolocale support when pseudolocales enabled for build variant

## Community and Docs

- [Wiki](../../wiki) for more details on language tag support and plugin configuration
- [Discussions](../../discussions) for project roadmap, ideas, Q&A, and polls
- [Issues](../../issues) for found bugs and reporting specific locale issues

## Setup

### Plugin Dependency Management

```kotlin
// File - settings.gradle.kts (project settings)

dependencyResolutionManagement {
    // don't modify
}

// add this if you don't have it
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}
```

### Project Locale Configuration

The `resourceConfigurations` property is a list of explicitly configured resource types to be added to your project. Often, it's used to prevent building your project with extra resources you don't need (like locale resources you don't support coming from project dependencies)

Since it's good practice to specify resources in use, this plugin utilizes the same `resourceConfigurations` list to guide its resource generation. Add your supported locales into this list, and the plugin will handle the rest!

```kotlin
// File -  build.gradle.kts (app module)

plugins {
    //...
    id("com.mermake.locale-resource-generator") version "{{latest}}"
}

android {
    //...
    defaultConfig {
        //...
        resourceConfigurations.addAll(
            listOf(
                "en",
                "en-rUS",
                "es",
                "es-rUS",
                "b+de+DE",
                "en-rXA",
                "ar-rXB"
            )
        )
    }
}
```
> See the wiki page on [Locales and Android Support](../../wiki/Locales-and-Android-Support) for details on supported language tags.

### Manifest Configuration

```xml
<!-- AndroidManifest.xml -->
<application>
    <!-- Include this line in your application tag. The file is generated when you build or run the locale config task. -->
    android:localeConfig="@xml/locale_config"
  
    <!-- Add this for supporting lower than API 33 (see Android docs for more info) -->
    <service
            android:name="androidx.appcompat.app.AppLocalesMetadataHolderService"
            android:enabled="false"
            android:exported="false">
            <meta-data
                android:name="autoStoreLocales"
                android:value="true" />
        </service>
</application>
```

## Gradle Tasks

The plugin runs its tasks automatically before the `preBuild` step of your normal builds. A variant task is configured for each of your project's build variants. (debug, release, wumbo, etc.)
- `generateLocaleConfig[Variant]` - generates `locale_config.xml` resource file
- `generateSupportedLocales[Variant]` - generates `SupportedLocales.kt` class
- `soakConfiguredLocales[Variant]` - creates the intermediate files with supported locale information that is used in the other tasks
