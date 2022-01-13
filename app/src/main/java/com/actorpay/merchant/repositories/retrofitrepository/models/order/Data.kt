package com.actorpay.merchant.repositories.retrofitrepository.models.order

data class Data(
    val items: ArrayList<Item>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

