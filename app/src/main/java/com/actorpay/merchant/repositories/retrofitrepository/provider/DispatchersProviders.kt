package com.actorpay.merchant.repositories.retrofitrepository.provider
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProviders {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}