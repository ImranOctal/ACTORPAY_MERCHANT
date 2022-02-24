package com.actorpay.merchant.ui.outlet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.DeleteOutParam
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.UpdateParam
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.ui.outlet.response.EmptyBody
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OutletViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository, ) : AndroidViewModel(
    Application())
{
    val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)
    fun getOutlet() {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            val body=EmptyBody()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getOutlet(token,"0",body)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

    fun deleteOutlet(ids: MutableList<String>) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            val body=DeleteOutParam(ids)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.deleteOutlet(token,body)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }
    fun updateOutlet(resourceType: String, licenceNumber: String, title: String, description: String, extensionNumber: String, addressLine1: String, addressLine2: String, contactNumber: String, zipCode: String, city: String, country: String, state: String, latitude: String, longitude: String, id: String) {
        val body= UpdateParam(id,resourceType,licenceNumber,title,description,extensionNumber,contactNumber,addressLine1,addressLine2,zipCode,city,state,country,latitude,longitude)
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.updateOutlet(token,body)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }
}