package com.actorpay.merchant.repositories.retrofitrepository.repo

import androidx.paging.PagingData
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.*
import com.actorpay.merchant.repositories.retrofitrepository.models.commission.CommissionParams
import com.actorpay.merchant.repositories.retrofitrepository.models.commission.CommissionResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.order.BeanViewAllOrder
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderParams
import com.actorpay.merchant.repositories.retrofitrepository.models.order.UpdateOrderStatus
import com.actorpay.merchant.repositories.retrofitrepository.models.ordernote.OrderNote
import com.actorpay.merchant.repositories.retrofitrepository.models.outlet.GetOutletById
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionDetails

import com.actorpay.merchant.repositories.retrofitrepository.models.products.addNewProduct.AddNewProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.GetAllCategoriesDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct.DeleteProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductById.success.GetProductDataById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.GetProductListResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.MerchantSettingsDTO
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.*
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.*
import com.actorpay.merchant.repositories.retrofitrepository.models.taxation.GetCurrentTaxDetail
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.ui.outlet.response.*
import com.actorpay.merchant.repositories.retrofitrepository.models.content.ContentResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.content.FAQResponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.ProductResponse
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListParams
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListResponse
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeSingleResponse
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.SendMessageParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    suspend fun saveProfile(email:String, shopAddress:String, fullAddress:String, businessName:String, licenceNumber:String, id:String, merchantSettingsDTOS:MutableList<MerchantSettingsDTO>, token: String):RetrofitResource<SuccessResponse>

    suspend fun getContent(type:String):RetrofitResource<ContentResponse>

    suspend fun addProduct(token:String, product:RequestBody, poduct_pic: MultipartBody.Part):RetrofitResource<AddNewProductResponse>

    suspend fun updateProduct(token: String, id: String, product: RequestBody, poduct_pic: MultipartBody.Part):RetrofitResource<ProductResponse>

    suspend fun getProduct(token:String, productId:String):RetrofitResource<GetProductDataById>

    suspend fun deleteProduct(token:String, productId:String):RetrofitResource<DeleteProductResponse>

    suspend fun getAllTaxDetail(token:String):RetrofitResource<GetCurrentTaxDetail>

    suspend fun getAllCategoriesDetail(token:String):RetrofitResource<GetAllCategoriesDetails>

    suspend fun getProductList(token: String, pageNo: Int, pageSize: Int, sortBy: String, asc: Boolean, data: ProductPram): RetrofitResource<GetProductListResponse>

    suspend fun getSubCatDataDetailsList(token: String, catId: String): RetrofitResource<GetSubCatDataDetails>

    suspend fun getById( id: String,token: String,): RetrofitResource<GetUserById>

    suspend fun updateStatus(token: String, body: UpdateStatus, status: String, orderNo: String): RetrofitResource<UpdateOrderStatus>

    suspend fun getAllOrder(token: String, orderParam: OrderParams, pageNo: String, pageSize: String):RetrofitResource<BeanViewAllOrder>
    suspend fun getAllCountries():RetrofitResource<CountryResponse>

    suspend fun getFAQ():RetrofitResource<FAQResponse>

    suspend fun createOutlet(token: String,param: OutletParam): RetrofitResource<AddOutletResponse>

    suspend fun updateOutlet(token: String,param: UpdateParam): RetrofitResource<UpdateOutlet>

    suspend fun addSubMerchant(token: String,param: AddSubMerchantParam): RetrofitResource<CreateSubMerchant>

    suspend fun updateSubMerchant(token: String,param: UpdateSubMerchantParam): RetrofitResource<UpdateSubMerchant>

    suspend fun getOutlet(token: String, pageNo: String, body: EmptyBody): RetrofitResource<GetOutlet>


    suspend fun getOutletById(token: String,id: String): RetrofitResource<GetOutletById>

    suspend fun getSubMerchants(token: String, pageNo: String, body: EMPTYJSON): RetrofitResource<GetAllSubMerchant>
    suspend fun getMerchantById(token: String, id: String): RetrofitResource<GetMerchantById>

    suspend fun deleteOutlet(token: String, body: DeleteOutParam): RetrofitResource<DeleteOutlet>

    suspend fun deleteMerchant(token: String, body: DeleteOutParam): RetrofitResource<DeleteSubMerchant>

    suspend fun getRoles(token: String, pageNo: Int, body: GetRolesParams): RetrofitResource<RolesResponse>

    suspend fun getRoleById(token: String, id: String): RetrofitResource<SingleRoleResponse>

    suspend fun addRole(token: String, sendRolesParmas: SendRolesParmas): RetrofitResource<SuccessResponse>

    suspend fun updateRole(token: String, sendRolesParmas: SendRolesParmas): RetrofitResource<SuccessResponse>

    suspend fun deleteRole(token: String, deleteRolesParams: DeleteRolesParams): RetrofitResource<SuccessResponse>
    suspend fun addNote(token: String, note: AddNoteParam): RetrofitResource<OrderNote>

    suspend fun getAllScreens(token: String): RetrofitResource<ScreenResponse>

    suspend fun getCommissions(token: String, pageNo: Int, body: CommissionParams): RetrofitResource<CommissionResponse>

    suspend fun getPermissions(token: String): RetrofitResource<PermissionDetails>

    suspend fun getAllDisputes(token: String,pageNo:Int,pageSize:Int,disputeListParams: DisputeListParams):RetrofitResource<DisputeListResponse>

    suspend fun getDispute(token: String,disputeId:String):RetrofitResource<DisputeSingleResponse>

    suspend fun sendDisputeMessage(token: String,sendMessageParams: SendMessageParams):RetrofitResource<SuccessResponse>
    suspend fun getProductsWithPaging(viewmodelscope: CoroutineScope, token: String, productParams: ProductPram): Flow<PagingData<Item>>
}

