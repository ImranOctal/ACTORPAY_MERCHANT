package com.actorpay.merchant.ui.manageProduct.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.ProductPram

import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource

import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductViewModel (
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository, ) : AndroidViewModel(Application())

{
     val responseLive = MutableStateFlow<ResponseSealed>(ResponseSealed.Empty)

    var pageNo=0
     var pageSize=10

    fun getCategory() {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getAllCategoriesDetail(token)) {
                    is RetrofitResource.Error -> responseLive.value = ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value = ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

    fun getSubCatDetalis(catId: String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getSubCatDataDetailsList(token,catId)) {
                    is RetrofitResource.Error -> responseLive.value =
                        ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.deleteProduct(token, productId)) {
                    is RetrofitResource.Error -> responseLive.value =
                        ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

    fun addProduct(product: String, file: File) {
        var r1: RequestBody? = null
        var f1: MultipartBody.Part? = null
        r1 = file.asRequestBody("/*".toMediaTypeOrNull())
        f1 = MultipartBody.Part.createFormData("file", "${System.currentTimeMillis()}.jpg", r1)
        val prod = product.toRequestBody("application/json".toMediaTypeOrNull())
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.addProduct(token, prod, f1)) {
                    is RetrofitResource.Error -> responseLive.value =
                        ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }
    fun getProduct(productId: String) {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getProduct(token, productId)) {
                    is RetrofitResource.Error -> responseLive.value =
                        ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }
    }

    fun updateProduct(product: String, file: File, isSelect: String) {
        var r1: RequestBody? = null
        var f1: MultipartBody.Part? = null
        r1 = file.asRequestBody("/*".toMediaTypeOrNull())
        val prod = product.toRequestBody("application/json".toMediaTypeOrNull())
        f1 = MultipartBody.Part.createFormData("file", "${System.currentTimeMillis()}.jpg", r1!!)
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                methodRepo.dataStore.getUserId().collect { userId ->
                    when (val response = apiRepo.updateProduct(token,userId, prod, f1)) {
                        is RetrofitResource.Error -> responseLive.value =
                            ResponseSealed.ErrorOnResponse(response.failResponse)
                        is RetrofitResource.Success -> responseLive.value =
                            ResponseSealed.Success(response.data!!)
                    }
                }
            }
        }
    }
    fun getTaxationDetails() {
        viewModelScope.launch(dispatcherProvider.IO) {
            responseLive.value = ResponseSealed.Loading(true)
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getAllTaxDetail(token)) {
                    is RetrofitResource.Error -> responseLive.value =
                        ResponseSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> responseLive.value =
                        ResponseSealed.Success(response.data!!)
                }
            }
        }

    }

    suspend fun getProductsWithPaging(token:String, name: String,categoryNam:String,status:Boolean,subCategoryName:String,merchantId:String)=
        apiRepo.getProductsWithPaging(
            viewModelScope, token,
            ProductPram(name,categoryNam,status,subCategoryName,merchantId)
        )
}