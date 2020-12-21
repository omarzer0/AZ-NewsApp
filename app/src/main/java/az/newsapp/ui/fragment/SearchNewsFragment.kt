package omar.az.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import az.newsapp.R
import az.newsapp.adapter.NewsAdapter
import az.newsapp.data.NewsResponse
import az.newsapp.ui.NewsActivity
import az.newsapp.ui.NewsViewModel
import az.newsapp.util.Constants
import az.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import az.newsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    private val TAG = "SearchNewsFragment"
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // return the viewModel of the NewsActivity
        // like java (getActivity).viewModel
        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()


        newsAdapter.setOnArticleClickListener {
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment3,
                bundle
            )
        }

        // we need to add a delay when the user types a char
        // to minimize the number of network calls
        // if the user writes another char stop the coroutine
        // then wait for SEARCH_NEWS_TIME_DELAY then make the network call
        var job: Job? = null
        fragmentSearchNewsET.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                // check if the EditText is not null and not empty
                if (editable.toString().isNotEmpty()) {
                    viewModel.searchNewsPage = 1
                    viewModel.searchNewsResponse = null
                    viewModel.searchNews(editable.toString())
                }
            }
        }
        // subscribe to observe the data of searchNews liveData
        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> onSuccessResponse(response)
                is Resource.Error -> onErrorResponse(response)
                is Resource.Loading -> showProgressBar()
            }
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        fragmentSearchNewsRV.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(customScrollListener)
        }
    }

    private fun onSuccessResponse(response: Resource<NewsResponse>) {
        // got the data successfully
        hideProgressBar()
        // check if it is null
        response.data.let { newsResponse ->
            // submit the articles list
            newsAdapter.differ.submitList(newsResponse?.articles?.toList())
//            val totalResultsNumber = newsResponse?.totalResults ?: 0
//            val totalPages = totalResultsNumber / Constants.QUERY_PAGE_SIZE + 2
//            isLastPage = (viewModel.searchNewsPage == totalPages)
//
//            if (isLastPage) fragmentSearchNewsRV.setPadding(0, 0, 0, 0)
        }
    }

    private fun onErrorResponse(response: Resource<NewsResponse>) {
        hideProgressBar()
        response.message?.let { message ->
            Log.e(TAG, message)
        }
    }

    private fun hideProgressBar() {
        fragmentSearchNewsPaginationPB.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        fragmentSearchNewsPaginationPB.visibility = View.VISIBLE
        isLoading = true
    }

    // these booleans defines the state if scrolling and loading
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    // create custom ScrollListener
    val customScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            // here we detect when we reach the bottom
            // the is no default way so we will calculate is
            // with the help of LayoutManager\
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            // the first visible item
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            // the visible item on the screen
            val visibleItemCount = layoutManager.childCount
            // the total number of items on the recyclerview
            val totalItemCount = layoutManager.itemCount


            val isNotLoadingAndNotTheLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisiblePosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisiblePosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotTheLastPage && isAtLastItem &&
                    isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.searchNews(fragmentSearchNewsET.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // check if we are scrolling
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }
    }
}