package com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory

data class Data(
    val items: List<Item>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)