package com.actorpay.merchant.ui.addMoney

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.AddMoneyParams
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddMoneyViewModel(val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository)  : AndroidViewModel(
    Application()
) {

    val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)


    fun addMoney(addMoneyParams: AddMoneyParams) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                    when (val response =
                        apiRepo.addMoney(token, addMoneyParams)) {
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

    fun getWalletBalance() {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
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