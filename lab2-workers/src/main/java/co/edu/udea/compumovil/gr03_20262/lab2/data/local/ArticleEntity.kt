package co.edu.udea.compumovil.gr03_20262.lab2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String?,
    val author: String,
    val publication: String,
    val paragraphs: String,
    val category: String,
    val date: String,
    val readMinutes: Int,
    val imageId: Int,
    val language: String
)
