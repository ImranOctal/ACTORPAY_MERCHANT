package com.actorpay.merchant.repositories.retrofitrepository.models.screens

data class ScreenResponse(
    val `data`: List<ScreenItem>,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class ScreenItem(
    val id: String,
    val screenName: String,
    val screenOrder: String,
    val screenPath: String,
    var read:Boolean=false,
    var write:Boolean=false
)