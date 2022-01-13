package com.actorpay.merchant.database.datastore
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.actorpay.merchant.R
import com.actorpay.merchant.database.datastore.PreferenceKeys.IS_APP_LOGGED_IN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreCustom(val context: Context/*private val dataStore: DataStore<Preferences>*/) :
    DataStoreBase {
    private val USER_PREFERENCES_NAME = context.getString(R.string.app_name)
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    override fun giveRepository(): String {
        return this.toString()
    }

    override suspend fun logOut() {
        context.dataStore.edit { preference ->
            preference.clear()
        }
    }

    //region CRUD Operation
    override suspend fun update(booleanKey: Boolean) {
        context.dataStore.edit { preference ->
            preference[PreferenceKeys.BOOLEAN_KEY] = booleanKey
        }
    }

    override suspend fun update(stringKey: String) {

        context.dataStore.edit { preference ->
            preference[PreferenceKeys.STRING_KEY] = stringKey
        }
    }

    override suspend fun updateAppname(appName: String) {

        context.dataStore.edit { preference ->
            preference[PreferenceKeys.APP_NAME] = appName
        }
    }


    override suspend fun setUserId(userId: String) {
        context.dataStore.edit { preferences -> preferences[PreferenceKeys.USERID] = userId }
    }

    override suspend fun setMerchantId(merchantId: String) {

        context.dataStore.edit {
                preferences -> preferences[PreferenceKeys.MERCHANTID] = merchantId

        }
    }

    override suspend fun setIsLoggedIn(value: Boolean) {
        context.dataStore.edit { mutablePreferences: MutablePreferences ->
            mutablePreferences[IS_APP_LOGGED_IN] = value
        }
    }

    override suspend fun setEmail(email: String) {
        context.dataStore.edit { preferences -> preferences.set(PreferenceKeys.EMAIL, email) }
    }

    override suspend fun setAccessToken(value: String) {

        context.dataStore.edit { preferences ->
            preferences.set(
                PreferenceKeys.ACCESS_TOKEN,
                value
            )
        }
    }

    override suspend fun setRefreshToken(value: String) {
        context.dataStore.edit { preferences ->
            preferences.set(
                PreferenceKeys.REFRESH_TOKEN,
                value
            )
        }
    }

    override suspend fun setBussinessName(value: String) {

        context.dataStore.edit { preferences ->
            preferences.set(
                PreferenceKeys.BUSSINESS_NAME,
                value
            )
        }
    }


    override suspend fun update(integerKey: Int) {
        context.dataStore.edit { preference ->
            preference[PreferenceKeys.INTEGER_KEY] = integerKey
        }
    }

    override suspend fun update(doubleKey: Double) {
        context.dataStore.edit { preference ->
            preference[PreferenceKeys.DOUBLE_KEY] = doubleKey
        }
    }

    override suspend fun update(longKey: Long) {
        context.dataStore.edit { preference ->
            preference[PreferenceKeys.LONG_KEY] = longKey
        }
    }

    override fun getBoolean(): Flow<Boolean> {
        return getBooleanData(PreferenceKeys.BOOLEAN_KEY)
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return getBooleanData(IS_APP_LOGGED_IN)
    }

    override fun getString(): Flow<String> {
        return getString(PreferenceKeys.STRING_KEY)
    }

    override fun getUserId(): Flow<String> {
        return getString(PreferenceKeys.USERID)
    }

    override fun getMerchantId(): Flow<String> {
        return getString(PreferenceKeys.MERCHANTID)
    }

    override fun getAppName(): Flow<String> {
        return getString(PreferenceKeys.APP_NAME)
    }


    override fun getLong(): Flow<Long> {
        return getLong(PreferenceKeys.LONG_KEY)
    }

    override fun getDouble(): Flow<Double> {
        return getDouble(PreferenceKeys.DOUBLE_KEY)
    }

    override fun getInteger(): Flow<Int> {
        return getIntegerData(PreferenceKeys.INTEGER_KEY)
    }

    //Predefine Function to get Data Using Keys
    fun getString(key: Preferences.Key<String>): Flow<String> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference[key] ?: "Null"
        }
    }

    fun getLong(key: Preferences.Key<Long>): Flow<Long> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference[key] ?: 0L
        }
    }

    fun getDouble(key: Preferences.Key<Double>): Flow<Double> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference[key] ?: 0.00
        }
    }

    fun getBooleanData(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference.get(key) ?: false
        }
    }

    fun getIntegerData(key: Preferences.Key<Int>): Flow<Int> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preference ->
            preference[key] ?: 0
        }
    }

    override fun getEmail(): Flow<String> {
        return getString(PreferenceKeys.EMAIL)
    }

    override fun getAccessToken(): Flow<String> {
        return getString(PreferenceKeys.ACCESS_TOKEN)
    }

    override fun getBussinessName(): Flow<String> {
        return getString(PreferenceKeys.BUSSINESS_NAME)
    }

    override fun getRefreshToken(): Flow<String> {
        return getString(PreferenceKeys.REFRESH_TOKEN)
    }

}