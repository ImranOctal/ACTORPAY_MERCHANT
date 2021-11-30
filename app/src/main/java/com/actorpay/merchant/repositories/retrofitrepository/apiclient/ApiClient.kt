package com.actorpay.merchant.retrofitrepository.apiclient

import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.*
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileParams
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileReesponse
import com.octal.actorpay.repositories.AppConstance.AppConstance
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_CONTENT
import com.octal.actorpay.repositories.retrofitrepository.models.content.ContentResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiClient {


    @POST(AppConstance.LOGIN)
    suspend fun LoginNow(@Body loginDetail: LoginParams): Response<LoginResponses>

    @POST(AppConstance.FORGETPASSWORD)
    suspend fun forgetPassword(@Body forgetPasswordParams: ForgetPasswordParams): Response<ForgetPasswordResponses>

    @POST(AppConstance.SIGNUP)
    suspend fun SignUpNow(@Body signUpParams: SignUpParams): Response<SuccessResponse>

    @POST(AppConstance.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body changePasswordParams: ChangePasswordParams
    ): Response<SuccessResponse>

    @GET(AppConstance.GET_PROFILE + "{id}")
    suspend fun getProfile(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ProfileReesponse>

    @PUT(AppConstance.UPDATE_PROFILE)
    suspend fun saveProfile(
        @Header("Authorization") token: String,
        @Body profileParams: ProfileParams
    ): Response<SuccessResponse>


    @GET(GET_CONTENT)
    suspend fun getContent(
        @Query("type") type: String
    ): Response<ContentResponse>
}
