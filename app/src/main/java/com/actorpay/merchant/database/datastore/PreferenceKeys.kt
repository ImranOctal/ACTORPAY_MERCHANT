package com.actorpay.merchant.database.datastore
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
import androidx.datastore.preferences.core.*

object PreferenceKeys {
    //Demo Keyt Genration
    val BOOLEAN_KEY : Preferences.Key<Boolean> = booleanPreferencesKey("boolean_key")
    val STRING_KEY : Preferences.Key<String> = stringPreferencesKey("string_key")
    val INTEGER_KEY : Preferences.Key<Int> = intPreferencesKey("integer_key")
    val DOUBLE_KEY : Preferences.Key<Double> = doublePreferencesKey("double_key")
    val LONG_KEY : Preferences.Key<Long> = longPreferencesKey("long_key")
    val APP_NAME : Preferences.Key<String> = stringPreferencesKey("Actor Pay")
    val IS_APP_LOGGED_IN : Preferences.Key<Boolean> = booleanPreferencesKey("app_logged_in")
    //val LIST_MODEL_KEY : Preferences.Key<List<CustomModel>> = preferencesKey<List<CustomModel>>("list_model_key")


    val USERID : Preferences.Key<String> = stringPreferencesKey("userId")
    val MERCHANTID : Preferences.Key<String> = stringPreferencesKey("merchantId")
    val BUSSINESS_NAME : Preferences.Key<String> = stringPreferencesKey("bussiness_name")
    val Role : Preferences.Key<String> = stringPreferencesKey("role")
    val EMAIL : Preferences.Key<String> = stringPreferencesKey("email")
    val ACCESS_TOKEN : Preferences.Key<String> = stringPreferencesKey("access_token")
    val REFRESH_TOKEN : Preferences.Key<String> = stringPreferencesKey("refresh_token")

}