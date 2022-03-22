package com.actorpay.merchant.ui.requestMoney

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.GetAllRequestMoneyParams
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestMoneyListData
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestMoneyParams
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.utils.ResponseSealed

import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RequestMoneyViewModel(val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository)  : AndroidViewModel(
    Application()
) {

    val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)

    var requestMoneyParams= GetAllRequestMoneyParams()
    val requestMoneyListData= RequestMoneyListData(0, 0, mutableListOf(), 0, 10)

    fun userExists(user:String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response =
                    apiRepo.userExists(token,user)) {
                    is RetrofitResource.Error ->{
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

    fun getAllRequest() {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getAllRequestMoney(token,requestMoneyListData.pageNumber,requestMoneyListData.pageSize,requestMoneyParams)) {
                    is RetrofitResource.Error ->{
                        responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                        this.cancel()
                    }
                    is RetrofitResource.Success -> {
                        responseLive.value = ResponseSealed.Success(response.data!!)
                        this.cancel()
                    }
                }
            }
        }
    }

    fun processRequest(isAccept:Boolean,requestId:String){
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response =
                    apiRepo.processRequest(token,isAccept, requestId)) {
                    is RetrofitResource.Error ->{
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
    fun requestMoney(requestMoneyParams: RequestMoneyParams) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response =
                    apiRepo.requestMoney(token,requestMoneyParams)) {
                    is RetrofitResource.Error ->{
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