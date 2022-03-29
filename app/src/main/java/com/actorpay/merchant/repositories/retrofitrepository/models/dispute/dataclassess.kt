package com.octal.actorpayuser.repositories.retrofitrepository.models.dispute


data class DisputeListParams(
    var startDate: String,
    var endDate: String,
    var status: String,
    var disputeCode:String
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
    var orderNo: String,
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
    var disputeFlag: Boolean,
    var disputeMessages:MutableList<DisputeMessage>
    )


data class DisputeMessage(
    var createdAt: String,
    var message: String,
    var postedById: String,
    var postedByName: String,
    var userType: String,
    var disputeId: String
)

data class DisputeSingleResponse(
    var message: String,
    var data: DisputeData,
    var status: String,
    var httpStatus: String
)

data class SendMessageParams(
    val disputeId:String,
    val message:String
)