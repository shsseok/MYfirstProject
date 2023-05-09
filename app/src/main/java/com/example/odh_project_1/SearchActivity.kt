package com.example.odh_project_1


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.odh_project_1.Adapters.ProductAdapter
import com.example.odh_project_1.DataAPi.NaverShoppingApi
import com.example.odh_project_1.DataClass.Product
import com.example.odh_project_1.DataClass.SearchProductsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.odh_project_1.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    lateinit var productList: MutableList<Product>
    lateinit var adapter:  ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productList = mutableListOf<Product>()
        lateinit var query:String
        val searchbinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchbinding.root)

        val retrofit = Retrofit.Builder().baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val recyclerView = searchbinding.searchproduct
        adapter = ProductAdapter(productList, this)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object :  ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@SearchActivity, MainInfoActivity::class.java)
                val product = productList[position]
                intent.putExtra("query",query)
                intent.putExtra("image_url", product.productImageUrl)
                intent.putExtra("product_name", product.productName)
                intent.putExtra("price", product.productPrice)
                intent.putExtra("product_link", product.productLink)
                startActivity(intent)
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(this)


        searchbinding.searchImage.setOnClickListener {
           query = searchbinding.searchBar.text.toString()
            if (query.isNotEmpty()) {
                searchProducts(query)
            }
        }
    }

    private fun searchProducts(query: String) {
        val naverShoppingApi = NaverShoppingApi.create()
        naverShoppingApi.searchProducts(
            clientId = "Q3hRbuSrFXi45ebILEqx",
            clientSecret = "Wr4TaArtQC",
            query = query,
            display = 15
        ).enqueue(object : Callback<SearchProductsResponse> {
            override fun onResponse(
                call: Call<SearchProductsResponse>, response: Response<SearchProductsResponse>
            ) {
                if (response.isSuccessful) {
                    productList.clear()
                    val searchProductsResponse = response.body()
                    searchProductsResponse?.items?.forEachIndexed { index, item ->
                        productList.add(
                            Product(
                                rank = productList.size + 1,
                                productName = item.title.let { it.removeHtmlTags() },
                                productPrice = item.lprice,
                                productImageUrl = item.image,
                                productLink = item.link
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<SearchProductsResponse>, t: Throwable) {
                Log.e("Error", "API 호출 실패: $t")
            }
        })
    }}






