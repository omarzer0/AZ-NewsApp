package az.newsapp.repository

import androidx.lifecycle.ViewModel
import az.newsapp.db.ArticleDatabase

class NewsRepository(val db: ArticleDatabase) : ViewModel() {
}