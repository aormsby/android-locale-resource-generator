package com.mermake.locale_resource_generator.data

class Compiled {
    static String[] propsAndFuncs(String packageName, String className) {
        return [
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
    }

    static Map<String, String> endonymsSmallBatch = [
            "en-US": "English (United States)",
            "en-XA": "English (Pseudo-Accents)",
            "zh-TW": "中文 (台灣)",
    ]

    static Map<String, Map<String, String>> exonymsSmallBatch = [
            "en-US": [
                    "en-US": "English (United States)",
                    "en-XA": "English (Pseudo-Accents)",
                    "zh-TW": "Chinese (Taiwan)",
            ],
            "en-XA": [
                    "en-US": "English (United States)",
                    "en-XA": "English (Pseudo-Accents)",
                    "zh-TW": "Chinese (Taiwan)",
            ],
            "zh-TW": [
                    "en-US": "英文 (美國)",
                    "en-XA": "英文 (偽區域)",
                    "zh-TW": "中文 (台灣)",
            ]
    ]

    // todo: implement after v1.1 fix for 'huge' map
//    static Map<String, String> endonymsAndroidSupported = [
//    ]

    // todo: implement after v1.1 fix for 'huge' map
//    static Map<String, Map<String, String>> exonymsAndroidSupported = [
//    ]
}
