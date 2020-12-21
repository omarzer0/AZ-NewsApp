package omar.az.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
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
import az.newsapp.util.Constants.Companion.COUNTRY_CODE
import az.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import az.newsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private val TAG = "BreakingNewsFragment"
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // return the viewModel of the NewsActivity
        // like java ((Activity)getActivity).viewModel
        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()

        newsAdapter.setOnArticleClickListener {
            val bundle = Bundle()
            bundle.putSerializable("article", it)
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment3,
                bundle
            )
        }


        // subscribe to observe the data of breakingNews liveData
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> onSuccessResponse(response)
                is Resource.Error -> onErrorResponse(response)

                is Resource.Loading -> showProgressBar()
            }
        })
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        fragmentBreakingNewsRV.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            // add our custom scrollListener
            addOnScrollListener(customScrollListener)
        }
    }

    private fun onSuccessResponse(response: Resource<NewsResponse>) {
        // got the data successfully
        hideProgressBar()
        // check if it is null
        response.data.let { newsResponse ->
            // submit the articles list
            newsAdapter.differ.submitList(newsResponse?.articles)
            val totalResultsNumber = newsResponse?.totalResults ?: 0
            val totalPages = totalResultsNumber / QUERY_PAGE_SIZE + 2
            isLastPage = (viewModel.breakingNewsPage == totalPages)
            // this is just for padding of the last page
            if (isLastPage) fragmentBreakingNewsRV.setPadding(0, 0, 0, 0)
        }
    }

    private fun onErrorResponse(response: Resource<NewsResponse>) {
        hideProgressBar()
        response.message?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideProgressBar() {
        fragmentBreakingNewsPaginationPB.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        fragmentBreakingNewsPaginationPB.visibility = View.VISIBLE
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
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotTheLastPage && isAtLastItem &&
                    isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews(COUNTRY_CODE)
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