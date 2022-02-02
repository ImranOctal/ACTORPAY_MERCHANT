package com.actorpay.merchant.ui.outlet.updateoutlet

import android.app.Activity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityUpdateOutletBinding
import com.actorpay.merchant.ui.outlet.OutletViewModel
import com.actorpay.merchant.ui.outlet.response.GetOutlet
import com.actorpay.merchant.ui.outlet.response.UpdateOutlet
import com.actorpay.merchant.utils.GlobalData
import com.actorpay.merchant.utils.ResponseSealed
import com.actorpay.merchant.utils.countrypicker.CountryPicker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class UpdateOutletActivity : BaseActivity() {
    private val outletViewModel: OutletViewModel by inject()
    private lateinit var binding: ActivityUpdateOutletBinding

    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_outlet)
        outletViewModel.getOutlet()
        apiResponse()

        binding.btnSubmit.setOnClickListener {
            validation()
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }
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
    }

    private fun validation() {
        if (binding.etTitle.text.isEmpty()) {
            binding.etTitle.error = this.getString(R.string.title_empty)
            binding.etTitle.requestFocus()
        } else if (binding.etResourceType.text.isEmpty()) {
            binding.etResourceType.error = this.getString(R.string.resource_empty)
            binding.etResourceType.requestFocus()
        } else if (binding.etLicenceNo.text.isEmpty()) {
            binding.etLicenceNo.error = this.getString(R.string.licence_empty)
            binding.etLicenceNo.requestFocus()
        } else if (binding.mobileNumber.text.toString().trim().isEmpty()) {
            binding.mobileNumber.error = getString(R.string.phone_empty)
            binding.mobileNumber.requestFocus()

        } else if (binding.mobileNumber.text.toString().trim().length < 7) {
            binding.mobileNumber.error = getString(R.string.number_not_correct)
            binding.mobileNumber.requestFocus()


        } else if (binding.mobileNumber.text.toString().trim()[0].toString() == "0") {
            binding.mobileNumber.error = getString(R.string.mobile_not_start_with_0)
            binding.mobileNumber.requestFocus()

        } else if (binding.etAddressOne.text.toString().trim().isEmpty()) {
            binding.etAddressOne.error = getString(R.string.address_empty)
            binding.etAddressOne.requestFocus()

        } else if (binding.etAddressTwo.text.toString().trim().isEmpty()) {
            binding.etAddressTwo.error = getString(R.string.address_empty)
            binding.etAddressTwo.requestFocus()

        }
        else if (binding.etZipCode.text.toString().trim().isEmpty()) {
            binding.etZipCode.error = getString(R.string.zip_code_empty)
            binding.etZipCode.requestFocus()

        }
        else if (binding.etCity.text.toString().trim().isEmpty()) {
            binding.etCity.error = getString(R.string.city_empty)
            binding.etCity.requestFocus()

        }
        else if (binding.etState.text.toString().trim().isEmpty()) {
            binding.etState.error = getString(R.string.state_empty)
            binding.etState.requestFocus()

        } else if (binding.etCountry.text.toString().trim().isEmpty()) {
            binding.etCountry.error = getString(R.string.country_empty)
            binding.etCountry.requestFocus()

        } else if (binding.etDescription.text.toString().trim().isEmpty()) {
            binding.etDescription.error = getString(R.string.address_empty)
            binding.etDescription.requestFocus()
        } else {

            updateOutlet()
        }
    }
    private fun updateOutlet() {
        val countryCode = binding.codePicker.text.toString().trim()
        val resourceType = binding.etResourceType.text.toString().trim()
        val licenceNumber = binding.etLicenceNo.text.toString().trim()
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val addressLine1 = binding.etAddressOne.text.toString().trim()
        val addressLine2 = binding.etAddressTwo.text.toString().trim()
        val contactNumber = binding.mobileNumber.text.toString().trim()
        val zipCode = binding.etZipCode.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val country = binding.etCountry.text.toString().trim()
        val state = binding.etState.text.toString().trim()
        val latitude = "23234343"
        val longitude = "3333333"
        outletViewModel.methodRepo.hideSoftKeypad(this)
        outletViewModel.updateOutlet(resourceType,licenceNumber,title,description,countryCode,addressLine1,addressLine2,contactNumber,zipCode,city,country,state,latitude,longitude,id)
    }

    private fun apiResponse() {
        lifecycleScope.launch {
            outletViewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetOutlet) {
                            id=it.response.data.items[0].id
                            binding.codePicker.text=it.response.data.items[0].extensionNumber
                            binding.etResourceType.setText(it.response.data.items[0].resourceType)
                            binding.etLicenceNo.setText(it.response.data.items[0].licenceNumber)
                            binding.etTitle.setText(it.response.data.items[0].title)
                            binding.etDescription.setText(it.response.data.items[0].description)
                            binding.etAddressOne.setText(it.response.data.items[0].addressLine1)
                            binding.etAddressTwo.setText(it.response.data.items[0].addressLine2)
                            binding.mobileNumber.setText(it.response.data.items[0].contactNumber)
                            binding.etZipCode.setText(it.response.data.items[0].zipCode)
                            binding.etCity.setText(it.response.data.items[0].city)
                            binding.etCountry.setText(it.response.data.items[0].country)
                            binding.etState.setText(it.response.data.items[0].state)
                        }
                        if (it.response is UpdateOutlet) {
                            setResult(Activity.RESULT_OK)
                            finish()
                            }
                      }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if(it.failResponse!!.code==403){
                            forcelogout(outletViewModel.methodRepo)
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
}