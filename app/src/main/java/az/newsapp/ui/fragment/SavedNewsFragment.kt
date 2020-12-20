package omar.az.newsapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import az.newsapp.R
import az.newsapp.adapter.NewsAdapter
import az.newsapp.ui.NewsActivity
import az.newsapp.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

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
                R.id.action_savedNewsFragment_to_articleFragment3,
                bundle
            )
        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        fragmentSavedNewsRV.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}