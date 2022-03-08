package com.actorpay.merchant.ui.more




import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse

import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource

import com.actorpay.merchant.repositories.retrofitrepository.models.content.FAQResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MoreViewModel(val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository) : AndroidViewModel(Application()){
    val faqList= mutableListOf<FAQResponseData>()
    val miscResponseLive = MutableStateFlow<ResponseMiscSealed>(ResponseMiscSealed.Empty)


    sealed class ResponseMiscSealed {
        class Success(val response: Any) : ResponseMiscSealed()
        class ErrorOnResponse(val failResponse: FailResponse?) : ResponseMiscSealed()
        class loading : ResponseMiscSealed()
        object Empty : ResponseMiscSealed()
    }

    override fun <T : Application?> getApplication(): T {
        return super.getApplication()

    }


    fun getFAQ(){
        viewModelScope.launch(dispatcherProvider.IO){
            miscResponseLive.value= ResponseMiscSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when(val response=apiRepo.getFAQ()){
                    is RetrofitResource.Error -> miscResponseLive.value = ResponseMiscSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> {
                        faqList.clear()
                        faqList.addAll(response.data!!.data)
                        miscResponseLive.value = ResponseMiscSealed.Success(response.data)
                    }
                }
            }
        }
    }

}


