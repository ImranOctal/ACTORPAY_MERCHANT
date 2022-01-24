package com.actorpay.merchant.ui.manageOrder

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityOrderDetailBinding
import com.actorpay.merchant.databinding.CancelBottomsheetBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.order.Item
import com.actorpay.merchant.repositories.retrofitrepository.models.order.UpdateOrderStatus
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.ui.manageOrder.adapter.OrderDetailAdapter
import com.actorpay.merchant.ui.manageOrder.adapter.OrderStatusAdapter
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.octal.actorpay.repositories.AppConstance.AppConstance
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OrderDetailActivity : BaseActivity() {
    lateinit var list: Item
    private val homeviewmodel: HomeViewModel by inject()
    var orderNo = ""

    private lateinit var binding: ActivityOrderDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        list = intent.getSerializableExtra("data") as Item
        binding.OrderType.text = list.orderStatus
        orderNo = list.orderNo
        binding.back.setOnClickListener {
            finish()
        }
        getIntentData(list)
        apiResponse()
    }
    private fun apiResponse() {
        lifecycleScope.launch {
            homeviewmodel.updateStatus.collect {
                when (it) {
                    is HomeSealedClasses.Companion.ResponseSealed.loading -> {
                        homeviewmodel.methodRepo.showLoadingDialog(this@OrderDetailActivity)
                    }
                    is HomeSealedClasses.Companion.ResponseSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (it.response is UpdateOrderStatus) {
                            CommonDialogsUtils.showCommonDialog(
                                this@OrderDetailActivity,
                                homeviewmodel.methodRepo,
                                "Update status successfully",
                                it.response.message,
                                autoCancelable = false,
                                isCancelAvailable = false,
                                isOKAvailable = true,
                                showClickable = false,
                                callback = object : CommonDialogsUtils.DialogClick {
                                    override fun onClick() {
                                        setResult(Activity.RESULT_OK)
                                        finish()

                                    }
                                    override fun onCancel() {

                                    }
                                }
                            )

                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }
                    }

                    else -> {
//                        signUpViewModel.methodRepo.hideLoadingDialog()
                    }
                }
            }
        }
    }
    private fun getIntentData(list: Item) {
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.orderRecyclerView.adapter = OrderDetailAdapter(this, list.orderItemDtos)
        binding.merchantName.text = list.orderItemDtos[0].merchantName
        binding.orderDateText.text = "Created On: " + methods.getFormattedOrderDate(list.createdAt)
        binding.orderNumber.text = list.orderNo
        binding.orderAmount.text = AppConstance.rupee + list.totalPrice
        binding.deliveryAddressAddress1.text = list.shippingAddressDTO.addressLine1
        binding.deliveryAddressAddress2.text = list.shippingAddressDTO.addressLine2
        binding.tvFirstName.text = "First Name: " + list.customer.firstName
        binding.tvLastName.text = "Last Name: " + list.customer.lastName
        binding.tvEmail.text = "Email: " + list.customer.email
        binding.tvContact.text = "Contact: " + list.customer.contactNumber
    }

    fun dialog(list: ArrayList<String>, root: View, orderItemId: MutableList<String>) {
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_status, null)
        val rvStatus = view.findViewById<RecyclerView>(R.id.rvStatus)
        val mpopup = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        mpopup.showAsDropDown(root, -10, (0))
        rvStatus.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvStatus.adapter = OrderStatusAdapter(this, list, orderItemId, mpopup)
    }

    fun cancelBottomSheet(itemStauts: String, orderItemId: MutableList<String>) {
        val binding: CancelBottomsheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.cancel_bottomsheet, null, false)
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        binding.btnSubmit.setOnClickListener {
            if (binding.etNote.text.isEmpty()) {
                showCustomToast(getString(R.string.add_note_description))
            } else {
                dialog.dismiss()
                homeviewmodel.updateStatus(binding.etNote.text.trim().toString(), orderItemId, itemStauts, orderNo)
            }
        }
        binding.btnCancnel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }

}