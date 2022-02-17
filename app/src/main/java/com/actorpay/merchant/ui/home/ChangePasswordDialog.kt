package com.actorpay.merchant.ui.home

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.PasswordTransformationMethod
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ChangePasswordDialogBinding
import com.actorpay.merchant.repositories.methods.MethodsRepo

class ChangePasswordDialog {
    private var showPasswordOld = false
    fun show(
        activity: Activity,
        methodsRepo: MethodsRepo,
        onClick: (oldPassword: String, newPassword: String) -> Unit
    ) {
        val alertDialog = Dialog(activity)
        val binding = DataBindingUtil.inflate<ChangePasswordDialogBinding>(
            activity.layoutInflater,
            R.layout.change_password_dialog,
            null,
            false
        )
        binding.tvOk.setOnClickListener {
            val oldPassword = binding.editChangePasswordOld.text.toString().trim()
            val newPassword = binding.editChangePasswordNew.text.toString().trim()
            val confirmPassword = binding.editChangePasswordConfirm.text.toString().trim()
            if (oldPassword.isEmpty()) {
                binding.editChangePasswordOld.error =
                    activity.getString(R.string.oops_your_password_is_empty)
            } else if (newPassword.length < 8 || !methodsRepo.isValidPassword(newPassword)) {
                binding.editChangePasswordNew.error =
                    activity.getString(R.string.oops_your_password_is_not_valid)
            } else if (confirmPassword != newPassword) {
                binding.editChangePasswordConfirm.error =
                    (activity.getString(R.string.cpass_and_pass_not_match))
            } else {
                alertDialog.dismiss()
                onClick(oldPassword, newPassword)
            }
        }
        binding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        binding.passwordShowHideOld.setOnClickListener {
            if (showPasswordOld) {
                binding.editChangePasswordOld.transformationMethod = PasswordTransformationMethod()
                showPasswordOld = false
                binding.passwordShowHideOld.setImageResource(R.drawable.hide)
                binding.editChangePasswordOld.setSelection(binding.editChangePasswordOld.text.toString().length)
            } else {
                binding.editChangePasswordOld.transformationMethod = null
                showPasswordOld = true
                binding.passwordShowHideOld.setImageResource(R.drawable.show)
                binding.editChangePasswordOld.setSelection(binding.editChangePasswordOld.text.toString().length)
            }
        }

        binding.passwordShowHideNew.setOnClickListener {
            if (showPasswordOld) {
                binding.editChangePasswordNew.transformationMethod = PasswordTransformationMethod()
                showPasswordOld = false
                binding.passwordShowHideNew.setImageResource(R.drawable.hide)
                binding.editChangePasswordNew.setSelection(binding.editChangePasswordNew.text.toString().length)
            } else {
                binding.editChangePasswordNew.transformationMethod = null
                showPasswordOld = true
                binding.passwordShowHideNew.setImageResource(R.drawable.show)
                binding.editChangePasswordNew.setSelection(binding.editChangePasswordNew.text.toString().length)
            }
        }

        binding.passwordShowHideConfirm.setOnClickListener {
            if (showPasswordOld) {
                binding.editChangePasswordConfirm.transformationMethod = PasswordTransformationMethod()
                showPasswordOld = false
                binding.passwordShowHideConfirm.setImageResource(R.drawable.hide)
                binding.editChangePasswordConfirm.setSelection(binding.editChangePasswordConfirm.text.toString().length)
            } else {
                binding.editChangePasswordConfirm.transformationMethod = null
                showPasswordOld = true
                binding.passwordShowHideConfirm.setImageResource(R.drawable.show)
                binding.editChangePasswordConfirm.setSelection(binding.editChangePasswordConfirm.text.toString().length)
            }
        }
        alertDialog.apply {
            window?.attributes?.windowAnimations = R.style.DialogAnimationTheme
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            window!!.setLayout(
                (methodsRepo.getDeviceWidth(context) / 100) * 90,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(true)

        }.also { it.show() }
    }
}