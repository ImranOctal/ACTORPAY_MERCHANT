package com.actorpay.merchant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.ForgetPasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.LoginParams
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.SignUpParams
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository
) : AndroidViewModel(
    Application()
) {
    val loginResponseLive = MutableStateFlow<ResponseLoginSealed>(ResponseLoginSealed.Empty)

    sealed class ResponseLoginSealed {
        class Success(val response: Any) : ResponseLoginSealed()
        class ErrorOnResponse(val failResponse: FailResponse?) : ResponseLoginSealed()
        class loading : ResponseLoginSealed()
        object Empty : ResponseLoginSealed()
    }

    fun login(email: String, password: String) {
        val body = LoginParams(email, password)
        viewModelScope.launch(dispatcherProvider.IO) {
            loginResponseLive.value = ResponseLoginSealed.loading()
            when (val response = apiRepo.LoginNow(body)) {
                is RetrofitResource.Error -> loginResponseLive.value =
                    ResponseLoginSealed.ErrorOnResponse(response.failResponse)
                is RetrofitResource.Success -> loginResponseLive.value =
                    ResponseLoginSealed.Success(response.data!!)
            }
        }
    }

    fun forgetPassword(email: String){
        val body= ForgetPasswordParams(email)
        viewModelScope.launch(dispatcherProvider.IO){
            loginResponseLive.value=ResponseLoginSealed.loading()
            when(val response=apiRepo.ForgetPassword(body)){
                is RetrofitResource.Error -> loginResponseLive.value =
                    ResponseLoginSealed.ErrorOnResponse(response.failResponse)
                is RetrofitResource.Success -> loginResponseLive.value =
                    ResponseLoginSealed.Success(response.data!!)
            }
        }
    }


    fun signUp(
        email: String,
        extensionNumber: String,
        contactNumber: String,
        password: String,
        shopAddress: String,
        fullAddress: String,
        businessName: String,
        licenceNumber: String
    ) {
        val body = SignUpParams(
            email,
            extensionNumber,
            contactNumber,
            password,
            shopAddress,
            fullAddress,
            businessName,
            licenceNumber
        )
        viewModelScope.launch(dispatcherProvider.IO) {
            loginResponseLive.value = ResponseLoginSealed.loading()
            when (val response = apiRepo.SignUpNow(body)) {
                is RetrofitResource.Error -> loginResponseLive.value =
                    ResponseLoginSealed.ErrorOnResponse(response.failResponse)
                is RetrofitResource.Success -> loginResponseLive.value =
                    ResponseLoginSealed.Success(response.data!!)
            }
        }
    }

}