package com.example.odh_project_1

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.odh_project_1.DataAPi.NaverShoppingApi
import com.example.odh_project_1.DataClass.SearchNewsResponse
import com.example.odh_project_1.databinding.ActivityMainInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainInfoActivity : AppCompatActivity() {
    private lateinit var maininfobinding: ActivityMainInfoBinding
    private lateinit var barcodeScanner: BarcodeScanner
    private var newsList: ArrayList<moreNewsItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val query = intent.getStringExtra("query")

        if (query != null) {
            searchNews(query)
        }


        maininfobinding = ActivityMainInfoBinding.inflate(layoutInflater)
        setContentView(maininfobinding.root)
        maininfobinding.loginbutton.setOnClickListener {
            val maininfointent1 = Intent(this@MainInfoActivity, LoginPage1::class.java)
            startActivity(maininfointent1)
        }
        maininfobinding.searchImage.setOnClickListener {
            val maininfointent2 = Intent(this@MainInfoActivity, SearchActivity::class.java)
            startActivity(maininfointent2)
        }
        maininfobinding.newsmore.setOnClickListener {
            val intent4 = Intent(this@MainInfoActivity, MoreNews::class.java)
            intent4.putParcelableArrayListExtra("news_list", newsList)
            startActivity(intent4)
        }
        barcodeScanner = BarcodeScanner(this)
        maininfobinding.cameraImage.setOnClickListener {
            barcodeScanner.startScan()
        }

        val imageUrl = intent.getStringExtra("image_url")
        val productName = intent.getStringExtra("product_name")
        val price = intent.getStringExtra("price")
        val searchInfo: ImageView = findViewById(R.id.search_info)
        val info: TextView = findViewById(R.id.info)
        val priceView: TextView = findViewById(R.id.price)
        Glide.with(this)
            .load(imageUrl)
            .into(searchInfo)
        info.text = "상품명: $productName"
        priceView.text = "가격: $price"

        val site: TextView = findViewById(R.id.site)
        val productLink = intent.getStringExtra("product_link")

// site TextView에 클릭 리스너를 설정합니다.
        site.setOnClickListener {
            // 웹 URL을 열기 위한 인텐트를 생성합니다.
            val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(productLink))
            // 인텐트를 처리할 수 있는 앱이 있는지 확인합니다.
            if (openUrlIntent.resolveActivity(packageManager) != null) {
                // URL을 열기 위한 앱이 있으면 인텐트를 실행합니다.
                startActivity(openUrlIntent)
            } else {
                // URL을 열 수 있는 앱이 없으면 사용자에게 메시지를 표시합니다.
                Toast.makeText(this, "링크를 열 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val barcodeResult = barcodeScanner.handleActivityResult(requestCode, resultCode, data)

        if (barcodeResult != null) {
            Toast.makeText(this, "Barcode: $barcodeResult", Toast.LENGTH_LONG).show()
            // 여기서 바코드 결과를 사용해 네이버 검색 API로 상품 검색을 수행할 수 있습니다.
        } else {
            Toast.makeText(this, "No barcode detected", Toast.LENGTH_SHORT).show()
        }
    }

    fun searchNews(query: String) {
        val naverShoppingApi = NaverShoppingApi.create()
        naverShoppingApi.searchNews(
            clientId = "Q3hRbuSrFXi45ebILEqx",
            clientSecret = "Wr4TaArtQC",
            query = query,
            display = 10
        ).enqueue(object : Callback<SearchNewsResponse> {
            override fun onResponse(
                call: Call<SearchNewsResponse>, response: Response<SearchNewsResponse>
            ) {
                if (response.isSuccessful) {
                    val searchNew = response.body()
                    Log.d("news",searchNew.toString())
                    // 뉴스의 정보를 보여주기
                    val newInfo1: TextView = findViewById(R.id.newsinfo1)
                    var newsInfoText = ""
                    searchNew?.items?.forEach { item ->
                        newsInfoText += "${item.title}\n"
                        newsList.add(moreNewsItem(item.title, item.link))

                    }

                    newInfo1.text = newsInfoText
                    searchNew?.items?.getOrNull(0)?.let { firstNews ->
                        newInfo1.text = HtmlCompat.fromHtml(
                            firstNews.title, HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        val firstNewsUrl = firstNews.link

                        newInfo1.setOnClickListener {
                            if (firstNewsUrl != null) {
                                val intent = Intent(
                                    Intent.ACTION_VIEW, Uri.parse(firstNewsUrl)
                                )
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@MainInfoActivity,
                                    "뉴스 링크가 존재하지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }                        }
}
                } else {
                    Log.e("Error", "API call failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<SearchNewsResponse>, t: Throwable) {
                Log.e("Errorrr", "API call failed: $t")
            }
        })
    }
}