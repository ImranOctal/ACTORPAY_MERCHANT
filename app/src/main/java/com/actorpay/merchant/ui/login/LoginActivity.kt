package com.actorpay.merchant.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityLoginBinding
import com.actorpay.merchant.ui.home.HomeActivity
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var disposable: Disposable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)
       Installation()
    }

    private fun Installation() {
     /*   binding.signinBtn.setOnClickListener {
            switchActivity(Intent(baseContext(), HomeActivity::class.java))
        }*/
        ClickListeners()
    }

    private fun ClickListeners() {
        disposable = binding.signinBtn.clicks().throttleFirst(CLICK_TIME, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe { unit: Unit ->
                if (binding.emailEdit.text == null || binding.emailEdit.text.isEmpty()) {
                    binding.errorOnEmail.visibility = View.VISIBLE
                    binding.errorOnEmail.text = getString(R.string.email_empty)
                    binding.errorOnPassword.visibility = View.GONE
                    methods.setBackGround(baseContext(), binding.emailLay, R.drawable.btn_search_outline)
                } else if (!methods.isValidEmail(binding.emailEdit.text.toString())) {
                    binding.errorOnEmail.visibility = View.VISIBLE
                    binding.errorOnEmail.text = getString(R.string.invalid_email)
                    binding.errorOnPassword.visibility = View.GONE
                    methods.setBackGround(baseContext(), binding.emailLay, R.drawable.btn_search_outline)
                } else if (binding.password.text == null || binding.password.text.isEmpty()) {
                    binding.errorOnPassword.visibility = View.VISIBLE
                    binding.errorOnPassword.text = getString(R.string.oops_your_password_is_empty)
                    binding.errorOnEmail.visibility = View.GONE
                    methods.setBackGround(baseContext(), binding.passLay, R.drawable.btn_search_outline)
                } else if (!binding.rememberMe.isChecked) {
                    showCustomAlert(getString(R.string.remember_me), binding.root)
                } else {
                    binding.errorOnEmail.visibility = View.GONE
                    binding.errorOnPassword.visibility = View.GONE
                    methods.setBackGround(baseContext(), binding.emailLay, R.drawable.btn_outline_gray)
                    methods.setBackGround(baseContext(), binding.passLay, R.drawable.btn_outline_gray)
                    switchActivity(Intent(baseContext(), HomeActivity::class.java))
                }

            }
    }

    override fun onResume() {
        super.onResume()
        SetToolbar()
    }
    private fun SetToolbar() {
        binding.toolbar.back.visibility= View.INVISIBLE
        binding.toolbar.ToolbarTitle.text=getString(R.string.login)
    }
}