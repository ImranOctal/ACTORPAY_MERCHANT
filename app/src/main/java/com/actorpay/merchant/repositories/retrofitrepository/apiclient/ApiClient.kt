package com.actorpay.merchant.retrofitrepository.apiclient

import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.*
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileParams
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileReesponse
import com.octal.actorpay.repositories.AppConstance.AppConstance
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.ADD_PRODUCT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.AUTH
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_CONTENT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.IDS
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.TYPE
import com.octal.actorpay.repositories.retrofitrepository.models.content.ContentResponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Header(AUTH) token: String,
        @Body changePasswordParams: ChangePasswordParams
    ): Response<SuccessResponse>

    @GET(AppConstance.GET_PROFILE + IDS)
    suspend fun getProfile(
        @Header(AUTH) token: String,
        @Path("id") id: String
    ): Response<ProfileReesponse>

    @PUT(AppConstance.UPDATE_PROFILE)
    suspend fun saveProfile(
        @Header(AUTH) token: String,
        @Body profileParams: ProfileParams
    ): Response<SuccessResponse>


    @GET(GET_CONTENT)
    suspend fun getContent(
        @Query(TYPE) type: String
    ): Response<ContentResponse>

    @Multipart
    @POST(ADD_PRODUCT)
    suspend fun addProduct(
        @Header(AUTH) token: String,
        @Part("product") product: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ProductResponse>

    @Multipart
    @PUT(ADD_PRODUCT+ IDS)
    suspend fun updateProduct(
        @Header(AUTH) token: String,
        @Part("product") product: RequestBody,
        @Part file: MultipartBody.Part,
        @Path("id") id: String
    ): Response<ProductResponse>

    @GET(ADD_PRODUCT + IDS)
    suspend fun getProduct(
        @Header(AUTH) token: String,
        @Path("id") id: String
    ): Response<ProductResponse>

    @DELETE(ADD_PRODUCT + IDS)
    suspend fun deleteProduct(
        @Header(AUTH) token: String,
        @Path("id") id: String
    ): Response<ProductResponse>
}
