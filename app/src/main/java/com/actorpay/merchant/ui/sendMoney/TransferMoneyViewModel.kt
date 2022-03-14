package com.actorpay.merchant.ui.sendMoney

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.actorpay.merchant.base.BaseViewModel
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.order.BeanViewAllOrder
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Data
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.google.android.gms.common.api.Response
import retrofit2.Call
import javax.security.auth.callback.Callback


class TransferMoneyViewModel(val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository) : BaseViewModel(){

}



