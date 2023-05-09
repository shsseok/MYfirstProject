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
import com.example.odh_project_1.Adapters.NewsAdapter
import com.example.odh_project_1.databinding.ActivityMoreNewsBinding

class MoreNews : AppCompatActivity() {
    private lateinit var morenewsbinding: ActivityMoreNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        morenewsbinding = ActivityMoreNewsBinding.inflate(layoutInflater)
        setContentView(morenewsbinding.root)

        // MainActivity에서 전달한 intent에서 news_list를 가져옵니다.
        val newsList =
            intent.getParcelableArrayListExtra<moreNewsItem>("news_list") ?: arrayListOf()

        // RecyclerView 초기화
        morenewsbinding.moretrend.apply {
            layoutManager = LinearLayoutManager(this@MoreNews)
            adapter = NewsAdapter(newsList)
        }
    }
}












































