package com.actorpay.merchant.ui.payroll

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityManageOrderBinding
import com.actorpay.merchant.databinding.ActivityPayRollBinding
import com.actorpay.merchant.ui.manageOrder.adapter.OrderAdapter
import com.actorpay.merchant.ui.payroll.adapter.PayrollAdapter
import java.util.ArrayList

class PayRollActivity : BaseActivity() {
    private lateinit var binding: ActivityPayRollBinding
    private lateinit var adapter: PayrollAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_roll)
        Installation()
    }

    private fun Installation() {
        adapter = PayrollAdapter(this)
        adapter.UpdateList(ArrayList<String>())
        binding.paymentDataRecycler.adapter = adapter
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.payroll)
        ClickListners()
    }
    private fun ClickListners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
        binding.addMoney.setOnClickListener {
            showCustomAlert("Money Added Successfully",binding.root)
        }
    }
}