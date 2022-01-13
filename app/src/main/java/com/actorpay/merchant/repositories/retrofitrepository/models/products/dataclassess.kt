package com.octal.actorpay.repositories.retrofitrepository.models.content


data class ProductResponse(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class Data(
    val actualPrice: Double,
    val categoryId: String,
    val cgst: Double,
    val createdAt: String,
    val dealPrice: Double,
    val description: String,
    val image: String,
    val merchantId: String,
    val merchantName: String,
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