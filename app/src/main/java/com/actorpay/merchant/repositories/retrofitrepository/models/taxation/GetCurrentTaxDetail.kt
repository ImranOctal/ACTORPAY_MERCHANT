package com.actorpay.merchant.repositories.retrofitrepository.models.taxation

data class GetCurrentTaxDetail(
    val `data`: List<Data>,
    val httpStatus: String,
    val message: String,
    val status: String
)