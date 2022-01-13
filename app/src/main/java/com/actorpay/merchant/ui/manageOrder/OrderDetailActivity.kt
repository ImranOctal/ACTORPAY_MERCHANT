package com.actorpay.merchant.ui.manageOrder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ActivityOrderDetailBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.order.Item
import com.actorpay.merchant.ui.manageOrder.adapter.OrderDetailAdapter
import com.octal.actorpay.repositories.AppConstance.AppConstance

class OrderDetailActivity : AppCompatActivity() {
    lateinit var  list: Item
    private lateinit var binding: ActivityOrderDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        list=intent.getSerializableExtra("data") as Item
        binding.back.setOnClickListener {
            finish()
        }
        getIntentData(list)
//        binding.orderSummaryText.text=list.orderNo


//        if(data!=null && data!!.size>0) {
//            binding.orderSummaryText.text = data!![0].productName
//        }
    }


//    companion object{
//        var data: List<OrderItemDto>?=null
//    }

//    override fun onDestroy() {
//        data=null
//        super.onDestroy()
//    }

    private fun getIntentData(list: Item) {
        binding.orderRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.orderRecyclerView.adapter=OrderDetailAdapter(this,list.orderItemDtos)
        binding.merchantName.text=list.orderItemDtos[0].merchantName
        binding.orderDateText.text=list.createdAt
        binding.orderNumber.text=list.orderNo
        binding.orderAmount.text=AppConstance.rupee+list.totalPrice
        binding.deliveryAddressAddress1.text=list.shippingAddressDTO.addressLine1
        binding.deliveryAddressAddress2.text=list.shippingAddressDTO.addressLine2
        binding.deliveryAddressCity.text=list.shippingAddressDTO.city+","+list.shippingAddressDTO.state
    }
}