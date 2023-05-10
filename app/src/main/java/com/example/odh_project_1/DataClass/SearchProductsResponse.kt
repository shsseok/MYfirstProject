package com.example.odh_project_1.DataClass

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