package com.actorpay.merchant.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityHomeBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.ui.commission.CommissionActivity
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.manageOrder.ManageOrderActivity
import com.actorpay.merchant.ui.manageProduct.ManageProductActivity
import com.actorpay.merchant.ui.more.MoreActivity
import com.actorpay.merchant.ui.outlet.OutletActivity
import com.actorpay.merchant.ui.payroll.PayRollActivity
import com.actorpay.merchant.ui.profile.ProfileActivity
import com.actorpay.merchant.ui.roles.RolesActivity
import com.actorpay.merchant.ui.subAdmin.SubMerchantActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData.permissionDataList
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_DASHBOARD
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_ORDER
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_OUTLET
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_PRODUCT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_ROLE
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_OUTLET
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_PAYMENT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_REPORTS
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_SUB_MERCHANT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_WALLET_BALANCE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*


class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var doubleBackToExitPressedOnce = false
    var Merchantrole=""
    var read=false
    var write=false
    var screenName=""

    private val homeviewmodel: HomeViewModel by inject()
    private var handler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        handler = Handler()
        homeviewmodel.getById()
        initialisation()
        WorkSource()
        clickListeners()
        homeviewmodel.getPermissions()
        lifecycleScope.launch {
            viewModel.methodRepo.dataStore.getRole().collect { role ->
                Merchantrole=role

            }
        }
    }



    private fun initialisation() {
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hamburger))
        binding.toolbar.ToolbarTitle.text = getString(R.string.dashboard)
        lifecycleScope.launchWhenCreated {
            homeviewmodel.methodRepo.dataStore.getBussinessName().collect { businessName ->
                binding.headerTitle.userProfileName.text = "$businessName"
            }
        }
        getPermissionDetails()
//        if(Merchantrole!="MERCHANT"){
//
//        }
    }

    private fun getPermissionDetails() {
        homeviewmodel.getPermissions()
    }

    private fun clickListeners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.back.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START, true)
            }
        }
        binding.myCommissionLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), CommissionActivity::class.java))
        }

        binding.myRolesLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), RolesActivity::class.java))
        }

        binding.dashboard.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        binding.constManageProduct.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageProductActivity::class.java))
        }

        binding.profileLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ProfileActivity::class.java))
        }

        binding.myOrderLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageOrderActivity::class.java))
        }

        binding.reportsLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), PayRollActivity::class.java))

        }
        binding.merchatLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), SubMerchantActivity::class.java))
        }

        binding.constMore.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), MoreActivity::class.java))
        }

        binding.outlet.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), OutletActivity::class.java))
        }

        binding.changePasswordLay.setOnClickListener {
            changePasswordUi()
        }

        binding.footer.btnHomeLogout.setOnClickListener {
            logOut()
        }
    }

    fun changePasswordUi() {
        ChangePasswordDialog().show(this, homeviewmodel.methodRepo) { oldPassword, newPassword -> homeviewmodel.changePassword(oldPassword, newPassword)
        }
    }

    fun WorkSource() {
        lifecycleScope.launchWhenStarted {
            homeviewmodel.homeResponseLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.ResponseHomeSealed.loading -> {
                        showLoadingDialog()
                    }
                    is HomeSealedClasses.Companion.ResponseHomeSealed.Success -> {
                        hideLoadingDialog()
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
                         else if(it.response is PermissionDetails){
                             for (i in it.response.data.indices){
                                 permissionDataList.forEachIndexed {
                                     index, permissionData ->
                                     if (permissionData.screenName == it.response.data[i].screenName) {
                                         permissionDataList[index].read = it.response.data[i].read
                                         permissionDataList[index].write = it.response.data[i].write
                                     }
                                 }
                             }
                            updateUi()
                        } else if (it.response is  GetUserById){
                            val data = it.response
                            Log.e("merchantId>>>", data.data.merchantId)
                            viewModel.methodRepo.dataStore.setMerchantId(data.data.merchantId)
                            binding.headerTitle.userProfileName.text = data.data.businessName
                            binding.tvBusinessName.text = "Hi\n"+data.data.businessName
                        }

                        else showCustomAlert(
                            getString(R.string.please_try_after_sometime),
                            binding.root
                        )
                    }
                    is HomeSealedClasses.Companion.ResponseHomeSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (it.failResponse!!.code == 403) {
                            forcelogout(homeviewmodel.methodRepo)
                        }else{
                            showCustomAlert(
                                it.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    else -> hideLoadingDialog()
                }
            }
        }

    }

    private fun updateUi() {
        permissionDataList.forEach {
            if(it.screenName== SCREEN_SUB_MERCHANT) {
                if(it.write||it.read){
                    binding.merchatLay.visibility=View.VISIBLE
                }else{
                    binding.merchatLay.visibility=View.GONE
                }
            }
            if(it.screenName == SCREEN_OUTLET){
                if(it.write||it.read){
                    binding.outlet.visibility=View.VISIBLE
                }else{
                    binding.outlet.visibility=View.GONE
                }
            }

            if(it.screenName == SCREEN_WALLET_BALANCE){
                if(it.write||it.read){
                    binding.headerTitle.userProfileBalance.visibility=View.VISIBLE
                }else{
                    binding.headerTitle.userProfileBalance.visibility=View.GONE
                }
            }

            if(it.screenName == SCREEN_PAYMENT){
                if(it.write||it.read){
                    binding.earnMoney.visibility=View.VISIBLE
                }else{
                    binding.earnMoney.visibility=View.GONE
                }
            }

            if(it.screenName == SCREEN_DASHBOARD){
                if(it.write||it.read){
                    binding.dashboard.visibility=View.VISIBLE
                }else{
                    binding.dashboard.visibility=View.GONE
                }
            }

            if(it.screenName == SCREEN_MANAGE_PRODUCT){
                if(it.write||it.read){
                    binding.constManageProduct.visibility=View.VISIBLE
                }else{
                    binding.constManageProduct.visibility=View.GONE
                }
            }
            if(it.screenName == SCREEN_MANAGE_ORDER){
                if(it.write||it.read){
                    binding.myOrderLay.visibility=View.VISIBLE
                }else{
                    binding.myOrderLay.visibility=View.GONE
                }
            }

            if(it.screenName == SCREEN_REPORTS){
                if(it.write||it.read){
                    binding.reportsLay.visibility=View.VISIBLE
                }else{
                    binding.reportsLay.visibility=View.GONE
                }
            }

            if(it.screenName == SCREEN_MANAGE_ROLE){
                if(it.write||it.read){
                    binding.myRolesLay.visibility=View.VISIBLE
                }else{
                    binding.myRolesLay.visibility=View.GONE
                }
            }
            if(it.screenName == SCREEN_MANAGE_OUTLET){
                if(it.write||it.read){
                    binding.outlet.visibility=View.VISIBLE
                }else{
                    binding.outlet.visibility=View.GONE
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
        showCustomAlert(getString(R.string.press_back_again), binding.root)
        //Toast.makeText(this, "Press back again", Toast.LENGTH_SHORT)
        lifecycleScope.launch(Dispatchers.Default) {
            delay(2000)
            doubleBackToExitPressedOnce = false
        }
    }
    override fun onResume() {
        super.onResume()
        homeviewmodel.getById()
    }
}