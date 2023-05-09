package com.example.odh_project_1.DataAPi

import com.example.odh_project_1.DataClass.SearchNewsResponse
import com.example.odh_project_1.DataClass.SearchProductsResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface NaverShoppingApi {
    @GET("v1/search/shop.json")
    fun searchProducts(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Header("display") display: Int,
        @Query("query") query: String,
        @Query("start") start: Int =1,
        @Query("sort") sort: String = "sim"
    ): Call<SearchProductsResponse>
    @GET("v1/search/news.json")
    fun searchNews(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int = 10
    ): Call<SearchNewsResponse>
    companion object {
        private const val BASE_URL = "https://openapi.naver.com/"

        fun create(): NaverShoppingApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(NaverShoppingApi::class.java)
        }
    }
}