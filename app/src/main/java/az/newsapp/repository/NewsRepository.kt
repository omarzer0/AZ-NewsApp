package az.newsapp.repository

import androidx.lifecycle.ViewModel
import az.newsapp.api.RetrofitInstance
import az.newsapp.db.ArticleDatabase

// responsible for DB and Network data fetching
class NewsRepository(val db: ArticleDatabase) : ViewModel() {

    // calls the singleton instance of retrofit to get the data
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)


    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)
}