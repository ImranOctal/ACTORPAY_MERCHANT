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
    val token_type: String
)

data class LoginParams(val email: String,val password: String)

data class ForgetPasswordParams(val emailId: String)


data class SignUpParams(val email:String,
                        val extensionNumber:String,
                        val contactNumber:String,
                        val password:String,
                        val shopAddress:String,
                        val fullAddress:String,
                        val businessName:String,
                        val licenceNumber:String,
)

data class ForgetPasswordResponses(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)