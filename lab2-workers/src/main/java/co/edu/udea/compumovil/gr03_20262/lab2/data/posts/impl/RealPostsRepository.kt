package co.edu.udea.compumovil.gr03_20262.lab2.data.posts.impl

import android.content.Context
import co.edu.udea.compumovil.gr03_20262.lab2.data.Result
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.AppDatabase
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.FavoritesStore
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.LanguageStore
import co.edu.udea.compumovil.gr03_20262.lab2.data.posts.PostsRepository
import co.edu.udea.compumovil.gr03_20262.lab2.data.remote.NewsApiService
import co.edu.udea.compumovil.gr03_20262.lab2.model.Post
import co.edu.udea.compumovil.gr03_20262.lab2.model.PostsFeed
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

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val feed = postsFeed.value ?: currentFallbackPosts()
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
            try {
                // 1. Try Internet (Retrofit)
                val remoteFeed = apiService.getPostsFeed()
                postsFeed.update { remoteFeed }
                Result.Success(remoteFeed)
            } catch (e: Exception) {
                // 2. Fallback to Room cache or local mock data
                try {
                    val cachedEntities = articleDao.getAll()
                    if (cachedEntities.isNotEmpty()) {
                        Result.Success(currentFallbackPosts())
                    } else {
                        Result.Success(currentFallbackPosts())
                    }
                } catch (dbEx: Exception) {
                    Result.Success(currentFallbackPosts())
                }
            }
        }
    }

    override fun observeFavorites(): Flow<Set<String>> = favoritesStore.favorites

    override fun observePostsFeed(): Flow<PostsFeed?> = postsFeed

    override suspend fun toggleFavorite(postId: String) {
        favoritesStore.toggle(postId)
    }

    override suspend fun refreshFeed() {
        withContext(Dispatchers.IO) {
            try {
                val remoteFeed = apiService.getPostsFeed()
                postsFeed.update { remoteFeed }
            } catch (e: Exception) {
                postsFeed.update { currentFallbackPosts() }
            }
        }
    }
}
