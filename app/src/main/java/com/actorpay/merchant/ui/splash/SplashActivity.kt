package com.actorpay.merchant.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityMainBinding
import com.actorpay.merchant.ui.home.HomeActivity
import com.actorpay.merchant.ui.signup.SignupActivity
import com.actorpay.merchant.viewmodel.ActorPayViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val actorPayViewModel: ActorPayViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        lifecycleScope.launch (Dispatchers.Main){
            delay(2000L)
            actorPayViewModel.methodRepo.dataStore.isLoggedIn().collect {
                if(it){
                    startActivity(Intent(baseContext(),HomeActivity::class.java))
                    finish()
                }
                else{
                    startActivity(Intent(baseContext(),SignupActivity::class.java))
                    finish()
                }
            }


           // switchActivity(Intent(baseContext(), ManageProductActivity::class.java))
        }
    }
}