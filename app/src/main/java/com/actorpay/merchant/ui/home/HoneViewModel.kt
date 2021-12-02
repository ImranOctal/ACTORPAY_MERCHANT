package com.actorpay.merchant.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class HomeViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository,
) : AndroidViewModel(
    Application()
) {

    val homeResponseLive = MutableStateFlow<ResponseHomeSealed>(ResponseHomeSealed.Empty)

    sealed class ResponseHomeSealed {
        class Success(val response: Any) : ResponseHomeSealed()
        class ErrorOnResponse(val failResponse: FailResponse?) : ResponseHomeSealed()
        class loading : ResponseHomeSealed()
        object Empty : ResponseHomeSealed()
    }

    fun changePassword(oldPassword: String,newPassword:String){
        val body= ChangePasswordParams(oldPassword,newPassword,newPassword)
        viewModelScope.launch(dispatcherProvider.IO){
            homeResponseLive.value= ResponseHomeSealed.loading()
            methodRepo.dataStore.getAccessToken().collect {
                token->
                when(val response=apiRepo.changePassword(body,token)){
                    is RetrofitResource.Error -> homeResponseLive.value =
                        ResponseHomeSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> homeResponseLive.value =
                        ResponseHomeSealed.Success(response.data!!)
                }
            }

        }
    }

    fun addProduct( product:String, file: File){
        var r1: RequestBody? = null
        var f1: MultipartBody.Part? = null
        r1 = file.asRequestBody("/*".toMediaTypeOrNull())
        f1 =
            MultipartBody.Part.createFormData(
                "profile_img",
                "${System.currentTimeMillis()}.jpg",
                r1
            )
        val prod=product.toRequestBody()
        viewModelScope.launch(dispatcherProvider.IO){
            homeResponseLive.value= ResponseHomeSealed.loading()
            methodRepo.dataStore.getAccessToken().collect {
                token->
                when(val response=apiRepo.addProduct(token,prod,f1)){
                    is RetrofitResource.Error -> homeResponseLive.value =
                        ResponseHomeSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> homeResponseLive.value =
                        ResponseHomeSealed.Success(response.data!!)
                }
            }

        }
    }

    fun updateProduct(productId: String, product:String, file: File){
        var r1: RequestBody? = null
        var f1: MultipartBody.Part? = null
        r1 = file.asRequestBody("/*".toMediaTypeOrNull())
        f1 =
            MultipartBody.Part.createFormData(
                "profile_img",
                "${System.currentTimeMillis()}.jpg",
                r1
            )
        val prod=product.toRequestBody()
        viewModelScope.launch(dispatcherProvider.IO){
            homeResponseLive.value= ResponseHomeSealed.loading()
            methodRepo.dataStore.getAccessToken().collect {
                token->
                when(val response=apiRepo.updateProduct(token,productId,prod,f1)){
                    is RetrofitResource.Error -> homeResponseLive.value =
                        ResponseHomeSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> homeResponseLive.value =
                        ResponseHomeSealed.Success(response.data!!)
                }
            }

        }
    }

    fun getProduct(productId:String){

        viewModelScope.launch(dispatcherProvider.IO){
            homeResponseLive.value= ResponseHomeSealed.loading()
            methodRepo.dataStore.getAccessToken().collect {
                token->
                when(val response=apiRepo.getProduct(token,productId)){
                    is RetrofitResource.Error -> homeResponseLive.value =
                        ResponseHomeSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> homeResponseLive.value =
                        ResponseHomeSealed.Success(response.data!!)
                }
            }

        }
    }

    fun deleteProduct(productId:String){

        viewModelScope.launch(dispatcherProvider.IO){
            homeResponseLive.value= ResponseHomeSealed.loading()
            methodRepo.dataStore.getAccessToken().collect {
                token->
                when(val response=apiRepo.deleteProduct(token,productId)){
                    is RetrofitResource.Error -> homeResponseLive.value =
                        ResponseHomeSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> homeResponseLive.value =
                        ResponseHomeSealed.Success(response.data!!)
                }
            }

        }
    }
}