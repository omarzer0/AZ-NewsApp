package az.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import az.newsapp.repository.NewsRepository

// this class is to use our own viewModel with prams
class NewsViewModelProviderFactory (val newsRepository: NewsRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository) as T
    }
}