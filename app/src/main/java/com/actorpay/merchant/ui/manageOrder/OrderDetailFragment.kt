package com.actorpay.merchant.ui.manageOrder

import android.content.Context
import android.graphics.Color
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
import com.actorpay.merchant.base.BaseFragment

import com.actorpay.merchant.databinding.CancelBottomsheetBinding
import com.actorpay.merchant.databinding.FragmentOrderDetaailBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.repositories.retrofitrepository.models.order.BeanViewAllOrder
import com.actorpay.merchant.repositories.retrofitrepository.models.order.Item
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderNotesDto
import com.actorpay.merchant.repositories.retrofitrepository.models.order.UpdateOrderStatus
import com.actorpay.merchant.repositories.retrofitrepository.models.ordernote.OrderNote
import com.actorpay.merchant.ui.manageOrder.adapter.AdapterNote
import com.actorpay.merchant.ui.manageOrder.adapter.OrderDetailAdapter
import com.actorpay.merchant.ui.manageOrder.adapter.OrderStatusAdapter
import com.actorpay.merchant.ui.manageOrder.viewModel.ManageOrderViewModel
import com.actorpay.merchant.ui.manageOrder.viewModel.OrderDetailViewModel
import com.actorpay.merchant.utils.ResponseSealed
import com.actorpay.merchant.utils.roundBorderedView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OrderDetailFragment : BaseFragment() {
    lateinit var list: Item
    private val orderViewModel: ManageOrderViewModel by inject()
    private val orderDetailViewModel: OrderDetailViewModel by inject()
    private lateinit var binding: FragmentOrderDetaailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater ,R.layout.fragment_order_detaail, container, false)
        list = arguments?.getSerializable("data") as Item

        binding.btnNote.setOnClickListener {
            addNote()
        }
        binding.pullToRefresh.setOnRefreshListener {
            getAllOrderApi()
            binding.pullToRefresh.isRefreshing=false

        }
        getAllOrderApi()
        getIntentData(list)
        apiResponse()
        return  binding.root

    }
    private fun getAllOrderApi() {
        orderViewModel.getAllOrder("", "", "", "", "", list.orderNo)
    }
    private fun setupRv(orderNotesDtos: List<OrderNotesDto>) {
        binding.rvNote.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvNote.adapter = AdapterNote(requireActivity(), (orderNotesDtos))
    }
    private fun apiResponse() {
        lifecycleScope.launch {
            orderViewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        when (it.response) {
                            is UpdateOrderStatus -> {
                                getAllOrderApi()
                            }

                            is BeanViewAllOrder -> {
                                if (it.response.data.items.size > 0) {
                                    binding.OrderType.text = it.response.data.items[0].orderStatus.replace("_"," ")
                                    binding.orderRecyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                                    binding.orderRecyclerView.adapter = OrderDetailAdapter(requireActivity(), it.response.data.items[0].orderItemDtos){ list: ArrayList<String>, root: View, orderItemId: MutableList<String> ->
                                        dialog(list,root,orderItemId)
                                    }
                                    setupRv(it.response.data.items[0].orderNotesDtos)
                                }
                            }
                            else -> {
                                showCustomAlert(
                                    getString(R.string.please_try_after_sometime),
                                    binding.root
                                )
                            }
                        }
                    }
                    is ResponseSealed.ErrorOnResponse->{
                        hideLoadingDialog()
                        if(it.failResponse!!.code==403){
                            forcelogout(orderViewModel.methodRepo)
                        }else{
                            showCustomAlert(
                                it.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    else->{
                        hideLoadingDialog()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderDetailViewModel.responseLive.collect{ action ->
                when (action) {
                    is  ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (action.response is OrderNote) {
                            showCustomToast("Add Order Note Successfully")
                            getAllOrderApi()
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if(action.failResponse!!.code==403){
                            forcelogout(orderViewModel.methodRepo)

                        }else{
                            showCustomAlert(
                                action.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    else -> hideLoadingDialog()
                }
            }
        }
    }
    private fun getIntentData(list: Item) {
        binding.merchantName.text = list.orderItemDtos[0].merchantName
        binding.orderDateText.text = "Created On: " + methods.getFormattedOrderDate(list.createdAt)
        binding.orderNumber.text = list.orderNo
        binding.orderAmount.text = AppConstance.rupee + list.totalPrice
        binding.deliveryAddressAddress1.text = list.shippingAddressDTO.addressLine1
        binding.deliveryAddressAddress2.text = list.shippingAddressDTO.addressLine2
        binding.tvFirstName.text = "First Name: " + list.customer.firstName
        binding.tvLastName.text = "Last Name: " + list.customer.lastName
        binding.tvEmail.text = "Email: " + list.customer.email
        binding.tvPaymentMode.text =  list.paymentMethod
        binding.tvContact.text = "Contact: " + list.customer.contactNumber
        if(list.orderStatus=="CANCELLED"){
            binding.OrderType.setTextColor(Color.parseColor(AppConstance.red_color))
            binding.OrderType.roundBorderedView(10,
                AppConstance.white_color,
                AppConstance.red_color,1)

        }else if(list.orderStatus=="PARTIALLY_RETURNED"||list.orderStatus=="PARTIALLY_RETURNING"||list.orderStatus=="PARTIALLY_CANCELLED"||list.orderStatus=="PARTIALLY_CANCELLED"){
            binding.OrderType.setTextColor(Color.parseColor(AppConstance.blue_color))
            binding.OrderType.roundBorderedView(10,
                AppConstance.white_color,
                AppConstance.blue_color,1)
        }else{
            binding.OrderType.setTextColor(Color.parseColor(AppConstance.green_color))
            binding.OrderType.roundBorderedView(10,
                AppConstance.white_color,
                AppConstance.green_color,1)
        }
    }

    fun dialog(list: ArrayList<String>, root: View, orderItemId: MutableList<String>) {
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_status, null)
        val rvStatus = view.findViewById<RecyclerView>(R.id.rvStatus)
        val mpopup = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        mpopup.showAsDropDown(root, -10, (0))
        rvStatus.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        rvStatus.adapter = OrderStatusAdapter(requireActivity(), list, orderItemId, mpopup){itemStauts: String, orderItemId: MutableList<String>->
            cancelBottomSheet(itemStauts,orderItemId)
        }
    }

    fun cancelBottomSheet(itemStauts: String, orderItemId: MutableList<String>) {
        val binding: CancelBottomsheetBinding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()), R.layout.cancel_bottomsheet, null, false)
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        binding.btnSubmit.setOnClickListener {
            if (binding.etNote.text.isEmpty()) {
                showCustomToast(getString(R.string.add_note_description))
            } else {
                dialog.dismiss()
                orderViewModel.updateStatus(binding.etNote.text.trim().toString(), orderItemId, itemStauts, list.orderNo)
            }
        }
        binding.btnCancnel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }
    private fun addNote() {
        val binding: CancelBottomsheetBinding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()), R.layout.cancel_bottomsheet, null, false)
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        binding.btnSubmit.setOnClickListener {
            if (binding.etNote.text.isEmpty()) {
                showCustomToast(getString(R.string.add_note_description))
            } else {
                dialog.dismiss()
                orderDetailViewModel.addNote(binding.etNote.text.toString().trim(), list.orderNo)
            }
        }
        binding.btnCancnel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }
}