package com.actorpay.merchant.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.ProductPram
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.UpdateStatus
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderParams
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File




class HomeViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository,
) : AndroidViewModel(
    Application()
) {

    val homeResponseLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseHomeSealed>(HomeSealedClasses.Companion.ResponseHomeSealed.Empty)
    val changePasswordLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseChangePasswordSealed>(HomeSealedClasses.Companion.ResponseChangePasswordSealed.Empty)

    fun changePassword(oldPassword: String, newPassword: String) {
        val body = ChangePasswordParams(oldPassword, newPassword, newPassword)
        viewModelScope.launch(dispatcherProvider.IO) {
            changePasswordLive.value = HomeSealedClasses.Companion.ResponseChangePasswordSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.changePassword(body, token)) {
                    is RetrofitResource.Error -> changePasswordLive.value =
                        HomeSealedClasses.Companion.ResponseChangePasswordSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> changePasswordLive.value =
                        HomeSealedClasses.Companion.ResponseChangePasswordSealed.Success(response.data!!)
                }
            }

        }
    }

    fun getById() {
        viewModelScope.launch(dispatcherProvider.IO) {
            homeResponseLive.value = HomeSealedClasses.Companion.ResponseHomeSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                methodRepo.dataStore.getUserId().collect { userId ->
                    when (val response = apiRepo.getById(token, userId)) {
                        is RetrofitResource.Error -> homeResponseLive.value = HomeSealedClasses.Companion.ResponseHomeSealed.ErrorOnResponse(response.failResponse)
                        is RetrofitResource.Success -> homeResponseLive.value =
                            HomeSealedClasses.Companion.ResponseHomeSealed.Success(response.data!!)
                    }
                }

            }

        }
    }

    fun getPermissions() {
        viewModelScope.launch(dispatcherProvider.IO) {
            homeResponseLive.value = HomeSealedClasses.Companion.ResponseHomeSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getPermissions(token)) {
                    is RetrofitResource.Error -> homeResponseLive.value = HomeSealedClasses.Companion.ResponseHomeSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> homeResponseLive.value = HomeSealedClasses.Companion.ResponseHomeSealed.Success(response.data!!)
                }
            }
        }
    }
}