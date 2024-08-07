package com.mermake.locale_resource_generator.data

class Intermediates {
    static File createFile(String[] input) {
        File tempFile = File.createTempFile("test_tags", "txt")
        tempFile.deleteOnExit()
        tempFile.write(input.join("\n"))
        return tempFile
    }

    static String[] pseudolocales = [
            "ar-XB,العربية (لكنات تجريبية ثنائية الاتجاه)",
            "en-XA,English (Pseudo-Accents)",
    ]

    static String[] withDuplicatesStripped = [
            "de,Deutsch",
            "en-US,English (United States)",
            "es-ES,español (España)",
            "fr-FR,français (France)",
            "it-IT,italiano (Italia)",
            "ja-JP,日本語 (日本)",
            "ko-KR,한국어 (대한민국)",
    ]

    static String[] smallBatch = [
            "en-US,English (United States)",
            "en-XA,English (Pseudo-Accents)",
            "zh-TW,中文 (台灣)",
    ]

    static String[] androidSupported = [
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
            "en,English",
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
            "zh,中文",
            "zh-CN,中文 (中国)",
            "zh-TW,中文 (台灣)",
    ]
}
