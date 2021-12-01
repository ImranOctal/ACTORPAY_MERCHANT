package com.octal.actorpay.repositories.AppConstance

class AppConstance {
    companion object{
        const val BASE_URL: String = "http://192.168.1.171:8765/"
        const val SUB_DOMAIN: String = "api/"
        const val SUB_DOMAIN2: String = "merchant-service/"
        const val SUB_DOMAIN3: String = "auth/"
        const val SUB_DOMAIN_MERCHANT: String = "merchant/"
        const val SUB_DOMAIN_FORGET: String = "forget/"
        const val SUB_DOMAIN_CMS: String = "cms-service/"
        const val BY:String="by/"
        const val ID:String="id/"
        const val B_Token:String="Bearer "
        const val UPDATE:String="update/"
        const val AUTH:String="Authorization"
        const val TYPE:String="type"
        const val BEARER:String="Bearer "
        const val TOKEN_ATRIBUTE:String="token"
        const val IDS:String="{id}"
        const val LOGIN: String = SUB_DOMAIN+SUB_DOMAIN2+SUB_DOMAIN3+"login"
        const val SIGNUP: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+"signup"
        const val FORGETPASSWORD: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+SUB_DOMAIN_FORGET+"password"
        const val GET_PROFILE: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+ BY+ ID
        const val UPDATE_PROFILE: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+ UPDATE
        const val CHANGE_PASSWORD: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+ "user/change/password"
        const val GET_CONTENT: String = SUB_DOMAIN+ SUB_DOMAIN_CMS+  "get/static/data/by/cms"
    }
}