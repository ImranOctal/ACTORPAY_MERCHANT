package com.actorpay.merchant.ui.setting

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivitySettingBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.ui.home.ChangePasswordDialog
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.utils.CommonDialogsUtils
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class SettingActivity : BaseActivity() {
    private val homeviewmodel: HomeViewModel by inject()
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        binding.toolbar.back.setOnClickListener {
            finish()
        }
        binding.toolbar.ToolbarTitle.text = getString(R.string.settings)

        binding.changePassword.setOnClickListener {

            changePasswordUi()
        }
        apiResponse()
    }

    private fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            homeviewmodel.changePasswordLive.collect {
                when (it) {
                    HomeSealedClasses.Companion.ResponseChangePasswordSealed.loading() -> {
                        showLoadingDialog()
                    }
                    is HomeSealedClasses.Companion.ResponseChangePasswordSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is SuccessResponse) {
                            CommonDialogsUtils.showCommonDialog(
                                this@SettingActivity,
                                homeviewmodel.methodRepo,
                                "Change Password",
                                it.response.message,
                                autoCancelable = false,
                                isCancelAvailable = false,
                                isOKAvailable = true,
                                showClickable = false,
                                callback = object : CommonDialogsUtils.DialogClick {
                                    override fun onClick() {

                                        finish()
                                    }

                                    override fun onCancel() {

                                    }
                                }
                            )
                        } else showCustomAlert(
                            getString(R.string.please_try_after_sometime),
                            binding.root
                        )

                    }
                    is HomeSealedClasses.Companion.ResponseChangePasswordSealed.ErrorOnResponse -> {
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

    fun changePasswordUi() {
        ChangePasswordDialog().show(this, homeviewmodel.methodRepo) { oldPassword, newPassword ->
            homeviewmodel.changePassword(oldPassword, newPassword)
        }
    }
}