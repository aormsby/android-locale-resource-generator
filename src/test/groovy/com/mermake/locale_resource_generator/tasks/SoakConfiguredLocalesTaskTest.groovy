package com.mermake.locale_resource_generator.tasks

import com.mermake.locale_resource_generator.data.Intermediates
import com.mermake.locale_resource_generator.data.Tags
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.TempDir

class SoakConfiguredLocalesTaskTest extends Specification {

    @TempDir
    File outputDir

    SoakConfiguredLocalesTask task
    File outputFile

    def setup() {
        def project = ProjectBuilder.builder().build()
        task = project.tasks.register('soakConfiguredLocales', SoakConfiguredLocalesTask).get()

        outputFile = new File(outputDir, 'soaked_locale_list.txt')
        task.languageTagListOutput.set(outputFile)
    }

    def "process input locales, output intermediate"(Set<String> input, String[] output) {
        given: "set of language tags"
        task.resourceConfigInput.set(input)

        when: "task executed"
        task.taskAction()

        then: "output file contains correctly formatted tags and endonyms"
        outputFile.text.split("\n") == output

        where:
        input                 || output
        Tags.androidSupported || Intermediates.androidSupported
        Tags.androidBPlus     || Intermediates.androidSupported
        Tags.withDuplicates   || Intermediates.withDuplicatesStripped
        Tags.pseudolocales    || Intermediates.pseudolocales
    }
}
