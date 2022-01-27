package com.actorpay.merchant.repositories.retrofitrepository.resource

import androidx.compose.runtime.Composable
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse

/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
sealed class RetrofitResource<T>(val data: T?, val failResponse: FailResponse?) {
    class Success<T>(data: T?) : RetrofitResource<T>(data, null)
    class Error<T>(failResponse: FailResponse) : RetrofitResource<T>(null, failResponse)
}