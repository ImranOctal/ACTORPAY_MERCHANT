package com.actorpay.merchant.ui.manageOrder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.DataBinderMapperImpl
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ActivityManageOrderBinding
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.manageOrder.adapter.OrderAdapter
import java.util.ArrayList

class ManageOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageOrderBinding
    private lateinit var adapter: OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_order)
        Installation()
    }

    private fun Installation() {
        adapter = OrderAdapter(this)
        adapter.UpdateList(ArrayList<String>())
        binding.manageOrder.adapter = adapter
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.manage_order)
        ClickListners()
    }

    private fun ClickListners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
    }
}