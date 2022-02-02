package com.actorpay.merchant.repositories.retrofitrepository.models.permission

data class PermissionDetails(
    val `data`: List<Data>,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class Data(
    val read: Boolean,
    val screenId: String,
    val screenName: String,
    val write: Boolean
)