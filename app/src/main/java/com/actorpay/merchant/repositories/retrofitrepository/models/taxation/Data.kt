package com.actorpay.merchant.repositories.retrofitrepository.models.taxation

data class Data(
    val active: Boolean,
    val chapter: String,
    val createdAt: String,
    val hsnCode: String,
    val id: String,
    val productDetails: String,
    val taxPercentage: Double,
    val updatedAt: String
)