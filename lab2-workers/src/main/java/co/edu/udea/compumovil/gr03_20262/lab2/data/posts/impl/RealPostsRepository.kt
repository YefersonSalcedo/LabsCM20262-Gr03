package co.edu.udea.compumovil.gr03_20262.lab2.data.posts.impl

import android.content.Context
import co.edu.udea.compumovil.gr03_20262.lab2.R
import co.edu.udea.compumovil.gr03_20262.lab2.data.Result
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.AppDatabase
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.ArticleEntity
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.FavoritesStore
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.LanguageStore
import co.edu.udea.compumovil.gr03_20262.lab2.data.posts.PostsRepository
import co.edu.udea.compumovil.gr03_20262.lab2.data.remote.ArticleDto
import co.edu.udea.compumovil.gr03_20262.lab2.data.remote.NewsApiService
import co.edu.udea.compumovil.gr03_20262.lab2.model.Metadata
import co.edu.udea.compumovil.gr03_20262.lab2.model.Paragraph
import co.edu.udea.compumovil.gr03_20262.lab2.model.ParagraphType
import co.edu.udea.compumovil.gr03_20262.lab2.model.Post
import co.edu.udea.compumovil.gr03_20262.lab2.model.PostAuthor
import co.edu.udea.compumovil.gr03_20262.lab2.model.PostsFeed
import co.edu.udea.compumovil.gr03_20262.lab2.model.Publication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class RealPostsRepository(
    private val apiService: NewsApiService,
    private val database: AppDatabase,
    private val favoritesStore: FavoritesStore,
    private val context: Context,
) : PostsRepository {

    private val postsFeed = MutableStateFlow<PostsFeed?>(null)
    private val articleDao = database.articleDao()

    private fun currentFallbackPosts(): PostsFeed =
        if (LanguageStore.getEffectiveLanguage(context).startsWith("es")) postsEs else posts

    private fun ArticleDto.toPost(): Post {
        val validImageId = if (imageId in 1..6) imageId else 1
        val imageRes = when (validImageId) {
            1 -> R.drawable.post_1
            2 -> R.drawable.post_2
            3 -> R.drawable.post_3
            4 -> R.drawable.post_4
            5 -> R.drawable.post_5
            6 -> R.drawable.post_6
            else -> R.drawable.post_1
        }
        val imageThumbRes = when (validImageId) {
            1 -> R.drawable.post_1_thumb
            2 -> R.drawable.post_2_thumb
            3 -> R.drawable.post_3_thumb
            4 -> R.drawable.post_4_thumb
            5 -> R.drawable.post_5_thumb
            6 -> R.drawable.post_6_thumb
            else -> R.drawable.post_1_thumb
        }

        val paragraphList = text.split("\n\n").map { paragraphText ->
            Paragraph(
                type = ParagraphType.Text,
                text = paragraphText.trim()
            )
        }.filter { it.text.isNotBlank() }

        return Post(
            id = id,
            title = title,
            subtitle = subtitle,
            url = "https://developer.android.com/jetnews",
            publication = Publication(name = publication, logoUrl = ""),
            metadata = Metadata(
                author = PostAuthor(name = author),
                date = date,
                readTimeMinutes = readTimeMinutes
            ),
            paragraphs = paragraphList,
            imageId = imageRes,
            imageThumbId = imageThumbRes
        )
    }

    private fun ArticleDto.toEntity(): ArticleEntity {
        return ArticleEntity(
            id = id,
            title = title,
            subtitle = subtitle,
            author = author,
            publication = publication,
            paragraphs = text,
            category = category,
            date = date,
            readMinutes = readTimeMinutes,
            imageId = imageId,
            language = language
        )
    }

    private fun ArticleEntity.toPost(): Post {
        val validImageId = if (imageId in 1..6) imageId else 1
        val imageRes = when (validImageId) {
            1 -> R.drawable.post_1
            2 -> R.drawable.post_2
            3 -> R.drawable.post_3
            4 -> R.drawable.post_4
            5 -> R.drawable.post_5
            6 -> R.drawable.post_6
            else -> R.drawable.post_1
        }
        val imageThumbRes = when (validImageId) {
            1 -> R.drawable.post_1_thumb
            2 -> R.drawable.post_2_thumb
            3 -> R.drawable.post_3_thumb
            4 -> R.drawable.post_4_thumb
            5 -> R.drawable.post_5_thumb
            6 -> R.drawable.post_6_thumb
            else -> R.drawable.post_1_thumb
        }

        val paragraphList = paragraphs.split("\n\n").map { paragraphText ->
            Paragraph(
                type = ParagraphType.Text,
                text = paragraphText.trim()
            )
        }.filter { it.text.isNotBlank() }

        return Post(
            id = id,
            title = title,
            subtitle = subtitle,
            url = "https://developer.android.com/jetnews",
            publication = Publication(name = publication, logoUrl = ""),
            metadata = Metadata(
                author = PostAuthor(name = author),
                date = date,
                readTimeMinutes = readMinutes
            ),
            paragraphs = paragraphList,
            imageId = imageRes,
            imageThumbId = imageThumbRes
        )
    }

    private fun List<Post>.toPostsFeed(): PostsFeed {
        val highlighted = this.getOrNull(0) ?: currentFallbackPosts().highlightedPost
        val recommended = this.drop(1).take(3).ifEmpty { currentFallbackPosts().recommendedPosts }
        val popular = this.drop(4).take(3).ifEmpty { currentFallbackPosts().popularPosts }
        val recent = this.drop(7).ifEmpty { currentFallbackPosts().recentPosts }
        return PostsFeed(
            highlightedPost = highlighted,
            recommendedPosts = recommended,
            popularPosts = popular,
            recentPosts = recent
        )
    }

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val feed = postsFeed.value ?: getPostsFeed().let { (it as? Result.Success)?.data } ?: currentFallbackPosts()
            val post = feed.allPosts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            val lang = LanguageStore.getEffectiveLanguage(context)
            val effectiveLang = if (lang.startsWith("es")) "es" else "en"

            // 1. Try Internet (Retrofit)
            try {
                val remoteDtos = apiService.getArticles(language = effectiveLang)
                if (remoteDtos.isNotEmpty()) {
                    val posts = remoteDtos.map { it.toPost() }
                    val feed = posts.toPostsFeed()
                    postsFeed.update { feed }
                    try {
                        articleDao.replaceAll(remoteDtos.map { it.toEntity() })
                    } catch (_: Exception) {}
                    return@withContext Result.Success(feed)
                }
            } catch (_: Exception) {
                // Fallthrough to Room
            }

            // 2. Fallback to Room cache
            try {
                val cachedEntities = articleDao.getAll()
                val filteredEntities = cachedEntities.filter { it.language.startsWith(effectiveLang) }
                if (filteredEntities.isNotEmpty()) {
                    val posts = filteredEntities.map { it.toPost() }
                    val feed = posts.toPostsFeed()
                    postsFeed.update { feed }
                    return@withContext Result.Success(feed)
                } else if (cachedEntities.isNotEmpty()) {
                    val posts = cachedEntities.map { it.toPost() }
                    val feed = posts.toPostsFeed()
                    postsFeed.update { feed }
                    return@withContext Result.Success(feed)
                }
            } catch (_: Exception) {
                // Fallthrough to FakePostsRepository backup
            }

            // 3. Fallback to FakePostsRepository data
            val fallback = currentFallbackPosts()
            postsFeed.update { fallback }
            Result.Success(fallback)
        }
    }

    override fun observeFavorites(): Flow<Set<String>> = favoritesStore.favorites

    override fun observePostsFeed(): Flow<PostsFeed?> = postsFeed

    override suspend fun toggleFavorite(postId: String) {
        favoritesStore.toggle(postId)
    }

    override suspend fun refreshFeed() {
        getPostsFeed()
    }
}
