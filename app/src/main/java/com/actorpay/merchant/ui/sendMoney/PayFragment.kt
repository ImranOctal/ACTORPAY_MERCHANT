package com.actorpay.merchant.ui.sendMoney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentPayBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_EMAIL
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_MOBILE
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_QR


class PayFragment : BaseFragment() {
    private lateinit var binding: FragmentPayBinding
    private var key = ""
    private var name = ""
    private var contact = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pay, container, false)
        binding.apply {
            if (key == KEY_QR) {
                cardContact.visibility = View.GONE
                beneficiaryName.text = "Pay to $contact"
            } else if (key == KEY_MOBILE || key == KEY_EMAIL) {
                cardContact.visibility = View.VISIBLE
                beneficiaryName.text = "Pay to $name"
                beneficiaryContact.setText(contact)
            } else {
                cardContact.visibility = View.VISIBLE
                beneficiaryName.text = "Pay John"
                beneficiaryContact.setText(getString(R.string.enter_mobile_no_or_email))
                payNow.visibility = View.VISIBLE
            }
            payNow.setOnClickListener {
                var isValidate = true
                val amount = binding.beneficiaryAmount.text.toString().trim()
                val reason = binding.beneficiaryReason.text.toString().trim()
                if (amount == "") {
                    binding.beneficiaryAmount.error = "Please Enter Amount"
                    binding.beneficiaryAmount.requestFocus()
                    isValidate = false
                } else if (amount.toDouble() < 1) {
                    binding.beneficiaryAmount.error = "Amount should not less 1"
                    binding.beneficiaryAmount.requestFocus()
                    isValidate = false
                }
                if (reason == "") {
                    binding.beneficiaryReason.error = "Please Enter Reason"
                    isValidate = false
                }
                if (isValidate) {
                    val navOptions= NavOptions.Builder()
                    navOptions.setPopUpTo(R.id.homeFragment,false)
                    Navigation.findNavController(requireView()).navigate(R.id.paymentFragment,null,  navOptions.build())

                }
            }
        }
        return binding.root
    }
}