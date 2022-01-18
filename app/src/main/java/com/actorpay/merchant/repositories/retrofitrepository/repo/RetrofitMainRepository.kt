package com.actorpay.merchant.repositories.retrofitrepository.repo
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
import android.content.Context
import com.actorpay.merchant.R
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
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
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileParams
import com.actorpay.merchant.repositories.retrofitrepository.models.taxation.GetCurrentTaxDetail
import com.actorpay.merchant.repositories.retrofitrepository.resource.RetrofitResource
import com.actorpay.merchant.retrofitrepository.apiclient.ApiClient
import com.octal.actorpay.repositories.AppConstance.AppConstance
import com.octal.actorpay.repositories.retrofitrepository.models.content.ContentResponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.FAQResponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.ProductResponse
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
                    val json = JSONObject(loginData.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(forgetData.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(loginData.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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

    override suspend fun saveProfile(
        email: String,
        shopAddress: String,
        fullAddress: String,
        businessName: String,
        licenceNumber: String,
        id: String,
        token: String
    ): RetrofitResource<SuccessResponse> {
        try {
            val data = apiClient.saveProfile(
                AppConstance.B_Token + token,
                ProfileParams(id, email, shopAddress, fullAddress, businessName, licenceNumber)
            )
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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

    override suspend fun addProduct(
        token: String,
        product: RequestBody,
        poduct_pic: MultipartBody.Part
    ): RetrofitResource<AddNewProductResponse> {
        try {

            val data = apiClient.addProduct(AppConstance.B_Token + token, product, poduct_pic)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
            val data =
                apiClient.updateProduct(AppConstance.B_Token + token, id, product, poduct_pic)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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

    override suspend fun getProductList(
        token: String,
        pageNo: String,
        pageSize: String,
        sortBy: String,
        asc: Boolean,
        data: JSONObject
    ): RetrofitResource<GetProductListResponse> {
        try {
            val data = apiClient.getProductList(
                AppConstance.B_Token + token,
                pageNo,
                pageSize,
                sortBy,
                asc,
                data = data
            )
            val result = data.body()
            if (data.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (data.errorBody() != null) {
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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

    override suspend fun getSubCatDataDetailsList(
        token: String,
        pageNo: String
    ): RetrofitResource<GetSubCatDataDetails> {
        try {
            val data = apiClient.getSubCategoryList(AppConstance.B_Token + token, pageNo)
            val result = data.body()
            if (data.isSuccessful && result != null) {
                if (result.data != null && result.data.items.size > 0) {
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json = JSONObject(data.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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

    override suspend fun updateStatus(token: String, orderNo: String, status: String): RetrofitResource<UpdateOrderStatus> {
        try {
            val updateStatus = apiClient.updateStatus(AppConstance.B_Token + token, orderNo, status)
            val result = updateStatus.body()
            if (updateStatus.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if (updateStatus.errorBody() != null) {
                    val json = JSONObject(updateStatus.errorBody()!!.string())
                    val status = json.getString("status")
                    val message = json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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

    override suspend fun getAllOrder(token: String, orderParam: OrderParams, pageNo: String, pageSize: String): RetrofitResource<BeanViewAllOrder> {
        try {
            val orderData = apiClient.getAllOrders(AppConstance.B_Token +token,pageNo,pageSize,orderParam)
            val result = orderData.body()
            if (orderData.isSuccessful && result != null) {
                return RetrofitResource.Success(result)
            } else {
                if(orderData.errorBody()!=null) {
                    val json=JSONObject(orderData.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
                    val json=JSONObject(data.errorBody()!!.string())
                    val status=json.getString("status")
                    val message=json.getString("message")
                    return RetrofitResource.Error(FailResponse(message, status))
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
}