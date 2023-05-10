package com.example.odh_project_1.DataClass


data class SearchNewsResponse(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<NewsItem>
)
data class NewsItem(
    val title: String, val link: String
)