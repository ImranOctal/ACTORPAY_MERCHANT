package com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductById.success

data class Data(
    val actualPrice: Double,
    val categoryId: String,
    val createdAt: String,
    val dealPrice: Double,
    val description: String,
    val image: String,
    val merchantId: String,
    val name: String,
    val productId: String,
    val quantity: Int,
    val status: Boolean,
    val subCategoryId: String,
    val taxId: String,
    val updatedAt: Any
)