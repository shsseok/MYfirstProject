package com.example.odh_project_1.DataAPi

import com.example.odh_project_1.DataClass.TrendRequestBody
import com.example.odh_project_1.DataClass.TrendResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NaverDataLabApi {
    @POST("v1/datalab/search")
    fun searchTrend(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Body body: TrendRequestBody
    ): Call<TrendResponse>
    companion object {
        private const val BASE_URL = "https://openapi.naver.com/"

        fun create(): NaverDataLabApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(NaverDataLabApi::class.java)
        }
    }
}