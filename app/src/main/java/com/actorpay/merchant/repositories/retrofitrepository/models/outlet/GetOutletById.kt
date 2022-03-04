package com.actorpay.merchant.repositories.retrofitrepository.models.outlet

data class GetOutletById(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class Data(
    val active: Boolean,
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val contactNumber: String,
    val country: String,
    val createdAt: String,
    val description: String,
    val extensionNumber: String,
    val id: String,
    val latitude: String,
    val licenceNumber: String,
    val longitude: String,
    val merchantId: Any,
    val resourceType: String,
    val state: String,
    val title: String,
    val updatedAt: Any,
    val zipCode: String
)