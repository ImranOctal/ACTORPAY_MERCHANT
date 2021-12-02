package com.actorpay.merchant.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityProfileBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileReesponse
import com.actorpay.merchant.utils.CommonDialogsUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.lang.Exception

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_profile)
        initialisation()
    }

    private fun initialisation() {

        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.my_profile)
        clickListeners()
        getProfile()
        apiResponse()


    }

    fun apiResponse() {
        lifecycleScope.launch {

            profileViewModel.profileResponseLive.collect {
                when (it) {

                    is ProfileViewModel.ResponseProfileSealed.loading -> {
                        profileViewModel.methodRepo.showLoadingDialog(this@ProfileActivity)
                    }
                    is ProfileViewModel.ResponseProfileSealed.Success -> {
                        profileViewModel.methodRepo.hideLoadingDialog()
                        if (it.response is ProfileReesponse) {
                            val reesponse2=it.response
                            binding.emailEdit.setText(reesponse2.email)
                            binding.businessName.setText(reesponse2.businessName)
                            binding.shopAddress.setText(reesponse2.shopAddress)
                            binding.address.setText(reesponse2.fullAddress)
                            binding.shopAct.setText(reesponse2.licenceNumber)
                            binding.mobileNumber.setText(reesponse2.contactNumber)
                            try {
                                var extContact = reesponse2.extensionNumber
                                if (extContact.isNotEmpty()) {
                                    extContact = extContact.replace("+", "")
                                    binding.profileCcp.setCountryForPhoneCode(extContact.toInt())
                                }
                            } catch (e: Exception) {
                                Log.d("Profile Fragment", "apiResponse: ${e.message}")
                            }
                        }
                        else if(it.response is SuccessResponse){
                            CommonDialogsUtils.showCommonDialog(this@ProfileActivity,profileViewModel.methodRepo,getString(
                                                            R.string.profile_update),it.response.message)
                        }

                        else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }
                    }
                    is ProfileViewModel.ResponseProfileSealed.ErrorOnResponse -> {
                        profileViewModel.methodRepo.hideLoadingDialog()
                      showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    is ProfileViewModel.ResponseProfileSealed.Empty -> {
                        profileViewModel.methodRepo.hideLoadingDialog()
                    }

                }
            }
        }
    }

    fun validate(){
        if (binding.emailEdit.text.toString().length<3 || !profileViewModel.methodRepo.isValidEmail(binding.emailEdit.text.toString())) {
            binding.errorOnEmail.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_search_outline)
        }
       /* else if (binding.password.text.toString().trim().length<8 || !profileViewModel.methodRepo.isValidPassword(binding.password.text.toString().trim())) {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnPassword.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.passLay, R.drawable.btn_search_outline)
        }*/
        else if (binding.businessName.text.toString().trim().length<3) {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility=View.VISIBLE
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.businessLay, R.drawable.btn_search_outline)
        }
        else if (binding.mobileNumber.text.toString().trim().length<6) {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility=View.GONE
            binding.errorOnMobile.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.businessLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_search_outline)
        }
        else if(binding.mobileNumber.text.toString().trim()[0].toString() == "0")
        {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility=View.GONE
            binding.errorOnMobile.visibility = View.VISIBLE
            binding.errorOnMobile.text=getString(R.string.mobile_not_start_with_0)
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.businessLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_search_outline)
        }
        else if(binding.shopAddress.text.toString().trim().length<3)
        {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility=View.GONE
            binding.errorOnMobile.visibility = View.GONE
            binding.errorOnShopAddress.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.businessLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.shopLay, R.drawable.btn_search_outline)
        }
        else if(binding.address.text.toString().trim().length<3)
        {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility=View.GONE
            binding.errorOnMobile.visibility = View.GONE
            binding.errorOnShopAddress.visibility = View.GONE
            binding.errorOnfullAddress.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.businessLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.shopLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.addressLay, R.drawable.btn_search_outline)
        }
        else if(binding.shopAct.text.toString().trim().length<3)
        {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility=View.GONE
            binding.errorOnMobile.visibility = View.GONE
            binding.errorOnShopAddress.visibility = View.GONE
            binding.errorOnfullAddress.visibility = View.GONE
            binding.errorOnShopAct.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.businessLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.addressLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.shopLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.ShopActLay, R.drawable.btn_search_outline)
        }
        else{
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility=View.GONE
            binding.errorOnMobile.visibility = View.GONE
            binding.errorOnShopAddress.visibility = View.GONE
            binding.errorOnfullAddress.visibility = View.GONE
            binding.errorOnShopAct.visibility = View.GONE
            profileViewModel.methodRepo.setBackGround(this, binding.emailLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.businessLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.mobileLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.addressLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.shopLay, R.drawable.btn_outline_gray)
            profileViewModel.methodRepo.setBackGround(this, binding.ShopActLay, R.drawable.btn_outline_gray)
            saveProfile()
        }
    }

    private fun saveProfile(){
        val email=binding.emailEdit.text.toString().trim()
        val contactNumber=binding.mobileNumber.text.toString().trim()
        val ext=binding.profileCcp.selectedCountryCodeWithPlus
        val shopAddress=binding.shopAddress.text.toString().trim()
        val fullAddress=binding.address.text.toString().trim()
        val businessName=binding.businessName.text.toString().trim()
        val licenceNumber=binding.shopAct.text.toString().trim()
        profileViewModel.saveProfile(email,shopAddress, fullAddress, businessName, licenceNumber)
    }

    private fun getProfile(){
        profileViewModel.getProfile()
    }

    private fun clickListeners() {
        binding.btnSaveProfile.setOnClickListener {
            validate()
        }
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
    }
}