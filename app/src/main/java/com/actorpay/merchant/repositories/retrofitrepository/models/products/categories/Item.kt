package com.actorpay.merchant.repositories.retrofitrepository.models.products.categories

data class Item(
    val createdAt: String,
    val description: String,
    val id: String,
    val image: String,
    val name: String,
    val status: Boolean
)