package com.actorpay.merchant.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityLoginBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.ForgetPasswordResponses
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.LoginResponses
import com.actorpay.merchant.ui.home.HomeActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.viewmodel.AuthViewModel
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var disposable: Disposable
    private var showPassword=false
    private val loginViewModel: AuthViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        installation()
        apiResponse()
    }

    private fun installation() {
        /*   binding.signInBtn.setOnClickListener {
               switchActivity(Intent(baseContext(), HomeActivity::class.java))
           }*/
        clickListeners()

    }
    private fun clickListeners() {
        disposable = binding.signinBtn.clicks().throttleFirst(CLICK_TIME, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
             validate()
            }
        disposable = binding.forgetPassword.clicks().throttleFirst(CLICK_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    forgetPassword()
                }

        binding.loginPasswordShowHide.setOnClickListener {
            if (showPassword) {
                binding.password.transformationMethod = PasswordTransformationMethod()
                showPassword = false
                binding.loginPasswordShowHide.setImageResource(R.drawable.hide)
                binding.password.setSelection(binding.password.text.toString().length)
            } else {
                binding.password.transformationMethod = null
                showPassword = true
                binding.loginPasswordShowHide.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                binding.password.setSelection(binding.password.text.toString().length)
            }
        }
      }

      private fun validate() {
        if (binding.emailEdit.text.isEmpty()) {

            binding.emailEdit.error=getString(R.string.email_empty)
            binding.emailEdit.requestFocus()

        } else if (!methods.isValidEmail(binding.emailEdit.text.toString())) {
            binding.emailEdit.error=getString(R.string.invalid_email)
            binding.emailEdit.requestFocus()

        } else if (binding.password.text.isEmpty()) {
            binding.password.error=getString(R.string.oops_your_password_is_empty)
            binding.password.requestFocus()

        }else if (!binding.rememberMe.isChecked) {
            showCustomToast(getString(R.string.agree_our_terms_and_condition_login))
        } else {

            login()

        }
    }

    private fun apiResponse() {

        lifecycleScope.launch {
            loginViewModel.loginResponseLive.collect {
                when (it) {
                    is AuthViewModel.ResponseLoginSealed.loading -> {
                        loginViewModel.methodRepo.showLoadingDialog(this@LoginActivity)
                    }
                    is AuthViewModel.ResponseLoginSealed.Success -> {

                        if (it.response is LoginResponses) {
                            viewModel.methodRepo.dataStore.setUserId(it.response.data.id)
                            viewModel.methodRepo.dataStore.setIsLoggedIn(true)
                            viewModel.methodRepo.dataStore.setEmail(it.response.data.email)
                            viewModel.methodRepo.dataStore.setAccessToken(it.response.data.access_token)
                            viewModel.methodRepo.dataStore.setRefreshToken(it.response.data.refresh_token)
                            viewModel.methodRepo.dataStore.setBussinessName(it.response.data.businessName)
                            showCustomAlert(
                                "Logged in Successfully",
                                binding.root
                            )
                            delay(1000)
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finishAffinity()
                        } else if (it.response is ForgetPasswordResponses) {
                            CommonDialogsUtils.showCommonDialog(
                                this@LoginActivity,
                                loginViewModel.methodRepo,
                                getString(R.string.forget_password),
                                it.response.message
                            )
                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }
                    }
                    is AuthViewModel.ResponseLoginSealed.ErrorOnResponse -> {
                        loginViewModel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        loginViewModel.methodRepo.hideLoadingDialog()
                    }
                }
            }
        }
    }
    fun login() {

        loginViewModel.methodRepo.hideSoftKeypad(this)
        loginViewModel.login(
            binding.emailEdit.text.toString().trim(),
            binding.password.text.toString().trim()
        )
    }


    fun forgetPassword() {
        ForgetPasswordDialog().show(this, loginViewModel.methodRepo) { email ->
            loginViewModel.forgetPassword(email)
        }
    }

    override fun onResume() {
        super.onResume()
        setToolbar()
    }
    private fun setToolbar() {
        binding.toolbar.back.visibility = View.INVISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.login)
    }
}