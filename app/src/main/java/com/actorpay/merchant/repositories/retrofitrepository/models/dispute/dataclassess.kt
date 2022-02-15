package com.octal.actorpayuser.repositories.retrofitrepository.models.dispute


data class DisputeListParams(
    var startDate: String,
    var endDate: String,
    var orderNo: String,
    var status: String,
)

data class DisputeListData(
    var totalPages: Int,
    var totalItems: Int,
    var items: MutableList<DisputeData>,
    var pageNumber: Int,
    val pageSize: Int
)

data class DisputeListResponse(
    var message: String,
    var data: DisputeListData,
    var status: String,
    var httpStatus: String
)


data class DisputeData(
    var createdAt: String,
    var disputeId: String,
    var disputeCode: String,
    var title: String,
    var description: String,
    var imagePath: String,
    var orderItemId: String,
    var penalityPercentage: String,
    var status: String,
    var userId: String,
    var userName: String,
    var merchantName: String,
    var merchantId: String,
    var disputeFlag: Boolean
    )