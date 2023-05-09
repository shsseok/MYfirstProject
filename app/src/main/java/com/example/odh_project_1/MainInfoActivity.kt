package com.example.odh_project_1

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.odh_project_1.databinding.ActivityMainInfoBinding

class MainInfoActivity : AppCompatActivity() {
    private lateinit var maininfobinding: ActivityMainInfoBinding
    private lateinit var barcodeScanner: BarcodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
}