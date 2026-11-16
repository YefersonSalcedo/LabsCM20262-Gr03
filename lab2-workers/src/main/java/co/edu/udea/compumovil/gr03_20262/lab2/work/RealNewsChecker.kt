package co.edu.udea.compumovil.gr03_20262.lab2.work

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import co.edu.udea.compumovil.gr03_20262.lab2.data.remote.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RealNewsChecker(
    private val context: Context? = null,
    private val apiService: NewsApiService = Retrofit.Builder()
        .baseUrl(NewsApiService.BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            )
        )
        .build()
        .create(NewsApiService::class.java)
) : NewsChecker {
    override suspend fun refreshAndCountNew(): Int {
        val articles = apiService.getArticles(language = "es")
        val currentSize = articles.size
        val ctx = context ?: return 0
        val prefs = ctx.getSharedPreferences("news_checker_prefs", Context.MODE_PRIVATE)
        val lastSize = prefs.getInt("last_size", -1)
        prefs.edit().putInt("last_size", currentSize).apply()

        if (lastSize == -1) {
            return 0
        }
        return (currentSize - lastSize).coerceAtLeast(0)
    }
}
