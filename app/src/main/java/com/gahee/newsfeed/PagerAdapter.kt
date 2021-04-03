package com.gahee.newsfeed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PagerRecyclerAdapter(private val newsItems: List<NewsItem>, val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder =
        PagerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_pager_item, parent, false),
            newsItems.size
        )

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bind(newsItems[position], position)

        holder.itemView.setOnClickListener {
            newsItems[position].link?.let { link ->
                NewsDetailFragment(
                    link = link
                ).show(
                    fragmentManager, "TAG"
                )
            }
        }
    }

    override fun getItemCount(): Int = newsItems.size
}

class PagerViewHolder(itemView: View, private val itemSize: Int) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(newsItem: NewsItem, position: Int) {
        itemView.apply {
            findViewById<TextView>(R.id.news_title).text = newsItem.title
            findViewById<TextView>(R.id.news_date).text = newsItem.pubDate
            Glide.with(context)
                .load(newsItem.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.default_news_image)
                .into(findViewById(R.id.news_image))
            findViewById<TextView>(R.id.news_content_numbers).text =
                context.getString(R.string.content_page_placeholder, position + 1, itemSize)
        }
    }
}