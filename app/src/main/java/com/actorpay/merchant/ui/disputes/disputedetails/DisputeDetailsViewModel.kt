package com.actorpay.merchant.ui.disputes.disputedetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.utils.ResponseSealed
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeData
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListData
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListParams
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.SendMessageParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DisputeDetailsViewModel (val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository)  : AndroidViewModel(
    Application()
) {

    val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)


    var disputeListData = DisputeListData(0, 0, mutableListOf(), 0, 20)
    var disputeListParams= DisputeListParams("","","","")

    fun getDispute(disputeId:String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response =
                    apiRepo.getDispute(token,disputeId)) {
                    is RetrofitResource.Error -> responseLive.value =
                        ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> {
                        responseLive.value =
                            ResponseSealed.Success(response.data!!)
                    }
                }
            }
        }
    }

    fun sendDisputeMessage(sendMessageParams: SendMessageParams){
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response =
                    apiRepo.sendDisputeMessage(token,sendMessageParams)) {
                    is RetrofitResource.Error -> responseLive.value =
                        ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> {
                        responseLive.value =
                            ResponseSealed.Success(response.data!!)
                    }
                }
            }
        }
    }

    fun getAllDisputes() {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response =
                    apiRepo.getAllDisputes(token,disputeListData.pageNumber,disputeListData.pageSize,
                        disputeListParams
                    )) {
                    is RetrofitResource.Error -> responseLive.value =
                        ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> {
                        responseLive.value =
                            ResponseSealed.Success(response.data!!)
                    }
                }
            }
        }
    }

    companion object{
        var disputeData:DisputeData?=null

    }

}