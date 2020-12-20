package omar.az.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import az.newsapp.R
import az.newsapp.adapter.NewsAdapter
import az.newsapp.data.NewsResponse
import az.newsapp.ui.NewsActivity
import az.newsapp.ui.NewsViewModel
import az.newsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    private val TAG = "BreakingNewsFragment"
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
        fragmentBreakingNewsPaginationPB.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        fragmentBreakingNewsPaginationPB.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        fragmentBreakingNewsRV.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}