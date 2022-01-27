package com.actorpay.merchant.utils

import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse

sealed class ResponseSealed {
    class Success(val response: Any) : ResponseSealed()
    class ErrorOnResponse(val failResponse: FailResponse?) : ResponseSealed()
    class Loading(val isLoading: Boolean?) : ResponseSealed()
    object Empty : ResponseSealed()
}