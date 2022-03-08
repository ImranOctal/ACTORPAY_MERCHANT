package com.actorpay.merchant.ui.login

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ForgetPasswordDialogBinding
import com.actorpay.merchant.repositories.methods.MethodsRepo

class ForgetPasswordDialog {


    fun show(activity:Activity, methodsRepo: MethodsRepo, onClick:(email:String)->Unit){
        val alertDialog = Dialog(activity)
        val binding = DataBindingUtil.inflate<ForgetPasswordDialogBinding>(
            activity.layoutInflater,
            R.layout.forget_password_dialog,
            null,
            false
        )

        binding.tvOk.setOnClickListener {
            val email=binding.tvMessage.text.toString().trim()
            if(email.isEmpty()){
                binding.tvMessage.error = activity.getString(R.string.email_empty)
            }
            else if(!methodsRepo.isValidEmail(email))
            {
                binding.tvMessage.setError(activity.getString(R.string.invalid_email))
            }
            else{
            alertDialog.dismiss()
                onClick(email)
            }
        }
        binding.tvCancel.setOnClickListener {
            alertDialog.dismiss()
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