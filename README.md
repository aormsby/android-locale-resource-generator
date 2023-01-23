# Android Locale Resource Generator

A Gradle plugin for Android projects to automate locale configuration and provide supporting source/resource files.
Built with Android Gradle Plugin (AGP) 7.4.0

## Primary Function

This plugin was built mostly to generate `locale-config.xml` file to support [per-app language settings](https://developer.android.com/guide/topics/resources/app-languages) for Android API 33+. Instead of remembering to modify files each time you add support for a locale, you just add the locale _once_ into your build.gradle file and let this plugin generate the xml resources you need for locale support.

**Bonus:** The plugin automatically includes pseudolocale options if you have them enabled for a build variant.

## Secondary Functions

A `SupportedLocales.kt` source file is generated with maps of the locales your app is set to support. It includes language tags, langauge names (both exonyms and endonyms provided), and functions to get them. This data is intended to assist with building an in-app language selector, but I'd love to hear about other uses.

## How to Use

```kotlin
// settings.gradle.kts (project settings)
dependencyResolutionManagement {
    // should already have this section, don't modify
}

// add this if you don't have it
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}
```

The `resourceConfigurations` property below is a list of explicitly configured resource types to be added to your project. Often, it's used to prevent building your project with extra resources you don't need (like tons of locale resources you don't support coming from dependencies you didn't know hda them.)

Since it's good practice to specify resources in use, this plugin utilizes the same `resourceConfigurations` list in during generation. Just add your supported locales into this list, and the plugin will handle the rest!

```kotlin
// build.gradle.kts (application level)
plugins {
    //...
    id("com.mermake.locale-resource-generator") version "0.1" // check release page for latest version
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
> Notice the different formats of the language tags below. They're not _quite_ Unicode tags, but it's okay. Android recognizes bothe the `xx-rXX` and `bxx+XX+` formats ([more info on locale resolution here](https://developer.android.com/guide/topics/resources/multilingual-support#postN)). This is important to know since some language tags like `de-DE` would cause a build failure and the `b+` format is required. In any case, the plugin converts everything to Unicode-friendly tags since that what we need in the manifest and source code anyway.

```xml
<!-- AndroidManifest.xml -->
<application>
    <!-- Include this line in your application tag. The file is generated when you build and resolves without issue. -->
    android:localeConfig="@xml/locale_config"
    
    
    <!-- Add this for supporting lower than API 33 (see linked Android docs for more details) -->
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

The plugin runs automatically as part of your normal builds. You can also run individual tasks:
- `generateLocaleConfig[Debug|Release|etc]` - generates `locale_config.xml` file for per-app language support

## Glossary

- **Endonym** - A name used by a group or category of people to refer to themselves or their language, as opposed to a name given to them by other groups.
    - In Germany (Deutcshland), the 'German' language is written as 'Deutsche'
    - In Japan (日本), the 'Japanese' language is written as '日本語'


- **Exonym** - Opposite of exonym. A name used to refer to a language or people that is not what they use to refer to themsleves their own language. Many languages have different names for each language.
    - 'French' in Japanese is 'フランス語'
    - 'French' in German is 'Französisch'
