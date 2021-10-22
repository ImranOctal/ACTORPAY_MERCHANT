package com.actorpay.merchant.ui.subAdmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityCreateSubAdminBinding
import com.actorpay.merchant.databinding.ActivityPayRollBinding
import com.actorpay.merchant.ui.payroll.adapter.PayrollAdapter
import java.util.ArrayList

class CreateSubAdminActivity :  BaseActivity() {
    private lateinit var binding: ActivityCreateSubAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_sub_admin)
        Installation()
    }

    private fun Installation() {
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.manage_new_sub_admin)
        ClickListners()
    }

    private fun ClickListners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }

    }
}