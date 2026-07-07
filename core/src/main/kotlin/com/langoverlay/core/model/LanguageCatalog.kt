package com.langoverlay.core.model

object LanguageCatalog {
    private val entries: List<LanguageEntry> = listOf(
        LanguageEntry("en", "EN"),
        LanguageEntry("ru", "RU"),
        LanguageEntry("ua", "UA"),
        LanguageEntry("de", "DE"),
        LanguageEntry("fr", "FR"),
        LanguageEntry("es", "ES"),
        LanguageEntry("it", "IT"),
        LanguageEntry("pt", "PT"),
        LanguageEntry("pl", "PL"),
        LanguageEntry("cs", "CS"),
        LanguageEntry("sk", "SK"),
        LanguageEntry("hu", "HU"),
        LanguageEntry("ro", "RO"),
        LanguageEntry("bg", "BG"),
        LanguageEntry("hr", "HR"),
        LanguageEntry("sr", "SR"),
        LanguageEntry("sl", "SL"),
        LanguageEntry("mk", "MK"),
        LanguageEntry("be", "BE"),
        LanguageEntry("kk", "KK"),
        LanguageEntry("tr", "TR"),
        LanguageEntry("ar", "AR"),
        LanguageEntry("he", "HE"),
        LanguageEntry("fa", "FA"),
        LanguageEntry("hi", "HI"),
        LanguageEntry("bn", "BN"),
        LanguageEntry("th", "TH"),
        LanguageEntry("vi", "VI"),
        LanguageEntry("id", "ID"),
        LanguageEntry("ms", "MS"),
        LanguageEntry("ja", "JA"),
        LanguageEntry("ko", "KO"),
        LanguageEntry("zh", "ZH"),
        LanguageEntry("nl", "NL"),
        LanguageEntry("sv", "SV"),
        LanguageEntry("no", "NO"),
        LanguageEntry("da", "DA"),
        LanguageEntry("fi", "FI"),
        LanguageEntry("et", "ET"),
        LanguageEntry("lv", "LV"),
        LanguageEntry("lt", "LT"),
        LanguageEntry("el", "EL"),
        LanguageEntry("is", "IS"),
        LanguageEntry("ga", "GA"),
        LanguageEntry("cy", "CY"),
        LanguageEntry("mt", "MT"),
        LanguageEntry("sq", "SQ"),
        LanguageEntry("hy", "HY"),
        LanguageEntry("ka", "KA"),
        LanguageEntry("az", "AZ"),
    )

    fun all(): List<LanguageEntry> = entries

    fun find(id: String): LanguageEntry? = entries.firstOrNull { it.id == id }

    fun localizedName(id: String): String = when (id) {
        "en" -> "English"
        "ru" -> "Русский"
        "ua" -> "Українська"
        "de" -> "Deutsch"
        "fr" -> "Français"
        "es" -> "Español"
        "it" -> "Italiano"
        "pt" -> "Português"
        "pl" -> "Polski"
        "cs" -> "Čeština"
        "sk" -> "Slovenčina"
        "hu" -> "Magyar"
        "ro" -> "Română"
        "bg" -> "Български"
        "hr" -> "Hrvatski"
        "sr" -> "Српски"
        "sl" -> "Slovenščina"
        "mk" -> "Македонски"
        "be" -> "Беларуская"
        "kk" -> "Қазақ"
        "tr" -> "Türkçe"
        "ar" -> "العربية"
        "he" -> "עברית"
        "fa" -> "فارسی"
        "hi" -> "हिन्दी"
        "bn" -> "বাংলা"
        "th" -> "ไทย"
        "vi" -> "Tiếng Việt"
        "id" -> "Bahasa Indonesia"
        "ms" -> "Bahasa Melayu"
        "ja" -> "日本語"
        "ko" -> "한국어"
        "zh" -> "中文"
        "nl" -> "Nederlands"
        "sv" -> "Svenska"
        "no" -> "Norsk"
        "da" -> "Dansk"
        "fi" -> "Suomi"
        "et" -> "Eesti"
        "lv" -> "Latviešu"
        "lt" -> "Lietuvių"
        "el" -> "Ελληνικά"
        "is" -> "Íslenska"
        "ga" -> "Gaeilge"
        "cy" -> "Cymraeg"
        "mt" -> "Malti"
        "sq" -> "Shqip"
        "hy" -> "Հայերեն"
        "ka" -> "ქართული"
        "az" -> "Azərbaycan"
        else -> id.uppercase()
    }
}
