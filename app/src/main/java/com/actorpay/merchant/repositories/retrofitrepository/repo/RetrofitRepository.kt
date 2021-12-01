package com.actorpay.merchant.repositories.retrofitrepository.repo

import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.*
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileReesponse
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.octal.actorpay.repositories.retrofitrepository.models.content.ContentResponse

/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */


interface RetrofitRepository {
    //Demo
   /* suspend fun GetAllVideo(userId: String): RetrofitResource<VideoResponse>*/
    suspend fun LoginNow(loginDetail: LoginParams): RetrofitResource<LoginResponses>

    suspend fun ForgetPassword(forgetPasswordParams: ForgetPasswordParams):RetrofitResource<ForgetPasswordResponses>

    suspend fun SignUpNow(signUpParams: SignUpParams): RetrofitResource<SuccessResponse>

    suspend fun changePassword(miscChangePasswordParams: ChangePasswordParams, token: String):RetrofitResource<SuccessResponse>

    suspend fun getProfile(id:String,token:String):RetrofitResource<ProfileReesponse>

    suspend fun saveProfile(email:String,shopAddress:String,fullAddress:String,businessName:String,licenceNumber:String,id:String,token: String):RetrofitResource<SuccessResponse>

    suspend fun getContent(type:String):RetrofitResource<ContentResponse>



}