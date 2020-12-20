package az.newsapp.repository

import androidx.lifecycle.ViewModel
import az.newsapp.api.RetrofitInstance
import az.newsapp.data.Article
import az.newsapp.db.ArticleDatabase

// responsible for DB and Network data fetching
class NewsRepository(val db: ArticleDatabase) : ViewModel() {

    // calls the singleton instance of retrofit to get the data
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)


    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun insertArticle(article: Article) = db.getArticleDao().insert(article)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()
}