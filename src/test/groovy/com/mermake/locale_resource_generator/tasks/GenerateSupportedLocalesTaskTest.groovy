package com.mermake.locale_resource_generator.tasks

import com.mermake.locale_resource_generator.data.Compiled
import com.mermake.locale_resource_generator.data.Intermediates
import com.mermake.locale_resource_generator.data.Tags
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
        task.languageListInput.set(createIntermediateFile(Intermediates.smallBatch))

        when: "task executed"
        File outputFile = runActionAndGetOutput()

        then: "output file contains expected package/properties/methods"
        Compiled.propsAndFuncs(packageName, className).each { str ->
            assert outputFile.text.contains(str)
        }
    }

    def "process locales, verify invoked function results"(String[] input, String[] testArgs, Object[] output) {
        given: "temp intermediate local tags file"
        task.languageListInput.set(createIntermediateFile(input))

        when: "task is executed, class is instantiated"
        File outputFile = runActionAndGetOutput()
        Object classInstance = compileSupportedLocales(outputFile, outputDir)

        then: "invoking functions returns expected data"
        classInstance.getTags() == output[0]
        classInstance.getEndonyms() == output[1]
        testArgs.each { arg ->
            assert classInstance.getExonyms(arg) == output[2][arg]
            assert classInstance.getExonyms(Locale.forLanguageTag(arg)) == output[2][arg]
        }

        where:
        input                    | testArgs        | output
        Intermediates.smallBatch | Tags.smallBatch | [Tags.smallBatch, Compiled.endonymsSmallBatch, Compiled.exonymsSmallBatch]
        // todo: implement after v1.1 fix for 'huge' map
//        Intermediates.androidSupported | Tags.androidSupported | [Tags.androidSupported, Compiled.endonymsAndroidSupported, Compiled.exonymsAndroidSupported]
    }

    def "process locales, verify invoked function failures"() {
        given: "temp intermediate local tags file"
        task.languageListInput.set(createIntermediateFile(Intermediates.smallBatch))

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

    private File createIntermediateFile(String[] input) {
        File tempFile = File.createTempFile("test_tags", "txt")
        tempFile.deleteOnExit()
        tempFile.write(input.join("\n"))
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
