package com.actorpay.merchant.repositories.retrofitrepository.models.profile


data class ProfileReesponse (
    val id: String,
    val email: String,
    val extensionNumber: String,
    val contactNumber: String,
    val profilePicture: Any? = null,
    val password: Any? = null,
    val resourceType: String,
    val businessName: String,
    val fullAddress: String,
    val shopAddress: String,
    val licenceNumber: String,
    val createdAt: String,
    val updatedAt: String,
    val active: Boolean
)


data class ProfileParams(
    val id:String,
    val email:String,
    val shopAddress:String,
    val fullAddress:String,
    val businessName:String,
    val licenceNumber:String,
)