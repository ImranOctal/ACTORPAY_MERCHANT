package com.octal.actorpay.repositories.AppConstance

class AppConstance {
    companion object{
        const val BASE_URL: String = "http://192.168.1.171:8765/"
        const val SUB_DOMAIN: String = "api/"
        const val SUB_DOMAIN2: String = "merchant-service/"
        const val USER_SERVICE: String = "user-service"
        const val SUB_DOMAIN3: String = "auth/"
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
        const val TAX_URL: String = SUB_DOMAIN+ SUB_DOMAIN2+ "taxes/active"
        const val CATEGORIES_URL: String = SUB_DOMAIN+ SUB_DOMAIN2+ "get/all/active/categories"
        const val PRODUCT_LIST: String = SUB_DOMAIN+SUB_DOMAIN2+PRODUCTS+"/list/paged"
        const val GET_BY_ID: String = SUB_DOMAIN+SUB_DOMAIN2+SUB_DOMAIN_MERCHANT+ BY+ ID
        const val SUB_CAT_URL: String = SUB_DOMAIN+SUB_DOMAIN2+"get/all/subcategories/paged"
        const val GENRATE_TOKEN_AGAIN: String = SUB_DOMAIN+USER_SERVICE+SUB_DOMAIN3+SLASH_TOKEN+REFRESH
        const val GET_ALL_ORDER:String=SUB_DOMAIN+SUB_DOMAIN2+ORDER+"list/paged"
        const val UPDATE_STATUS:String=SUB_DOMAIN+SUB_DOMAIN2+ORDER+"status"


    }
}