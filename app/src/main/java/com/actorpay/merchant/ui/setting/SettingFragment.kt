package com.actorpay.merchant.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentSettingBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.ui.home.ChangePasswordDialog
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class SettingFragment : BaseFragment() {
    private val settingViewModel: SettingViewModel by inject()
    private lateinit var binding: FragmentSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        binding.changePassword.setOnClickListener {
            changePasswordUi()
        }
        apiResponse()
        return binding.root
    }

    private fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            settingViewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is SuccessResponse) {
                            CommonDialogsUtils.showCommonDialog(
                                requireActivity(),
                                settingViewModel.methodRepo,
                                "Change Password",
                                it.response.message,
                                autoCancelable = false,
                                isCancelAvailable = false,
                                isOKAvailable = true,
                                showClickable = false,
                                callback = object : CommonDialogsUtils.DialogClick {
                                    override fun onClick() {

                                        requireActivity().onBackPressed()

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
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()

                        if (it.failResponse!!.code == 403) {
                            forcelogout(settingViewModel.methodRepo)
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
        ChangePasswordDialog().show(
            requireActivity(),
            settingViewModel.methodRepo
        ) { oldPassword, newPassword ->
            settingViewModel.changePassword(oldPassword, newPassword)
        }
    }
}