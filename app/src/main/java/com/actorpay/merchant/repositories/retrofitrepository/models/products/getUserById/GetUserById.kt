package com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById

data class GetUserById(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class Data(
    val active: Boolean,
    val businessName: String,
    val contactNumber: String,
    val createdAt: String,
    val email: String,
    val extensionNumber: String,
    val fullAddress: String,
    val id: String,
    val licenceNumber: String,
    val merchantId: String,
    val password: Any,
    val profilePicture: Any,
    val resourceType: Any,
    val shopAddress: String,
    val updatedAt: String
)