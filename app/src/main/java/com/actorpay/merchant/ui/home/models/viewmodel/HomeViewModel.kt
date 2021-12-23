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
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class HomeViewModel(
    val dispatcherProvider: CoroutineContextProvider,
    val methodRepo: MethodsRepo,
    val apiRepo: RetrofitRepository,
) : AndroidViewModel(
    Application()
) {

    val homeResponseLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseHomeSealed>(HomeSealedClasses.Companion.ResponseHomeSealed.Empty)
    val getProductByIDLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseGetProductSealed>(HomeSealedClasses.Companion.ResponseGetProductSealed.Empty)
    val editProductByIDLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseEditProductSealed>(HomeSealedClasses.Companion.ResponseEditProductSealed.Empty)
    val addProductByIDLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseAddProductSealed>(HomeSealedClasses.Companion.ResponseAddProductSealed.Empty)
    val productListLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseProductListSealed>(HomeSealedClasses.Companion.ResponseProductListSealed.Empty)
    val deleteproductLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseDeleteSealed>(HomeSealedClasses.Companion.ResponseDeleteSealed.Empty)
    val changePasswordLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseChangePasswordSealed>(HomeSealedClasses.Companion.ResponseChangePasswordSealed.Empty)
    val genrateNewTokenLive = MutableStateFlow<HomeSealedClasses.Companion.ResponseGenrateNewTokenSealed>(HomeSealedClasses.Companion.ResponseGenrateNewTokenSealed.Empty)
    val CatogryLive = MutableStateFlow<HomeSealedClasses.Companion.CatogrySealed>(HomeSealedClasses.Companion.CatogrySealed.Empty)
    val subCatLive = MutableStateFlow<HomeSealedClasses.Companion.SubCatSealed>(HomeSealedClasses.Companion.SubCatSealed.Empty)
    val taxListLive = MutableStateFlow<HomeSealedClasses.Companion.TaxationSealed>(HomeSealedClasses.Companion.TaxationSealed.Empty)




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

    fun addProduct(product: String, file: File) {
        var r1: RequestBody? = null
        var f1: MultipartBody.Part? = null
        r1 = file.asRequestBody("/*".toMediaTypeOrNull())
        f1 =
            MultipartBody.Part.createFormData(
                "file",
                "${System.currentTimeMillis()}.jpg",
                r1
            )
        val prod = product.toRequestBody()
        viewModelScope.launch(dispatcherProvider.IO) {
            addProductByIDLive.value = HomeSealedClasses.Companion.ResponseAddProductSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.addProduct(token, prod, f1)) {
                    is RetrofitResource.Error -> addProductByIDLive.value =
                        HomeSealedClasses.Companion.ResponseAddProductSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> addProductByIDLive.value =
                        HomeSealedClasses.Companion.ResponseAddProductSealed.Success(response.data!!)
                }
            }

        }
    }

    fun updateProduct(productId: String, product: String, file: File) {
        var r1: RequestBody? = null
        var f1: MultipartBody.Part? = null
        r1 = file.asRequestBody("/*".toMediaTypeOrNull())
        f1 =
            MultipartBody.Part.createFormData(
                "profile_img",
                "${System.currentTimeMillis()}.jpg",
                r1
            )
        val prod = product.toRequestBody()
        viewModelScope.launch(dispatcherProvider.IO) {
            editProductByIDLive.value = HomeSealedClasses.Companion.ResponseEditProductSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.updateProduct(token, productId, prod, f1)) {
                    is RetrofitResource.Error -> editProductByIDLive.value =
                        HomeSealedClasses.Companion.ResponseEditProductSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> editProductByIDLive.value =
                        HomeSealedClasses.Companion.ResponseEditProductSealed.Success(response.data!!)
                }
            }

        }
    }

    fun getProduct(productId: String) {

        viewModelScope.launch(dispatcherProvider.IO) {
            getProductByIDLive.value = HomeSealedClasses.Companion.ResponseGetProductSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getProduct(token, productId)) {
                    is RetrofitResource.Error -> getProductByIDLive.value =
                        HomeSealedClasses.Companion.ResponseGetProductSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> getProductByIDLive.value =
                        HomeSealedClasses.Companion.ResponseGetProductSealed.Success(response.data!!)
                }
            }

        }
    }

    fun deleteProduct(productId: String) {

        viewModelScope.launch(dispatcherProvider.IO) {
            deleteproductLive.value = HomeSealedClasses.Companion.ResponseDeleteSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.deleteProduct(token, productId)) {
                    is RetrofitResource.Error -> deleteproductLive.value =
                        HomeSealedClasses.Companion.ResponseDeleteSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> deleteproductLive.value =
                        HomeSealedClasses.Companion.ResponseDeleteSealed.Success(response.data!!)
                }
            }

        }
    }

    fun getProductList(pageno: String,data:JSONObject) {
        viewModelScope.launch(dispatcherProvider.IO) {
            productListLive.value = HomeSealedClasses.Companion.ResponseProductListSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getProductList(token, pageno,"100","createdAt",asc = true,data)) {
                    is RetrofitResource.Error -> productListLive.value =
                       HomeSealedClasses.Companion.ResponseProductListSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> productListLive.value =
                        HomeSealedClasses.Companion.ResponseProductListSealed.Success(response.data!!)
                }
            }

        }
    }
    fun getCatogrys() {
        viewModelScope.launch(dispatcherProvider.IO) {
            CatogryLive.value = HomeSealedClasses.Companion.CatogrySealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getAllCategoriesDetail(token)) {
                    is RetrofitResource.Error -> CatogryLive.value =
                        HomeSealedClasses.Companion.CatogrySealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> CatogryLive.value =
                        HomeSealedClasses.Companion.CatogrySealed.Success(response.data!!)
                }
            }

        }

    }

    fun getSubCatDetalis() {
        viewModelScope.launch(dispatcherProvider.IO) {
            subCatLive.value = HomeSealedClasses.Companion.SubCatSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getSubCatDataDetailsList(token,"0")) {
                    is RetrofitResource.Error -> subCatLive.value =
                        HomeSealedClasses.Companion.SubCatSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> subCatLive.value =
                        HomeSealedClasses.Companion.SubCatSealed.Success(response.data!!)
                }
            }

        }

    }

    fun getTaxationDetails() {
        viewModelScope.launch(dispatcherProvider.IO) {
            taxListLive.value = HomeSealedClasses.Companion.TaxationSealed.loading()
            methodRepo.dataStore.getAccessToken().collect { token ->
                when (val response = apiRepo.getAllTaxDetail(token)) {
                    is RetrofitResource.Error -> taxListLive.value =
                        HomeSealedClasses.Companion.TaxationSealed.ErrorOnResponse(response.failResponse)
                    is RetrofitResource.Success -> taxListLive.value =
                        HomeSealedClasses.Companion.TaxationSealed.Success(response.data!!)
                }
            }

        }

    }

}