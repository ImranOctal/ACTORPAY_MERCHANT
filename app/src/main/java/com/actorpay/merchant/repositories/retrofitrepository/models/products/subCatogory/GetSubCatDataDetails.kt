package com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory

data class GetSubCatDataDetails(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)