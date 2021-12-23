package com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct

data class DeleteProductResponse(
    val `data`: String,
    val httpStatus: String,
    val message: String,
    val status: String
)