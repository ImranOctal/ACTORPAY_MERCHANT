package com.actorpay.merchant.ui.roles.details

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityRoleDetailsBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.ScreenAccessPermission
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.SendRolesParmas
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.SingleRoleResponse
import com.actorpay.merchant.utils.GlobalData
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RoleDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityRoleDetailsBinding
    private val rolesDetailsViewModel: RolesDetailsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_role_details)

        rolesDetailsViewModel.id = intent.getStringExtra("id")!!
        apiResponse()

        binding.back.setOnClickListener {
            onBackPressed()
        }

        if (rolesDetailsViewModel.id == "") {
            setAdapter()
        } else {
            rolesDetailsViewModel.getRoleById()
        }

        binding.btnSubmit.setOnClickListener {
            validate()
        }


    }

    fun validate() {
        val title = binding.etName.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        if (title == "") {
            binding.etName.error = "Please enter name"
        } else if (desc == "") {
            binding.etDescription.error = "Please enter description"
        } else {
            val tempList = mutableListOf<ScreenAccessPermission>()
            rolesDetailsViewModel.allScreens.forEach {
                if (it.read || it.write)
                    tempList.add(ScreenAccessPermission(it.id, it.screenName, it.read, it.write))
            }
            if(rolesDetailsViewModel.id == "") {
                val sendRolesParmas = SendRolesParmas(null, title, desc, tempList)
                rolesDetailsViewModel.addRole(sendRolesParmas)
            }
            else{
                val sendRolesParmas = SendRolesParmas(rolesDetailsViewModel.id, title, desc, tempList)
                rolesDetailsViewModel.updateRole(sendRolesParmas)
            }
            Log.d("TAG", "validate: ")
        }
    }

    fun apiResponse() {
        lifecycleScope.launch {
            rolesDetailsViewModel.responseLive.collect { event ->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (event.response is SingleRoleResponse) {

                            val roleItem = event.response.data
                            rolesDetailsViewModel.screenPermissionsList.clear()
                            rolesDetailsViewModel.screenPermissionsList.addAll(roleItem.screenAccessPermission!!)
                            binding.etName.setText(roleItem.name)
                            binding.etDescription.setText(roleItem.description)
                            setAdapter()
                        }
                        else if(event.response is SuccessResponse){
                            showCustomToast(event.response.message)
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if(event.failResponse!!.code==403){
                            forcelogout(viewModel.methodRepo)
                        }else{
                            showCustomAlert(
                                event.failResponse.message,
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

    fun setAdapter() {
        if (GlobalData.allScreens.size > 0) {
            rolesDetailsViewModel.allScreens.clear()
            GlobalData.allScreens.forEach {
                it.read = false
                it.write = false
            }
            rolesDetailsViewModel.allScreens.addAll(GlobalData.allScreens.toMutableList())

            rolesDetailsViewModel.allScreens.forEach { screen ->
                rolesDetailsViewModel.screenPermissionsList.forEach { screenAccessPermission ->
                    if (screen.id == screenAccessPermission.screenId) {
                        screen.read = screenAccessPermission.read
                        screen.write = screenAccessPermission.write
                    }
                }
            }
        }
        val adapter = RoleDetailsAdapter(rolesDetailsViewModel.methodRepo, rolesDetailsViewModel.allScreens) { pos, action ->

        }
        binding.rvScreens.layoutManager = LinearLayoutManager(this)
        binding.rvScreens.adapter = adapter

    }
}