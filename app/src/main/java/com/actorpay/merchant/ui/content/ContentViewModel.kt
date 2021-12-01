package com.actorpay.merchant.ui.content

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

class ContentViewModel(val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository)  : AndroidViewModel(
    Application()
){

    val contentResponseLive = MutableStateFlow<ResponseContentSealed>(ResponseContentSealed.Empty)
    sealed class ResponseContentSealed {
        class Success(val response: Any) : ResponseContentSealed()
        class ErrorOnResponse(val message: FailResponse?) : ResponseContentSealed()
        class loading : ResponseContentSealed()
        object Empty : ResponseContentSealed()
    }

    companion object{

        var type:Int=1
    }


    fun getContent(type: String) {

        viewModelScope.launch(dispatcherProvider.IO) {
            contentResponseLive.value = ResponseContentSealed.loading()
            when (val response = apiRepo.getContent(type)) {
                is RetrofitResource.Error -> contentResponseLive.value =
                    ResponseContentSealed.ErrorOnResponse(response.failResponse)
                is RetrofitResource.Success -> contentResponseLive.value =
                    ResponseContentSealed.Success(response.data!!)
            }
        }
    }

}