package com.actorpay.merchant.repositories.retrofitrepository.models.submerchant

data class GetMerchantById(
    val `data`: DataMerchant,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class DataMerchant(
    val active: Boolean,
    val contactNumber: String,
    val createdAt: String,
    val email: String,
    val extensionNumber: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val merchantId: String,
    val profilePicture: Any,
    val updatedAt: Any
)