package omar.az.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import az.newsapp.R
import az.newsapp.adapter.NewsAdapter
import az.newsapp.data.NewsResponse
import az.newsapp.ui.NewsActivity
import az.newsapp.ui.NewsViewModel
import az.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import az.newsapp.util.Resource
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
                editable?.toString().isNullOrEmpty().let {
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

    private fun onSuccessResponse(response: Resource<NewsResponse>) {
        // got the data successfully
        hideProgressBar()
        // check if it is null
        response.data.let { newsResponse ->
            // submit the articles list
            newsAdapter.differ.submitList(newsResponse?.articles)
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
    }

    private fun showProgressBar() {
        fragmentSearchNewsPaginationPB.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        fragmentSearchNewsRV.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}