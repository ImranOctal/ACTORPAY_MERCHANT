package com.actorpay.merchant.repositories.retrofitrepository.models.commission




data class CommissionResponse(
    val `data`: CommissionData,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class CommissionData(
    val items: ArrayList<CommissionItem>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)
data class CommissionItem(
    val id: String,
    val createdAt: String,
    val actorCommissionAmt: Double,
    val commissionPercentage: Double,
    val productId: String,
    val orderNo: String,
    val merchantId: String,
    val merchantName: String,
    val orderItemDTO: OrderItemDTO,
    val productName: String,
    val merchantEarnings: Double,
    val quantity: Int,
    val settlementStatus: String,
    val orderStatus: String,
)

data class CommissionParams(
    var orderNo: String = "",
    var merchantName: String = "",
    var orderStatus: String = "",
    var settlementStatus : String = "",
    var startDate : String = "",
    var endDate : String = "",
    var merchantEarningsRangeFrom : String = "",
    var merchantEarningsRangeTo : String = "",
    var actorCommissionAmtRangeFrom : String = "",
    var actorCommissionAmtRangeTo : String = "",
)
data class OrderItemDTO(
    val active: Boolean,
    val actualPrice: Double,
    val categoryId: String,
    val createdAt: String,
    val image: String,
    val merchantId: String,
    val orderItemId: String,
    val orderItemStatus: String,
    val productCgst: Double,
    val productId: String,
    val productPrice: Double,
    val productQty: Int,
    val productSgst: Double,
    val shippingCharge: Int,
    val subcategoryId: String,
    val taxPercentage: Double,
    val taxableValue: Double,
    val totalPrice: Double
)