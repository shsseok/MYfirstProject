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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.odh_project_1.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    lateinit var productList: MutableList<Product>
    lateinit var adapter: ProductAdapter2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productList = mutableListOf<Product>()
        val searchbinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchbinding.root)
        searchbinding
        val retrofit = Retrofit.Builder().baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val recyclerView = searchbinding.searchproduct
        adapter = ProductAdapter2(productList, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setOnItemClickListener(object : ProductAdapter2.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d("MainActivity", "Item clicked at position $position")
                // ProductDetailsActivity로 이동할 인텐트를 생성합니다.
                val intent = Intent(this@SearchActivity, MainInfoActivity::class.java)
                val product = productList[position]
                intent.putExtra("image_url", product.productImageUrl)
                intent.putExtra("product_name", product.productName)
                intent.putExtra("price", product.productPrice)
                intent.putExtra("product_link", product.productLink)
                startActivity(intent)
            }
        })
        searchbinding.searchImage.setOnClickListener {
            val query = searchbinding.searchBar.text.toString()
            if (query.isNotEmpty()) {
                searchProducts(query)
            }
        }
    }

    private fun searchProducts(query: String) {
        val retrofit = Retrofit.Builder().baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val naverShoppingApi = retrofit.create(NaverShoppingApi::class.java)
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
                                productName = item.title,
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
    }

    class ProductAdapter2(
        private val productList: List<Product>, private val context: Context
    ) : RecyclerView.Adapter<ProductAdapter2.ViewHolder>() {
        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }

        private var onItemClickListener: OnItemClickListener? = null
        fun setOnItemClickListener(listener: OnItemClickListener) {
            onItemClickListener = listener
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val rank: TextView = view.findViewById(R.id.rank)
            val productName: TextView = view.findViewById(R.id.product_name)
            val productImage: ImageView = view.findViewById(R.id.product_image)

            init {
                view.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(position)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_trend, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return productList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = productList[position]

            holder.rank.text = product.rank.toString()
            holder.productName.text = product.productName

            // Glide를 사용하여 이미지 로드
            Glide.with(context).load(product.productImageUrl).into(holder.productImage)
        }

    }

}




