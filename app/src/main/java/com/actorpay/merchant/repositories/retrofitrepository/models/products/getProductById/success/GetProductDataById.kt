package com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductById.success

data class GetProductDataById(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)