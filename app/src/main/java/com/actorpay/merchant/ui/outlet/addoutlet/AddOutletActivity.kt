package com.actorpay.merchant.ui.outlet.addoutlet

import android.app.Activity
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.AddOutletActitvityBinding
import com.actorpay.merchant.ui.outlet.response.AddOutletResponse
import com.actorpay.merchant.utils.GlobalData
import com.actorpay.merchant.utils.ResponseSealed
import com.actorpay.merchant.utils.countrypicker.CountryPicker
import com.google.android.libraries.places.widget.Autocomplete
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*

class AddOutletActivity : BaseActivity() {
    private lateinit var binding: AddOutletActitvityBinding
    private val addOutletViewModel: AddOutletViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.add_outlet_actitvity)
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
        apiResponse()

//        binding.etAddressOne.setOnClickListener {
//            if (!Places.isInitialized()) {
//                Places.initialize(applicationContext, getString(R.string.place_api_key), Locale.US);
//            }
//            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
//            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
//                .build(this)
//            startForAddressResult.launch(intent)
//        }
    }

    private fun validation() {

        var isValidate = true
        if (binding.etDescription.text.toString().trim().isEmpty()) {
            binding.etDescription.error = getString(R.string.address_empty)
            binding.etDescription.requestFocus()
            isValidate = false
        }
        if (binding.etCountry.text.toString().trim().isEmpty()) {
            binding.etCountry.error = getString(R.string.country_empty)
            binding.etCountry.requestFocus()
            isValidate = false

        }
        if (binding.etState.text.toString().trim().isEmpty()) {
            binding.etState.error = getString(R.string.state_empty)
            binding.etState.requestFocus()

            isValidate = false
        }

        if (binding.etCity.text.toString().trim().isEmpty()) {
            binding.etCity.error = getString(R.string.city_empty)
            binding.etCity.requestFocus()
            isValidate = false

        }
        if (binding.etZipCode.text.toString().trim().isEmpty()) {
            binding.etZipCode.error = getString(R.string.zip_code_empty)
            binding.etZipCode.requestFocus()
            isValidate = false

        }

        if (binding.etAddressTwo.text.toString().trim().isEmpty()) {
            binding.etAddressTwo.error = getString(R.string.address_empty)
            binding.etAddressTwo.requestFocus()
            isValidate = false

        }
        if (binding.etAddressOne.text.toString().trim().isEmpty()) {
            binding.etAddressOne.error = getString(R.string.address_empty)
            binding.etAddressOne.requestFocus()
            isValidate = false

        } else if (binding.mobileNumber.text.toString().trim()[0].toString() == "0") {
            binding.mobileNumber.error = getString(R.string.mobile_not_start_with_0)
            binding.mobileNumber.requestFocus()
            isValidate = false

        } else if (binding.mobileNumber.text.toString().trim().length < 7) {
            binding.mobileNumber.error = getString(R.string.number_not_correct)
            binding.mobileNumber.requestFocus()
            isValidate = false


        }
        if (binding.mobileNumber.text.toString().trim().isEmpty()) {
            binding.mobileNumber.error = getString(R.string.phone_empty)
            binding.mobileNumber.requestFocus()
            isValidate = false

        }
        if (binding.etLicenceNo.text.isEmpty()) {
            binding.etLicenceNo.error = this.getString(R.string.licence_empty)
            binding.etLicenceNo.requestFocus()
            isValidate = false
        }

        if (binding.etResourceType.text.isEmpty()) {
            binding.etResourceType.error = this.getString(R.string.resource_empty)
            binding.etResourceType.requestFocus()
            isValidate = false
        }
        if (binding.etTitle.text.isEmpty()) {
            binding.etTitle.error = this.getString(R.string.title_empty)
            binding.etTitle.requestFocus()
            isValidate = false
        }
        if (isValidate) {
            createOutlet()
        }

    }

    private fun createOutlet() {
        val countryCode = binding.codePicker.text.toString().trim()
        val resourceType = binding.etResourceType.text.toString().trim()
        val licenceNumber = binding.etLicenceNo.text.toString().trim()
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val extensionNumber = countryCode
        val addressLine1 = binding.etAddressOne.text.toString().trim()
        val addressLine2 = binding.etAddressTwo.text.toString().trim()
        val contactNumber = binding.mobileNumber.text.toString().trim()
        val zipCode = binding.etZipCode.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val country = binding.etCountry.text.toString().trim()
        val state = binding.etState.text.toString().trim()
        val latitude = "23234343"
        val longitude = "3333333"

        addOutletViewModel.methodRepo.hideSoftKeypad(this)
        addOutletViewModel.createOutlet(
            resourceType,
            licenceNumber,
            title,
            description,
            extensionNumber,
            addressLine1,
            addressLine2,
            contactNumber,
            zipCode,
            city,
            country,
            state,
            latitude,
            longitude
        )

    }

    private val startForAddressResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val place = Autocomplete.getPlaceFromIntent(intent!!)
                val latLng = place.latLng
                latLng?.let {
                    var latitude = it.latitude
                    var longitude = it.longitude
                    getAddress(latitude, longitude)
                }
            }
        }

    private fun getAddress(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (addresses.size > 0) {
            val address = addresses[0].getAddressLine(0)
            val postalCode = addresses[0].postalCode
        }
    }

    fun apiResponse() {
        lifecycleScope.launch {
            addOutletViewModel.AddresponseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is AddOutletResponse) {
                            showCustomToast(it.response.message)
                            setResult(Activity.RESULT_OK)
                            finish()

                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (it.failResponse!!.code == 403) {
                            forcelogout(viewModel.methodRepo)
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
}