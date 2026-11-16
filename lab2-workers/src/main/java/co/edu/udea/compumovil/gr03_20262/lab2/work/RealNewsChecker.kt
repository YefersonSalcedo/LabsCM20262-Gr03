package co.edu.udea.compumovil.gr03_20262.lab2.work

import co.edu.udea.compumovil.gr03_20262.lab2.data.remote.NewsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RealNewsChecker(
    private val apiService: NewsApiService = Retrofit.Builder()
        .baseUrl(NewsApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApiService::class.java)
) : NewsChecker {
    override suspend fun refreshAndCountNew(): Int {
        return try {
            val feed = apiService.getPostsFeed()
            feed.allPosts.size.coerceAtMost(2)
        } catch (e: Exception) {
            0
        }
    }
}
