package az.newsapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    val author: String,
    @PrimaryKey(autoGenerate = true)
    // null as not every article will have id
    // only the ones to be add in the room db
    val id: Int? = null,
    val content: String,
    val description: String,
    val publishedAt: String,
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
)