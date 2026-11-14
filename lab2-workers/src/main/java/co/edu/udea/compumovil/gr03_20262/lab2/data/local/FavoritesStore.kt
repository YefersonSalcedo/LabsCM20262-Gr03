package co.edu.udea.compumovil.gr03_20262.lab2.data.local

import android.content.Context
import android.content.SharedPreferences

class FavoritesStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun get(): Set<String> {
        return prefs.getStringSet(KEY_IDS, emptySet()) ?: emptySet()
    }

    fun toggle(id: String) {
        val currentSet = HashSet(get())
        if (currentSet.contains(id)) {
            currentSet.remove(id)
        } else {
            currentSet.add(id)
        }
        prefs.edit().putStringSet(KEY_IDS, currentSet).apply()
    }

    companion object {
        private const val PREFS_NAME = "favorites"
        private const val KEY_IDS = "ids"
    }
}
