package com.actorpay.merchant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ActorPayViewModel(val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository)  : AndroidViewModel(Application()) {

    val actorcResponseLive = MutableStateFlow<ResponseActorSealed>(ResponseActorSealed.Empty)
    sealed class ResponseActorSealed {
        class Success(val response: Any) : ResponseActorSealed()
        class ErrorOnResponse(val failResponse: FailResponse?) : ResponseActorSealed()
        class loading : ResponseActorSealed()
        object Empty : ResponseActorSealed()
    }

    fun getAllCountries(){
        viewModelScope.launch(dispatcherProvider.IO){
            actorcResponseLive.value= ResponseActorSealed.loading()
            when(val response=apiRepo.getAllCountries()){
                is RetrofitResource.Error -> actorcResponseLive.value =
                    ResponseActorSealed.ErrorOnResponse(response.failResponse)
                is RetrofitResource.Success -> actorcResponseLive.value =
                    ResponseActorSealed.Success(response.data!!)
            }
        }
    }


}