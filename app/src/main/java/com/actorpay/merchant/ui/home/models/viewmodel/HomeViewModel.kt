package com.actorpay.merchant.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo

import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource

import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.cancel
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
    val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)
    fun getById(userId:String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(false)
            methodRepo.dataStore.getAccessToken().collect { token ->
                    when (val response = apiRepo.getById(token, userId)) {
                        is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                        is RetrofitResource.Success -> responseLive.value =
                            ResponseSealed.Success(response.data!!)
                    }
                }
            }

    }

    fun getPermissions() {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getPermissions(token)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value = ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

    fun getWalletBalance() {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(false)
            methodRepo.dataStore.getAccessToken().collect { token ->
                methodRepo.dataStore.getUserId().collect { id ->
                    when (val response = apiRepo.getWalletBalance(token, id)) {
                        is RetrofitResource.Error -> {
                            responseLive.value =
                                ResponseSealed.ErrorOnResponse(response.failResponse)
                            this.cancel()
                        }
                        is RetrofitResource.Success -> {
                            responseLive.value =
                                ResponseSealed.Success(response.data!!)
                            this.cancel()
                        }
                    }
                }
            }
        }
    }
}