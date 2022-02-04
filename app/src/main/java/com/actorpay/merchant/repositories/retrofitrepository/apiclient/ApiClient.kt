package com.actorpay.merchant.retrofitrepository.apiclient

import android.os.Build.ID
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.*
import com.actorpay.merchant.repositories.retrofitrepository.models.commission.CommissionParams
import com.actorpay.merchant.repositories.retrofitrepository.models.commission.CommissionResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.home.ChangePasswordParams
import com.actorpay.merchant.repositories.retrofitrepository.models.order.BeanViewAllOrder
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderParams
import com.actorpay.merchant.repositories.retrofitrepository.models.order.UpdateOrderStatus
import com.actorpay.merchant.repositories.retrofitrepository.models.ordernote.OrderNote
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.addNewProduct.AddNewProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.GetAllCategoriesDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct.DeleteProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductById.success.GetProductDataById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.GetProductListResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileParams
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileReesponse
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.*
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.*
import com.actorpay.merchant.repositories.retrofitrepository.models.taxation.GetCurrentTaxDetail
import com.actorpay.merchant.ui.outlet.response.*
import com.octal.actorpay.repositories.AppConstance.AppConstance
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.ADD_PRODUCT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.ADD_ROLE
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.ADD_SUBMERCHANT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.AUTH
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.Add_Note
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.CATEGORIES_URL
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.CREATE_OUTLET
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.DELETE_OUTLET
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.DELETE_ROLE
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.DELETE_SUBMERCHANT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.DELET_PRODUCT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_ALL_COMMISSIONS
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_ALL_ORDER
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_ALL_SCREENS
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_CONTENT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_COUNTRIES
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_FAQ
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_MERCHANT_BY_ID
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_OUTLET
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_PERMISSION
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_ROLES
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_SINGLE_ROLE
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GET_SUBMERCHANTS
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.IDS
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.ID_VAR
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.PRODUCT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.PRODUCT_ID
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.PRODUCT_LIST
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SUB_CAT_URL
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.TAX_URL
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.TOKEN_ATTRIBUTE
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.TYPE
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.UPDATE_OUTLET
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.UPDATE_ROLE
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.UPDATE_STATUS
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.UPDATE_SUBMERCHANT
import com.octal.actorpay.repositories.retrofitrepository.models.content.ContentResponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.FAQResponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiClient {


    @POST(AppConstance.LOGIN)
    suspend fun LoginNow(@Body loginDetail: LoginParams): Response<LoginResponses>

    @POST(AppConstance.FORGETPASSWORD)
    suspend fun forgetPassword(@Body forgetPasswordParams: ForgetPasswordParams): Response<ForgetPasswordResponses>

    @POST(AppConstance.SIGNUP)
    suspend fun SignUpNow(@Body signUpParams: SignUpParams): Response<SuccessResponse>

    @POST(AppConstance.CHANGE_PASSWORD)
    suspend fun changePassword(
        @Header(AUTH) token: String,
        @Body changePasswordParams: ChangePasswordParams
    ): Response<SuccessResponse>

    @GET(AppConstance.GET_PROFILE + IDS)
    suspend fun getProfile(
        @Header(AUTH) token: String,
        @Path(ID_VAR) id: String
    ): Response<GetUserById>


    @GET(AppConstance.GET_BY_ID + IDS)
    suspend fun getById(
        @Header(AUTH) token: String,
        @Path(ID_VAR) id: String
    ): Response<GetUserById>

    @GET(AppConstance.GENRATE_TOKEN_AGAIN)
    suspend fun getGenratedToken(@Query(TOKEN_ATTRIBUTE) token: String): Response<ProfileReesponse>

    @PUT(AppConstance.UPDATE_PROFILE)
    suspend fun saveProfile(
        @Header(AUTH) token: String,
        @Body profileParams: ProfileParams
    ): Response<SuccessResponse>


    @GET(GET_CONTENT)
    suspend fun getContent(
        @Query(TYPE) type: String
    ): Response<ContentResponse>

    @Multipart
    @POST(ADD_PRODUCT)
    suspend fun addProduct(
        @Header(AUTH) token: String,
        @Part(PRODUCT) product: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<AddNewProductResponse>


    @Multipart
    @PUT(ADD_PRODUCT + IDS)
    suspend fun updateProduct(
        @Header(AUTH) token: String,
        @Path(ID_VAR) id: String,
        @Part(PRODUCT) product: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ProductResponse>

    @GET(ADD_PRODUCT + IDS)
    suspend fun getProduct(
        @Header(AUTH) token: String,
        @Path(ID_VAR) id: String
    ): Response<GetProductDataById>

    @POST(PRODUCT_LIST)
    suspend fun getProductList(
        @Header(AUTH) token: String,
        @Query(AppConstance.PAGE_NO) pageNo: String,
        @Query(AppConstance.PAGE_SIZE) pageSize: String,
        @Query(AppConstance.SORT_BY) sortBy: String,
        @Query(AppConstance.ASCECNDING) asc: Boolean,
        @Body data: ProductPram
    ): Response<GetProductListResponse>

    @DELETE(DELET_PRODUCT)
    suspend fun deleteProduct(
        @Header(AUTH) token: String,
        @Query(PRODUCT_ID) id: String
    ): Response<DeleteProductResponse>

    @GET(TAX_URL)
    suspend fun getAllTaxDataApi(
        @Header(AUTH) token: String,
        @Query(AppConstance.SORT_BY) sortBy: String = "hsnCode",
        @Query(AppConstance.ASCECNDING) asc: Boolean = true,
        @Query(AppConstance.isActive) isActive: Boolean = true
    ): Response<GetCurrentTaxDetail>


    @GET(CATEGORIES_URL)
    suspend fun getAllCategoriesDataApi(
        @Header(AUTH) token: String,
        @Query(AppConstance.PAGE_NO) pageNo: String="0",
        @Query(AppConstance.PAGE_SIZE) pageSize: String = "100",
        @Query(AppConstance.SORT_BY) sortBy: String = "name",
        @Query(AppConstance.ASCECNDING) asc: Boolean = true
    ): Response<GetAllCategoriesDetails>


    @GET(SUB_CAT_URL)
    suspend fun getSubCategoryList(
        @Header(AUTH) token: String,
        @Query(AppConstance.PAGE_NO) pageNo: String,
        @Query(AppConstance.PAGE_SIZE) pageSize: String = "100",
        @Query(AppConstance.SORT_BY) sortBy: String = "name",
        @Query(AppConstance.ASCECNDING) asc: Boolean = true
    ): Response<GetSubCatDataDetails>


    @POST(GET_ALL_ORDER)
    suspend fun getAllOrders(
        @Header(AUTH) token: String,
        @Query(AppConstance.PAGE_NO) pageNo: String,
        @Query(AppConstance.PAGE_SIZE) pageSize: String = "10",
        @Body orderParam: OrderParams
    ): Response<BeanViewAllOrder>

    @PUT(UPDATE_STATUS)
    suspend fun updateStatus(
        @Header(AUTH) token: String,
        @Query(AppConstance.STATUS) status: String,
        @Query(AppConstance.ORDERNO) orderNo: String,
        @Body statusParam: UpdateStatus
    ): Response<UpdateOrderStatus>

    @GET(GET_FAQ)
    suspend fun getFAQ(
    ): Response<FAQResponse>

    @GET(GET_COUNTRIES)
    suspend fun getAllCountries(
    ): Response<CountryResponse>


    @POST(CREATE_OUTLET)
    suspend fun createOutlet(
        @Header(AUTH) token: String,
        @Body param: OutletParam
    ): Response<AddOutletResponse>


  @POST(GET_OUTLET)
    suspend fun getOutlet(
        @Header(AUTH) token: String,
        @Query(AppConstance.PAGE_NO) pageNo: String,
        @Body empty: EmptyBody,
        @Query(AppConstance.PAGE_SIZE) pageSize: String = "10",

  ): Response<GetOutlet>

  @PUT(UPDATE_OUTLET)
    suspend fun updateOutlet(
        @Header(AUTH) token: String,
        @Body param: UpdateParam,

  ): Response<UpdateOutlet>


  @HTTP(method = "DELETE", path = DELETE_OUTLET,hasBody = true)
    suspend fun deleteOutlet(
        @Header(AUTH) token: String,
        @Body param: DeleteOutParam,
  ): Response<DeleteOutlet>

    @Headers("Content-Type:application/json")
    @POST(GET_ROLES)
    suspend fun getAllRoles(
        @Header(AUTH) token: String,
        @Query(AppConstance.PAGE_NO) pageNo: Int,
        @Body param: GetRolesParams,
        @Query(AppConstance.ASCECNDING) asc: Boolean=true,
        @Query(AppConstance.SORT_BY) sortBy: String="name",
        @Query(AppConstance.PAGE_SIZE) pageSize: String = "100",

        ): Response<RolesResponse>



    @POST(GET_SUBMERCHANTS)
    suspend fun getSubMerchants(
        @Header(AUTH) token: String,
        @Query(AppConstance.PAGE_NO) pageNo: String,
        @Body empty: EMPTYJSON,
        @Query(AppConstance.PAGE_SIZE) pageSize: String = "100",
        @Query(AppConstance.ASCECNDING) asc: Boolean = true,

    ):Response<GetAllSubMerchant>



    @POST(ADD_SUBMERCHANT)
    suspend fun addSubMerchant(
        @Header(AUTH) token: String,
        @Body param: AddSubMerchantParam,
        ): Response<CreateSubMerchant>


    @HTTP(method = "DELETE", path = DELETE_SUBMERCHANT,hasBody = true)
    suspend fun deleteSubMerchant(
        @Header(AUTH) token: String,
        @Body param: DeleteOutParam,
    ): Response<DeleteSubMerchant>


    @GET(GET_MERCHANT_BY_ID+IDS)
    suspend fun getMerchantById(
        @Header(AUTH) token: String,
        @Path(ID_VAR) id: String,
    ): Response<GetMerchantById>

    @PUT(UPDATE_SUBMERCHANT)
    suspend fun updateSubMerchant(
        @Header(AUTH) token: String,
        @Body param: UpdateSubMerchantParam,
        ): Response<UpdateSubMerchant>

    @GET(GET_SINGLE_ROLE)
    suspend fun getSingleRole(
        @Header(AUTH) token: String,
        @Query(AppConstance.ID_VAR) id: String,
    ): Response<SingleRoleResponse>

    @POST(ADD_ROLE)
    suspend fun addRole(
        @Header(AUTH) token: String,
        @Body sendRolesParmas: SendRolesParmas,
    ): Response<SuccessResponse>

    @PUT(UPDATE_ROLE)
    suspend fun updateRole(
        @Header(AUTH) token: String,
        @Body sendRolesParmas: SendRolesParmas
    ): Response<SuccessResponse>

    @Headers("Content-Type:application/json")
    @HTTP(method = "DELETE", path = DELETE_ROLE, hasBody = true)
    suspend fun deleteRole(
        @Header(AUTH) token: String,
        @Body  deleteRolesParams: DeleteRolesParams
    ): Response<SuccessResponse>

    @GET(GET_ALL_SCREENS)
    suspend fun getAllScreens(
        @Header(AUTH) token: String,
    ): Response<ScreenResponse>

    @POST(GET_ALL_COMMISSIONS)
    suspend fun getAllCommission(
        @Header(AUTH) token: String,
        @Query(AppConstance.PAGE_NO) pageNo: Int,
        @Body  commissionParams: CommissionParams,
        @Query(AppConstance.PAGE_SIZE) pageSize: Int = 20,
        @Query(AppConstance.ASCECNDING) asc: Boolean = true,
    ): Response<CommissionResponse>


    @POST(Add_Note)
    suspend fun addNote(
        @Header(AUTH) token: String,
        @Body  addNoteParam: AddNoteParam,
    ): Response<OrderNote>


    @GET(GET_PERMISSION)
    suspend fun getPermission(
        @Header(AUTH) token: String,
    ): Response<PermissionDetails>
}