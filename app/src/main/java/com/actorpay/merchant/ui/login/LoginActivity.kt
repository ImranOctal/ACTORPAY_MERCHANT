package com.actorpay.merchant.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityLoginBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.ForgetPasswordResponses
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.LoginResponses
import com.actorpay.merchant.ui.home.HomeActivity
import com.actorpay.merchant.ui.signup.SignupActivity
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
    var isPasswrodValid=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        init()
        clickListeners()
        apiResponse()
    }

    fun init(){

        binding.emailEdit.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty() || loginViewModel.methodRepo.isValidEmail(text.toString().trim())) {
                binding.errorOnEmail.visibility = View.GONE
            } else{
                binding.errorOnEmail.visibility = View.VISIBLE
            }
        }
        binding.password.doOnTextChanged {  text, start, before, count ->

            val password = text.toString()
            var temp = ""
            if (password.length < 8)
                temp = getString(R.string.error_passord_8_characters)
            if (!loginViewModel.methodRepo.isSpecialCharacter(password))
                temp = getString(R.string.error_password_special)
            if (!loginViewModel.methodRepo.isDigit(password))
                temp = getString(R.string.error_password_digit)
            if (!loginViewModel.methodRepo.isSmallLetter(password))
                temp = getString(R.string.error_password_small)
            if (!loginViewModel.methodRepo.isCapitalLetter(password))
                temp = getString(R.string.error_password_capital)

            if (temp!="" && password.length != 0) {
                binding.errorOnPassword.visibility = View.VISIBLE
                binding.errorOnPassword.text = temp
            } else {
                binding.errorOnPassword.visibility = View.GONE
                binding.errorOnPassword.text = ""
            }

            isPasswrodValid=temp==""

        }
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
            }else {
                binding.password.transformationMethod = null
                showPassword = true
                binding.loginPasswordShowHide.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                binding.password.setSelection(binding.password.text.toString().length)
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }
      }

      private fun validate() {
          var isValid=true
          if(!isPasswrodValid){
              binding.password.error = this.getString(R.string.oops_your_password_is_not_valid)
              binding.password.requestFocus()
              isValid=false
          }
        if (binding.emailEdit.text.isEmpty()) {
            binding.emailEdit.error=getString(R.string.email_empty)
            binding.emailEdit.requestFocus()
            isValid=false

        }
          if (!methods.isValidEmail(binding.emailEdit.text.toString())) {
            binding.emailEdit.error=getString(R.string.invalid_email)
            binding.emailEdit.requestFocus()
            isValid=false

        }

        if(isValid) {
            login()
        }
    }
    private fun apiResponse() {
        lifecycleScope.launch {
            loginViewModel.loginResponseLive.collect {
                when (it) {
                    is AuthViewModel.ResponseLoginSealed.loading -> {
                        showLoadingDialog()
                    }
                    is AuthViewModel.ResponseLoginSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is LoginResponses) {
                            viewModel.methodRepo.dataStore.setUserId(it.response.data.id)
                            viewModel.methodRepo.dataStore.setIsLoggedIn(true)
                            viewModel.methodRepo.dataStore.setEmail(it.response.data.email)
                            viewModel.methodRepo.dataStore.setAccessToken(it.response.data.access_token)
                            viewModel.methodRepo.dataStore.setRefreshToken(it.response.data.refresh_token)
                            viewModel.methodRepo.dataStore.setBussinessName(it.response.data.businessName)
                            viewModel.methodRepo.dataStore.setRole(it.response.data.role)
                            showCustomAlert("Logged in Successfully", binding.root)
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
                        hideLoadingDialog()
                        if (it.failResponse!!.code == 403) {
                            forcelogout(loginViewModel.methodRepo)
                        }else{
                            showCustomAlert(
                                it.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    else -> {
                        hideLoadingDialog()
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