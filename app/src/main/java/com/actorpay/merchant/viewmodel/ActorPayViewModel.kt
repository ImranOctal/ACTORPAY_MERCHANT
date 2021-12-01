package com.actorpay.merchant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository

class ActorPayViewModel(val dispatcherProvider: CoroutineContextProvider, val methodRepo: MethodsRepo, val apiRepo: RetrofitRepository)  : AndroidViewModel(
    Application()
) {

}