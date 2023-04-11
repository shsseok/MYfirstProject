package com.example.odh_project_1

import android.annotation.SuppressLint
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
import com.example.odh_project_1.databinding.ActivityMoreTrendBinding
import moreTrendItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MoreTrend : AppCompatActivity() {
    private lateinit var moretrendbinding: ActivityMoreTrendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moretrendbinding = ActivityMoreTrendBinding.inflate(layoutInflater)
        setContentView(moretrendbinding.root)

        moretrendbinding.loginbutton.setOnClickListener {
            val morenewsintent1 = Intent(this@MoreTrend, LoginPage1::class.java)
            startActivity(morenewsintent1)
        }
        moretrendbinding.searchImage.setOnClickListener {
            val morenewsintent2 = Intent(this@MoreTrend, SearchActivity::class.java)
            startActivity(morenewsintent2)
        }
        val productList: ArrayList<moreTrendItem> = intent.getParcelableArrayListExtra("moreItems") ?: arrayListOf()


        Log.d("MoreTrend1", "Product List Size: ${productList.size}")
        val adapter = productMoreAdapter(productList, this@MoreTrend)
        val recyclerView = findViewById<RecyclerView>(R.id.moretrend)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setOnItemClickListener(object : productMoreAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MoreTrend, MainInfoActivity::class.java)
                val product = productList[position]
                intent.putExtra("image_url", product.productImageUrl)
                intent.putExtra("product_name", product.productName)
                intent.putExtra("price", product.productPrice)
                intent.putExtra("product_link", product.productLink)
                startActivity(intent)
            }
        })

    }

    class productMoreAdapter(
        private val productmoreList: List<moreTrendItem>, private val context: Context
    ) : RecyclerView.Adapter<productMoreAdapter.ViewHolder>() {
        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }

        // 클릭 리스너를 초기화합니다.
        private var onItemClickListener: OnItemClickListener? = null

        // 클릭 리스너를 설정하는 메서드를 추가합니다.
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
            return  productmoreList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product =  productmoreList[position]

            holder.rank.text = product.rank.toString()
            holder.productName.text = product.productName

            // Glide를 사용하여 이미지 로드
            Glide.with(context).load(product.productImageUrl).into(holder.productImage)
        }

    }}