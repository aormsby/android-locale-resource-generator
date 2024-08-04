package com.mermake.locale_resource_generator.tasks

import com.mermake.locale_resource_generator.data.Intermediates
import com.mermake.locale_resource_generator.data.Xml
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.TempDir

class GenerateLocaleConfigTaskTest extends Specification {

    @TempDir
    File outputDir

    GenerateLocaleConfigTask task
    File outputFile

    def setup() {
        def project = ProjectBuilder.builder().build()
        task = project.tasks.register("generateLocaleConfig", GenerateLocaleConfigTask).get()

        outputFile = new File(outputDir, "locale_config.xml")
        task.localeConfigOutput.set(outputFile)
    }

    def "process locales, output correct locale config"(String[] input, String[] output) {
        given: "temp intermediate local tags file"
        task.languageListInput.set(Intermediates.createFile(input))

        when: "task is executed, xml is generated"
        task.taskAction()

        then: "output file contains correctly formatted language tag data"
        outputFile.readLines().eachWithIndex { String s, int i ->
            assert s.trim() == output[i]
        }

        where:
        input                          || output
        Intermediates.smallBatch       || Xml.smallBatch
        Intermediates.androidSupported || Xml.androidSupported
        Intermediates.pseudolocales    || Xml.pseudolocales
    }
}
