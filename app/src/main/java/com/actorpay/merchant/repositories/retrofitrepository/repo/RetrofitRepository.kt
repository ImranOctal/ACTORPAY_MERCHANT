package com.actorpay.merchant.repositories.retrofitrepository.repo

import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.*
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.order.BeanViewAllOrder
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderParams
import com.actorpay.merchant.repositories.retrofitrepository.models.order.UpdateOrderStatus

import com.actorpay.merchant.repositories.retrofitrepository.models.products.addNewProduct.AddNewProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.GetAllCategoriesDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct.DeleteProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductById.success.GetProductDataById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.GetProductListResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.taxation.GetCurrentTaxDetail
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.octal.actorpay.repositories.retrofitrepository.models.content.ContentResponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.FAQResponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */


interface RetrofitRepository {

    suspend fun LoginNow(loginDetail: LoginParams): RetrofitResource<LoginResponses>

    suspend fun ForgetPassword(forgetPasswordParams: ForgetPasswordParams):RetrofitResource<ForgetPasswordResponses>

    suspend fun SignUpNow(signUpParams: SignUpParams): RetrofitResource<SuccessResponse>

    suspend fun changePassword(miscChangePasswordParams: ChangePasswordParams, token: String):RetrofitResource<SuccessResponse>

    suspend fun getProfile(id:String,token:String):RetrofitResource<GetUserById>

   // suspend fun genrateTokenAgain(token:String):RetrofitResource<ProfileReesponse>

    suspend fun saveProfile(email:String,shopAddress:String,fullAddress:String,businessName:String,licenceNumber:String,id:String,token: String):RetrofitResource<SuccessResponse>

    suspend fun getContent(type:String):RetrofitResource<ContentResponse>

    suspend fun addProduct(token:String, product:RequestBody, poduct_pic: MultipartBody.Part):RetrofitResource<AddNewProductResponse>

    suspend fun updateProduct(token: String, id: String, product: RequestBody, poduct_pic: MultipartBody.Part):RetrofitResource<ProductResponse>

    suspend fun getProduct(token:String, productId:String):RetrofitResource<GetProductDataById>

    suspend fun deleteProduct(token:String, productId:String):RetrofitResource<DeleteProductResponse>

    suspend fun getAllTaxDetail(token:String):RetrofitResource<GetCurrentTaxDetail>

    suspend fun getAllCategoriesDetail(token:String):RetrofitResource<GetAllCategoriesDetails>

    suspend fun getProductList(token: String, pageNo: String, pageSize: String, sortBy: String, asc: Boolean,data:JSONObject): RetrofitResource<GetProductListResponse>

    suspend fun getSubCatDataDetailsList(token: String, pageNo: String): RetrofitResource<GetSubCatDataDetails>

    suspend fun getById( id: String,token: String,): RetrofitResource<GetUserById>

    suspend fun updateStatus(token: String,orderNo:String,status: String,): RetrofitResource<UpdateOrderStatus>

    suspend fun getAllOrder(token: String, orderParam: OrderParams, pageNo: String, pageSize: String):RetrofitResource<BeanViewAllOrder>

    suspend fun getFAQ():RetrofitResource<FAQResponse>
}