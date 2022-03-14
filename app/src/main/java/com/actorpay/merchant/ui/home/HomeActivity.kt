package com.actorpay.merchant.ui.home

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityHomeBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_DASHBOARD
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_ORDER
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_PRODUCT
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_ROLE
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_OUTLET
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_PAYMENT
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_REPORTS
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_SUB_MERCHANT
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_WALLET_BALANCE
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.ui.commission.EarnFragment
import com.actorpay.merchant.ui.disputes.DisputeFragment
import com.actorpay.merchant.ui.invitation.InvitationFragment
import com.actorpay.merchant.ui.login.AuthBottomSheetDialog
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.manageOrder.ManageOrderFragment
import com.actorpay.merchant.ui.manageProduct.ManageProductFragment
import com.actorpay.merchant.ui.more.MoreFragment
import com.actorpay.merchant.ui.outlet.OutletFragment
import com.actorpay.merchant.ui.payroll.PayrollFragment
import com.actorpay.merchant.ui.profile.ProfileFragment
import com.actorpay.merchant.ui.roles.RoleFragment
import com.actorpay.merchant.ui.setting.SettingFragment
import com.actorpay.merchant.ui.subAdmin.SubMerchantFragment
import com.actorpay.merchant.ui.wallet.WalletFragment
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData.permissionDataList
import com.actorpay.merchant.utils.OnFilterClick
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.getScopeName
import java.util.concurrent.Executor


class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var doubleBackToExitPressedOnce = false
    var Merchantrole = ""
    private var authSheet: AuthBottomSheetDialog? = null
    var read = false
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private var flagDrawer = false
    var write = false
    private val homeviewmodel: HomeViewModel by inject()
    private lateinit var filterClick: OnFilterClick
    private lateinit var navController: NavController
    private lateinit var navHostFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.container)!!
        navController = findNavController(R.id.container)
        setupNavigation()
        WorkSource()
        clickListeners()
        callSideDrawer()
        lifecycleScope.launch {
            viewModel.methodRepo.dataStore.getRole().collect { role ->
                Merchantrole = role
                initialisation()
                fingerPrint()
                this.cancel()
            }
        }
    }

    private fun callSideDrawer() {
        cardview_content?.radius = 0f
        actionBarDrawerToggle = object : ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar.toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            private val scaleFactor = 6f
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slideX = drawerView.width * slideOffset
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    cardview_content?.translationX = slideX
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        drawer_layout?.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    }
                }
                cardview_content?.scaleX = 1 - slideOffset / scaleFactor
                cardview_content?.scaleY = 1 - slideOffset / scaleFactor
                cardview_content?.radius = 0f
                Log.e("qwerty", "onDrawerSlide")
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                cardview_content?.radius = 0f
                Log.e("qwerty", "onDrawerClosed")
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                flagDrawer = true
                cardview_content?.radius = 30f
                Log.e("qwerty", "onDrawerOpened")

            }

            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if (!flagDrawer) {
                    cardview_content?.radius = 0f
                } else {
                    cardview_content?.radius = 30f
                }
                flagDrawer = false
                Log.e("qwerty", "onDrawerStateChanged")
            }
        }

        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        binding.drawerLayout.setScrimColor(Color.WHITE)
        binding.drawerLayout.drawerElevation = 0f
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle as ActionBarDrawerToggle)
        (actionBarDrawerToggle as ActionBarDrawerToggle).isDrawerSlideAnimationEnabled =
            true //disable "hamburger to arrow" drawable
        (actionBarDrawerToggle as ActionBarDrawerToggle).isDrawerIndicatorEnabled =
            false //disable "hamburger to arrow" drawable
        (actionBarDrawerToggle as ActionBarDrawerToggle).syncState()



    }

    fun isBioMetricAvailable(): Boolean {
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
        if (!isBioMetricAvailable()) return
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    keyGuard()
                }
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
            val i = km.createConfirmDeviceCredentialIntent("Authentication required", "password")
            resultLauncher.launch(i)
           } else {
           }
    }

    private fun initialisation() {
        lifecycleScope.launchWhenCreated {
            homeviewmodel.methodRepo.dataStore.getBussinessName().collect { businessName ->
                binding.headerTitle.userProfileName.text = "$businessName"
            }
        }
    }

    private fun getPermissionDetails() {
        if (Merchantrole != "MERCHANT") {
            homeviewmodel.getPermissions()
        } else {
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
            binding.viewOrder.visibility = View.VISIBLE
            binding.viewReport.visibility = View.VISIBLE
        }
    }

    private fun clickListeners() {
        binding.toolbar.ivFilter.setOnClickListener {
            filterClick.onClick()
        }

        binding.toolbar.back.setOnClickListener {
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is HomeFragment) {
                onBackPressed()
            } else {
                drawer_layout.openDrawer(GravityCompat.START, true)
            }
        }

        binding.constWallet.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is WalletFragment) {
                navController.navigate(R.id.walletFragment)
            }
        }
        binding.myCommissionLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is EarnFragment) {
                navController.navigate(R.id.earnFragment)
            }
        }
        binding.disputeLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is DisputeFragment) {
                navController.navigate(R.id.disputeFragment)
            }

        }

        binding.myRolesLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is RoleFragment) {
                navController.navigate(R.id.roleFragment)
            }
        }

        binding.dashboard.setOnClickListener {
            binding.drawerLayout.closeDrawers()
        }

        binding.constManageProduct.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is ManageProductFragment) {
                navController.navigate(R.id.manageProductFragment)
            }
        }


        binding.constSetting.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is SettingFragment) {
                navController.navigate(R.id.settingFragment)
            }
        }
        binding.profileLay.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is ProfileFragment) {
                navController.navigate(R.id.profileFragment)
            }
        }
        binding.myOrderLay.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is ManageOrderFragment) {
                navController.navigate(R.id.manageOrderFragment)
            }
        }


        reportsLay.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is PayrollFragment) {
                navController.navigate(R.id.payrollFragment)
            }

        }
        merchatLay.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is SubMerchantFragment) {
                navController.navigate(R.id.subMerchantFragment)
            }

        }


        constReferal.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is InvitationFragment) {
                navController.navigate(R.id.invitationFragment)
            }

        }

        constMore.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is MoreFragment) {
                navController.navigate(R.id.moreFragment)
            }
        }
        outlet.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawers()
            }
            val fragment = navHostFragment.childFragmentManager.fragments[0]
            if (fragment !is OutletFragment) {
                navController.navigate(R.id.outletFragment)
            }
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

                        if (it.response is PermissionDetails) {
                            for (i in it.response.data.indices) {
                                permissionDataList.forEachIndexed { index, permissionData ->
                                    if (permissionData.screenName == it.response.data[i].screenName) {
                                        permissionDataList[index].read = it.response.data[i].read
                                        permissionDataList[index].write = it.response.data[i].write
                                    }
                                }
                            }
                            homeviewmodel.getById()
                            updateUi()
                        } else if (it.response is GetUserById) {
                            val data = it.response
                            Log.e("merchantId>>>", data.data.merchantId)
                            viewModel.methodRepo.dataStore.setMerchantId(data.data.merchantId)
                            binding.headerTitle.userProfileName.text = data.data.businessName
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
            if (it.screenName == SCREEN_SUB_MERCHANT) {
                if (it.write || it.read) {
                    binding.merchatLay.visibility = View.VISIBLE
                    binding.viewSubMerchant.visibility = View.VISIBLE
                } else {
                    binding.merchatLay.visibility = View.GONE
                }
            }
            if (it.screenName == SCREEN_OUTLET) {
                if (it.write || it.read) {
                    binding.outlet.visibility = View.VISIBLE
                    binding.viewOutlet.visibility = View.VISIBLE
                } else {
                    binding.outlet.visibility = View.GONE
                }
            }

            if (it.screenName == SCREEN_WALLET_BALANCE) {
                if (it.write || it.read) {
                    binding.headerTitle.userProfileBalance.visibility = View.VISIBLE
                } else {
                    binding.headerTitle.userProfileBalance.visibility = View.GONE
                }
            }

            if (it.screenName == SCREEN_PAYMENT) {
                if (it.write || it.read) {
                    binding.earnMoney.visibility = View.VISIBLE
                    binding.viewEarMoney.visibility = View.VISIBLE
                } else {
                    binding.earnMoney.visibility = View.GONE
                }
            }

            if (it.screenName == SCREEN_DASHBOARD) {
                if (it.write || it.read) {
                    binding.dashboard.visibility = View.VISIBLE
                } else {
                    binding.dashboard.visibility = View.GONE
                }
            }
            if (it.screenName == SCREEN_MANAGE_PRODUCT) {
                if (it.write || it.read) {
                    binding.constManageProduct.visibility = View.VISIBLE
                    binding.viewMangeProduct.visibility = View.VISIBLE
                } else {
                    binding.constManageProduct.visibility = View.GONE
                }
            }
            if (it.screenName == SCREEN_MANAGE_ORDER) {
                if (it.write || it.read) {
                    binding.myOrderLay.visibility = View.VISIBLE
                    binding.viewOrder.visibility = View.VISIBLE
                } else {
                    binding.myOrderLay.visibility = View.GONE
                }
            }

            if (it.screenName == SCREEN_REPORTS) {
                if (it.write || it.read) {
                    binding.reportsLay.visibility = View.VISIBLE
                    binding.viewReport.visibility = View.VISIBLE
                } else {
                    binding.reportsLay.visibility = View.GONE
                }
            }

            if (it.screenName == SCREEN_MANAGE_ROLE) {
                if (it.write || it.read) {
                    binding.myRolesLay.visibility = View.VISIBLE
                    binding.viewRole.visibility = View.VISIBLE
                } else {
                    binding.myRolesLay.visibility = View.GONE
                }
            }

        }
    }

    override fun onBackPressed() {
        val fragment = navHostFragment.childFragmentManager.fragments[0]
        if (fragment is HomeFragment) {
            if (doubleBackToExitPressedOnce) {
                finish()
                return
            }
            doubleBackToExitPressedOnce = true
            showCustomAlert(getString(R.string.press_back_again), binding.root)
            lifecycleScope.launch(Dispatchers.Default) {
                delay(2000)
                doubleBackToExitPressedOnce = false
            }

        } else {
            super.onBackPressed()
        }

    }

    override fun onResume() {
        super.onResume()
        homeviewmodel.getById()
    }

    fun logOut() {
        CommonDialogsUtils.showCommonDialog(this, viewModel.methodRepo, getString(R.string.log_out),
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


    fun setupNavigation() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.hamburger)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.dashboard)
                    binding.toolbar.ivNotification.visibility = View.VISIBLE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }
                R.id.earnFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.earning)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.VISIBLE

                }
                R.id.manageProductFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.manage_product)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.VISIBLE

                }

                R.id.manageOrderFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.manage_order)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.VISIBLE

                }
                R.id.orderDetailFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.order_summary)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }
                R.id.outletFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.outlet)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.roleFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.all_roles)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.roleDetailFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.role)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.subMerchantFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.sub_merchants)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }
                R.id.profileFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.my_profile)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }
                R.id.payrollFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.payroll)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.addNewProductFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.addNewProduct)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.updateProductFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.updateProduct)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }
                R.id.addOutletFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.add_outlet)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.updateOutletFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.update_outlet)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.addSubMerchantFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.add_merchant_new)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE

                }

                R.id.disputeFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.disputes)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.VISIBLE

                }
                R.id.disputeDetailFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.dispute_details)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.settingFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.settings)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.moreFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.more)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.faqFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.faq)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.walletFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.wallet)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }
                R.id.addMoneyFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.add_money)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.paymentFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.payment)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.transactionHistoryFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.transaction_history)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.VISIBLE
                }

                R.id.transactionDetailFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.transaction_detail)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.transferMoneyFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.Transfer_money)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }
                R.id.payFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.Transfer_money)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }

                R.id.requestMoneyFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.request_money)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.VISIBLE
                }



                R.id.invitationFragment -> {
                    binding.toolbar.back.setImageResource(R.drawable.back)
                    binding.toolbar.ToolbarTitle.text = getString(R.string.my_refer)
                    binding.toolbar.ivNotification.visibility = View.GONE
                    binding.toolbar.ivFilter.visibility = View.GONE
                }
            }
        }
    }
    fun onFilterClick(filterClick: OnFilterClick) {
        this.filterClick = filterClick
    }
    fun updateToolbarText(title: String) {
        binding.toolbar.ToolbarTitle.text = title
    }
}