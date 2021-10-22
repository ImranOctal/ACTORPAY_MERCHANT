package com.actorpay.merchant.ui.addnewproduct


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityAddNewProductBinding
import com.actorpay.merchant.ui.manageOrder.adapter.OrderAdapter
import java.util.ArrayList

class AddNewProduct : BaseActivity() {
    private lateinit var binding:ActivityAddNewProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_new_product)
        Installation()
    }
    private fun Installation() {
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.addNewProduct)
        ClickListners()
    }
    private fun ClickListners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }

    }
}