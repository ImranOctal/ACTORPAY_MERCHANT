package com.actorpay.merchant.ui.outlet.updateoutlet

import android.app.Activity
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentUpdateOutletBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.outlet.GetOutletById
import com.actorpay.merchant.ui.outlet.OutletViewModel
import com.actorpay.merchant.ui.outlet.response.GetOutlet
import com.actorpay.merchant.ui.outlet.response.UpdateOutlet
import com.actorpay.merchant.utils.GlobalData
import com.actorpay.merchant.utils.ResponseSealed
import com.actorpay.merchant.utils.countrypicker.CountryPicker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*


class UpdateOutletFragment : BaseFragment() {
    private val outletViewModel: OutletViewModel by inject()
    private lateinit var binding: FragmentUpdateOutletBinding
    var long=0.0
    var lat=0.0
    var id=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_outlet,container,false)
        outletViewModel.getOutletById(arguments?.getString("id"))
        apiResponse()

        binding.btnSubmit.setOnClickListener {
            validation()
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
            CountryPicker(requireActivity(), viewModel.methodRepo, GlobalData.allCountries) {
                binding.codePicker.text = GlobalData.allCountries[it].countryCode
            }.show()
        }

        binding.etAddressOne.setOnClickListener {
            if (!Places.isInitialized()) {
                Places.initialize(requireActivity().applicationContext, getString(R.string.place_api_key), Locale.US);
            }
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireActivity())
            startForAddressResult.launch(intent)
        }
        return  binding.root
    }

    private val startForAddressResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val place = Autocomplete.getPlaceFromIntent(intent!!)
                val latLng = place.latLng
                latLng?.let {
                    lat = it.latitude
                    long = it.longitude
                    getAddress(lat, long)
                }
            }
        }

    private fun getAddress(lat: Double, lng: Double) {
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (addresses.size > 0) {
            val address = addresses[0].getAddressLine(0)
            val address2 = addresses[0].featureName
            binding.etAddressOne.setText(address)
            binding.etAddressTwo.setText(address2)
            binding.etZipCode.setText(addresses[0].postalCode)
            binding.etCity.setText(addresses[0].locality)
            binding.etState.setText(addresses[0].adminArea)
            binding.etCountry.setText(addresses[0].countryName)

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
        outletViewModel.methodRepo.hideSoftKeypad(requireActivity())
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
                        if (it.response is GetOutletById) {
                            id=it.response.data.id
                            binding.codePicker.text=it.response.data.extensionNumber
                            binding.etResourceType.setText(it.response.data.resourceType)
                            binding.etLicenceNo.setText(it.response.data.licenceNumber)
                            binding.etTitle.setText(it.response.data.title)
                            binding.etDescription.setText(it.response.data.description)
                            binding.etAddressOne.setText(it.response.data.addressLine1)
                            binding.etAddressTwo.setText(it.response.data.addressLine2)
                            binding.mobileNumber.setText(it.response.data.contactNumber)
                            binding.etZipCode.setText(it.response.data.zipCode)
                            binding.etCity.setText(it.response.data.city)
                            binding.etCountry.setText(it.response.data.country)
                            binding.etState.setText(it.response.data.state)
                        }
                        if (it.response is UpdateOutlet) {
                            requireActivity().onBackPressed()
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