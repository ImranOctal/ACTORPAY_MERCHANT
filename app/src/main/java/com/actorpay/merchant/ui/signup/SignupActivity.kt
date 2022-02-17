package com.actorpay.merchant.ui.signup

import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivitySignupBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.ui.content.ContentActivity
import com.actorpay.merchant.ui.content.ContentViewModel
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData
import com.actorpay.merchant.viewmodel.AuthViewModel
import com.actorpay.merchant.utils.countrypicker.CountryPicker
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit
import com.actorpay.merchant.utils.SingleClickListener


class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val signUpViewModel: AuthViewModel by inject()

    private var showPassword = false

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)

        installation()

        val codeList= mutableListOf<String>()
        GlobalData.allCountries.forEach {
            val code=it.countryCode
            codeList.add(code)
        }
        if(GlobalData.allCountries.size>0){
            binding.codePicker.text=GlobalData.allCountries[0].countryCode
        }
        binding.countryLayout.setOnClickListener {
            CountryPicker(this,viewModel.methodRepo,GlobalData.allCountries){
                binding.codePicker.text=GlobalData.allCountries[it].countryCode
            }.show()
        }

        apiResponse()
    }

    private fun installation() {
        binding.toolbar.back.visibility = View.INVISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.signup)
        signUpViewModel.methodRepo.makeTextLink(binding.signupTermsText, getString(R.string.terms_of_use), true, null) {
            ContentViewModel.type = 3
            startActivity(Intent(this, ContentActivity::class.java))
        }

        signUpViewModel.methodRepo.makeTextLink(binding.signupTermsText, getString(R.string.privacy_policy), true, null) {
            ContentViewModel.type = 2
            startActivity(Intent(this, ContentActivity::class.java))
        }

        binding.signupPasswordShowHide.setOnClickListener {
            if (showPassword) {
                binding.password.transformationMethod = PasswordTransformationMethod()
                showPassword = false
                binding.signupPasswordShowHide.setImageResource(R.drawable.hide)
                binding.password.setSelection(binding.password.text.toString().length)
            } else {
                binding.password.transformationMethod = null
                showPassword = true
                binding.signupPasswordShowHide.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                binding.password.setSelection(binding.password.text.toString().length)
            }
        }

        clickListeners()
    }

    private fun clickListeners() {
        binding.login.setOnClickListener {
            switchActivity(Intent(baseContext(), LoginActivity::class.java))
        }
        binding.signupbtn.setOnClickListener(object : SingleClickListener() {
            override fun performClick(view: View?) {
                validate()
            }
        })
    }


    fun validate() {
        if (binding.emailEdit.text.trim().isEmpty()) {
            binding.emailEdit.error = this.getString(R.string.email_empty)
            binding.emailEdit.requestFocus()

        } else if (!methods.isValidEmail(binding.emailEdit.text.toString())) {
            binding.emailEdit.error = this.getString(R.string.invalid_email)
            binding.emailEdit.requestFocus()

        }else if (binding.password.text.toString().trim().isEmpty()) {
            binding.password.error = this.getString(R.string.password_empty)
            binding.password.requestFocus()
        }
        else if (binding.password.text.toString().trim().length < 8 || !signUpViewModel.methodRepo.isValidPassword(binding.password.text.toString().trim())) {
            binding.password.error = this.getString(R.string.oops_your_password_is_not_valid)
            binding.password.requestFocus()

        } else if (binding.businessName.text.toString().trim().isEmpty()) {
            binding.businessName.error = this.getString(R.string.business_empty)
            binding.businessName.requestFocus()
        }
        else if (binding.businessName.text.toString().trim().length < 3) {
            binding.businessName.error = this.getString(R.string.error_business)
            binding.businessName.requestFocus()
        } else if (binding.mobileNumber.text.toString().trim().length < 7) {
            binding.mobileNumber.error = getString(R.string.error_phone)
            binding.mobileNumber.requestFocus()
        } else if (binding.mobileNumber.text.toString().trim()[0].toString() == "0") {
            binding.mobileNumber.error = getString(R.string.mobile_not_start_with_0)
            binding.mobileNumber.requestFocus()

        } else if (binding.shopAddress.text.toString().trim().isEmpty()) {
            binding.shopAddress.error = getString(R.string.shop_address_empty)
            binding.shopAddress.requestFocus()
        }else if (binding.shopAddress.text.toString().trim().length < 3) {
            binding.shopAddress.error = getString(R.string.error_shop_address)
            binding.shopAddress.requestFocus()

        } else if (binding.address.text.toString().trim().isEmpty()) {
            binding.address.error = getString(R.string.address_empty)
            binding.address.requestFocus()

        } else if (binding.address.text.toString().trim().length < 3) {
            binding.address.error = getString(R.string.address_error)
            binding.address.requestFocus()

        } else if (binding.shopAct.text.toString().trim().isEmpty()) {
            binding.shopAct.error = getString(R.string.shop_act_empty)
            binding.shopAct.requestFocus()
        }
        else if (binding.shopAct.text.toString().trim().length < 3) {
            binding.shopAct.error = getString(R.string.shop_Act_length)
            binding.shopAct.requestFocus()
        } else if (!binding.rememberMe.isChecked) {
            showCustomToast(getString(R.string.agree_our_terms_and_condition))
        } else {
            signUp()
        }
    }

    private fun apiResponse() {
        lifecycleScope.launch {
            signUpViewModel.loginResponseLive.collect {
                when (it) {
                    is AuthViewModel.ResponseLoginSealed.loading -> {
                        showLoadingDialog()
                    }
                    is AuthViewModel.ResponseLoginSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is SuccessResponse) {
                           CommonDialogsUtils.showCommonDialog(
                                this@SignupActivity,
                                signUpViewModel.methodRepo,
                                "Signed Up",
                                it.response.message,
                                autoCancelable = false,
                                isCancelAvailable = false,
                                isOKAvailable = true,
                                showClickable = false,
                                callback = object : CommonDialogsUtils.DialogClick {
                                    override fun onClick() {
                                        startActivity(
                                            Intent(
                                                this@SignupActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finishAffinity()
                                    }

                                    override fun onCancel() {

                                    }
                                }
                            )


                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }

                    }
                    is AuthViewModel.ResponseLoginSealed.ErrorOnResponse -> {
                        if(it.failResponse!!.code==403){
                            forcelogout(signUpViewModel.methodRepo)
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

    private fun signUp() {
        val countryCode = binding.codePicker.text.toString().trim()
        signUpViewModel.methodRepo.hideSoftKeypad(this)
        signUpViewModel.signUp(binding.emailEdit.text.toString().trim(), countryCode, binding.mobileNumber.text.toString().trim(), binding.password.text.toString().trim(), binding.shopAddress.text.toString().trim(), binding.address.text.toString().trim(), binding.businessName.text.toString().trim(), binding.shopAct.text.toString().trim(),)
    }
}