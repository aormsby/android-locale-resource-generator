package com.mermake.locale_resource_generator

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

class LocaleResourceGeneratorPluginTest extends Specification {

    @TempDir
    File testProjectDir
    File buildFile

    def setup() {
        buildFile = new File(testProjectDir, 'build.gradle.kts')
        buildFile << """
            plugins {
                id("com.android.application")
                id("com.mermake.locale-resource-generator") version "0.1"
            }
            android {
                compileSdk = 30
            }
        """
    }

    def "locale generation tasks run as dependents of preBuild"(String variant, String[] expectedTasks) {
        given: "android build runner"
        def runner = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments("pre${variant}Build", "--dry-run")
                .withPluginClasspath()

        when: "run preBuild variant"
        def result = runner.build()

        then: "dependent locale generation tasks are run"
        expectedTasks.each {
            assert result.output.contains(it)
        }

        where:
        variant   || expectedTasks
        "Debug"   || taskList("Debug")
        "Release" || taskList("Release")
    }

    private def taskList(String variant) {
        return [
                "pre${variant}Build",
                "soakConfiguredLocales${variant}",
                "generateLocaleConfig${variant}",
                "generateSupportedLocales${variant}",
        ]
    }
}
