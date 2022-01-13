package com.actorpay.merchant.repositories.retrofitrepository.models.order

data class UpdateOrderStatus(
    val `data`: String,
    val httpStatus: String,
    val message: String,
    val status: String
)