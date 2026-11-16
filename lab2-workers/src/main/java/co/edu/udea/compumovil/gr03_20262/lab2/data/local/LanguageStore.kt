package co.edu.udea.compumovil.gr03_20262.lab2.data.local

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageStore {

    private const val PREFS_NAME = "app_language"
    private const val KEY_LANGUAGE = "language"

    fun getLanguage(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, null)

    fun setLanguage(context: Context, languageTag: String?) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply {
                if (languageTag == null) {
                    remove(KEY_LANGUAGE)
                } else {
                    putString(KEY_LANGUAGE, languageTag)
                }
            }
            .apply()
    }

    fun getEffectiveLanguage(context: Context): String {
        val tag = getLanguage(context)
        if (tag != null) return tag
        return context.resources.configuration.locales[0]?.language ?: "en"
    }

    fun applyLocale(context: Context): Context {
        val tag = getLanguage(context) ?: return context
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale.forLanguageTag(tag))
        return context.createConfigurationContext(config)
    }
}
