package com.actorpay.merchant.ui.payroll

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment

import com.actorpay.merchant.databinding.FragmentPayrollBinding
import com.actorpay.merchant.ui.payroll.adapter.PayrollAdapter
import java.util.ArrayList

class PayrollFragment : BaseFragment() {
    private lateinit var binding: FragmentPayrollBinding
    private lateinit var adapter: PayrollAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_payroll,container,false)
        Installation()
        return  binding.root
    }

    private fun Installation() {
        adapter = PayrollAdapter(requireActivity())
        adapter.UpdateList(ArrayList<String>())
        binding.paymentDataRecycler.adapter = adapter

        ClickListners()
    }
    private fun ClickListners() {
        binding.addMoney.setOnClickListener {
            showCustomAlert("Money Added Successfully",binding.root)
        }
    }

}