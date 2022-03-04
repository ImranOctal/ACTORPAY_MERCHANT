package com.actorpay.merchant.di


import com.actorpay.merchant.viewmodel.ActorPayViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.actorpay.merchant.database.datastore.*
import com.actorpay.merchant.database.prefrence.SharedPre
import com.actorpay.merchant.di.models.CoroutineContextProvider
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitMainRepository
import com.actorpay.merchant.repositories.retrofitrepository.repo.RetrofitRepository
import com.actorpay.merchant.retrofitrepository.apiclient.ApiClient
import com.actorpay.merchant.ui.commission.EarningViewModel
import com.actorpay.merchant.ui.content.ContentViewModel
import com.actorpay.merchant.ui.disputes.disputedetails.DisputeDetailsViewModel
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.manageOrder.viewModel.OrderDetailViewModel
import com.actorpay.merchant.ui.more.MoreViewModel
import com.actorpay.merchant.ui.outlet.OutletViewModel
import com.actorpay.merchant.ui.outlet.addoutlet.AddOutletViewModel
import com.actorpay.merchant.ui.profile.ProfileViewModel
import com.actorpay.merchant.ui.roles.RolesViewModel
import com.actorpay.merchant.ui.roles.details.RolesDetailsViewModel
import com.actorpay.merchant.ui.subAdmin.SubMerchantsViewModel
import com.actorpay.merchant.ui.subAdmin.addSubMerchant.AddSubMerchantViewModel
import com.actorpay.merchant.viewmodel.AuthViewModel
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.AUTH
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.BASE_URL
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.BEARER
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.TOKEN_ATTRIBUTE
import com.actorpay.merchant.ui.manageOrder.viewModel.ManageOrderViewModel
import com.actorpay.merchant.ui.manageProduct.viewModel.ProductViewModel
import com.actorpay.merchant.ui.setting.SettingViewModel
import com.actorpay.merchant.ui.disputes.DisputeViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.android.ext.koin.androidContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val appKoinModule = module {
    single { CoroutineContextProvider() }

    single { DataStoreCoroutinesHandler }


    single<DataStoreBase>{
        DataStoreCustom(androidContext())
    }

    single{
        MethodsRepo(context = androidContext(),dataStore = get())
    }
    single{
        SharedPre.getInstance(context = androidContext())
    }
    single<OkHttpClient>{
        OkHttpClient.Builder()
            /* .addInterceptor(okhttp3.Interceptor {chain ->
               val request:Request=chain.request().newBuilder().addHeader("Authorization", "Bearer " + sharedPre.jwtToken).build()
                 chain.proceed(request)
             })*/
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    single<okhttp3.Interceptor>{
        okhttp3.Interceptor {chain ->
            val request: Request =chain.request().newBuilder().addHeader(AUTH, BEARER + TOKEN_ATTRIBUTE).build()
            chain.proceed(request)
        }
    }
    single<ApiClient>{
        Retrofit.Builder().baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiClient::class.java)
    }
    single <RetrofitRepository>{
        RetrofitMainRepository(androidContext(),apiClient =get())
    }

    viewModel {
        ActorPayViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        AuthViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        HomeViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        ProfileViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        ContentViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        MoreViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        OutletViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        AddOutletViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        RolesViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
     SubMerchantsViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        AddSubMerchantViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        RolesDetailsViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        EarningViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        OrderDetailViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        DisputeViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        DisputeDetailsViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }

    viewModel {
        ManageOrderViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }

    viewModel {
        ProductViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
    viewModel {
        SettingViewModel(dispatcherProvider = get(),methodRepo=get(),apiRepo = get())
    }
   /* factory<ScreenNavigator> {
        LegacyNavigator()
    }*/
}

val appModule = listOf(appKoinModule)
