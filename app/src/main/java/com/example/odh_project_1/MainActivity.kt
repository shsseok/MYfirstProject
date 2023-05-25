package com.example.odh_project_1


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.example.odh_project_1.Adapters.ProductAdapter
import com.example.odh_project_1.DataAPi.NaverShoppingApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import moreTrendItem
import com.example.odh_project_1.DataClass.Product
import com.example.odh_project_1.DataClass.SearchNewsResponse
import com.example.odh_project_1.DataClass.SearchProductsResponse


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
                    "MainActivity", "Item clicked at position $position"
                )
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
        val popularKeywords = "사이다"
        val naverShoppingApi = NaverShoppingApi.create()
        naverShoppingApi.searchProducts(
            clientId = "Q3hRbuSrFXi45ebILEqx",
            clientSecret = "Wr4TaArtQC",
            query = popularKeywords,
            display = 30
        ).enqueue(object : Callback<SearchProductsResponse> {
            override fun onResponse(
                call: Call<SearchProductsResponse>, response: Response<SearchProductsResponse>
            ) {
                if (response.isSuccessful) {
                    val searchProductsResponse = response.body()

                    searchProductsResponse?.items?.forEachIndexed { index, item ->
                        Log.d("product123","${item.title}")
                        if (index < 5) {
                            // MainActivity에서 상품 5개 보여주기
                            productList.add(
                                Product(
                                    rank = productList.size + 1,
                                    productName = item.title.let { it.removeHtmlTags() },
                                    productPrice = item.lprice,
                                    productImageUrl = item.image,
                                    productLink = item.link
                                )
                            )
                        } else {
                            producttrendList.add(
                                moreTrendItem(
                                    rank = productList.size + 1,
                                    productName = item.title.let { it.removeHtmlTags() },
                                    productPrice = item.lprice,
                                    productImageUrl = item.image,
                                    productLink = item.link
                                )
                            )
                        }
                    }

                    if (productList.isNotEmpty()) {
                        val firstProductName = "롯데칠성음료 칠성사이다 190ml"
                        Log.d("product1", firstProductName)
                        // NaverShoppingApi를 사용하여 뉴스 검색
                        naverShoppingApi.searchNews(
                            clientId = "Q3hRbuSrFXi45ebILEqx",
                            clientSecret = "Wr4TaArtQC",
                            query = firstProductName,
                            display = 30
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


