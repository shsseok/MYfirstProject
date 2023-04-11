package com.example.odh_project_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.odh_project_1.databinding.ActivityMoreNewsBinding

class MoreNews : AppCompatActivity() {
    private lateinit var morenewsbinding: ActivityMoreNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        morenewsbinding = ActivityMoreNewsBinding.inflate(layoutInflater)
        setContentView(morenewsbinding.root)

        // MainActivity에서 전달한 intent에서 news_list를 가져옵니다.
        val newsList = intent.getParcelableArrayListExtra<moreNewsItem>("news_list") ?: arrayListOf()

        // RecyclerView 초기화
        morenewsbinding.moretrend.apply {
            layoutManager = LinearLayoutManager(this@MoreNews)
            adapter = NewsAdapter(newsList)
        }
    }

    private inner class NewsAdapter(private val newsList: List<moreNewsItem>) :
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
                    startActivity(intent)
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
}










































