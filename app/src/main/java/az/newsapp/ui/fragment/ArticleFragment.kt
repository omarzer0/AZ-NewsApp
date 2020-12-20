package az.newsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import az.newsapp.R
import az.newsapp.ui.NewsActivity
import az.newsapp.ui.NewsViewModel
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    // since this is needed in every fragment
    // better practice is to create a base fragment which contains
    // all the commons between every fragment and it inherits from Fragment
    // and  every other fragment extends the base fragment
    lateinit var viewModel: NewsViewModel

    // receives the article sent by other fragments by the navigation component
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // return the viewModel of the NewsActivity
        // like java (getActivity).viewModel
        viewModel = (activity as NewsActivity).viewModel
        val article = args.article

        fragmentArticleWebView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
    }
}