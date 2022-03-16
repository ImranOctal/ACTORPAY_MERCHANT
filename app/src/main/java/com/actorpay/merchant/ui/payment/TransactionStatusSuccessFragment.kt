package com.actorpay.merchant.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentTransactionStatusSuccessBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance

class TransactionStatusSuccessFragment : BaseFragment() {
    lateinit var binding: FragmentTransactionStatusSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_status_success,container,false)

        var amount =arguments?.getString("amount")
        binding.paymentStatusText.text= "Amount ${AppConstance.rupee+amount}\n added into wallet successfully"
        binding.done.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack(R.id.homeFragment,false)
        }

        binding.history.setOnClickListener {
            val navOptions=NavOptions.Builder()
            navOptions.setPopUpTo(R.id.homeFragment,false)
           Navigation.findNavController(requireView()).navigate(R.id.transactionHistoryFragment,null,  navOptions.build())
        }
        return binding.root
    }
}