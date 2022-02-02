package com.actorpay.merchant.repositories.retrofitrepository.models.ordernote

data class OrderNote(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class Data(
    val active: Boolean,
    val createdAt: String,
    val merchantId: String,
    val orderId: String,
    val orderNo: String,
    val orderNoteBy: String,
    val orderNoteDescription: String,
    val orderNoteId: String,
    val userId: String,
    val userType: String
)