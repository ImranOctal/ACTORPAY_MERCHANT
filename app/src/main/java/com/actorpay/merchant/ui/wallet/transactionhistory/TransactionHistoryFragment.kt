package com.actorpay.merchant.ui.wallet.transactionhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.WalletBalance
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.WalletHistoryResponse
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.WalletListData
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentTransactionHistoryBinding
import com.actorpay.merchant.ui.wallet.WalletFilterDialog
import com.actorpay.merchant.utils.OnFilterClick
import com.actorpay.merchant.utils.ResponseSealed
import com.techno.taskmanagement.utils.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject


class TransactionHistoryFragment : BaseFragment(),OnFilterClick {
    lateinit var binding: FragmentTransactionHistoryBinding
    private val transactionHistoryViewModel: TransactionHistoryViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_history, container, false)
        transactionHistoryViewModel.walletListData.pageNumber=0
        transactionHistoryViewModel.walletListData.items.clear()
        binding.rvItemsWalletID.adapter?.notifyDataSetChanged()
        transactionHistoryViewModel.getWalletBalance()
        apiResponse()

        setupRv()
        onFilterClick(this)
        return binding.root
    }
    private fun setupRv() {

        val mLayoutManager = LinearLayoutManager(requireContext())
        val endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener =
            object : EndlessRecyclerViewScrollListener(mLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    if (transactionHistoryViewModel.walletListData.pageNumber < transactionHistoryViewModel.walletListData.totalPages - 1) {
                        transactionHistoryViewModel.walletListData.pageNumber += 1
                        transactionHistoryViewModel.getWalletHistory()
                    }
                }
            }
        binding.rvItemsWalletID.layoutManager=mLayoutManager
        binding.rvItemsWalletID.addOnScrollListener(endlessRecyclerViewScrollListener)
        binding.rvItemsWalletID.adapter= AdapterTransactionHistory(requireContext(),transactionHistoryViewModel.walletListData.items,transactionHistoryViewModel.methodRepo){ pos ->
            val bundle= bundleOf("item" to transactionHistoryViewModel.walletListData.items[pos])
            Navigation.findNavController(requireView()).navigate(R.id.transactionDetailFragment,bundle)
        }
    }

    fun updateUI(walletListData: WalletListData){

        transactionHistoryViewModel.walletListData.pageNumber =
            walletListData.pageNumber
        transactionHistoryViewModel.walletListData.totalPages =
            walletListData.totalPages
        transactionHistoryViewModel.walletListData.items.addAll(walletListData.items)


        binding.rvItemsWalletID.adapter?.notifyDataSetChanged()

        if(transactionHistoryViewModel.walletListData.items.size>0) {
            binding.imageEmpty.visibility=View.GONE
            binding.textEmpty.visibility=View.GONE
        }
        else{
            binding.imageEmpty.visibility=View.VISIBLE
            binding.textEmpty.visibility=View.VISIBLE
        }
    }

    fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            transactionHistoryViewModel.responseLive.collect { event ->
                when (event) {
                    is ResponseSealed.Loading -> {
                        if(event.isLoading)
                            showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        when (event.response) {
                            is WalletHistoryResponse -> {
                                updateUI(event.response.data)
                            }
                            is WalletBalance ->{
                                binding.tvAmount.text= "₹ "+event.response.data.amount.toString()

                                transactionHistoryViewModel.walletListData.pageNumber=0
                                transactionHistoryViewModel.walletListData.items.clear()
                                transactionHistoryViewModel.getWalletHistory()
                            }
                        }
                        transactionHistoryViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (event.failResponse!!.code == 403) {
                            forcelogout(transactionHistoryViewModel.methodRepo)
                        }
                        transactionHistoryViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.Empty -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
    }

    override fun onClick() {

        filterBottomSheet()
    }

    private fun filterBottomSheet() {
        WalletFilterDialog(transactionHistoryViewModel.walletParams,requireActivity(),transactionHistoryViewModel.methodRepo){
            transactionHistoryViewModel.walletParams=it
            transactionHistoryViewModel.walletListData.pageNumber=0
            transactionHistoryViewModel.walletListData.totalPages=0
            transactionHistoryViewModel.walletListData.items.clear()
            binding.rvItemsWalletID.adapter?.notifyDataSetChanged()
            transactionHistoryViewModel.getWalletHistory()
        }.show()
    }
}