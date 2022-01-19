package com.actorpay.merchant.ui.manageOrder

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityOrderDetailBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.order.Item
import com.actorpay.merchant.ui.manageOrder.adapter.OrderDetailAdapter
import com.actorpay.merchant.ui.manageOrder.adapter.OrderStatusAdapter
import com.octal.actorpay.repositories.AppConstance.AppConstance

class OrderDetailActivity : BaseActivity() {
    lateinit var list: Item
    var bean = ArrayList<String>()

    var orderStatus = ""

    private lateinit var binding: ActivityOrderDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        list = intent.getSerializableExtra("data") as Item
        orderStatus = list.orderStatus
        binding.OrderType.text = list.orderStatus
        binding.back.setOnClickListener {
            finish()
        }
        binding.ivNext.setOnClickListener {
            binding.cardDropDown.visibility = View.VISIBLE
            binding.ivDropDown.visibility = View.VISIBLE
            binding. ivNext.visibility = View.GONE
        }

        binding.ivDropDown.setOnClickListener {
            binding.cardDropDown.visibility = View.GONE
            binding.ivDropDown.visibility = View.GONE
            binding. ivNext.visibility = View.VISIBLE

        }
        addOrderStatus()
        getIntentData(list)
        setRv()
    }

    private fun setRv() {
        binding.orderStatusSpinner.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.orderStatusSpinner.adapter = OrderStatusAdapter(this, bean)

    }

    private fun addOrderStatus() {
        if (orderStatus == "SUCCESS") {
            bean.add("READY")
            bean.add("CANCELED")
        } else if (orderStatus == "READY") {
            bean.add("CANCELED")
            bean.add("DISPATCHED")

        } else if (orderStatus == "RETURNING") {
            bean.add("RETURNED")

        } else if (orderStatus == "DELIVERED") {
            bean.clear()
        }

    }

    private fun getIntentData(list: Item) {
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.orderRecyclerView.adapter = OrderDetailAdapter(this, list.orderItemDtos)
        binding.merchantName.text = list.orderItemDtos[0].merchantName
        binding.orderDateText.text="Order Date & Time: "+methods.getFormattedOrderDate(list.createdAt)

        binding.orderNumber.text = "Order Number: " + list.orderNo
        binding.orderAmount.text = AppConstance.rupee + list.totalPrice
        binding.deliveryAddressAddress1.text = list.shippingAddressDTO.addressLine1
        binding.deliveryAddressAddress2.text = list.shippingAddressDTO.addressLine2
        binding.tvFirstName.text = "First Name: " + list.customer.firstName
        binding.tvLastName.text = "Last Name: " + list.customer.lastName
        binding.tvEmail.text = "Email: " + list.customer.email
        binding.tvContact.text = "Contact: " + list.customer.contactNumber
    }
}