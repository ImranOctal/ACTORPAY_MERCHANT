package com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList

data class Item(
    val actualPrice: Double,
    val categoryId: String,
    val cgst: Double,
    val createdAt: String,
    val dealPrice: Double,
    val description: String,
    val image: String,
    val merchantId: String,
    val name: String,
    val productId: String,
    val productTaxId: String,
    val sgst: Double,
    val status: Boolean,
    val stockCount: Int,
    val stockStatus: String,
    val subCategoryId: String,
    val taxId: String,
    val updatedAt: String
)