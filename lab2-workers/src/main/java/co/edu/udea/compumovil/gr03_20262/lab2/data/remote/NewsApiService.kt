package co.edu.udea.compumovil.gr03_20262.lab2.data.remote

import co.edu.udea.compumovil.gr03_20262.lab2.model.Post
import co.edu.udea.compumovil.gr03_20262.lab2.model.PostsFeed
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("posts.json")
    suspend fun getPostsFeed(): PostsFeed

    // Endpoint support for mockapi.io (16 articles: 8 in English, 8 in Spanish)
    @GET("articles")
    suspend fun getArticles(
        @Query("lang") language: String? = null
    ): List<Post>

    companion object {
        // Base URL for mockapi.io or fallback Jetnews endpoint
        // To use mockapi.io, replace BASE_URL with your mockapi.io project base URL, e.g.:
        // "https://<your-mockapi-id>.mockapi.io/api/v1/"
        const val BASE_URL = "https://raw.githubusercontent.com/android/compose-samples/main/Jetnews/"
    }
}
