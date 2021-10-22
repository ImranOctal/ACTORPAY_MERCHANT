package com.actorpay.merchant.ui.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityLoginBinding
import com.actorpay.merchant.databinding.ActivitySignupBinding
import com.actorpay.merchant.ui.login.LoginActivity

class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_signup)
        Installation()
    }

    private fun Installation() {
        binding.toolbar.back.visibility= View.INVISIBLE
        binding.toolbar.ToolbarTitle.text=getString(R.string.signup)
        ClickListners()
    }

    private fun ClickListners() {
        binding.login.setOnClickListener {
            switchActivity(Intent(baseContext(), LoginActivity::class.java))
        }
    }
}