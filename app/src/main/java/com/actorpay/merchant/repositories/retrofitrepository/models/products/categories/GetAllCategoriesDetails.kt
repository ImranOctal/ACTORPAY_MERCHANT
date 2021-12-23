package com.actorpay.merchant.repositories.retrofitrepository.models.products.categories

data class GetAllCategoriesDetails(
    val `data`: List<Data>,
    val httpStatus: String,
    val message: String,
    val status: String
)