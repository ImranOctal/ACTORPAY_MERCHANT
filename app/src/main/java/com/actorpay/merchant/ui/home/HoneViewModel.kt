package com.actorpay.merchant.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository,
) : AndroidViewModel(
    Application()
) {

    val homeResponseLive = MutableStateFlow<ResponseHomeSealed>(ResponseHomeSealed.Empty)

    sealed class ResponseHomeSealed {
        class Success(val response: Any) : ResponseHomeSealed()
        class ErrorOnResponse(val failResponse: FailResponse?) : ResponseHomeSealed()
        class loading : ResponseHomeSealed()
        object Empty : ResponseHomeSealed()
    }

    fun changePassword(oldPassword: String,newPassword:String){
        val body= ChangePasswordParams(oldPassword,newPassword,newPassword)
        viewModelScope.launch(dispatcherProvider.IO){
            homeResponseLive.value= ResponseHomeSealed.loading()
            methodRepo.dataStore.getAccessToken().collect {
                token->
                when(val response=apiRepo.changePassword(body,token)){
                    is RetrofitResource.Error -> homeResponseLive.value =
                        ResponseHomeSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> homeResponseLive.value =
                        ResponseHomeSealed.Success(response.data!!)
                }
            }

        }
    }
}