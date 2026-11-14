package co.edu.udea.compumovil.gr03_20262.lab2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles")
    suspend fun getAll(): List<ArticleEntity>

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<ArticleEntity>) {
        clear()
        insertAll(items)
    }
}
