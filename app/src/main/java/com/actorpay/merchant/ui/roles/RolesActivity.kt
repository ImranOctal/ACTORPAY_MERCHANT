package com.actorpay.merchant.ui.roles

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityRolesBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RolesResponse
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RolesActivity : BaseActivity() {

    private lateinit var binding: ActivityRolesBinding
    private val rolesViewModel: RolesViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_roles)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        apiResponse()
        rolesViewModel.getAllRoles()

    }

    fun apiResponse(){
        lifecycleScope.launch {
            rolesViewModel.responseLive.collect {
                event->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (event.response is RolesResponse) {

                            rolesViewModel.pageNo=event.response.data.pageNumber
                            rolesViewModel.rolesList.addAll(event.response.data.items)
                            showCustomToast(rolesViewModel.rolesList.size.toString())


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
    }

}