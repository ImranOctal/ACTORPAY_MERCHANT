package com.actorpay.merchant.app

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.actorpay.merchant.di.appModule
import com.actorpay.merchant.viewmodel.ActorPayViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

/**
 * MyApplication
 * @author Imran Khan
 * @version 1.0
 * @since 1.0
 */


class MyApplication : MultiDexApplication() {

    /**
     * companion object
     */
    companion object {
        lateinit var application: MyApplication
    }

    lateinit var context: Context


    /**
     * onCreate
     *
     */
    override fun onCreate() {
        super.onCreate()
        //AppLocal.init(this)
        application = this
        context = this
        startKoin {
            // use Koin logger
            printLogger()
            // declare used Android context
            androidContext(context)
            // declare modules
            modules(appModule)
        }
        //initFirbae()
    }

    /*fun initFirbae() {
        FirebaseApp.initializeApp(this)
        //initFirbase()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM_reg_tokenfailed", task.exception.toString())
                return@OnCompleteListener
            }
            try {
                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = token
                Log.d("TAG", msg!!.toString())
                SharedPreferenceUtility.getInstance().save(Constant.FIREBASETOKEN, token)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        })
    }*/

    fun getInstance(): MyApplication {
        return application
    }

    /**
     * get app context instance
     *
     * @return
     */
    fun getAppContext(): Context {
        return context
    }


}