package com.actorpay.merchant.repositories.retrofitrepository.models


data class FailResponse(
    val message: String,
    val status: String
)
data class SuccessResponse(
    val message: String,
    val status: String,
    val `data`: Any,
)