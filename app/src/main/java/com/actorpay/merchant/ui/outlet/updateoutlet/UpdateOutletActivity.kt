package com.actorpay.merchant.ui.outlet.updateoutlet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityOutletBinding

class UpdateOutletActivity : AppCompatActivity() {
    class OutletActivity : BaseActivity() {
        private lateinit var binding: ActivityOutletBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this, R.layout.add_outlet_actitvity)
        }
    }
}