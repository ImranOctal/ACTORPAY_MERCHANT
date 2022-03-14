package com.actorpay.merchant.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentWalletBinding
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment : BaseFragment() {
    lateinit var binding: FragmentWalletBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false)
        listener()
        return binding.root
    }
    private fun listener() {

        binding.cvAddMoney.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.addMoneyFragment)
        }

        binding.linearTransaction.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.transactionHistoryFragment)
        }

        binding. llTransferMoney.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.transferMoneyFragment)
        }


        binding. cvRequestMoney.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.requestMoneyFragment)
        }
    }
}