package az.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.newsapp.data.Article
import az.newsapp.data.NewsResponse
import az.newsapp.repository.NewsRepository
import az.newsapp.util.Constants.Companion.COUNTRY_CODE
import az.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

// to prevent the fragment from dealing with coroutines
// we use it here to get the data when it is called from the fragment
class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {

    var breakingNewsResponse: NewsResponse? = null
    var searchNewsResponse: NewsResponse? = null

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    // we manage the pagination here due to surviving lifeCycle changes
    // if it is used in fragment it will be reset every time we rotate the device
    var breakingNewsPage = 1

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1

    init {
        // call this method every time initiating this viewModel
        // when the app is opened it shows Breaking news that is
        // why we used it here
        getBreakingNews(COUNTRY_CODE)
    }

    // using viewModelScope means that the coroutine will live only as long as the viewModel lives
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        // before making the actual call we should send the state (which is loading here)
        // when ever we postValue all subscribers will get notified with the newest changes
        breakingNews.postValue(Resource.Loading())
        // the actual call
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        // after the response is fetched call postValue
        // when ever we postValue all subscribers will get notified with the newest changes
        breakingNews.postValue(handelBreakingNewsResponse(response))
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handelSearchNewsResponse(response))
    }

    private fun handelBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                // if 1st time breakingNewsResponse will be null so init it with the response
                // else get the old (from breakingNewsResponse ) and the new articles
                // add them together and increase the page number
                // return resultResponse if the 1st time else return breakingNewsResponse
                if (breakingNewsResponse == null) breakingNewsResponse = resultResponse
                else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    private fun handelSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) searchNewsResponse = resultResponse
                else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun insertArticle(article: Article) = viewModelScope.launch {
        newsRepository.insertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()
}