package com.actorpay.merchant.repositories.retrofitrepository.models.products.addNewProduct

data class AddNewProductResponse(
    val `data`: List<Any>,
    val httpStatus: String,
    val message: String,
    val status: String
)