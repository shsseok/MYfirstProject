package com.example.odh_project_1.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.odh_project_1.DataClass.Product
import com.example.odh_project_1.R

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