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
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.MerchantSettingsDTO
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.utils.CommonDialogsUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
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
                    is HomeSealedClasses.Companion.ResponseSealed.loading -> {
                        showLoadingDialog()
                    }
                    is HomeSealedClasses.Companion.ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetUserById) {
                            updateUI(it.response)
                        } else if (it.response is SuccessResponse) {
                            CommonDialogsUtils.showCommonDialog(
                                this@ProfileActivity, profileViewModel.methodRepo, getString(
                                    R.string.profile_update
                                ), it.response.message
                            )
                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }
                    }
                    is HomeSealedClasses.Companion.ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if(it.failResponse!!.code==403){
                            forcelogout(profileViewModel.methodRepo)
                        }else{
                            showCustomAlert(
                                it.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    is HomeSealedClasses.Companion.ResponseSealed.Empty -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
    }

    fun updateUI(reesponse2: GetUserById){

        binding.emailEdit.setText(reesponse2.data.email)
        binding.businessName.setText(reesponse2.data.businessName)
        binding.shopAddress.setText(reesponse2.data.shopAddress)
        binding.address.setText(reesponse2.data.fullAddress)
        binding.shopAct.setText(reesponse2.data.licenceNumber)
        binding.mobileNumber.setText(reesponse2.data.contactNumber)
        binding.textView.text = reesponse2.data.businessName
        profileViewModel.merchantSettingsDTOList.clear()
        profileViewModel.merchantSettingsDTOList.addAll(reesponse2.data.merchantSettingsDTOS)

        var adminCommission="0"
        var returnFee="0"
        var cancellationFee="0"
        profileViewModel.merchantSettingsDTOList.forEach {
            if(it.paramName == "return-fee")
                returnFee=it.paramValue
            else if(it.paramName == "cancellation-fee")
                cancellationFee=it.paramValue
            else if(it.paramName == "admin-commission")
                adminCommission=it.paramValue
        }

        binding.adminCommission.setText(adminCommission)
        binding.cancellation.setText(cancellationFee)
        binding.returnedt.setText(returnFee)



        try {
            var extContact = reesponse2.data.extensionNumber
            if (extContact.isNotEmpty()) {
                extContact = extContact.replace("+", "")
                binding.codePicker.text=extContact
            }
        } catch (e: Exception) {
            Log.d("Profile Fragment", "apiResponse: ${e.message}")
        }
    }

    fun validate() {
        val returnFee=binding.returnedt.text.toString()
        val cancellationFee=binding.cancellation.text.toString()
        var isRerunFee=false
        var isCancelFee=false
        try {
            val returnF=returnFee.toDouble()
            if(returnF >=1 && returnF <=99)
                isRerunFee=true
        }
        catch (e:Exception){ }
        try {
            val cancellationF=cancellationFee.toDouble()
            if(cancellationF >=1 && cancellationF <=99)
                isCancelFee=true
        }
        catch (e:Exception){ }
        if (binding.emailEdit.text.toString().length < 3 || !profileViewModel.methodRepo.isValidEmail(
                binding.emailEdit.text.toString())) {
            binding.errorOnEmail.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(
                this,
                binding.emailLay,
                R.drawable.btn_search_outline
            )
        }
        else if (binding.businessName.text.toString().trim().isEmpty()) {
            binding.businessName.error = getString(R.string.business_empty)
            binding.businessName.requestFocus()
        } else if (binding.businessName.text.toString().trim().length < 3) {
            binding.businessName.error = getString(R.string.error_business)
            binding.businessName.requestFocus()

        } else if (binding.mobileNumber.text.toString().trim().length < 6) {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility = View.GONE
            binding.errorOnMobile.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(
                this,
                binding.emailLay,
                R.drawable.btn_outline_gray
            )
            profileViewModel.methodRepo.setBackGround(
                this,
                binding.businessLay,
                R.drawable.btn_outline_gray
            )
            profileViewModel.methodRepo.setBackGround(
                this,
                binding.mobileLay,
                R.drawable.btn_search_outline
            )

        } else if (binding.mobileNumber.text.toString().trim()[0].toString() == "0") {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility = View.GONE
            binding.errorOnMobile.visibility = View.VISIBLE
            binding.errorOnMobile.text = getString(R.string.mobile_not_start_with_0)
            profileViewModel.methodRepo.setBackGround(
                this,
                binding.emailLay,
                R.drawable.btn_outline_gray
            )
            profileViewModel.methodRepo.setBackGround(
                this,
                binding.businessLay,
                R.drawable.btn_outline_gray
            )
            profileViewModel.methodRepo.setBackGround(
                this,
                binding.mobileLay,
                R.drawable.btn_search_outline
            )


        } else if (binding.shopAddress.text.toString().trim().isEmpty()) {
            binding.shopAddress.error = getString(R.string.shop_address_empty)
            binding.shopAddress.requestFocus()

        } else if (binding.shopAddress.text.toString().trim().length < 3) {
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
        } else if (binding.shopAct.text.toString().trim().length < 3) {
            binding.shopAct.error = getString(R.string.shop_Act_length)
            binding.shopAct.requestFocus()
        }
        else if(!isRerunFee){
            binding.returnedt.error = getString(R.string.error_percentage_numeric)
            binding.returnedt.requestFocus()
        }
        else if(!isCancelFee){
            binding.cancellation.error = getString(R.string.error_percentage_numeric)
            binding.cancellation.requestFocus()
        }
        else {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val email = binding.emailEdit.text.toString().trim()
        val contactNumber = binding.mobileNumber.text.toString().trim()
//        val ext = binding.profileCcp.selectedCountryCodeWithPlus
        val shopAddress = binding.shopAddress.text.toString().trim()
        val fullAddress = binding.address.text.toString().trim()
        val businessName = binding.businessName.text.toString().trim()
        val licenceNumber = binding.shopAct.text.toString().trim()
        val returnFee = binding.returnedt.text.toString().trim()
        val cancellationFee = binding.cancellation.text.toString().trim()

        val merchantSettingsDTOList= mutableListOf<MerchantSettingsDTO>()

        profileViewModel.merchantSettingsDTOList.forEach {
            if(it.paramName == "return-fee"){
                it.paramValue=returnFee
                merchantSettingsDTOList.add(it)
            }
            else if(it.paramName == "cancellation-fee"){
                it.paramValue=cancellationFee
                merchantSettingsDTOList.add(it)
            }
        }

        profileViewModel.saveProfile(email, shopAddress, fullAddress, businessName, licenceNumber,merchantSettingsDTOList)
    }

    private fun getProfile() {
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