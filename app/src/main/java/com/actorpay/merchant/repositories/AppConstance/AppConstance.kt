package com.octal.actorpay.repositories.AppConstance

import java.text.SimpleDateFormat
import java.util.*

class AppConstance {
    companion object{
        const val BASE_URL: String = "http://192.168.1.171:8765/"
        const val SUB_DOMAIN: String = "api/"
        const val SUB_DOMAIN2: String = "merchant-service/"
        const val USER_SERVICE: String = "user-service"
        const val SUB_DOMAIN3: String = "auth/"
        const val SUB_DOMAIN_GLOBAL: String = "global-service/"
        const val SUB_DOMAIN_MERCHANT: String = "merchant/"
        const val SUB_DOMAIN_FORGET: String = "forget/"
        const val SUB_DOMAIN_CMS: String = "cms-service/"
        const val SLASH_TOKEN: String = "/token/"
        const val REFRESH: String = "refresh"
        private const val BY:String="by/"
        private const val ID:String="id/"
        const val ID_VAR:String="id"
        const val dollar:String="$"
        const val rupee:String="₹"
        const val PRODUCT_ID:String="productId"
        const val PAGE_NO:String="pageNo"
        const val STATUS:String="status"
        const val ORDERNO:String="orderNo"
        const val SORT_BY:String="sortBy"
        const val ASCECNDING:String="asc"
        const val isActive:String="isActive"
        const val PAGE_SIZE:String="pageSize"
        const val B_Token:String="Bearer "
        const val UPDATE:String="update/"
        const val AUTH:String="Authorization"
        const val TYPE:String="type"
        const val BEARER:String="Bearer "
        const val TOKEN_ATTRIBUTE:String="token"
        const val IDS:String="{id}"
        const val FILE:String="file"
        const val PRODUCT:String="product"
        const val oultet:String="outlet"
        const val PRODUCTS:String="products"
        private const val PRODUCT_API:String="product/"
        private const val PASSWORD:String="password"
        private const val LOGIN_VAR:String="login"
        private const val USER:String="user"
        private const val ORDER:String="orders/"
        private const val CHANGE:String="/change/"
        const val GET_STATIC_CMS:String="get/static/data/by/cms"
        private const val SIGNUP_VAR:String="signup"
        const val LOGIN: String = SUB_DOMAIN+SUB_DOMAIN2+SUB_DOMAIN3+LOGIN_VAR
        const val SIGNUP: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+SIGNUP_VAR
        const val FORGETPASSWORD: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+SUB_DOMAIN_FORGET+PASSWORD
        const val GET_PROFILE: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+ BY+ ID
        const val UPDATE_PROFILE: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+ UPDATE
        const val CHANGE_PASSWORD: String = SUB_DOMAIN+SUB_DOMAIN2+ SUB_DOMAIN_MERCHANT+USER+CHANGE+PASSWORD
        const val GET_CONTENT: String = SUB_DOMAIN+ SUB_DOMAIN_CMS+GET_STATIC_CMS
        const val ADD_PRODUCT: String = SUB_DOMAIN+ SUB_DOMAIN2+ PRODUCTS+"/"
        const val DELET_PRODUCT: String = SUB_DOMAIN+ SUB_DOMAIN2+ PRODUCTS+"/remove"
        const val TAX_URL: String = SUB_DOMAIN+ SUB_DOMAIN2+ "taxes/get/all"


        const val CATEGORIES_URL: String = SUB_DOMAIN+ SUB_DOMAIN2+ "get/all/active/categories"
        const val PRODUCT_LIST: String = SUB_DOMAIN+SUB_DOMAIN2+PRODUCTS+"/list/paged"
        const val GET_BY_ID: String = SUB_DOMAIN+SUB_DOMAIN2+SUB_DOMAIN_MERCHANT+ BY+ ID
        const val SUB_CAT_URL: String = SUB_DOMAIN+SUB_DOMAIN2+"get/all/subcategories/paged"
        const val GENRATE_TOKEN_AGAIN: String = SUB_DOMAIN+USER_SERVICE+SUB_DOMAIN3+SLASH_TOKEN+REFRESH
        const val GET_ALL_ORDER:String=SUB_DOMAIN+SUB_DOMAIN2+ORDER+"list/paged"
        const val UPDATE_STATUS:String=SUB_DOMAIN+SUB_DOMAIN2+ORDER+"status"
        const val CREATE_OUTLET:String=SUB_DOMAIN+SUB_DOMAIN2+"v1/merchant/outlet/create"
        const val GET_OUTLET:String=SUB_DOMAIN+SUB_DOMAIN2+"v1/merchant/outlet/get/all/paged"
        const val DELETE_OUTLET:String=SUB_DOMAIN+SUB_DOMAIN2+"v1/merchant/outlet/delete/by/ids"

        const val GET_FAQ: String = SUB_DOMAIN+ SUB_DOMAIN_CMS+  "faq/all"
        const val GET_COUNTRIES: String = SUB_DOMAIN+ SUB_DOMAIN_GLOBAL+"v1/country/get/all"
        val dateFormate3= SimpleDateFormat("yyyy-MM-dd HH:MM", Locale.ENGLISH)
        val dateFormate4= SimpleDateFormat("dd MMM yyyy HH:MM", Locale.ENGLISH)
        const val STATUS_SUCCESS:String="SUCCESS"
        const val STATUS_READY:String="READY"
        const val STATUS_CANCELLED:String="CANCELLED"
        const val STATUS_PARTIALLY_CANCELLED:String="PARTIALLY_CANCELLED"
        const val STATUS_DISPATCHED:String="DISPATCHED"
        const val STATUS_RETURNING:String="RETURNING"
        const val STATUS_PARTIALLY_RETURNING:String="PARTIALLY_RETURNING"
        const val STATUS_RETURNED :String="RETURNED"
        const val STATUS_PARTIALLY_RETURNED:String="PARTIALLY_RETURNED"
        const val STATUS_DELIVERED:String="DELIVERED"
        const val STATUS_PENDING:String="PENDING"
        const val STATUS_FAILED:String="FAILED"
        const val STATUS_COMPLETED:String="COMPLETED"
        const val green_color:String="#00A63D"
        const val white_color:String="#FFFFFF"
        const val blue_color:String="#0078b7"
        const val red_color:String="#F44336"

    }


}