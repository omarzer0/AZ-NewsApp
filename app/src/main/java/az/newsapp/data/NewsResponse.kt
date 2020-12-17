package az.newsapp.data

import az.newsapp.data.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)