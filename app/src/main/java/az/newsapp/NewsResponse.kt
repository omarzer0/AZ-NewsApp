package az.newsapp

import az.newsapp.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)