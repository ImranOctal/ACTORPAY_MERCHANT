package com.actorpay.merchant.repositories.retrofitrepository.models.submerchant

data class GetAllSubMerchant(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class EMPTYJSON(
    val empty:String?=null
)

data class Data(
    val items: List<Item>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

data class Item(
    val active: Boolean,
    val contactNumber: String,
    val createdAt: String,
    val email: String,
    val extensionNumber: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val merchantId: Any,
    val profilePicture: Any,
    val updatedAt: Any
)