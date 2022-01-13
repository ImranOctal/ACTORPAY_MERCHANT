package com.actorpay.merchant.ui.signup

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
import com.actorpay.merchant.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signUpViewModel: AuthViewModel by inject()
    private var showPassword=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_signup)
        installation()
        apiResponse()
    }

    private fun installation() {
        binding.toolbar.back.visibility= View.INVISIBLE
        binding.toolbar.ToolbarTitle.text=getString(R.string.signup)

        signUpViewModel.methodRepo.makeTextLink(binding.signupTermsText,getString(R.string.terms_of_use),true,null){
            ContentViewModel.type=3
            startActivity(Intent(this, ContentActivity::class.java))
        }
        signUpViewModel.methodRepo.makeTextLink(binding.signupTermsText,getString(R.string.privacy_policy),true,null){
            ContentViewModel.type=2
            startActivity(Intent(this,ContentActivity::class.java))
        }
        binding.signupPasswordShowHide.setOnClickListener {
            if(showPassword)
            {
                binding.password.transformationMethod = PasswordTransformationMethod()
                showPassword=false
            }
            else{
                binding.password.transformationMethod = null
                showPassword=true
            }
        }

        clickListeners()
    }

    private fun clickListeners() {
        binding.login.setOnClickListener {
            switchActivity(Intent(baseContext(), LoginActivity::class.java))
        }
        binding.signupbtn.setOnClickListener {
            validate()
        }
    }
    fun validate(){
        var isValidate=true
        if (binding.emailEdit.text.toString().length<3 || !signUpViewModel.methodRepo.isValidEmail(binding.emailEdit.text.toString())) {
            isValidate=false
            binding.errorOnEmail.visibility = View.VISIBLE
        }
        else{

            binding.errorOnEmail.visibility = View.GONE

        }
        if (binding.password.text.toString().trim().length<8 || !signUpViewModel.methodRepo.isValidPassword(binding.password.text.toString().trim())) {
            isValidate=false
            binding.errorOnPassword.visibility = View.VISIBLE
            signUpViewModel.methodRepo.setBackGround(this, binding.passLay, R.drawable.btn_search_outline)
        }
        else{
            binding.errorOnPassword.visibility = View.GONE
            signUpViewModel.methodRepo.setBackGround(this, binding.passLay, R.drawable.btn_outline_gray)
        }
        if (binding.businessName.text.toString().trim().length<3) {
            isValidate=false
            binding.errorOnBusinessName.visibility=View.VISIBLE
        }
        else{
            binding.errorOnBusinessName.visibility=View.GONE

        }
        if (binding.mobileNumber.text.toString().trim().length<6) {
            isValidate=false
            binding.errorOnMobile.visibility = View.VISIBLE
            binding.errorOnMobile.text=getString(R.string.error_phone)
            signUpViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_search_outline)
        }
        else {
            if(binding.mobileNumber.text.toString().trim()[0].toString() == "0")
            {
                isValidate=false
                binding.errorOnMobile.visibility = View.VISIBLE
                binding.errorOnMobile.text=getString(R.string.mobile_not_start_with_0)
                signUpViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_search_outline)
            }
            else{
                binding.errorOnMobile.visibility = View.GONE
                signUpViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_outline_gray)
            }
        }

        if(binding.shopAddress.text.toString().trim().length<3) {
            isValidate=false
            binding.errorOnShopAddress.visibility = View.VISIBLE
        }

        else{
            binding.errorOnShopAddress.visibility = View.GONE
        }
        if(binding.address.text.toString().trim().length<3)
        {
            isValidate=false
            binding.errorOnfullAddress.visibility = View.VISIBLE
        }
        else{
            binding.errorOnfullAddress.visibility = View.GONE
        }
        if(binding.shopAct.text.toString().trim().length<3)
        {
            isValidate=false
            binding.errorOnShopAct.visibility = View.VISIBLE
        }
        else{
            binding.errorOnShopAct.visibility = View.GONE
        }
        if (!binding.rememberMe.isChecked){
            isValidate=false
            showCustomToast(getString(R.string.agree_our_terms_and_condition))
        }
        if(isValidate){
           signUp()
        }
    }

    private fun apiResponse() {
        lifecycleScope.launch {

            signUpViewModel.loginResponseLive.collect {
                when(it){
                    is AuthViewModel.ResponseLoginSealed.loading->{
                        signUpViewModel.methodRepo.showLoadingDialog(this@SignupActivity)
                    }
                    is AuthViewModel.ResponseLoginSealed.Success -> {
                        signUpViewModel.methodRepo.hideLoadingDialog()
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
                        }
                        else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }

                    }
                    is AuthViewModel.ResponseLoginSealed.ErrorOnResponse -> {
                        signUpViewModel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        signUpViewModel.methodRepo.hideLoadingDialog()
                    }
                }

            }
        }
    }

    private fun signUp(){
        val countryCode=binding.singupCcp.selectedCountryCodeWithPlus
        signUpViewModel.methodRepo.hideSoftKeypad(this)
        signUpViewModel.signUp(binding.emailEdit.text.toString().trim(), countryCode,
            binding.mobileNumber.text.toString().trim(),
            binding.password.text.toString().trim(),
            binding.shopAddress.text.toString().trim(),
            binding.address.text.toString().trim(),
            binding.businessName.text.toString().trim(),
            binding.shopAct.text.toString().trim(),
        )
    }
}