package com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory

data class Item(
    val active: Boolean,
    val categoryId: String,
    val categoryName: String,
    val createdAt: String,
    val description: String,
    val id: String,
    val image: String,
    val name: String
)