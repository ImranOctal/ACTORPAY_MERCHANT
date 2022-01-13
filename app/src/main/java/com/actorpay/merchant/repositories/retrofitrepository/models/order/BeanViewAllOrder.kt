package com.actorpay.merchant.repositories.retrofitrepository.models.order

import java.io.Serializable


data class BeanViewAllOrder(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)
data class Customer(
    val contactNumber: String,
    val email: String,
    val firstName: String,
    val id: String,
    val kycDone: Boolean,
    val lastName: String,

//    val roles: List<Any>
):Serializable


 data class OrderItemDto (
    val active: Boolean,
    val categoryId: String,
    val createdAt: String,
    val deleted: Boolean,
    val image: String,
    val merchantId: String,
    val merchantName: String,
    val productCgst: Double,
    val productId: String,
    val productName: String,
    val productPrice: Double,
    val productQty: Int,
    val productSgst: Double,
    val shippingCharge: Int,
    val subcategoryId: String,
    val taxPercentage: Double,
    val taxableValue: Double,
    val totalPrice: Double,
    val updatedAt: String
):Serializable
data class OrderParams(val startDate: String,val endDate: String,val merchantId: String,val status: String,val customerEmail: String,val orderNo: String)

data class ShippingAddressDTO(
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val country: String,
    val id: String,
    val primaryContactNumber: String,
    val secondaryContactNumber: String,
    val state: String
):Serializable

