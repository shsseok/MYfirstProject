package com.example.odh_project_1

import kotlinx.parcelize.Parcelize


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.odh_project_1.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.odh_project_1.moreNewsItem
import moreTrendItem

data class SearchRequestBody(
    val startDate: String,
    val endDate: String,
    val timeUnit: String,
    val category: String, // 추가된 필드
    val keywordGroups: List<KeywordGroup>
) {
    data class KeywordGroup(
        val groupName: String, val keywords: List<String>
    )
}

data class Product(
    val rank: Int,
    val productName: String,
    val productPrice: String,
    val productImageUrl: String,
    val productLink: String
)

data class SearchTrendsResponse(
    val startDate: String, val endDate: String, val timeUnit: String, val results: List<Result>
) {
    data class Result(
        val title: String, val keywords: List<String>, val data: List<Data>
    ) {
        data class Data(
            val period: String, val ratio: Float
        )
    }
}

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

data class SearchProductsResponse(
    val items: List<ProductItem>
)

data class ProductItem(
    val title: String,
    val link: String,
    val image: String,
    val lprice: String,
    val hprice: String,
    val mallName: String,
    val productId: String,
    val productType: String
)

data class User(
    val name: String? = null
)

class MainActivity : AppCompatActivity() {
    private lateinit var newsInfo1: TextView
    private var firstNewsUrl: String? = null
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    private lateinit var mAuth: FirebaseAuth
    private lateinit var welcomeText: TextView
    private lateinit var productList: MutableList<Product>
    private lateinit var mainbinding: ActivityMainBinding
    private var newsList: ArrayList<moreNewsItem> = ArrayList()
    private var producttrendList: ArrayList<moreTrendItem> = ArrayList()
    private fun updateWelcomeText(userName: String) {
        welcomeText.text = "반갑습니다. $userName"
        welcomeText.visibility = View.VISIBLE
    }

    private fun fetchUserName(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                val userName = user?.name ?: ""
                updateWelcomeText(userName)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("MainActivity", "fetchUserName:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainbinding.root)
        productList = mutableListOf()
        newsInfo1 = mainbinding.newsinfo1
        mAuth = FirebaseAuth.getInstance()
        welcomeText = mainbinding.welcomeText
        mainbinding.loginbutton.setOnClickListener {
            val user = mAuth.currentUser
            if (user != null) {
                // 로그인 상태인 경우
                AlertDialog.Builder(this).setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("예") { _, _ ->
                        mAuth.signOut()
                        mainbinding.loginbutton.text = "로그인"
                    }.setNegativeButton("아니오", null).show()
            } else {
                // 로그아웃 상태인 경우
                val intent1 = Intent(this@MainActivity, LoginPage1::class.java)
                startActivity(intent1)

            }
        }
        mainbinding.searchImage.setOnClickListener {
            val intent2 = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent2)
        }
        mainbinding.more1.setOnClickListener {
            val intent3 = Intent(this@MainActivity, MoreTrend::class.java)
            intent3.putParcelableArrayListExtra("moreItems", producttrendList)
            startActivity(intent3)
        }
        mainbinding.newsmore1.setOnClickListener {
            val intent4 = Intent(this@MainActivity, MoreNews::class.java)
            intent4.putParcelableArrayListExtra("news_list", newsList)
            startActivity(intent4)
        }
        val adapter = ProductAdapter(productList, this)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d(
                    "MainActivity", "Item clicked at position $position")
                // ProductDetailsActivity로 이동할 인텐트를 생성합니다.
                val intent = Intent(this@MainActivity, MainInfoActivity::class.java)
                val product = productList[position]
                intent.putExtra("image_url", product.productImageUrl)
                intent.putExtra("product_name", product.productName)
                intent.putExtra("price", product.productPrice)
                intent.putExtra("product_link", product.productLink)
                startActivity(intent)
            }
        })
        val retrofit = Retrofit.Builder().baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val popularKeywords = "사이다"

        val naverShoppingApi = retrofit.create(NaverShoppingApi::class.java)
        naverShoppingApi.searchProducts(
            clientId = "Q3hRbuSrFXi45ebILEqx",
            clientSecret = "Wr4TaArtQC",
            query = popularKeywords,
            display = 15
        ).enqueue(object : Callback<SearchProductsResponse> {
            override fun onResponse(
                call: Call<SearchProductsResponse>, response: Response<SearchProductsResponse>
            ) {
                if (response.isSuccessful) {
                    val searchProductsResponse = response.body()
                    searchProductsResponse?.items?.forEachIndexed { index, item ->
                        if (index < 5) {
                            // MainActivity에서 상품 5개 보여주기
                            productList.add(
                                Product(
                                    rank = productList.size + 1,
                                    productName = item.title,
                                    productPrice = item.lprice,
                                    productImageUrl = item.image,
                                    productLink = item.link
                                )
                            )
                        } else {


                            producttrendList.add(
                                moreTrendItem(
                                    rank = productList.size + 1,
                                    productName = item.title,
                                    productPrice = item.lprice,
                                    productImageUrl = item.image,
                                    productLink = item.link
                                )
                            )
                        }
                    }
                    // 상품 데이터 추출 및 productList에 추가

                    // 첫 번째 상품 이름 추출 및 뉴스 검색 실행
                    if (productList.isNotEmpty()) {
                        val firstProductName = "쇼핑"
                        Log.d("product1", firstProductName)
                        // NaverShoppingApi를 사용하여 뉴스 검색
                        naverShoppingApi.searchNews(
                            clientId = "Q3hRbuSrFXi45ebILEqx",
                            clientSecret = "Wr4TaArtQC",
                            query = firstProductName,
                            display = 10
                        ).enqueue(object : Callback<SearchNewsResponse> {
                            override fun onResponse(
                                call: Call<SearchNewsResponse>,
                                response: Response<SearchNewsResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val searchNewsResponse = response.body()

                                    if (searchNewsResponse?.items != null && searchNewsResponse.items.isNotEmpty()) {
                                        // 뉴스 데이터 추출
                                        searchNewsResponse.items.forEach { item ->
                                            newsList.add(moreNewsItem(item.title, item.link))
                                            // 뉴스 데이터를 사용하여 UI를 업데이트하거나 필요한 작업 수행
                                            Log.d("MainActivity1", "News title: ${item.title}")
                                        }
                                        searchNewsResponse.items.getOrNull(0)?.let { firstNews ->
                                            newsInfo1.text = HtmlCompat.fromHtml(
                                                firstNews.title, HtmlCompat.FROM_HTML_MODE_LEGACY
                                            )
                                            firstNewsUrl = firstNews.link

                                            newsInfo1.setOnClickListener {
                                                if (firstNewsUrl != null) {
                                                    val intent = Intent(
                                                        Intent.ACTION_VIEW, Uri.parse(firstNewsUrl)
                                                    )
                                                    startActivity(intent)
                                                } else {
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "뉴스 링크가 존재하지 않습니다.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Log.d("MoreNews", "검색된 뉴스가 없습니다.")
                                    }
                                } else {
                                    Log.e("Errorr", "API 호출 실패: ${response.errorBody()?.string()}")
                                }
                            }

                            override fun onFailure(call: Call<SearchNewsResponse>, t: Throwable) {
                                Log.e("Error", "API 호출 실패: $t")
                            }
                        })
                    }

                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("Error", "API 호출 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<SearchProductsResponse>, t: Throwable) {
                Log.e("Error", "API 호출 실패: $t")
            }
        })
        mFirebaseAuth = FirebaseAuth.getInstance()

        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                // 로그인 상태인 경우
                mainbinding.loginbutton.text = "로그아웃"

                welcomeText.visibility = View.VISIBLE
                mainbinding.loginbutton.setOnClickListener {
                    mFirebaseAuth.signOut()
                }
                fetchUserName(user.uid)
            } else {
                // 로그아웃 상태인 경우
                mainbinding.loginbutton.text = "로그인"
                welcomeText.visibility = View.GONE
                mainbinding.loginbutton.setOnClickListener {
                    val intent1 = Intent(this@MainActivity, LoginPage1::class.java)
                    startActivity(intent1)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
    }
}

class ProductAdapter(
    private val productList: List<Product>, private val context: Context
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trend, parent, false)
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
