package com.example.odh_project_1.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.odh_project_1.R
import com.example.odh_project_1.moreNewsItem

class NewsAdapter(val newsList: List<moreNewsItem>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val newsTitle: TextView = view.findViewById(R.id.news_title)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val newsItem = newsList[position]
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.link))
                v?.context?.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = newsList[position]
        holder.newsTitle.text = HtmlCompat.fromHtml(currentItem.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}