package com.actorpay.merchant.ui.subAdmin.addSubMerchant

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityCreateSubAdminBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RoleItem
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RolesResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.CreateSubMerchant
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.GetMerchantById
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.UpdateSubMerchant
import com.actorpay.merchant.ui.roles.RolesViewModel
import com.actorpay.merchant.utils.GlobalData
import com.actorpay.merchant.utils.ResponseSealed
import com.actorpay.merchant.utils.countrypicker.CountryPicker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateSubAdminActivity :  BaseActivity() {
    private lateinit var binding: ActivityCreateSubAdminBinding
    private val addSubViewModel: AddSubMerchantViewModel by inject()
    private val rolesViewModel: RolesViewModel by inject()
    var dob=""
    private var showPassword = false
    var roleId=""
    var gender=""
    var list=ArrayList<RoleItem>()
    lateinit var adapter: ArrayAdapter<RoleItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_sub_admin)
        Installation()
        rolesViewModel.getAllRoles()
        if(intent.getStringExtra("from")=="edit"){ intent.getStringExtra("id")?.let {
                addSubViewModel.getMerchantById(it)
                binding.emailEdit.isFocusable = false
                binding.emailEdit.setTextColor(Color.parseColor("#8E8D8D"))
            }
        }
        apiResponse()

        binding.submit.setOnClickListener {
            if(intent.getStringExtra("from")=="edit"){

                updateMerchant(intent.getStringExtra("id")!!)
            }else{
                validation()
            }

        }
        binding.etDob.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this, { view, yearR, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val f = DecimalFormat("00");
                val dayMonth = f.format(dayOfMonth)
                val monthYear = f.format(monthOfYear + 1)
                binding.etDob.setText("$yearR-$monthYear-$dayMonth")
                dob = "$year-$monthYear-$dayMonth"

            }, year, month, day)
            dpd.show()
            dpd.getDatePicker().setMaxDate(Date().time)
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                roleId=list[position].id
            }
        }
        ArrayAdapter.createFromResource(this, R.array.Gender, android.R.layout.simple_spinner_item).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerGender.adapter = adapter
        }
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                gender = binding.spinnerGender.selectedItem.toString()
            }
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
        binding.subPasswordShowHide.setOnClickListener {
            if (showPassword) {
                binding.etPassword.transformationMethod = PasswordTransformationMethod()
                showPassword = false
                binding.subPasswordShowHide.setImageResource(R.drawable.hide)
                binding.etPassword.setSelection(binding.etPassword.text.toString().length)
            } else {
                binding.etPassword.transformationMethod = null
                showPassword = true
                binding.subPasswordShowHide.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                binding.etPassword.setSelection(binding.etPassword.text.toString().length)
            }
        }
    }

    private fun updateMerchant(id: String) {


        val countryCode = binding.codePicker.text.toString().trim()
        val firstName = binding.firstName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()
        val email = binding.emailEdit.text.toString().trim()
        val contactNumber = binding.mobileNumber.text.toString().trim()
        addSubViewModel.updateSubMerchant(countryCode,firstName,lastName,email,contactNumber,roleId,gender,id)
    }


    private fun apiResponse() {
        lifecycleScope.launch {
            rolesViewModel.responseLive.collect { event->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (event.response is RolesResponse) {
                            list=event.response.data.items
                            adapter= ArrayAdapter(this@CreateSubAdminActivity, android.R.layout.simple_spinner_dropdown_item, list)
                            binding.spinnerRole.adapter=adapter
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        showCustomAlert(
                            event.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
        lifecycleScope.launch {
            addSubViewModel.responseLive.collect { it->
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                         if (it.response is GetMerchantById) {
                            binding.codePicker.text = it.response.data.extensionNumber
                            binding.firstName.setText(it.response.data.firstName)
                            binding.lastName.setText(it.response.data.lastName)
                            binding.emailEdit.setText(it.response.data.email)
                            binding.mobileNumber.setText(it.response.data.contactNumber)
                            binding.llPassword.visibility=View.GONE
                            binding.llDob.visibility=View.GONE
                        }
                        if (it.response is CreateSubMerchant) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        if (it.response is UpdateSubMerchant) {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if(it.failResponse!!.code==403){
                            forcelogout(viewModel.methodRepo)
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
    private fun validation() {
        var isValidate=true
        if (binding.etDob.text.toString().trim().isEmpty()) {
            binding.etDob.error = getString(R.string.dob_empty)
            binding.etDob.requestFocus()
            isValidate = false
        }
        if (binding.mobileNumber.text.toString().trim().isEmpty()) {
            binding.mobileNumber.error = getString(R.string.phone_empty)
            binding.mobileNumber.requestFocus()
            isValidate = false

        } else if (binding.mobileNumber.text.toString().trim().length < 7) {
            binding.mobileNumber.error = getString(R.string.number_not_correct)
            binding.mobileNumber.requestFocus()
            isValidate = false

        } else if (binding.mobileNumber.text.toString().trim()[0].toString() == "0") {
            binding.mobileNumber.error = getString(R.string.mobile_not_start_with_0)
            binding.mobileNumber.requestFocus()
            isValidate = false
        }

        else if (binding.etPassword.text.toString().trim().length < 8 || !addSubViewModel.methodRepo.isValidPassword(binding.etPassword.text.toString().trim())) {
            binding.etPassword.error = this.getString(R.string.oops_your_password_is_not_valid)
            binding.etPassword.requestFocus()
            isValidate = false
        }

        if (binding.etPassword.text.toString().trim().isEmpty()) {
            binding.etPassword.error = this.getString(R.string.password_empty)
            binding.etPassword.requestFocus()
            isValidate = false
        }

        if (!methods.isValidEmail(binding.emailEdit.text.toString())) {
            binding.emailEdit.error = this.getString(R.string.invalid_email)
            binding.emailEdit.requestFocus()
            isValidate = false
        }

         if (binding.emailEdit.text.trim().isEmpty()) {
            binding.emailEdit.error = this.getString(R.string.email_empty)
            binding.emailEdit.requestFocus()
             isValidate = false
        }

         if (binding.lastName.text.isEmpty()) {
            binding.lastName.error = this.getString(R.string.last_name_empty)
            binding.lastName.requestFocus()
             isValidate = false
        }

        if (binding.firstName.text.isEmpty()) {
            binding.firstName.error = this.getString(R.string.first_name_empty)
            binding.firstName.requestFocus()
            isValidate = false
        }
        if(isValidate) {
            addMerchant()
        }

    }

    private fun addMerchant() {
        val countryCode = binding.codePicker.text.toString().trim()
        val firstName = binding.firstName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()
        val email = binding.emailEdit.text.toString().trim()
        val contactNumber = binding.mobileNumber.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        addSubViewModel.methodRepo.hideSoftKeypad(this)
        addSubViewModel.addSubMerchant(countryCode,firstName,lastName,email,contactNumber,dob,roleId,gender,password)
    }

    private fun Installation() {
        binding.toolbar.back.visibility = View.VISIBLE

        if(intent.getStringExtra("from")=="edit"){
            binding.toolbar.ToolbarTitle.text = getString(R.string.update_new_merchant)

        }else{
            binding.toolbar.ToolbarTitle.text = getString(R.string.add_merchant_new)
        }
        ClickListners()
    }

    private fun ClickListners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }

    }

}