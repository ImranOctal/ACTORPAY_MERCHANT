package com.actorpay.merchant.ui.roles

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityRolesBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RolesResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenResponse
import com.actorpay.merchant.ui.roles.details.RoleDetailsActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData
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
        setAdapter()
        rolesViewModel.getAllRoles()
        binding.btnAddRole.setOnClickListener {
            val intent= Intent(this,RoleDetailsActivity::class.java)
            intent.putExtra("id","")
            resultLauncher.launch(intent)
        }

    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode== Activity.RESULT_OK) {
            rolesViewModel.getAllRoles()
        }
    }

    fun setAdapter(){
        val adapter= RoleAdapter(rolesViewModel.methodRepo,rolesViewModel.rolesList){
                pos, action ->
            when(action){
                "edit"->{
                    val intent=Intent(this, RoleDetailsActivity::class.java)
                    intent.putExtra("id",rolesViewModel.rolesList[pos].id)
                    resultLauncher.launch(intent)
                }
                "delete"->{
                    CommonDialogsUtils.showCommonDialog(this,rolesViewModel.methodRepo,"Delete Role","Are you sure?",false,true,true,false,object :CommonDialogsUtils.DialogClick{
                        override fun onClick() {
                            rolesViewModel.deleteRole(rolesViewModel.rolesList[pos].id)
                        }
                        override fun onCancel() {
                        }
                    })
                }
            }

        }
        binding.rvRoles.layoutManager= LinearLayoutManager(this)
        binding.rvRoles.adapter=adapter
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
                            rolesViewModel.rolesList.clear()
                            rolesViewModel.rolesList.addAll(event.response.data.items)
                            setAdapter()
                            rolesViewModel.getAllScreen()

                        }
                        else if (event.response is ScreenResponse) {
                            GlobalData.allScreens.clear()
                            GlobalData.allScreens.addAll(event.response.data)
                        }
                        else if (event.response is SuccessResponse) {
                            showCustomToast(event.response.message)
                            rolesViewModel.getAllRoles()

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
}