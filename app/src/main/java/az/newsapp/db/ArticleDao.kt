package az.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import az.newsapp.data.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // the return type is the id of the inserted item
    suspend fun insert(article: Article): Long

    // room query not retrofit ;)
    // get all articles stored in room db
    // it returns a liveData
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}