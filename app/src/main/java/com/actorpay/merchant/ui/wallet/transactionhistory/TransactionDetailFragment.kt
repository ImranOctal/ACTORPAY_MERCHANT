package com.actorpay.merchant.ui.wallet.transactionhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.WalletData
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentWalletDetailBinding
import org.koin.android.ext.android.inject

class TransactionDetailFragment : BaseFragment() {
    lateinit var binding: FragmentWalletDetailBinding
    private val transactionHistoryViewModel: TransactionHistoryViewModel by inject()
    lateinit var walletItem: WalletData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_wallet_detail,container,false)

        arguments.let {
            walletItem=requireArguments().getSerializable("item") as WalletData
        }

        if(walletItem!=null){

            binding.rowWalletText.text=walletItem.transactionRemark.replace(","," ")
            binding.rowWalletTxn.text=walletItem.walletTransactionId
            binding.rowWalletDate.text=transactionHistoryViewModel.methodRepo.getFormattedOrderDate(walletItem.createdAt)
            binding.rowWalletAmountDebit.text="₹ "+walletItem.transactionAmount.toString()
            binding.rowWalletName.text=walletItem.toUserName.replace(" ,","")

            transactionHistoryViewModel.methodRepo.makeTextLink(binding.rowWalletName,"${walletItem.toUserName.replace(", ","")}",true,
                ContextCompat.getColor(requireContext(),R.color.primary)){

                val bundle= bundleOf("item" to walletItem)
                Navigation.findNavController(requireView()).navigate(R.id.walletUserFragment,bundle)

            }

            if(walletItem.purchaseType == "TRANSFER"){
                binding.rowWalletName.visibility=View.VISIBLE
                binding.rowWalletNameText.visibility=View.VISIBLE
//                binding.rowWalletText.text="Money Transferred Successfully"
            }
            else if(walletItem.purchaseType == "SHOPPING"){
//                binding.rowWalletText.text="Online Shopping"
            }

            binding.rowWalletName.setOnClickListener {

            }

            if(walletItem.transactionTypes == "DEBIT"){
                binding.rowWalletAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                binding.rowWalletAmountDebit.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                binding.rowWalletAmountDebit.text="₹ "+walletItem.transactionAmount.toString()
            }
            if(walletItem.transactionTypes == "CREDIT"){
                binding.rowWalletAmount.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_color))
                binding.rowWalletAmountDebit.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_color))
                binding.rowWalletAmountDebit.text="₹ "+walletItem.transactionAmount.toString()
            }

            if(walletItem.percentage > 0.0) {
                binding.rowWalletAmount.text="₹ "+walletItem.transactionAmount.toString()
                binding.transferAmount.text="₹ "+walletItem.transferAmount.toString()
                binding.adminAmount.text="₹ "+walletItem.adminCommission.toString()
            }
            else{
                binding.cardSecondLayout.visibility=View.GONE
                binding.rowWalletAmountDebit.visibility=View.VISIBLE
                binding.rowWalletAmountDebitText.visibility=View.VISIBLE
            }

        }

        binding.btnDone.setOnClickListener {
            requireActivity().onBackPressed()
        }


        return  binding.root
    }
}