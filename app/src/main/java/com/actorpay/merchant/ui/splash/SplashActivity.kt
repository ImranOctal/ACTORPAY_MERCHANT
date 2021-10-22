package com.actorpay.merchant.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityMainBinding
import com.actorpay.merchant.ui.signup.SignupActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        lifecycleScope.launch (Dispatchers.Main){
            delay(2000L)
          switchActivity(Intent(baseContext(),SignupActivity::class.java))
           // switchActivity(Intent(baseContext(), ManageProductActivity::class.java))
        }
    }
}