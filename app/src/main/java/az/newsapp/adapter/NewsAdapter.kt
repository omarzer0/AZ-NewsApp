package az.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import az.newsapp.R
import az.newsapp.data.Article
import az.newsapp.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_article.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // anonymous class
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            // i cannot use id as the articles come from retrofit has no ID
            // url is also unique and available for articles form Room and Retrofit
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    // calculate the difference asynchronously
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        // instead of the old way of passing a list every time
        // differ now has list so we can get it's size
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = differ.currentList[position]
        // use apply to apply all what is inside to the outer object
        // relief from the hell of holder. holder. holder.
        val requestOptions =
            RequestOptions().placeholder(R.drawable.no_image_foreground)
        holder.itemView.apply {
            // this is the view not the adapter
            Glide.with(this).setDefaultRequestOptions(requestOptions)
                .load(currentArticle.urlToImage).into(itemArticleImgImageView)

            itemArticleSourceTV.text = currentArticle.source?.name ?: "unknown"

            itemArticleTitleTV.text = currentArticle.title ?: "unknown"

            itemArticleAuthor.text = currentArticle.author ?: "unknown"

            itemArticlePublishedDate.text =
                Utils.getTimeFromTimeStamp(currentArticle.publishedAt)?.get(1) ?: "unknown"

            itemArticlePublishedTime.text =
                Utils.getTimeFromTimeStamp(currentArticle.publishedAt)?.get(0) ?: "unknown"

            setOnClickListener { onArticleClickListener?.let { it(currentArticle) } }
        }
    }

    // this takes lambda (receives an Article 'that has been clicked') and returns nothing
    private var onArticleClickListener: ((Article) -> Unit)? = null

    // must have more clarifications but it works fine every time
    // it assigns the passed listener to a the clickListener
    fun setOnArticleClickListener(listener: (Article) -> Unit) {
        onArticleClickListener = listener
    }
}