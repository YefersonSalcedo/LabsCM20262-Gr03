package co.edu.udea.compumovil.gr03_20262.lab2.work

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import co.edu.udea.compumovil.gr03_20262.lab2.data.remote.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RealNewsChecker(
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
        return articles.size.coerceAtMost(2)
    }
}
