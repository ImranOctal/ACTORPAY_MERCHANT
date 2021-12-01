package com.actorpay.merchant.database.datastore
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
import kotlinx.coroutines.flow.Flow

interface DataStoreBase {

    fun giveRepository(): String

    suspend fun logOut(): Unit

    suspend fun update(booleanKey: Boolean)

    suspend fun update(stringKey: String)

    suspend fun updateAppname(appName: String)

    suspend fun update(integerKey: Int)

    suspend fun update(doubleKey: Double)

    suspend fun update(longKey: Long)

    suspend fun setUserId(userId:String)

    suspend fun setIsLoggedIn(value:Boolean)

    suspend fun setEmail(email:String)

    suspend fun setAccessToken(value:String)

    suspend fun setBussinessName(value:String)

    fun getBoolean(): Flow<Boolean>

    fun isLoggedIn(): Flow<Boolean>

    fun getString(): Flow<String>

    fun getUserId(): Flow<String>

    fun getAppName(): Flow<String>

    fun getInteger(): Flow<Int>

    fun getDouble(): Flow<Double>

    fun getLong(): Flow<Long>

    fun getEmail(): Flow<String>

    fun getAccessToken(): Flow<String>

    fun getBussinessName(): Flow<String>
}