package co.edu.udea.compumovil.gr03_20262.lab2.data.remote

import co.edu.udea.compumovil.gr03_20262.lab2.model.PostsFeed
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class ArticleDto(
    val id: String,
    val title: String,
    val subtitle: String?,
    val author: String,
    val publication: String,
    val text: String,
    val category: String,
    val date: String,
    val readTimeMinutes: Int,
    val imageId: Int,
    val language: String
)

interface NewsApiService {
    @GET("posts.json")
    suspend fun getPostsFeed(): PostsFeed

    @GET("articles")
    suspend fun getArticles(
        @Query("language") language: String? = null
    ): List<ArticleDto>

    companion object {
        const val BASE_URL = "https://6afb3c99730c1d152f3357e3.mockapi.io/api/v1/"
    }
}
