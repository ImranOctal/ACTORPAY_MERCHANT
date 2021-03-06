package com.actorpay.merchant.repositories.retrofitrepository.models.permission

data class PermissionDetails(
    val `data`: List<PermissionData>,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class PermissionData(
    var read: Boolean,
    val screenId: String,
    val screenName: String,
    var write: Boolean
)