package com.actorpay.merchant.repositories.retrofitrepository.models.products.categories

data class GetAllCategoriesDetails(
    val `data`: DataCategory,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class DataCategory(
    val items: List<ItemCategory>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

data class ItemCategory(
    val createdAt: String,
    val description: String,
    val id: String,
    val image: String,
    val name: String,
    val status: Boolean
)