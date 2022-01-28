package com.actorpay.merchant.ui.roles.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.GetRolesParams
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RoleItem
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.ScreenAccessPermission
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.SendRolesParmas
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenItem
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.ui.outlet.response.EmptyBody
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RolesDetailsViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository, ) : AndroidViewModel(
    Application()
)
 {
     val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)

     var id=""
     var allScreens= mutableListOf<ScreenItem>()
     val screenPermissionsList= mutableListOf<ScreenAccessPermission>()

     fun getRoleById() {

         viewModelScope.launch(dispatcherProvider.IO) {
             responseLive.value = ResponseSealed.Loading(true)

             methodRepo.dataStore.getAccessToken().collect { token ->
                 when (val response = apiRepo.getRoleById(token,id)) {
                     is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                     is RetrofitResource.Success -> responseLive.value =
                         ResponseSealed.Success(response.data!!)
                 }
             }
         }
     }

     fun addRole(sendRolesParmas: SendRolesParmas) {

         viewModelScope.launch(dispatcherProvider.IO) {
             responseLive.value = ResponseSealed.Loading(true)

             methodRepo.dataStore.getAccessToken().collect { token ->
                 when (val response = apiRepo.addRole(token,sendRolesParmas)) {
                     is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                     is RetrofitResource.Success -> responseLive.value =
                         ResponseSealed.Success(response.data!!)
                 }
             }
         }
     }

     fun updateRole(sendRolesParmas: SendRolesParmas) {

         viewModelScope.launch(dispatcherProvider.IO) {
             responseLive.value = ResponseSealed.Loading(true)

             methodRepo.dataStore.getAccessToken().collect { token ->
                 when (val response = apiRepo.updateRole(token,sendRolesParmas)) {
                     is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                     is RetrofitResource.Success -> responseLive.value =
                         ResponseSealed.Success(response.data!!)
                 }
             }
         }
     }



 }