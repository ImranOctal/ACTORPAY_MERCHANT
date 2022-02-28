package com.actorpay.merchant.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.MerchantSettingsDTO
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource

import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository, ) : AndroidViewModel(Application()) {
    val profileResponseLive  = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)
    val merchantSettingsDTOList= mutableListOf<MerchantSettingsDTO>()


    sealed class ResponseProfileSealed {
        class Success(val response: Any) : ResponseProfileSealed()
        class ErrorOnResponse(val failResponse: FailResponse?) : ResponseProfileSealed()
        class loading : ResponseProfileSealed()
        object Empty : ResponseProfileSealed()
    }


    fun getProfile() {
        viewModelScope.launch(dispatcherProvider.IO) {
            profileResponseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                methodRepo.dataStore.getUserId().collect { userId ->
                    when (val response = apiRepo.getProfile(userId, token)) {
                        is RetrofitResource.Error -> profileResponseLive.value =
                            ResponseSealed.ErrorOnResponse(response.failResponse)
                        is RetrofitResource.Success -> profileResponseLive.value =
                            ResponseSealed.Success(response.data!!)
                    }
                }

            }
        }
    }

    fun saveProfile(email: String, shopAddress: String, fullAddress: String, businessName: String, licenceNumber: String, merchantSettingsDTO: MutableList<MerchantSettingsDTO>) {
        viewModelScope.launch(dispatcherProvider.IO) {
            profileResponseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                methodRepo.dataStore.getUserId().collect { userId ->
                    when (val response = apiRepo.saveProfile(
                        email,
                        shopAddress,
                        fullAddress,
                        businessName,
                        licenceNumber,
                        userId,
                        merchantSettingsDTO,
                        token
                    )) {
                        is RetrofitResource.Error -> profileResponseLive.value =
                            ResponseSealed.ErrorOnResponse(response.failResponse)
                        is RetrofitResource.Success -> profileResponseLive.value =
                            ResponseSealed.Success(response.data!!)
                    }
                }
            }
        }
    }

}