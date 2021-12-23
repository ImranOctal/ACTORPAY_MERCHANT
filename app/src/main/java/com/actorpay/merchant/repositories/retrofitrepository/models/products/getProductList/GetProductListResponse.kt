package com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList

data class GetProductListResponse(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)