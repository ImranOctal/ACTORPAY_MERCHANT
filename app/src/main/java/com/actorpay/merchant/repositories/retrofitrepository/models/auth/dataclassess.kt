package com.actorpay.merchant.repositories.retrofitrepository.models.auth

data class LoginResponses(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class Data(
    val id: String,
    val email: String,
    val businessName: String,
    val access_token: String,
    val refresh_token: String,
    val role: String,
    val token_type: String
)

data class LoginParams(val email: String,val password: String)

data class ForgetPasswordParams(val emailId: String)


data class UpdateStatus(val orderNoteDescription: String,val orderItemIds:MutableList<String>)


data class SignUpParams(val email:String,
                        val extensionNumber:String,
                        val contactNumber:String,
                        val password:String,
                        val shopAddress:String,
                        val fullAddress:String,
                        val businessName:String,
                        val licenceNumber:String,
                        val fcmToken:String
)

data class OutletParam(val resourceType:String,
                        val licenceNumber:String,
                        val title:String,
                        val description:String,
                        val extensionNumber:String,
                        val contactNumber:String,
                        val addressLine1:String,
                        val addressLine2:String,
                        val zipCode:String,
                        val city:String,
                        val state:String,
                        val country:String,
                        val latitude:String,
                        val longitude:String
)

data class UpdateParam(
    val id:String, val resourceType:String,
    val licenceNumber:String,
                        val title:String,
                        val description:String,
                        val extensionNumber:String,
                        val contactNumber:String,
                        val addressLine1:String,
                        val addressLine2:String,
                        val zipCode:String,
                        val city:String,
                        val state:String,
                        val country:String,
                        val latitude:String,
                        val longitude:String
)







data class ForgetPasswordResponses(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)
data class CountryResponse (
    val message    : String,
    val data       : List<CountryItem> = arrayListOf(),
    val status     : String,
    val httpStatus : String
)

data class CountryItem(
    val country:String,
    val countryCode:String,
    val countryFlag:String?,
)
data class DeleteOutParam(val ids: MutableList<String>)

data class ProductPram(val name: String,val categoryName:String,val status:Boolean,val subcategoryName:String,val merchantId:String)

data class UpdateSubMerchantParam(val firstName: String,val lastName: String,val contactNumber: String,val extensionNumber: String,val roleId: String,val id:String,val gender:String,val email:String)


data class AddSubMerchantParam(val extensionNumber: String,val firstName:String,val lastName:String,val email:String,val contactNumber:String,val dateOfBirth:String,val roleId:String,val gender:String ,val isDefaultPassword:Boolean,val password:String)


