package com.mermake.locale_resource_generator.tasks


import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.TempDir

class GenerateSupportedLocalesTaskTest extends Specification {

    @TempDir
    File outputDir

    GenerateSupportedLocalesTask task
    String packageName = "com.test.loc_res"
    String className = "SupportedLocales"

    def setup() {
        def project = ProjectBuilder.builder().build()
        task = project.tasks.register('generateSupportedLocales', GenerateSupportedLocalesTask).get()

        task.packageName.set(packageName)
        task.outputDir.set(outputDir)
    }

    def "process locales, verify properties and functions"() {
        given: "temp intermediate local tags file"
        task.languageListInput.set(createIntermediateFile(locInput))

        when: "task executed"
        File outputFile = runActionAndGetOutput()

        then: "output file contains expected properties/methods/locale data"
        propsAndFuncs.forEach { s ->
            assert outputFile.text.contains(s)
        }
    }

    def "process locales, verify invoked function results"() {
        given: "temp intermediate local tags file"
        task.languageListInput.set(createIntermediateFile(locInput))

        when: "task is executed, class is instantiated"
        File outputFile = runActionAndGetOutput()
        Object classInstance = compileSupportedLocales(outputFile, outputDir)

        then: "invoking functions returns expected data"
        classInstance.getTags() == tags
        classInstance.getEndonyms() == endonyms
        classInstance.getExonyms("en-US") == enExonymns
        classInstance.getExonyms(Locale.forLanguageTag("en-US")) == enExonymns
    }

    def "process locales, verify invoked function failures"() {
        given: "temp intermediate local tags file"
        task.languageListInput.set(createIntermediateFile(locInput))

        when: "task is executed, class is instantiated"
        File outputFile = runActionAndGetOutput()
        Object classInstance = compileSupportedLocales(outputFile, outputDir)

        and: "invoke functions with bad language tag"
        classInstance.getExonyms("zz")

        then: "exception thrown"
        thrown(NoSuchElementException)

        when: "invoke functions with bad Local"
        classInstance.getExonyms(Locale.forLanguageTag("zz"))

        then: "exception thrown"
        thrown(NoSuchElementException)
    }

    String[] locInput = [
            "en-US,English (United States)",
            "en-XA,English (Pseudo-Accents)",
            "es-ES,español (España)",
            "fr-FR,français (France)",
    ]

    def tags = [
            "en-US",
            "en-XA",
            "es-ES",
            "fr-FR",
    ]

    def endonyms = [
            "en-US": "English (United States)",
            "en-XA": "English (Pseudo-Accents)",
            "es-ES": "español (España)",
            "fr-FR": "français (France)",
    ]

    def enExonymns = [
            "en-US": "English (United States)",
            "en-XA": "English (Pseudo-Accents)",
            "es-ES": "Spanish (Spain)",
            "fr-FR": "French (France)",
    ]

    def propsAndFuncs = [
            "package ${packageName}",
            "public class ${className}",
            "private val tags: List<String>",
            "private val endonyms: Map<String, String>",
            "private val exonyms: Map<String, Map<String, String>>",
            "private val errorTagNotFound",
            "public fun getTags()",
            "public fun getEndonyms()",
            "public fun getExonyms(languageTag: String)",
            "public fun getExonyms(locale: Locale)",
    ]

    private File createIntermediateFile(String[] input) {
        File tempFile = File.createTempFile("test_tags", "txt")
        tempFile.deleteOnExit()
        tempFile.write(locInput.join("\n"))
        return tempFile
    }

    private File runActionAndGetOutput() {
        task.taskAction()
        return new File(outputDir, "${packageName.replace('.', '/')}/${className}.kt")
    }

    private Object compileSupportedLocales(File kotlinFile, File outputDir) {
        String[] compileCommand = ["kotlinc", kotlinFile.absolutePath, "-d", outputDir.absolutePath]
        Process compileProcess = Runtime.getRuntime().exec(compileCommand)
        int exitCode = compileProcess.waitFor()

        if (exitCode == 0) {
            String className = "${packageName}.${className}"
            URLClassLoader classLoader = new URLClassLoader([outputDir.toURI().toURL()] as URL[])
            Class compiledClass = classLoader.loadClass(className)
            Object instance = compiledClass.getDeclaredConstructor().newInstance()
            return instance
        } else {
            throw new RuntimeException("Compilation failed with exit code $exitCode")
        }
    }
}
