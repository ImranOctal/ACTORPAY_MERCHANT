package com.actorpay.merchant.ui.manageOrder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.AddNoteParam
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.DeleteRolesParams
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OrderDetailViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository, ) : AndroidViewModel(
    Application()
)
{
    val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)
    fun addNote(note: String, orderNo: String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            val body= AddNoteParam(note,orderNo)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.addNote(token,body)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }


}