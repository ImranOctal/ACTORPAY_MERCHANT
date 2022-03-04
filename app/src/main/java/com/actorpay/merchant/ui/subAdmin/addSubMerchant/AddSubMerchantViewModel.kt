package com.actorpay.merchant.ui.subAdmin.addSubMerchant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.AddSubMerchantParam
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.UpdateSubMerchantParam
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddSubMerchantViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository, ) : AndroidViewModel(
    Application()
) {
    val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)

    fun addSubMerchant(countryCode: String, firstName: String, lastName: String, email: String, contactNumber: String,dob:String,roleId:String,gender:String,password:String) {
        val body=AddSubMerchantParam(countryCode,firstName,lastName,email,contactNumber,dob,roleId,gender,false,password)
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.addSubMerchant(token,body)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }


    fun getMerchantById(id:String?) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = id?.let { apiRepo.getMerchantById(token, it) }) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

    fun updateSubMerchant(countryCode: String, firstName: String, lastName: String, email: String, contactNumber: String, roleId: String, gender: String,id:String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            val body=UpdateSubMerchantParam(firstName,lastName,contactNumber,countryCode,roleId,id,gender,email)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.updateSubMerchant(token,body)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }
}