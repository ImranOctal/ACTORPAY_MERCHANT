package com.actorpay.merchant.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.CancelBottomsheetBinding
import com.actorpay.merchant.databinding.CommonDialogBinding
import com.actorpay.merchant.databinding.NetworkDialogBinding
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.FailResponse
import com.actorpay.merchant.ui.home.HomeActivity
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.manageOrder.adapter.OrderStatusAdapter
import com.actorpay.merchant.ui.splash.SplashActivity
import com.actorpay.merchant.viewmodel.ActorPayViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.ktor.routing.RoutingPath.Companion.root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.internal.notify


class CommonDialogsUtils {


    /**
     * Interface for get Click on Dialog
     */
    interface DialogClick {
        fun onClick()
        fun onCancel()
    }


    /**
     * Method for show common dialog.
     */
    companion object {
        fun showCommonDialog(
            activity: Activity,
            methodsRepo: MethodsRepo,
            title: String,
            msg: String,
            autoCancelable: Boolean = false,
            isCancelAvailable: Boolean = false,
            isOKAvailable: Boolean = true,
            showClickable: Boolean = false,
            callback: DialogClick? = null
        ) {
            val alertDialog = Dialog(activity)

            val binding = DataBindingUtil.inflate<CommonDialogBinding>(
                activity.layoutInflater,
                R.layout.common_dialog,
                null,
                false
            )

            binding.tvTitle.text = title
            binding.tvMessage.text = msg

            val span = Spannable.Factory.getInstance()
                .newSpannable("If you have any questions, require assistance or would like to offer feedback, click here to contact us.")
            span.setSpan(object : ClickableSpan() {
                override fun onClick(v: View) {

                    val mailto = "mailto:actorpay@gmail.com"
                    val emailIntent = Intent(Intent.ACTION_SENDTO)
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Experience with the Actor Pay App")
                    emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    emailIntent.data = Uri.parse(mailto)

                    try {
                        activity.startActivity(emailIntent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            activity,
                            "There is no app on your device to send this email!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }


                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = ResourcesCompat.getColor(activity.resources, R.color.black,null)
                    ds.linkColor = ContextCompat.getColor(activity, R.color.black)
                }
            }, 79, 104, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

// All the rest will have the same spannable.

            binding.tvSupportMessage.text = span

            binding.tvSupportMessage.movementMethod = LinkMovementMethod.getInstance()
            //binding.tvSupportMessage.movementMethod = LinkMovementMethod.getInstance()
            if (showClickable) {
                binding.tvSupportMessage.visibility = View.VISIBLE
            } else {
                binding.tvSupportMessage.visibility = View.GONE
            }
            //binding.tvSupportMessage.setLinkTextColor(R.color.material_blue_grey_800)

            if (binding.tvTitle.text.toString() == activity.getString(R.string.not_connected_internet)) {
                binding.tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.black))
            }

            binding.tvOk.setOnClickListener {
                alertDialog.dismiss()
                callback?.onClick()
            }
            if (isCancelAvailable)
                binding.tvCancel.visibility = View.VISIBLE else binding.tvCancel.visibility =
                    View.GONE

             if (isOKAvailable)
                binding.tvOk.visibility = View.VISIBLE else binding.tvOk.visibility =
                    View.GONE

            binding.tvCancel.setOnClickListener {
                alertDialog.dismiss()
                callback?.onCancel()
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
                setCanceledOnTouchOutside(autoCancelable)

            }.also { it.show() }
        }




        fun showApiErrorDialog(context: Activity, methodsRepo: MethodsRepo, failResponse:  FailResponse?, optionalMessage: String) {
            var message = ""
            message = failResponse?.message ?: optionalMessage
            showCommonDialog(context,methodsRepo, "Error", message,
                autoCancelable = true,
                isCancelAvailable = false,
                isOKAvailable = true,
                showClickable = false
            )
        }


        fun networkDialog(activity: Activity,methodsRepo: MethodsRepo,onClick:()->Unit) {
            val binding: NetworkDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.network_dialog, null, false)
            val dialog = Dialog(activity, R.style.MainDialog)
            binding.btnRetry.setOnClickListener {
                if(methodsRepo.isNetworkConnected()){
                     dialog.dismiss()
                     onClick()
                }else{
                    Toast.makeText(activity,"No Internet Connection",Toast.LENGTH_LONG).show()
                }
            }
            dialog.setContentView(binding.root)
            dialog.setCancelable(false)
            dialog.show()
        }
    }
}