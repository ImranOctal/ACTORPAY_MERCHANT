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
import com.actorpay.merchant.utils.GlobalData
import com.actorpay.merchant.utils.SingleClickListener
import com.actorpay.merchant.utils.countrypicker.CountryPicker
import com.actorpay.merchant.viewmodel.AuthViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class SignupActivity : BaseActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val signUpViewModel: AuthViewModel by inject()

    private var showPassword = false

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)

        installation()

        val codeList = mutableListOf<String>()
        GlobalData.allCountries.forEach {
            val code = it.countryCode
            codeList.add(code)
        }
        if (GlobalData.allCountries.size > 0) {
            binding.codePicker.text = GlobalData.allCountries[0].countryCode
        }
        binding.countryLayout.setOnClickListener {
            CountryPicker(this, viewModel.methodRepo, GlobalData.allCountries) {
                binding.codePicker.text = GlobalData.allCountries[it].countryCode
            }.show()
        }

        apiResponse()
    }

    private fun installation() {
        binding.toolbar.back.visibility = View.INVISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.signup)
        signUpViewModel.methodRepo.makeTextLink(
            binding.signupTermsText,
            getString(R.string.terms_of_use),
            true,
            null
        ) {
            ContentViewModel.type = 3
            startActivity(Intent(this, ContentActivity::class.java))
        }

        signUpViewModel.methodRepo.makeTextLink(
            binding.signupTermsText,
            getString(R.string.privacy_policy),
            true,
            null
        ) {
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

//    fun validate(){
//        var isValidate=true
//        val countryCode = binding.codePicker.text.toString().trim()
//        if(binding.adhar.text.toString().trim().length<16){
//            isValidate=false
//            binding.adhar.error=getString(R.string.enter_valid_adhar)
////            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupAdhar, R.drawable.btn_search_outline)
//            binding.adhar.requestFocus()
////            binding.scrollView.smoothScrollTo(0,binding.adhar.top)
//        }
//        else{
//            binding.errorOnAdhar.visibility = View.GONE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupAdhar, R.drawable.btn_outline_gray)
//        }
//
//        if(!signupViewModel.methodRepo.isValidPAN(binding.pan.text.toString().trim())){
//            isValidate=false
//            binding.pan.error=getString(R.string.please_valid_pan)
////            binding.errorOnPan.visibility = View.VISIBLE
////            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupPan, R.drawable.btn_search_outline)
//            binding.pan.requestFocus()
////            binding.scrollView.smoothScrollTo(0,binding.pan.top)
//        }
//        else{
//            binding.errorOnPan.visibility = View.GONE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupPan, R.drawable.btn_outline_gray)
//        }
//
//
//        if(binding.dob.text.toString().trim().equals("")){
//            isValidate=false
//            binding.errorOnDate.visibility = View.VISIBLE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupDob, R.drawable.btn_search_outline)
////            binding.dob.requestFocus()
////            binding.scrollView.smoothScrollTo(0,binding.dob.top)
//        }
//        else{
//            binding.errorOnDate.visibility = View.GONE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupDob, R.drawable.btn_outline_gray)
//        }
//        if(binding.spinnerAutocomplete.text.toString().trim().equals("")){
//            isValidate=false
//            binding.errorOnGender.visibility = View.VISIBLE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupGender2, R.drawable.btn_search_outline)
////            binding.spinnerAutocomplete.requestFocus()
//            binding.scrollView.smoothScrollTo(0,binding.spinnerAutocomplete.top)
//        }
//        else{
//            binding.errorOnGender.visibility = View.GONE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupGender2, R.drawable.btn_outline_gray)
//        }
//        if (binding.password.text.toString().trim().length<8) {
//            isValidate=false
//            binding.password.error=getString(R.string.oops_your_password_is_not_valid)
////            binding.errorOnPassword.visibility = View.VISIBLE
////            binding.errorOnPassword.text = getString(R.string.oops_your_password_is_not_valid)
////            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupPassword, R.drawable.btn_search_outline)
//            binding.password.requestFocus()
//        }
//        else{
//            if (binding.password.text.toString().trim().contains(" ") || !signupViewModel.methodRepo.isValidPassword(binding.password.text.toString().trim())) {
//                isValidate=false
//                binding.password.error = getString(R.string.oops_your_password_is_not_valid2)
////                binding.errorOnPassword.visibility = View.VISIBLE
////                signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupPassword, R.drawable.btn_search_outline)
//                binding.password.requestFocus()
//            }
//            else{
//                binding.errorOnPassword.visibility = View.GONE
//                signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupPassword, R.drawable.btn_outline_gray)
//            }
//        }
//
//        if (binding.email.text.toString().length<3 || !signupViewModel.methodRepo.isValidEmail(binding.email.text.toString())) {
//            isValidate=false
//            binding.email.error=getString(R.string.oops_your_email_is_not_correct_or_empty)
////            binding.errorOnEmail.visibility = View.VISIBLE
////            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupEmail, R.drawable.btn_search_outline)
//            binding.email.requestFocus()
//        }
//        else{
//            binding.errorOnEmail.visibility = View.GONE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupEmail, R.drawable.btn_outline_gray)
//        }
//
//        if (binding.lastName.text.toString().trim().length<3) {
//            isValidate=false
//            binding.lastName.error=getString(R.string.error_l_name)
////            binding.errorOnLastName.visibility = View.VISIBLE
////            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupLast, R.drawable.btn_search_outline)
//            binding.lastName.requestFocus()
//        }
//        else{
//            binding.errorOnLastName.visibility = View.GONE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupLast, R.drawable.btn_outline_gray)
//        }
//
//        if (binding.firstName.text.toString().trim().isEmpty()) {
//            isValidate=false
//            binding.firstName.error=getString(R.string.error_name)
////            binding.errorOnName.visibility = View.VISIBLE
////            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupFirst, R.drawable.btn_search_outline)
//            binding.firstName.requestFocus()
//        }
//        else{
//            binding.errorOnName.visibility = View.GONE
//            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupFirst, R.drawable.btn_outline_gray)
//        }
//
//        if (binding.editTextMobile.text.toString().trim().length<7) {
//            isValidate=false
////            binding.errorOnPhone.visibility = View.VISIBLE
//            binding.editTextMobile.error=getString(R.string.error_phone)
////            signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupPhone, R.drawable.btn_search_outline)
//            binding.editTextMobile.requestFocus()
//        }
//        else{
//            if(binding.editTextMobile.text.toString().trim()[0].toString() == "0")
//            {
//                isValidate=false
////                binding.errorOnPhone.visibility = View.VISIBLE
//                binding.editTextMobile.error=getString(R.string.mobile_not_start_with_0)
////                signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupPhone, R.drawable.btn_search_outline)
//                binding.editTextMobile.requestFocus()
//            }
//            else{
//                binding.errorOnPhone.visibility = View.GONE
//                signupViewModel.methodRepo.setBackGround(requireContext(), binding.signupPhone, R.drawable.btn_outline_gray)
//            }
//        }
//        if(!binding.signCheckTerms.isChecked){
//            isValidate=false
//            showCustomToast("Please agree to our terms to sign up")
//        }
//        if(isValidate){
//
////            val countryCode=binding.ccp.selectedCountryCodeWithPlus
//
//
//            )
//        }
//    }

    fun validate() {
        var isValidate = true
        if (binding.shopAct.text.toString().trim().isEmpty()) {
            binding.shopAct.error = getString(R.string.shop_act_empty)
            binding.shopAct.requestFocus()
            isValidate = false

        } else if (binding.shopAct.text.toString().trim().length < 3) {
            binding.shopAct.error = getString(R.string.shop_Act_length)
            binding.shopAct.requestFocus()
            isValidate = false
        }
        if (binding.address.text.toString().trim().isEmpty()) {
            binding.address.error = getString(R.string.address_empty)
            binding.address.requestFocus()
            isValidate = false
        } else if (binding.address.text.toString().trim().length < 3) {
            binding.address.error = getString(R.string.address_error)
            binding.address.requestFocus()
            isValidate = false

        }
        if (binding.shopAddress.text.toString().trim().isEmpty()) {
            binding.shopAddress.error = getString(R.string.shop_address_empty)
            binding.shopAddress.requestFocus()
            isValidate = false

        } else if (binding.shopAddress.text.toString().trim().length < 3) {
            binding.shopAddress.error = getString(R.string.error_shop_address)
            binding.shopAddress.requestFocus()
            isValidate = false
        }

        if (binding.mobileNumber.text.toString().trim().isEmpty()) {
            binding.mobileNumber.error = getString(R.string.phone_empty)
            binding.mobileNumber.requestFocus()
            isValidate = false
        } else if (binding.mobileNumber.text.toString().trim().length < 7) {
            binding.mobileNumber.error = getString(R.string.error_phone)
            binding.mobileNumber.requestFocus()
            isValidate = false
        }

        if (binding.mobileNumber.text.startsWith("0")) {
            binding.mobileNumber.error = getString(R.string.mobile_not_start_with_0)
            binding.mobileNumber.requestFocus()
            isValidate = false
        }

        if (binding.businessName.text.toString().trim().isEmpty()) {
            binding.businessName.error = this.getString(R.string.business_empty)
            binding.businessName.requestFocus()
            isValidate = false
        }

        if (binding.password.text.toString().trim().isEmpty()) {
            binding.password.error = this.getString(R.string.password_empty)
            binding.password.requestFocus()
            isValidate = false
        }

       else if (binding.password.text.toString().trim().length < 8 || !signUpViewModel.methodRepo.isValidPassword(binding.password.text.toString().trim())) {
            binding.password.error = this.getString(R.string.oops_your_password_is_not_valid)
            binding.password.requestFocus()
            isValidate = false
        }

        if (binding.emailEdit.text.trim().isEmpty()) {
            binding.emailEdit.error = this.getString(R.string.email_empty)
            binding.emailEdit.requestFocus()
            isValidate = false
        }
        if (!methods.isValidEmail(binding.emailEdit.text.toString())) {
            binding.emailEdit.error = this.getString(R.string.invalid_email)
            binding.emailEdit.requestFocus()
            isValidate = false
        }
        if (!binding.rememberMe.isChecked) {
            showCustomToast(getString(R.string.agree_our_terms_and_condition))
            isValidate = false
        }
        if (isValidate) {
            signUp()
        }


        //        if (binding.shopAct.text.toString().trim().isEmpty()) {
//            binding.shopAct.error = getString(R.string.shop_act_empty)
//            binding.shopAct.requestFocus()
//            isValidate=false
//        }
    }
//    fun validate() {
//        var isValidate=true;
//        if (binding.emailEdit.text.trim().isEmpty()) {
//            binding.emailEdit.error = this.getString(R.string.email_empty)
//            binding.emailEdit.requestFocus()
//            isValidate=false
//        }
//        if (!methods.isValidEmail(binding.emailEdit.text.toString())) {
//            binding.emailEdit.error = this.getString(R.string.invalid_email)
//            binding.emailEdit.requestFocus()
//            isValidate=false
//        }
//        if (binding.password.text.toString().trim().isEmpty()) {
//            binding.password.error = this.getString(R.string.password_empty)
//            binding.password.requestFocus()
//            isValidate=false
//        }
//        else  if (binding.password.text.toString().trim().length < 8 || !signUpViewModel.methodRepo.isValidPassword(binding.password.text.toString().trim())) {
//            binding.password.error = this.getString(R.string.oops_your_password_is_not_valid)
//            binding.password.requestFocus()
//            isValidate=false
//        }
//
//        if (binding.businessName.text.toString().trim().isEmpty()) {
//            binding.businessName.error = this.getString(R.string.business_empty)
//            binding.businessName.requestFocus()
//            isValidate=false
//        }
//       else   if (binding.businessName.text.toString().trim().length < 3) {
//            binding.businessName.error = this.getString(R.string.error_business)
//            binding.businessName.requestFocus()
//            isValidate=false
//        }
//       else  if (binding.mobileNumber.text.toString().trim().length < 7) {
//            binding.mobileNumber.error = getString(R.string.error_phone)
//            binding.mobileNumber.requestFocus()
//            isValidate=false
//        }
//        if (binding.mobileNumber.text.startsWith("0")) {
//            binding.mobileNumber.error = getString(R.string.mobile_not_start_with_0)
//            binding.mobileNumber.requestFocus()
//            isValidate=false
//
//        }
//        if (binding.shopAddress.text.toString().trim().isEmpty()) {
//            binding.shopAddress.error = getString(R.string.shop_address_empty)
//            binding.shopAddress.requestFocus()
//        }else if (binding.shopAddress.text.toString().trim().length < 3) {
//            binding.shopAddress.error = getString(R.string.error_shop_address)
//            binding.shopAddress.requestFocus()
//            isValidate=false
//
//        }
//
//        if (binding.address.text.toString().trim().isEmpty()) {
//            binding.address.error = getString(R.string.address_empty)
//            binding.address.requestFocus()
//            isValidate=false
//        }
//
//        if (binding.address.text.toString().trim().length < 3) {
//            binding.address.error = getString(R.string.address_error)
//            binding.address.requestFocus()
//            isValidate=false
//
//        }
//        if (binding.shopAct.text.toString().trim().isEmpty()) {
//            binding.shopAct.error = getString(R.string.shop_act_empty)
//            binding.shopAct.requestFocus()
//            isValidate=false
//        }
//         else if (binding.shopAct.text.toString().trim().length < 3) {
//            binding.shopAct.error = getString(R.string.shop_Act_length)
//            binding.shopAct.requestFocus()
//            isValidate=false
//
//
//        } else if (!binding.rememberMe.isChecked) {
//            showCustomToast(getString(R.string.agree_our_terms_and_condition))
//            isValidate=false
//        }
//
//        if(isValidate){
//            signUp()
//        }
//    }

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
                            showCustomAlert(getString(R.string.please_try_after_sometime), binding.root)
                        }
                    }
                    is AuthViewModel.ResponseLoginSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (it.failResponse!!.code == 403) {
                            forcelogout(signUpViewModel.methodRepo)
                        } else {
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
        signUpViewModel.signUp(
            binding.emailEdit.text.toString().trim(),
            countryCode,
            binding.mobileNumber.text.toString().trim(),
            binding.password.text.toString().trim(),
            binding.shopAddress.text.toString().trim(),
            binding.address.text.toString().trim(),
            binding.businessName.text.toString().trim(),
            binding.shopAct.text.toString().trim(),
        )
    }
}