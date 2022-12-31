package com.mermake.locale_resource_generator.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.redundent.kotlin.xml.Namespace
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.xml

/**
 * Generates a `locale_config.xml` file to be referenced in an Android project Manifest for
 * configuring application locale support. Debug and Release versions are added to their respective
 * source sets.
 */
abstract class GenerateLocaleConfigTask : DefaultTask() {
    @get:InputFile
    abstract val languageListInput: RegularFileProperty

    @get:OutputFile
    abstract val localeConfigOutput: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val langList = languageListInput.get().asFile.reader()

        val xml = xml("locale-config") {
            globalProcessingInstruction("xml", "version" to "1.0", "encoding" to "utf-8")

            val namespace = Namespace("android", "http://schemas.android.com/apk/res/android")
            namespace(namespace)

            langList.forEachLine {
                val line = it.split(',')

                comment(line.last())
                element("locale") {
                    attribute("name", line.first(), namespace)
                }
            }
        }

        localeConfigOutput.get().asFile.writeText(
            xml.toString(PrintOptions(pretty = true, singleLineTextElements = true))
        )

        logger.info("`locale_config.xml` output to ${localeConfigOutput.get()}")
    }
}