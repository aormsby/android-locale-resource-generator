package com.mermake.locale_resource_generator.tasks


import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir
import spock.lang.Unroll

class SoakConfiguredLocalesTaskTest extends Specification {

    @TempDir
    File tempDir

    SoakConfiguredLocalesTask task
    File outputFile

    def setup() {
        def project = ProjectBuilder.builder().build()
        task = project.tasks.register('soakConfiguredLocales', SoakConfiguredLocalesTask).get()

        outputFile = new File(tempDir, 'soaked_locale_list.txt')
        task.languageTagListOutput.set(outputFile)
    }

    @Unroll
    def "task correctly processes configured input locales"(Set<String> input) {
        given: "set of language tags"
        task.resourceConfigInput.set(input)

        when: "task is executed"
        task.taskAction()

        then: "output file contains correctly formatted tags and endonyms"
        outputFile.text.split("\n") == output

        where:
        input                   || output
        androidSupportedLocales || fullLocaleOutput
        androidBPlusInput       || fullLocaleOutput
        duplicatesInput         || duplicatesStrippedOutput
        pseudolocaleInput       || pseudolocaleOutput
    }

    @Shared
    Set<String> duplicatesInput = [
            'en-US', 'fr-FR', 'es-ES', 'de', 'it-IT', 'ja-JP', 'ko-KR',
            'en-US', 'fr-FR', 'es-ES', 'de', 'it-IT', 'ja-JP', 'ko-KR',
    ]

    @Shared
    String[] duplicatesStrippedOutput = [
            "de,Deutsch",
            "en-US,English (United States)",
            "es-ES,español (España)",
            "fr-FR,français (France)",
            "it-IT,italiano (Italia)",
            "ja-JP,日本語 (日本)",
            "ko-KR,한국어 (대한민국)",
    ]

    @Shared
    String[] pseudolocaleInput = ['ar-XB', 'en-XA']

    @Shared
    String[] pseudolocaleOutput = [
            "ar-XB,العربية (لكنات تجريبية ثنائية الاتجاه)",
            "en-XA,English (Pseudo-Accents)",
    ]

    @Shared
    Set<String> androidSupportedLocales = [
            "af", "af-rZA",
            "am", "am-rET",
            "ar", "ar-rEG", "ar-rIL",
            "as",
            "az",
            "be",
            "bg", "bg-rBG",
            "bn",
            "bs",
            "ca", "ca-rES",
            "cs", "cs-rCZ",
            "da", "da-rDK",
            "de", "de-rAT", "de-rCH", "de-rDE", "de-rLI",
            "el", "el-rGR",
            "en-rAU", "en-rCA", "en-rGB", "en-rIE", "en-rIN", "en-rNZ", "en-rSG", "en-rUS", "en-rZA",
            "es", "es-rCO", "es-rCR", "es-rEC", "es-rES", "es-rGT", "es-rHN", "es-rMX", "es-rNI", "es-rPA", "es-rPE", "es-rSV", "es-rUS",
            "et",
            "eu",
            "fa", "fa-rIR",
            "fi", "fi-rFI",
            "fr", "fr-rBE", "fr-rCA", "fr-rCH", "fr-rFR",
            "gl",
            "gu",
            "he",
            "hi", "hi-rIN",
            "hr", "hr-rHR",
            "hu", "hu-rHU",
            "hy",
            "in", "in-rID",
            "is",
            "it", "it-rCH", "it-rIT",
            "iw", "iw-rIL",
            "ja", "ja-rJP",
            "ka",
            "kk",
            "km",
            "kn",
            "ko", "ko-rKR",
            "ky",
            "lo",
            "lt", "lt-rLT",
            "lv", "lv-rLV",
            "sr-Latn",
    ]

    @Shared
    Set<String> androidBPlusInput = [
            "b+af", "b+af+ZA",
            "b+am", "b+am+ET",
            "b+ar", "b+ar+EG", "b+ar+IL",
            "b+as",
            "b+az",
            "b+be",
            "b+bg", "b+bg+BG",
            "b+bn",
            "b+bs",
            "b+ca", "b+ca+ES",
            "b+cs", "b+cs+CZ",
            "b+da", "b+da+DK",
            "b+de", "b+de+AT", "b+de+CH", "b+de+DE", "b+de+LI",
            "b+el", "b+el+GR",
            "b+en+AU", "b+en+CA", "b+en+GB", "b+en+IE", "b+en+IN", "b+en+NZ", "b+en+SG", "b+en+US", "b+en+ZA",
            "b+es", "b+es+CO", "b+es+CR", "b+es+EC", "b+es+ES", "b+es+GT", "b+es+HN", "b+es+MX", "b+es+NI", "b+es+PA", "b+es+PE", "b+es+SV", "b+es+US",
            "b+et",
            "b+eu",
            "b+fa", "b+fa+IR",
            "b+fi", "b+fi+FI",
            "b+fr", "b+fr+BE", "b+fr+CA", "b+fr+CH", "b+fr+FR",
            "b+gl",
            "b+gu",
            "b+he",
            "b+hi", "b+hi+IN",
            "b+hr", "b+hr+HR",
            "b+hu", "b+hu+HU",
            "b+hy",
            "b+in", "b+in+ID",
            "b+is",
            "b+it", "b+it+CH", "b+it+IT",
            "b+iw", "b+iw+IL",
            "b+ja", "b+ja+JP",
            "b+ka",
            "b+kk",
            "b+km",
            "b+kn",
            "b+ko", "b+ko+KR",
            "b+ky",
            "b+lo",
            "b+lt", "b+lt+LT",
            "b+lv", "b+lv+LV",
            "b+sr+Latn",
    ]

    @Shared
    String[] fullLocaleOutput = [
            "af,Afrikaans",
            "af-ZA,Afrikaans (Suid-Afrika)",
            "am,አማርኛ",
            "am-ET,አማርኛ (ኢትዮጵያ)",
            "ar,العربية",
            "ar-EG,العربية (مصر)",
            "ar-IL,العربية (إسرائيل)",
            "as,অসমীয়া",
            "az,azərbaycan",
            "be,беларуская",
            "bg,български",
            "bg-BG,български (България)",
            "bn,বাংলা",
            "bs,bosanski",
            "ca,català",
            "ca-ES,català (Espanya)",
            "cs,čeština",
            "cs-CZ,čeština (Česko)",
            "da,dansk",
            "da-DK,dansk (Danmark)",
            "de,Deutsch",
            "de-AT,Deutsch (Österreich)",
            "de-CH,Deutsch (Schweiz)",
            "de-DE,Deutsch (Deutschland)",
            "de-LI,Deutsch (Liechtenstein)",
            "el,Ελληνικά",
            "el-GR,Ελληνικά (Ελλάδα)",
            "en-AU,English (Australia)",
            "en-CA,English (Canada)",
            "en-GB,English (United Kingdom)",
            "en-IE,English (Ireland)",
            "en-IN,English (India)",
            "en-NZ,English (New Zealand)",
            "en-SG,English (Singapore)",
            "en-US,English (United States)",
            "en-ZA,English (South Africa)",
            "es,español",
            "es-CO,español (Colombia)",
            "es-CR,español (Costa Rica)",
            "es-EC,español (Ecuador)",
            "es-ES,español (España)",
            "es-GT,español (Guatemala)",
            "es-HN,español (Honduras)",
            "es-MX,español (México)",
            "es-NI,español (Nicaragua)",
            "es-PA,español (Panamá)",
            "es-PE,español (Perú)",
            "es-SV,español (El Salvador)",
            "es-US,español (Estados Unidos)",
            "et,eesti",
            "eu,euskara",
            "fa,فارسی",
            "fa-IR,فارسی (ایران)",
            "fi,suomi",
            "fi-FI,suomi (Suomi)",
            "fr,français",
            "fr-BE,français (Belgique)",
            "fr-CA,français (Canada)",
            "fr-CH,français (Suisse)",
            "fr-FR,français (France)",
            "gl,galego",
            "gu,ગુજરાતી",
            "he,עברית",
            "hi,हिन्दी",
            "hi-IN,हिन्दी (भारत)",
            "hr,hrvatski",
            "hr-HR,hrvatski (Hrvatska)",
            "hu,magyar",
            "hu-HU,magyar (Magyarország)",
            "hy,հայերեն",
            "in,Indonesia",
            "in-ID,Indonesia (Indonesia)",
            "is,íslenska",
            "it,italiano",
            "it-CH,italiano (Svizzera)",
            "it-IT,italiano (Italia)",
            "iw,עברית",
            "iw-IL,עברית (ישראל)",
            "ja,日本語",
            "ja-JP,日本語 (日本)",
            "ka,ქართული",
            "kk,қазақ тілі",
            "km,ខ្មែរ",
            "kn,ಕನ್ನಡ",
            "ko,한국어",
            "ko-KR,한국어 (대한민국)",
            "ky,кыргызча",
            "lo,ລາວ",
            "lt,lietuvių",
            "lt-LT,lietuvių (Lietuva)",
            "lv,latviešu",
            "lv-LV,latviešu (Latvija)",
            "sr-Latn,srpski (latinica)",
    ]
}
