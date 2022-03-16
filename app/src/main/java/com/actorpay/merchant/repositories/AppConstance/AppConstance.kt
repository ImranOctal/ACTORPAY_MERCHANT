package com.actorpay.merchant.repositories.AppConstance

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
        const val CATEGORY_ID:String="categoryId"
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
        const val LOGIN: String = SUB_DOMAIN + SUB_DOMAIN2 + SUB_DOMAIN3 + LOGIN_VAR
        const val SIGNUP: String = SUB_DOMAIN + SUB_DOMAIN2 + SUB_DOMAIN_MERCHANT + SIGNUP_VAR
        const val FORGETPASSWORD: String = SUB_DOMAIN + SUB_DOMAIN2 + SUB_DOMAIN_MERCHANT + SUB_DOMAIN_FORGET + PASSWORD
        const val GET_PROFILE: String = SUB_DOMAIN + SUB_DOMAIN2 + SUB_DOMAIN_MERCHANT + BY + ID
        const val UPDATE_PROFILE: String = SUB_DOMAIN + SUB_DOMAIN2 + SUB_DOMAIN_MERCHANT + UPDATE
        const val CHANGE_PASSWORD: String = SUB_DOMAIN + SUB_DOMAIN2 + SUB_DOMAIN_MERCHANT + USER + CHANGE + PASSWORD
        const val GET_CONTENT: String = SUB_DOMAIN + SUB_DOMAIN_CMS + GET_STATIC_CMS
        const val ADD_PRODUCT: String = SUB_DOMAIN + SUB_DOMAIN2 + PRODUCTS +"/"
        const val DELET_PRODUCT: String = SUB_DOMAIN + SUB_DOMAIN2 + PRODUCTS +"/remove"
        const val TAX_URL: String = SUB_DOMAIN + SUB_DOMAIN2 + "taxes/get/all"


        const val CATEGORIES_URL: String = SUB_DOMAIN + SUB_DOMAIN2 + "get/all/categories"



        const val PRODUCT_LIST: String = SUB_DOMAIN + SUB_DOMAIN2 + PRODUCTS +"/list/paged"
        const val GET_BY_ID: String = SUB_DOMAIN + SUB_DOMAIN2 + SUB_DOMAIN_MERCHANT + BY + ID

        const val SUB_CAT_URL: String = SUB_DOMAIN + SUB_DOMAIN2 +"get/all/subcategories/by/category"


        const val GENRATE_TOKEN_AGAIN: String = SUB_DOMAIN + USER_SERVICE + SUB_DOMAIN3 + SLASH_TOKEN + REFRESH
        const val GET_ALL_ORDER:String= SUB_DOMAIN + SUB_DOMAIN2 + ORDER +"list/paged"
        const val UPDATE_STATUS:String= SUB_DOMAIN + SUB_DOMAIN2 + ORDER +"status"
        const val CREATE_OUTLET:String= SUB_DOMAIN + SUB_DOMAIN2 +"v1/merchant/outlet/create"
        const val GET_OUTLET:String= SUB_DOMAIN + SUB_DOMAIN2 +"v1/merchant/outlet/get/all/paged"
        const val DELETE_OUTLET:String= SUB_DOMAIN + SUB_DOMAIN2 +"v1/merchant/outlet/delete/by/ids"
        const val DELETE_SUBMERCHANT:String= SUB_DOMAIN + SUB_DOMAIN2 +"submerchant/delete/by/ids"
        const val UPDATE_OUTLET:String= SUB_DOMAIN + SUB_DOMAIN2 +"v1/merchant/outlet/update"
        const val GET_OUTLET_BY_ID:String= SUB_DOMAIN + SUB_DOMAIN2 +"v1/merchant/outlet/by/id/"

        const val ADD_MONEY:String=SUB_DOMAIN+ SUB_DOMAIN2+"v1/wallet/addMoney"
        const val GET_WALLET_BALANCE: String = SUB_DOMAIN+ SUB_DOMAIN2+"v1/wallet/"
        const val WALLET_HISTORY:String=SUB_DOMAIN+ SUB_DOMAIN2+"v1/wallet/list/paged"


        const val GET_FAQ: String = SUB_DOMAIN + SUB_DOMAIN_CMS +  "faq/all"
        const val GET_COUNTRIES: String = SUB_DOMAIN + SUB_DOMAIN_GLOBAL +"v1/country/get/all"
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
        const val STATUS_RETURN_ACCEPT:String="RETURNING_ACCEPTED"
        const val STATUS_RETURN_DECLINE:String="RETURNING_DECLINED"
        const val SCREEN_SUB_MERCHANT:String="Manage Submerchant"
        const val SCREEN_OUTLET:String="Manage Outlets"

        const val SCREEN_WALLET_BALANCE:String="Wallet & Wallet Balance"
        const val SCREEN_PAYMENT:String="Payment & Earning"
        const val SCREEN_DASHBOARD:String="Dashboard"
        const val SCREEN_MANAGE_PRODUCT:String="Manage Product"
        const val SCREEN_MANAGE_ORDER:String="Manage Orders"
        const val SCREEN_MANAGE_ORDER_NOTE:String="Manage Order Notes"
        const val SCREEN_REPORTS:String="Reports"
        const val SCREEN_MANAGE_ROLE:String="Manage Role"
        const val SCREEN_MANAGE_OUTLET:String="Manage Outlets"


        const val green_color:String="#00A63D"
        const val white_color:String="#FFFFFF"
        const val blue_color:String="#0078b7"
        const val red_color:String="#F44336"
        const val GET_ROLES:String= SUB_DOMAIN + SUB_DOMAIN2 +"role/get/all/paged"
        const val GET_SUBMERCHANTS:String= SUB_DOMAIN + SUB_DOMAIN2 +"submerchant/get/all/paged"
        const val ADD_SUBMERCHANT:String= SUB_DOMAIN + SUB_DOMAIN2 +"submerchant/create"
        const val GET_MERCHANT_BY_ID:String= SUB_DOMAIN + SUB_DOMAIN2 +"submerchant/read/by/id/"
        const val UPDATE_SUBMERCHANT:String= SUB_DOMAIN + SUB_DOMAIN2 +"submerchant/update"
        const val GET_SINGLE_ROLE:String= SUB_DOMAIN + SUB_DOMAIN2 +"role/by/id"

        const val ADD_ROLE:String= SUB_DOMAIN + SUB_DOMAIN2 +"role/create"
        const val UPDATE_ROLE:String= SUB_DOMAIN + SUB_DOMAIN2 +"role/update"
        const val DELETE_ROLE:String= SUB_DOMAIN + SUB_DOMAIN2 +"role/delete/by/ids"

        const val GET_ALL_SCREENS:String= SUB_DOMAIN + SUB_DOMAIN2 +"get/all/screens"

        const val GET_ALL_COMMISSIONS:String= SUB_DOMAIN + SUB_DOMAIN2 +"productCommission/list/paged"
        const val Add_Note:String= SUB_DOMAIN + SUB_DOMAIN2 +"orderNotes/post"


        const val GET_PERMISSION:String= SUB_DOMAIN + SUB_DOMAIN2 +"get/user/permissions"

        const val GET_ALL_DISPUTES: String = SUB_DOMAIN + SUB_DOMAIN2 +"dispute/list/paged"
        const val GET_DISPUTE: String = SUB_DOMAIN + SUB_DOMAIN2 +"dispute/get/"
        const val SEND_DISPUTE_MESSAGE: String = SUB_DOMAIN + SUB_DOMAIN2 +"dispute/send/message"
        const val KEY_KEY="key"
        const val KEY_NAME="name"
        const val KEY_CONTACT="contact"
        const val KEY_QR="qr"
        const val KEY_MOBILE="mobile"
        const val KEY_EMAIL="email"
        const val KEY_AMOUNT="amount"

    }


}