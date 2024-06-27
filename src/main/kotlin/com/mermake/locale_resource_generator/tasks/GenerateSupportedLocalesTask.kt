package com.mermake.locale_resource_generator.tasks

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.util.*

/**
 * Generates a Kotlin source file `SupportedLocales.kt` that contains a map of language tags to
 * language names (both endonyms and exonyms). Debug and Release versions are added to their respective
 * source sets.
 */
abstract class GenerateSupportedLocalesTask : DefaultTask() {
    @get:Input
    abstract val packageName: Property<String>

    @get:InputFile
    abstract val languageListInput: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val supportedLanguageSets = languageListInput.get().asFile.readLines().map { it.split(',') }.toSet()
        val supportedLanguageTags = supportedLanguageSets.map { it.first() }

        val outputClass = "SupportedLocales"
        val fileSpec = outputClass.fileSpecBuilder(supportedLanguageSets, supportedLanguageTags)

        fileSpec.writeTo(outputDir.get().asFile)
        logger.lifecycle("$outputClass.kt output to ${outputDir.get().asFile}")
    }

    private fun String.fileSpecBuilder(
        supportedLanguageSets: Set<List<String>>,
        supportedLanguageTags: List<String>,
    ): FileSpec {
        val classKdoc =
            """Generated class containing the locales supported by your project in the form of list and maps.
            |Language tags and their corresponding names (endonyms and exonyms) are retrievable through public functions.
            """.trimMargin()

        // todo: v1.1 fix 'method too long' issue and reduce passes over tags to build these items
        // private property/func builders
        val localeList = buildLocaleList(supportedLanguageTags)
        val tagsList = buildTagsList(supportedLanguageTags)
        val endonymsMap = buildEndonymsMap(supportedLanguageSets)
        val exonymsMap = buildExonymsMap(localeList)
        val errorTagNotFoundMessage = buildErrorTagNotFound()

        // public func builders
        val tagsFunc = buildTagsAccessor(tagsList)
        val endonymsFunc = buildEndonymsAccessor(endonymsMap)
        val exonymsFromTagFunc = buildExonymsFromTagAccessor(exonymsMap, errorTagNotFoundMessage)
        val exonymsFromLocaleFunc = buildExonymsFromLocaleAccessor(exonymsMap, errorTagNotFoundMessage)

        // build and return file contents
        return FileSpec.builder(packageName.get(), this)
            .addType(
                TypeSpec.classBuilder(this)
                    .addKdoc(classKdoc)
                    .addProperties(
                        listOf(
                            tagsList,
                            endonymsMap,
                            exonymsMap,
                            errorTagNotFoundMessage
                        )
                    ).addFunctions(
                        listOf(
                            tagsFunc,
                            endonymsFunc,
                            exonymsFromTagFunc,
                            exonymsFromLocaleFunc,
                        )
                    ).build()
            ).build()
    }

    private fun buildLocaleList(supportedLanguageTags: List<String>) =
        supportedLanguageTags.map { Locale.forLanguageTag(it) }

    private fun buildTagsList(languageTags: List<String>) =
        PropertySpec.builder(
            name = "tags",
            type = List::class.parameterizedBy(String::class)
        ).initializer(
            "listOf(\n%L\n)",
            languageTags.joinToString(",\n") { "\"$it\"" }
        ).addModifiers(
            KModifier.PRIVATE
        ).build()

    private fun buildEndonymsMap(languageSets: Set<List<String>>) =
        PropertySpec.builder(
            name = "endonyms",
            type = Map::class.parameterizedBy(String::class, String::class)
        ).initializer(
            "mapOf(\n%L\n)",
            languageSets.joinToString(",\n") {
                "\"${it.first()}\" to \"${it.last()}\""
            }
        ).addModifiers(
            KModifier.PRIVATE
        ).build()

    private fun buildExonymsMap(locales: List<Locale>) =
        PropertySpec.builder(
            name = "exonyms",
            type = Map::class.asClassName()
                .parameterizedBy(
                    String::class.asTypeName(),
                    Map::class.parameterizedBy(String::class, String::class)
                )
        ).initializer(
            "mapOf(\n%L\n)",
            generateExonymMapString(locales)
        ).addModifiers(
            KModifier.PRIVATE
        ).build()

    private fun generateExonymMapString(locales: List<Locale>) =
        locales.map { loc ->
            val exonyms = locales.map { it.toLanguageTag() }
                .zip(locales.map { it.getDisplayName(loc) })

            "\"${loc.toLanguageTag()}\" to mapOf(\n${
                exonyms.joinToString(",\n") { "\"${it.first}\" to \"${it.second}\"" }
            }\n)"
        }.joinToString(",\n")

    private fun buildTagsAccessor(property: PropertySpec) =
        FunSpec.builder("getTags")
            .addKdoc("@returns List of language tags supported by your project.")
            .addStatement("return %N", property)
            .build()

    private fun buildEndonymsAccessor(property: PropertySpec) =
        FunSpec.builder("getEndonyms")
            .addKdoc(
                """@returns Map of language tags and their written endoynms.
                |(Endonyms are the preferred name of a language as written in that language.)
                |""".trimMargin()
            )
            .addStatement("return %N", property)
            .build()

    private fun buildExonymsFromTagAccessor(property: PropertySpec, error: PropertySpec): FunSpec {
        val langTagParam = ParameterSpec.builder("languageTag", String::class)
            .build()

        return FunSpec.builder("getExonyms")
            .addKdoc(
                """@param ${langTagParam.name} a Unicode-formatted language tag in [String] form such as "en-US".
                |@returns Map of language tags and their written exoynms according to the resolved ${langTagParam.name}.
                |(Exonyms are the name of a language written in another language.)
                |""".trimMargin()
            )
            .addParameter(langTagParam)
            .addStatement("return %N[%N] ?: throw %N", property, langTagParam, error)
            .build()
    }

    private fun buildExonymsFromLocaleAccessor(property: PropertySpec, error: PropertySpec): FunSpec {
        val localeParam = ParameterSpec.builder("locale", Locale::class)
            .build()

        return FunSpec.builder("getExonyms")
            .addKdoc(
                """@param ${localeParam.name} a Java [Locale] object.
                |@returns Map of language tags and their written exoynms according to the resolved ${localeParam.name}.
                |(Exonyms are the name of a language written in another language.)
                |""".trimMargin()
            )
            .addParameter(localeParam)
            .addStatement("return %N[%N.toLanguageTag()] ?: throw %N", property, localeParam, error)
            .build()
    }

    private fun buildErrorTagNotFound() =
        PropertySpec
            .builder(name = "errorTagNotFound", type = NoSuchElementException::class)
            .initializer(
                "\n%T(\n%L)", NoSuchElementException::class,
                """
                    |"The·supplied·language·tag·was·not·found·in·your·supported·locales." +
                    |"Did·you·add·it·to·the·'resourceConfigurations'·property·of·your·gradle·build·settings?"
                    |""".trimMargin()
            ).addModifiers(
                KModifier.PRIVATE
            ).build()
}