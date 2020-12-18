package az.newsapp.ui

import androidx.lifecycle.ViewModel
import az.newsapp.repository.NewsRepository

class NewsViewModel (val newsRepository : NewsRepository): ViewModel() {
}