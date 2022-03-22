package com.actorpay.merchant.repositories.retrofitrepository.models.sendMoney

data class AllSendMoney(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)
data class Data(
    val items: List<Item>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

data class Item(
    val accepted: Boolean,
    val active: Boolean,
    val amount: Double,
    val createdAt: String,
    val expired: Boolean,
    val requestId: String,
    val requestMoneyStatus: String,
    val toUserEmail: String,
    val toUserId: String,
    val toUserName: String,
    val updatedAt: String,
    val userEmail: String,
    val userId: String,
    val userName: String
)