package co.edu.udea.compumovil.gr03_20262.lab2.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FavoritesStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _favorites = MutableStateFlow(loadFavorites())

    val favorites: Flow<Set<String>> = _favorites

    @Synchronized
    fun toggle(id: String) {
        val currentSet = HashSet(_favorites.value)
        if (currentSet.contains(id)) {
            currentSet.remove(id)
        } else {
            currentSet.add(id)
        }
        prefs.edit().putStringSet(KEY_IDS, currentSet).apply()
        _favorites.value = currentSet
    }

    private fun loadFavorites(): Set<String> {
        return HashSet(prefs.getStringSet(KEY_IDS, emptySet()) ?: emptySet())
    }

    companion object {
        private const val PREFS_NAME = "favorites"
        private const val KEY_IDS = "ids"
    }
}
