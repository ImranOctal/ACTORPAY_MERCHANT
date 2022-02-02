package com.actorpay.merchant.repositories.retrofitrepository.models.order


import java.io.Serializable

data class Item (
    val createdAt: String,
    val customer: Customer,
    val merchantId: String,
    val orderId: String,
    val orderItemDtos: List<OrderItemDto>,
    val orderNotesDtos: List<OrderNotesDto>,
    val orderNo: String,
    val orderStatus: String,
    val shippingAddressDTO: ShippingAddressDTO,
    val totalCgst: Double,
    val totalPrice: Double,
    val totalQuantity: Int,
    val totalSgst: Double,
    val totalTaxableValue: Double
):Serializable