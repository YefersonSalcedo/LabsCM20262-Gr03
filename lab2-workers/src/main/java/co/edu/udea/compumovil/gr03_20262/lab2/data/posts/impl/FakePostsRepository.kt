/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.edu.udea.compumovil.gr03_20262.lab2.data.posts.impl

import android.content.Context
import co.edu.udea.compumovil.gr03_20262.lab2.data.Result
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.FavoritesStore
import co.edu.udea.compumovil.gr03_20262.lab2.data.local.LanguageStore
import co.edu.udea.compumovil.gr03_20262.lab2.data.posts.PostsRepository
import co.edu.udea.compumovil.gr03_20262.lab2.model.Post
import co.edu.udea.compumovil.gr03_20262.lab2.model.PostsFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext


class FakePostsRepository(
    private val favoritesStore: FavoritesStore,
    private val context: Context,
) : PostsRepository {

    private val postsFeed = MutableStateFlow<PostsFeed?>(null)

    private fun currentPosts(): PostsFeed =
        if (LanguageStore.getEffectiveLanguage(context).startsWith("es")) postsEs else posts

    override suspend fun getPost(postId: String?): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = currentPosts().allPosts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            delay(800) // pretend we're on a slow network
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                postsFeed.update { currentPosts() }
                Result.Success(currentPosts())
            }
        }
    }

    override fun observeFavorites(): Flow<Set<String>> = favoritesStore.favorites
    override fun observePostsFeed(): Flow<PostsFeed?> = postsFeed

    override suspend fun toggleFavorite(postId: String) {
        favoritesStore.toggle(postId)
    }

    override suspend fun refreshFeed() {
        postsFeed.update { currentPosts() }
    }

    // deterministic failure every 5 requests so the first request succeeds
    private var requestCount = 0

    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}
