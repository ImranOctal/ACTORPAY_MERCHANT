package com.actorpay.merchant.ui.outlet.response

data class GetOutlet(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class EmptyBody(
    val idfdljf:String?=null
)

data class Data(
    val items: List<OutletItem>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

data class OutletItem(
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