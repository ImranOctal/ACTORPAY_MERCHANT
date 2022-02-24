package com.actorpay.merchant.ui.manageOrder.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.UpdateStatus
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderParams

import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource

import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ManageOrderViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository, ) : AndroidViewModel(
    Application()
)
{
    val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)


    fun getAllOrder(startDate: String, endDate: String, merchantIid: String, status: String, customerEmail: String, orderNo: String) {
        val body= OrderParams(startDate,endDate,merchantIid,status,customerEmail,orderNo)
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value =  ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getAllOrder(token,body,"0","100",)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value = ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

    fun updateStatus(etNote: String, orderItemId: MutableList<String>, status: String, orderNo: String) {
        val body= UpdateStatus(etNote,orderItemId)
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.updateStatus(token,body,status,orderNo)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                      ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

}