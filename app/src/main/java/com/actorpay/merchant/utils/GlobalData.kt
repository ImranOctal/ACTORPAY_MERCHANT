package com.actorpay.merchant.utils
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.CountryItem
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenItem
import com.actorpay.merchant.repositories.AppConstance.AppConstance

object GlobalData {
    val allCountries= mutableListOf<CountryItem>()
    val allScreens= mutableListOf<ScreenItem>()
    val permissionDataList= mutableListOf<PermissionData>(
        PermissionData(false,"0", AppConstance.SCREEN_SUB_MERCHANT,false),
        PermissionData(false,"1", AppConstance.SCREEN_OUTLET,false),
        PermissionData(false,"2", AppConstance.SCREEN_WALLET_BALANCE,false),
        PermissionData(false,"3", AppConstance.SCREEN_PAYMENT,false),
        PermissionData(false,"4", AppConstance.SCREEN_DASHBOARD,false),
        PermissionData(false,"5", AppConstance.SCREEN_MANAGE_PRODUCT,false),
        PermissionData(false,"6", AppConstance.SCREEN_MANAGE_ORDER,false),
        PermissionData(false,"7", AppConstance.SCREEN_MANAGE_ORDER_NOTE,false),
        PermissionData(false,"7", AppConstance.SCREEN_REPORTS,false),
        PermissionData(false,"7", AppConstance.SCREEN_MANAGE_ROLE,false),
        PermissionData(false,"7", AppConstance.SCREEN_MANAGE_OUTLET,false),
    )


}