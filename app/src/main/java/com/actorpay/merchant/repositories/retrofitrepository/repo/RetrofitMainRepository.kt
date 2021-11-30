package com.actorpay.merchant.repositories.retrofitrepository.repo
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */

import android.content.Context
import com.actorpay.merchant.R
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.*
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileParams
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileReesponse
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.retrofitrepository.apiclient.ApiClient
import com.octal.actorpay.repositories.AppConstance.AppConstance
import com.octal.actorpay.repositories.retrofitrepository.models.content.ContentResponse
import org.json.JSONObject

class RetrofitMainRepository constructor(var context: Context, private var apiClient: ApiClient) :
    RetrofitRepository {


    override suspend fun LoginNow(loginDetail: LoginParams): RetrofitResource<LoginResponses> {
        try {
            val loginData = apiClient.LoginNow(loginDetail)
            val result = loginData.body()
            if (loginData.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(loginData.errorBody()!=null) {
                    val json= JSONObject(loginData.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }
    }

    override suspend fun ForgetPassword(forgetPasswordParams: ForgetPasswordParams): RetrofitResource<ForgetPasswordResponses> {
        try {
            val forgetData = apiClient.forgetPassword(forgetPasswordParams)
            val result = forgetData.body()
            if (forgetData.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(forgetData.errorBody()!=null) {
                    val json=JSONObject(forgetData.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }
    }

    override suspend fun SignUpNow(signUpParams: SignUpParams): RetrofitResource<SuccessResponse> {
        try {
            val loginData = apiClient.SignUpNow(signUpParams)
            val result = loginData.body()
            if (loginData.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(loginData.errorBody()!=null) {
                    val json= JSONObject(loginData.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }
    }

    override suspend fun changePassword(miscChangePasswordParams: ChangePasswordParams, token: String): RetrofitResource<SuccessResponse> {
        try {
            val data = apiClient.changePassword(AppConstance.B_Token +token,miscChangePasswordParams)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    val json=JSONObject(data.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse( message, status))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        }
        catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }
    }

    override suspend fun getProfile(id: String, token: String): RetrofitResource<ProfileReesponse> {
        try {

            val data = apiClient.getProfile(AppConstance.B_Token +token,id)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    val json=JSONObject(data.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }
    }

    override suspend fun saveProfile(
        email: String,
        shopAddress: String,
        fullAddress: String,
        businessName: String,
        licenceNumber: String,
        id: String,
        token: String
    ): RetrofitResource<SuccessResponse> {
        try {
            val data = apiClient.saveProfile(AppConstance.B_Token +token,
                ProfileParams(id,email, shopAddress, fullAddress, businessName, licenceNumber)
            )
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    val json=JSONObject(data.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse( message, status))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        }
        catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }
    }


    override suspend fun getContent(type: String): RetrofitResource<ContentResponse> {

        try {

            val data = apiClient.getContent(type)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    val json=JSONObject(data.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }
    }

}