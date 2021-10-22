package com.actorpay.merchant.database.datastore
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.actorpay.merchant.R
import com.actorpay.merchant.database.datastore.PreferenceKeys.IS_APP_LOGGED_IN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreCustom(val context:Context/*private val dataStore: DataStore<Preferences>*/) :DataStoreBase {
    private  val USER_PREFERENCES_NAME = context.getString(R.string.app_name)
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    override fun giveRepository() : String {
        return this.toString()
    }
    //region CRUD Operation
    override suspend fun update(booleanKey : Boolean) {
        context.dataStore.edit { preference ->
            preference.set(PreferenceKeys.BOOLEAN_KEY, booleanKey)
        }
    }

    override suspend fun update(stringKey : String) {

        context.dataStore?.edit { preference ->
            preference.set(PreferenceKeys.STRING_KEY, stringKey)
        }
    }
    override suspend fun updateAppname(appName : String) {

        context.dataStore?.edit { preference ->
            preference.set(PreferenceKeys.APP_NAME, appName)
        }
    }

    override suspend fun setPhoneNumber(mobileNumber: String) {
        context.dataStore?.edit { preference ->
            preference.set(PreferenceKeys.MOBILE, mobileNumber)
        }
    }

    override suspend fun setCountryCode(countryCode: String) {
        context.dataStore?.edit { preference ->
            preference.set(PreferenceKeys.COUNTRY_CODE, countryCode)
        }
    }

    override suspend fun setName(name: String) {
        context.dataStore?.edit { preference ->
            preference.set(PreferenceKeys.NAME, name)
        }
    }

    override suspend fun setUserId(userId: String) {
       context.dataStore?.edit { preferences -> preferences.set(PreferenceKeys.USERID,userId) }
    }

    override suspend fun setIsLoggedIn(value: Boolean) {
      context.dataStore?.edit { mutablePreferences: MutablePreferences -> mutablePreferences.set(IS_APP_LOGGED_IN,value) }
    }

    override suspend fun update(integerKey : Int) {
        context.dataStore?.edit { preference ->
            preference.set(PreferenceKeys.INTEGER_KEY, integerKey)
        }
    }

    override suspend fun update(doubleKey : Double) {
        context.dataStore?.edit { preference ->
            preference.set(PreferenceKeys.DOUBLE_KEY, doubleKey)
        }
    }

    override suspend fun update(longKey : Long) {
        context.dataStore?.edit { preference ->
            preference.set(PreferenceKeys.LONG_KEY, longKey)
        }
    }

    override fun getBoolean() : Flow<Boolean> {
        return getBooleanData(PreferenceKeys.BOOLEAN_KEY)
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return getBooleanData(IS_APP_LOGGED_IN)
    }

    override fun getString() : Flow<String> {
        return getString(PreferenceKeys.STRING_KEY)
    }

    override fun getUserId(): Flow<String> {
        return getString(PreferenceKeys.USERID)
    }

    override fun getAppName() : Flow<String> {
        return getString(PreferenceKeys.APP_NAME)
    }

    override fun getMobileNumber(): Flow<String> {
       return getString(PreferenceKeys.MOBILE)
    }

    override fun getName(): Flow<String> {
        return getString(PreferenceKeys.NAME)
    }

    override fun getCountryCode(): Flow<String> {
        return getString(PreferenceKeys.COUNTRY_CODE)
    }

    override fun getLong() : Flow<Long> {
        return getLong(PreferenceKeys.LONG_KEY)
    }
    override fun getDouble() : Flow<Double> {
        return getDouble(PreferenceKeys.DOUBLE_KEY)
    }

    override fun getInteger() : Flow<Int> {
        return getIntegerData(PreferenceKeys.INTEGER_KEY)
    }

    //Predefine Function to get Data Using Keys
    fun getString(key:Preferences.Key<String> ):Flow<String>{
        return context.dataStore?.data?.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }?.map { preference ->
            preference.get(key) ?: "Null"
        } ?: emptyFlow()
    }
     fun getLong(key:Preferences.Key<Long>) : Flow<Long> {
        return context.dataStore?.data?.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }?.map { preference ->
            preference.get(key) ?: 0L
        } ?: emptyFlow()
    }
     fun getDouble(key:Preferences.Key<Double>) : Flow<Double> {
        return context.dataStore?.data?.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }?.map { preference ->
            preference.get(key) ?: 0.00
        } ?: emptyFlow()
    }
     fun getBooleanData(key:Preferences.Key<Boolean>) : Flow<Boolean> {
        return context.dataStore?.data?.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }?.map { preference ->
            preference.get(key) ?: false
        } ?: emptyFlow()
    }
     fun getIntegerData(key:Preferences.Key<Int>) : Flow<Int> {
        return context.dataStore?.data?.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }?.map { preference ->
            preference.get(key) ?: 0
        } ?: emptyFlow()
    }

    //endregion
}