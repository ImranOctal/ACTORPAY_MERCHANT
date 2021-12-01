package com.actorpay.merchant.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityHomeBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.ui.profile.ProfileActivity
import com.actorpay.merchant.ui.addnewproduct.AddNewProduct
import com.actorpay.merchant.ui.home.adapter.ManageProductAdapter
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.manageOrder.ManageOrderActivity
import com.actorpay.merchant.ui.payroll.PayRollActivity
import com.actorpay.merchant.ui.subAdmin.CreateSubAdminActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.ArrayList

class HomeActivity : BaseActivity(){
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ManageProductAdapter
    private var doubleBackToExitPressedOnce = false
    private val homeviewmodel: HomeViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_home)
        initialisation()
    }



    private fun initialisation() {
        adapter = ManageProductAdapter(this)
        adapter.UpdateList(ArrayList<String>())
        binding.manageProduct.adapter = adapter
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.back.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.hamburger))
        binding.toolbar.ToolbarTitle.text = getString(R.string.manage_product)
        clickListeners()
        apiResponse()

        lifecycleScope.launchWhenCreated {
        homeviewmodel.methodRepo.dataStore.getBussinessName().collect {
            businessName->
            binding.headerTitle.userProfileName.text="$businessName"
        }
        }

    }
    private fun clickListeners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.back.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawers()
            }else{
                binding.drawerLayout.openDrawer(GravityCompat.START,true)
            }
        }
        binding.AddNewProductButton.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), AddNewProduct::class.java))
        }
        binding.profileLay.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ProfileActivity::class.java))
        }
        binding.myOrderLay.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageOrderActivity::class.java))
        }

        binding.reportsLay.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), PayRollActivity::class.java))
        }
        binding.merchatLay.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), CreateSubAdminActivity::class.java))
        }
        binding.changePasswordLay.setOnClickListener {
            changePasswordUi()
        }
        binding.footer.btnHomeLogout.setOnClickListener {
            logOut()
        }
    }

        fun changePasswordUi(){
            ChangePasswordDialog().show(this,homeviewmodel.methodRepo){
                    oldPassword, newPassword ->
                homeviewmodel.changePassword(oldPassword,newPassword    )
            }
        }

    private fun logOut(){
        CommonDialogsUtils.showCommonDialog(this,viewModel.methodRepo, getString(R.string.log_out),
            getString(R.string.are_you_sure),
            autoCancelable = true,
            isCancelAvailable = true,
            isOKAvailable = true,
            showClickable = false,
            callback = object : CommonDialogsUtils.DialogClick {
                override fun onClick() {
                    lifecycleScope.launchWhenCreated {
                        viewModel.methodRepo.dataStore.logOut()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                }

                override fun onCancel() {
                }
            })
    }

    private fun apiResponse() {
        lifecycleScope.launch {

            homeviewmodel.homeResponseLive.collect {
                when(it){
                    is HomeViewModel.ResponseHomeSealed.loading->{
                        homeviewmodel.methodRepo.showLoadingDialog(this@HomeActivity)
                    }
                    is HomeViewModel.ResponseHomeSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (it.response is SuccessResponse) {
                            CommonDialogsUtils.showCommonDialog(
                                this@HomeActivity,
                                homeviewmodel.methodRepo,
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
                                                this@HomeActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finishAffinity()
                                    }

                                    override fun onCancel() {

                                    }
                                }
                            )
                        }
                        else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }

                    }
                    is HomeViewModel.ResponseHomeSealed.ErrorOnResponse -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                    }
                }

            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }
        doubleBackToExitPressedOnce = true
        showCustomAlert(getString(R.string.press_back_again),binding.root)
        //Toast.makeText(this, "Press back again", Toast.LENGTH_SHORT)
        lifecycleScope.launch(Dispatchers.Default) {
            delay(2000)
            doubleBackToExitPressedOnce = false
        }
    }
}