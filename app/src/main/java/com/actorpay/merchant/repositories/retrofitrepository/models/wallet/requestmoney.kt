package com.octal.actorpayuser.repositories.retrofitrepository.models.wallet

import java.io.Serializable

data class GetAllRequestMoneyResponse(
    var message: String,
    var data: RequestMoneyListData,
    var status: String,
    var httpStatus: String
)

data class RequestMoneyListData(
    var totalPages: Int,
    var totalItems: Int,
    var items: MutableList<RequestMoneyData>,
    var pageNumber: Int,
    val pageSize: Int
)

data class RequestMoneyData(
    var createdAt:String?,
    var requestId:String,
    var userId:String,
    var userEmail:String,
    var toUserId:String,
    var toUserEmail:String,
    var toUserName:String,
    var amount:String,
    var requestMoneyStatus:String,
    var myRequest:Boolean,
    var accepted:Boolean,
    var expired:Boolean
):Serializable


data class GetAllRequestMoneyParams(
    var fromAmount: String="",
    var toAmount: String="",
    var startDate: String="",
    var endDate: String="",
    var requestMoneyStatus:String?=null,
)



data class RequestMoneyParams(
    var userIdentity:String="",
    var amount:String="",
    var reason:String=""
)


data class RequestMoneyResponse(
    var message: String,
    var data: RequestMoneyData,
    var status: String,
    var httpStatus: String
)


data class RequestProcessResponse(
    var message: String,
    var data: RequestMoneyDTO,
    var status: String,
    var httpStatus: String
)

data class RequestMoneyDTO(
    var requestMoneyDTO: RequestMoneyData
)


