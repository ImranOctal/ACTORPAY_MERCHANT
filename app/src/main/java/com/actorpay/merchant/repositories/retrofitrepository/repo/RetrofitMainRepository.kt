package com.actorpay.merchant.repositories.retrofitrepository.repo
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.actorpay.merchant.R
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
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
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.MerchantSettingsDTO
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileParams
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.*
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.*
import com.actorpay.merchant.repositories.retrofitrepository.models.taxation.GetCurrentTaxDetail
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.retrofitrepository.apiclient.ApiClient
import com.actorpay.merchant.ui.outlet.response.*
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.B_Token
import com.actorpay.merchant.repositories.retrofitrepository.models.outlet.GetOutletById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.ui.paging.PagingDataSource
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
import org.json.JSONObject

class RetrofitMainRepository constructor(var context: Context, private var apiClient: ApiClient) :
    RetrofitRepository {


    override suspend fun LoginNow(loginDetail: LoginParams): RetrofitResource<LoginResponses> {
        try {
            val loginData = apiClient.LoginNow(loginDetail)
            val result = loginData.body()
            if (loginData.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (loginData.errorBody() != null) {
                    return RetrofitResource.Error(handleError(loginData.code(),loginData.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun ForgetPassword(forgetPasswordParams: ForgetPasswordParams): RetrofitResource<ForgetPasswordResponses> {
        try {
            val forgetData = apiClient.forgetPassword(forgetPasswordParams)
            val result = forgetData.body()
            if (forgetData.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (forgetData.errorBody() != null) {
                    return RetrofitResource.Error(handleError(forgetData.code(),forgetData.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun SignUpNow(signUpParams: SignUpParams): RetrofitResource<SuccessResponse> {
        try {
            val loginData = apiClient.SignUpNow(signUpParams)
            val result = loginData.body()
            if (loginData.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (loginData.errorBody() != null) {
                    return RetrofitResource.Error(handleError(loginData.code(),loginData.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun changePassword(
        miscChangePasswordParams: ChangePasswordParams,
        token: String
    ): RetrofitResource<SuccessResponse> {
        try {
            val data =
                apiClient.changePassword(AppConstance.B_Token + token, miscChangePasswordParams)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getProfile(id: String, token: String): RetrofitResource<GetUserById> {
        try {
            val data = apiClient.getProfile(AppConstance.B_Token + token, id)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun saveProfile(email: String, shopAddress: String, fullAddress: String, businessName: String, licenceNumber: String, id: String, merchantSettingsDTOS:MutableList<MerchantSettingsDTO>, token: String): RetrofitResource<SuccessResponse> {
        try {
            val data = apiClient.saveProfile(AppConstance.B_Token + token, ProfileParams(id, email, shopAddress, fullAddress, businessName, licenceNumber,merchantSettingsDTOS))
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getContent(type: String): RetrofitResource<ContentResponse> {
        try {
            val data = apiClient.getContent(type)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun addProduct(token: String, product: RequestBody, poduct_pic: MultipartBody.Part): RetrofitResource<AddNewProductResponse> {
        try {
            val data = apiClient.addProduct(AppConstance.B_Token + token, product, poduct_pic)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun updateProduct(
        token: String,
        id: String,
        product: RequestBody,
        poduct_pic: MultipartBody.Part
    ): RetrofitResource<ProductResponse> {
        try {
            val data = apiClient.updateProduct(AppConstance.B_Token + token, id, product, poduct_pic)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getProduct(
        token: String,
        productId: String, ): RetrofitResource<GetProductDataById> {
        try {
            val data = apiClient.getProduct(AppConstance.B_Token + token, productId)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun deleteProduct(
        token: String,
        productId: String, ): RetrofitResource<DeleteProductResponse> {
        try {
            val data = apiClient.deleteProduct(AppConstance.B_Token + token, productId)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getAllTaxDetail(token: String): RetrofitResource<GetCurrentTaxDetail> {
        try {
            val data = apiClient.getAllTaxDataApi(AppConstance.B_Token + token)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getAllCategoriesDetail(token: String): RetrofitResource<GetAllCategoriesDetails> {
        try {
            val data = apiClient.getAllCategoriesDataApi(AppConstance.B_Token + token)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getProductList(token: String, pageNo: Int, pageSize: Int, sortBy: String, asc: Boolean, data: ProductPram): RetrofitResource<GetProductListResponse> {
        try {
            val data = apiClient.getProductList(AppConstance.B_Token + token, pageNo, pageSize, sortBy, asc,  data)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getSubCatDataDetailsList(token: String ,catId: String): RetrofitResource<GetSubCatDataDetails> {
        try {
            val data = apiClient.getSubCategoryList(AppConstance.B_Token + token, catId)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                if (result.data != null && result.data.size > 0) {
                    return RetrofitResource.Success(result)
                } else {
                    return RetrofitResource.Error(
                        FailResponse(
                            context.getString(R.string.data_not_available),
                            ""
                        )
                    )
                }

            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getById(token: String, id: String, ): RetrofitResource<GetUserById> {
        try {
            val data = apiClient.getById(AppConstance.B_Token + token, id)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun updateStatus(token: String, body: UpdateStatus, status: String, orderNo: String): RetrofitResource<UpdateOrderStatus> {
        try {
            val updateStatus = apiClient.updateStatus(AppConstance.B_Token +token,status,orderNo,body)
            val result = updateStatus.body()
            if (updateStatus.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(updateStatus.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(updateStatus.code(),updateStatus.errorBody()!!.string()))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }
    }


    override suspend fun getAllOrder(token: String, orderParam: OrderParams, pageNo: String, pageSize: String): RetrofitResource<BeanViewAllOrder> {
        try {
            val orderData = apiClient.getAllOrders(AppConstance.B_Token +token,pageNo,pageSize,orderParam)
            val result = orderData.body()
            if (orderData.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(orderData.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(orderData.code(),orderData.errorBody()!!.string()))
                }
                return RetrofitResource.Error(FailResponse(context.getString(R.string.please_try_after_sometime),""))
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(FailResponse(e.message ?: context.getString(R.string.server_not_responding),""))
        }

    }


    override suspend fun getFAQ(): RetrofitResource<FAQResponse> {

        try {

            val data = apiClient.getFAQ()
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun createOutlet(token: String, param: OutletParam): RetrofitResource<AddOutletResponse> {
        try {
            val data = apiClient.createOutlet(AppConstance.B_Token +token,param)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun updateOutlet(token: String, param: UpdateParam): RetrofitResource<UpdateOutlet> {
        try {
            val data = apiClient.updateOutlet(AppConstance.B_Token+token,param)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun addSubMerchant(token: String, param: AddSubMerchantParam): RetrofitResource<CreateSubMerchant> {
        try {
            val data = apiClient.addSubMerchant(AppConstance.B_Token+token,param)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun updateSubMerchant(token: String, param: UpdateSubMerchantParam): RetrofitResource<UpdateSubMerchant> {
        try {
            val data = apiClient.updateSubMerchant(AppConstance.B_Token+token,param)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getOutlet(token: String, pageNo: String, body: EmptyBody): RetrofitResource<GetOutlet> {
        try {
            val data = apiClient.getOutlet(AppConstance.B_Token+token,pageNo,body)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getOutletById(token: String,id: String): RetrofitResource<GetOutletById> {
        try {
            val data = apiClient.getOutletById(AppConstance.B_Token+token,id)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }


    override suspend fun getSubMerchants(token: String, pageNo: String, body: EMPTYJSON): RetrofitResource<GetAllSubMerchant> {
        try {
            val data = apiClient.getSubMerchants(AppConstance.B_Token+token,pageNo, body)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getMerchantById(token: String, id: String): RetrofitResource<GetMerchantById> {
        try {
            val data = apiClient.getMerchantById(AppConstance.B_Token+token, id)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }


    override suspend fun deleteOutlet(token: String, body: DeleteOutParam): RetrofitResource<DeleteOutlet> {
        try {
            val data = apiClient.deleteOutlet(AppConstance.B_Token+token, body)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun deleteMerchant(token: String, body: DeleteOutParam): RetrofitResource<DeleteSubMerchant> {
        try {
            val data = apiClient.deleteSubMerchant(AppConstance.B_Token+token, body)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }

    }

    override suspend fun getAllCountries(): RetrofitResource<CountryResponse> {
        try {
            val data = apiClient.getAllCountries()
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getRoles(token: String, pageNo: Int, body: GetRolesParams): RetrofitResource<RolesResponse> {
        try {
            val data = apiClient.getAllRoles(AppConstance.B_Token+token,pageNo,body)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }




    override suspend fun getAllScreens(
        token: String
    ): RetrofitResource<ScreenResponse> {
        try {
            val data = apiClient.getAllScreens(AppConstance.B_Token+token)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getRoleById(
        token: String,
        id: String
    ): RetrofitResource<SingleRoleResponse> {
        try {
            val data = apiClient.getSingleRole(AppConstance.B_Token+token,id)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun addRole(
        token: String,
        sendRolesParmas: SendRolesParmas
    ): RetrofitResource<SuccessResponse> {
        try {
            val data = apiClient.addRole(AppConstance.B_Token+token,sendRolesParmas)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun updateRole(
        token: String,
        sendRolesParmas: SendRolesParmas
    ): RetrofitResource<SuccessResponse> {
        try {
            val data = apiClient.updateRole(AppConstance.B_Token+token,sendRolesParmas)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun deleteRole(
        token: String,
        deleteRolesParams: DeleteRolesParams
    ): RetrofitResource<SuccessResponse> {
        try {
            val data = apiClient.deleteRole(AppConstance.B_Token+token,deleteRolesParams)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun addNote(token: String, note: AddNoteParam): RetrofitResource<OrderNote> {
        try {
            val data = apiClient.addNote(AppConstance.B_Token+token,note)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getCommissions(token: String, pageNo: Int, body: CommissionParams): RetrofitResource<CommissionResponse> {
        try {
            val data = apiClient.getAllCommission(AppConstance.B_Token+token,pageNo,body)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getPermissions(token: String): RetrofitResource<PermissionDetails> {
        try {
            val data = apiClient.getPermission(AppConstance.B_Token+token)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(data.errorBody()!=null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }


    override suspend fun getAllDisputes(
        token: String,
        pageNo: Int,
        pageSize: Int,
        disputeListParams: DisputeListParams
    ): RetrofitResource<DisputeListResponse> {
        try {
            val data = apiClient.getAllDispute(AppConstance.B_Token +token,pageNo, pageSize, disputeListParams)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }


    override suspend fun getDispute(
        token: String,
        disputeId: String
    ): RetrofitResource<DisputeSingleResponse> {
        try {
            val data = apiClient.getDispute(B_Token +token,disputeId)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun sendDisputeMessage(
        token: String,
        sendMessageParams: SendMessageParams
    ): RetrofitResource<SuccessResponse> {
        try {
            val data = apiClient.sendDisputeMessage(B_Token +token,sendMessageParams)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    return RetrofitResource.Error(handleError(data.code(),data.errorBody()!!.string()))
                }
                return RetrofitResource.Error(
                    FailResponse(
                        context.getString(R.string.please_try_after_sometime),
                        ""
                    )
                )
            }
        } catch (e: Exception) {
            return RetrofitResource.Error(
                FailResponse(
                    e.message ?: context.getString(R.string.server_not_responding), ""
                )
            )
        }
    }

    override suspend fun getProductsWithPaging(viewmodelscope: CoroutineScope, token: String, productParams: ProductPram): Flow<PagingData<Item>> {
        val products: Flow<PagingData<Item>> =
            Pager(config = PagingConfig(pageSize = 10, prefetchDistance = 2),
                pagingSourceFactory = {
                    PagingDataSource(apiClient,token,  productParams)

                }
            ).flow.cachedIn(viewmodelscope)

        return products
    }

    fun handleError(code:Int,error:String):FailResponse{
        if (code == 403) {
            return FailResponse("", "", code)
        }
        val json = JSONObject(error)
        val status = json.getString("status")
        val message = json.getString("message")
        return FailResponse(message, status)
    }
}