package com.actorpay.merchant.ui.roles

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.DeleteRolesParams
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.GetRolesParams
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RoleItem
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.ui.outlet.response.EmptyBody
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RolesViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository, ) : AndroidViewModel(Application())
 {
     val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)
     var pageNo=0
     val getRolesParams=GetRolesParams("","")
     val rolesList= mutableListOf<RoleItem>()

     fun getAllRoles() {
         viewModelScope.launch(dispatcherProvider.IO) {
             responseLive.value = ResponseSealed.Loading(true)
             methodRepo.dataStore.getAccessToken().collect { token ->
                 when (val response = apiRepo.getRoles(token,pageNo,getRolesParams)) {
                     is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                     is RetrofitResource.Success -> responseLive.value =
                         ResponseSealed.Success(response.data!!)
                 }
             }
         }
     }
     fun deleteRole(id:String) {

         viewModelScope.launch(dispatcherProvider.IO) {
             responseLive.value = ResponseSealed.Loading(true)

             val deleteRolesParams= DeleteRolesParams(mutableListOf(id))

             methodRepo.dataStore.getAccessToken().collect { token ->
                 when (val response = apiRepo.deleteRole(token,deleteRolesParams)) {
                     is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                     is RetrofitResource.Success -> responseLive.value =
                         ResponseSealed.Success(response.data!!)
                 }
             }
         }
     }

     fun getAllScreen() {

         viewModelScope.launch(dispatcherProvider.IO) {
             responseLive.value = ResponseSealed.Loading(true)

             methodRepo.dataStore.getAccessToken().collect { token ->
                 when (val response = apiRepo.getAllScreens(token)) {
                     is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                     is RetrofitResource.Success -> responseLive.value =
                         ResponseSealed.Success(response.data!!)
                 }
             }
         }
     }

 }