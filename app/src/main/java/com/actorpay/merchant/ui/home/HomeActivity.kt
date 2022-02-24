package com.actorpay.merchant.ui.home

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityHomeBinding
import com.actorpay.merchant.notification.NotificationActivity
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.ui.commission.CommissionActivity
import com.actorpay.merchant.ui.disputes.DisputeActivity
import com.actorpay.merchant.ui.login.AuthBottomSheetDialog
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.manageOrder.ManageOrderActivity
import com.actorpay.merchant.ui.manageProduct.ManageProductActivity
import com.actorpay.merchant.ui.more.MoreActivity
import com.actorpay.merchant.ui.outlet.OutletActivity
import com.actorpay.merchant.ui.payroll.PayRollActivity
import com.actorpay.merchant.ui.profile.ProfileActivity
import com.actorpay.merchant.ui.roles.RolesActivity
import com.actorpay.merchant.ui.setting.SettingActivity
import com.actorpay.merchant.ui.subAdmin.SubMerchantActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData.permissionDataList
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_DASHBOARD
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_ORDER
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_PRODUCT
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_ROLE
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_OUTLET
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_PAYMENT
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_REPORTS
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_SUB_MERCHANT
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_WALLET_BALANCE
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.Executor

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var doubleBackToExitPressedOnce = false
    var Merchantrole = ""
    private var authSheet:AuthBottomSheetDialog?=null
    var read = false
    var write = false
    private val homeviewmodel: HomeViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        homeviewmodel.getById()
        WorkSource()
        clickListeners()


        lifecycleScope.launch {
            viewModel.methodRepo.dataStore.getRole().collect { role ->
                Merchantrole = role
                initialisation()
                fingerPrint()
                this.cancel()
            }
        }
    }

    fun isBioMetricAvailable():Boolean{
        val biometricManager: BiometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                return true

            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                return false

            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                return false
            }
        }

        return false

    }

    private fun fingerPrint() {
        if(!isBioMetricAvailable())
            return
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    keyGuard()
                }
                // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authSheet!!.dismiss()

                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                }
            })
        val fingerPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Actor Pay")
            .setDescription("Sign-in using Fingerprint ID")
            .setNegativeButtonText("Use pattern lock")
            .setConfirmationRequired(false)
            .build()
        if (authSheet == null) {
            authSheet = AuthBottomSheetDialog {
                biometricPrompt?.authenticate(fingerPromptInfo)
            }
            authSheet?.isCancelable = false
            authSheet?.show(supportFragmentManager, "auth sheet")
            biometricPrompt?.authenticate(fingerPromptInfo)

        } else {
            if (authSheet?.isVisible!!.not())
                authSheet?.show(supportFragmentManager, "auth sheet")
        }
    }

    fun keyGuard() {
        val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (km.isKeyguardSecure) {
            val i =
                km.createConfirmDeviceCredentialIntent("Authentication required", "password")
            // startActivityForResult(i, Constants.CODE_AUTHENTICATION_VERIFICATION)
            resultLauncher.launch(i)

        } else {
//            authSheet!!.dismiss()
//            showCustomToast("No any security setup. please setup it.")
        }
    }

    private fun initialisation() {
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ivNotification.visibility = View.VISIBLE

        binding.toolbar.back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hamburger))
        binding.toolbar.ToolbarTitle.text = getString(R.string.dashboard)
        lifecycleScope.launchWhenCreated {
            homeviewmodel.methodRepo.dataStore.getBussinessName().collect { businessName ->
                binding.headerTitle.userProfileName.text = "$businessName"

            }
        }
        binding.toolbar.ivNotification.setOnClickListener {
            switchActivity(Intent(baseContext(), NotificationActivity::class.java))
        }

    }
    private fun getPermissionDetails() {
        if (Merchantrole != "MERCHANT") {
            homeviewmodel.getPermissions()
        }
        else {
            binding.reportsLay.visibility = View.VISIBLE
            binding.merchatLay.visibility = View.VISIBLE
            binding.headerTitle.userProfileBalance.visibility = View.VISIBLE
            binding.outlet.visibility = View.VISIBLE
            binding.dashboard.visibility = View.VISIBLE
            binding.constManageProduct.visibility = View.VISIBLE
            binding.myOrderLay.visibility = View.VISIBLE
            binding.myRolesLay.visibility = View.VISIBLE
            binding.viewMangeProduct.visibility = View.VISIBLE
            binding.viewSubMerchant.visibility = View.VISIBLE
            binding.viewOutlet.visibility = View.VISIBLE
            binding.viewRole.visibility = View.VISIBLE
            binding.viewOrder.visibility=View.VISIBLE
            binding.viewReport.visibility=View.VISIBLE
        }
    }
    private fun clickListeners() {

        binding.cvRole.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), RolesActivity::class.java))
        }

        binding.cvProduct.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageProductActivity::class.java))
        }

        binding.cvOrder.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageOrderActivity::class.java))
        }

        binding.cvSubMerchant.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), SubMerchantActivity::class.java))
        }

   binding.cvReport.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), PayRollActivity::class.java))
        }
        binding.cvOutlet.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), OutletActivity::class.java))
        }

        binding.cvEarnMoney.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), CommissionActivity::class.java))
        }



        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.back.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            } else {
                drawer_layout.openDrawer(GravityCompat.START, true)
            }
        }
        binding.myCommissionLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), CommissionActivity::class.java))
        }
        binding.disputeLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), DisputeActivity::class.java))
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

        binding.constSetting.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), SettingActivity::class.java))
        }

        binding.profileLay.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }

            val intent=Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.myOrderLay.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageOrderActivity::class.java))
        }


        reportsLay.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), PayRollActivity::class.java))

        }
        merchatLay.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), SubMerchantActivity::class.java))
        }

        constMore.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), MoreActivity::class.java))
        }

        outlet.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), OutletActivity::class.java))
        }

        binding.footer.btnHomeLogout.setOnClickListener {
            logOut()
        }
    }

    fun WorkSource() {
        lifecycleScope.launchWhenStarted {
            homeviewmodel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
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
                        else if (it.response is PermissionDetails) {
                            for (i in it.response.data.indices) {
                                 permissionDataList.forEachIndexed {
                                     index, permissionData ->
                                     if (permissionData.screenName == it.response.data[i].screenName) {
                                         permissionDataList[index].read = it.response.data[i].read
                                         permissionDataList[index].write = it.response.data[i].write
                                     }
                                 }
                            }

                            updateUi()
                        } else if (it.response is GetUserById) {
                            val data = it.response
                            Log.e("merchantId>>>", data.data.merchantId)
                            viewModel.methodRepo.dataStore.setMerchantId(data.data.merchantId)
                            binding.headerTitle.userProfileName.text = data.data.businessName
                            binding.tvBusinessName.text = "Hi\n" + data.data.businessName

                            getPermissionDetails()


                        } else showCustomAlert(
                            getString(R.string.please_try_after_sometime),
                            binding.root
                        )

                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (it.failResponse!!.code == 403) {
                            forcelogout(homeviewmodel.methodRepo)
                        } else {
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
                    binding.viewSubMerchant.visibility = View.VISIBLE
                }else{
                    binding.merchatLay.visibility=View.GONE
                }
            }
            if(it.screenName == SCREEN_OUTLET){
                if(it.write||it.read){
                    binding.outlet.visibility=View.VISIBLE
                    binding.viewOutlet.visibility = View.VISIBLE
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
                    binding.viewEarMoney.visibility=View.VISIBLE
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
                    binding.viewMangeProduct.visibility = View.VISIBLE
                }else{
                    binding.constManageProduct.visibility=View.GONE
                }
            }
            if(it.screenName == SCREEN_MANAGE_ORDER){
                if(it.write||it.read){
                    binding.myOrderLay.visibility=View.VISIBLE
                    binding.viewOrder.visibility=View.VISIBLE
                }else{
                    binding.myOrderLay.visibility=View.GONE
                }
            }

            if(it.screenName == SCREEN_REPORTS){
                if(it.write||it.read){
                    binding.reportsLay.visibility=View.VISIBLE
                    binding.viewReport.visibility = View.VISIBLE
                }else{
                    binding.reportsLay.visibility=View.GONE
                }
            }

            if(it.screenName == SCREEN_MANAGE_ROLE){
                if(it.write||it.read){
                    binding.myRolesLay.visibility=View.VISIBLE
                    binding.viewRole.visibility = View.VISIBLE
                }else{
                    binding.myRolesLay.visibility=View.GONE
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
    fun logOut(){
        CommonDialogsUtils.showCommonDialog(this,viewModel.methodRepo, getString(R.string.log_out),
            getString(R.string.are_you_sure),
            autoCancelable = true,
            isCancelAvailable = true,
            isOKAvailable = true,
            showClickable = false,
            callback = object : CommonDialogsUtils.DialogClick {
                override fun onClick() {
                    lifecycleScope.launchWhenCreated {
//                        delay(2000)
                        viewModel.methodRepo.dataStore.logOut()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                }

                override fun onCancel() {

                }
            })
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authSheet?.dismiss()

        }
        if (result.resultCode == Activity.RESULT_CANCELED) {

        }
    }
}