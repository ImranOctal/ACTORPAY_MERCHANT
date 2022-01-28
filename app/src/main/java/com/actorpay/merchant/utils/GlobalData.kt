package com.actorpay.merchant.utils
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.CountryItem
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenItem

object GlobalData {
    val allCountries= mutableListOf<CountryItem>()
    val allScreens= mutableListOf<ScreenItem>()


}