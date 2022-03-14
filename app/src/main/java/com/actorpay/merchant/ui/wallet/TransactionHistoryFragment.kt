package com.actorpay.merchant.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentTransactionHistoryBinding
import com.actorpay.merchant.ui.wallet.adapter.AdapterTransactionHistory
import com.actorpay.merchant.utils.OnFilterClick


class TransactionHistoryFragment : BaseFragment(),OnFilterClick {
    lateinit var binding: FragmentTransactionHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_history, container, false)
        setupRv()
        onFilterClick(this)
        return binding.root
    }
    private fun setupRv() {
        binding.rvItemsWalletID.layoutManager=LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.rvItemsWalletID.adapter=AdapterTransactionHistory(){pos ->
            Navigation.findNavController(requireView()).navigate(R.id.transactionDetailFragment)
        }
    }

    override fun onClick() {

        filterBottomSheet()
    }

    private fun filterBottomSheet() {
        WalletFilterDialog(requireActivity()).show()
    }
}