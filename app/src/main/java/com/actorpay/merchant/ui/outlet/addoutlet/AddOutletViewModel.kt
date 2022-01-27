package com.actorpay.merchant.ui.outlet.addoutlet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.OutletParam
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource

import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddOutletViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository,
) : AndroidViewModel(
    Application()
) {
    val AddresponseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)
    fun createOutlet(resourceType: String, licenceNumber: String, title: String, description: String, extensionNumber: String, addressLine1: String, addressLine2: String, contactNumber: String, zipCode: String, city: String, country: String, state: String, latitude: String, longitude: String) {
        val body= OutletParam(resourceType,licenceNumber,title,description,extensionNumber,contactNumber,addressLine1,addressLine2,zipCode,city,state,country,latitude,longitude)

        viewModelScope.launch(dispatcherProvider.IO) {
            AddresponseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.createOutlet(token,body)) {
                    is RetrofitResource.Error -> AddresponseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> AddresponseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

}