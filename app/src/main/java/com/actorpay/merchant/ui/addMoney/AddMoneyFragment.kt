package com.actorpay.merchant.ui.addMoney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.AddMoneyParams
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.AddMoneyResponse
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.WalletBalance
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentAddMoneyBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.payment.BeanPayment
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class AddMoneyFragment : BaseFragment() {
    val list = mutableListOf<BeanPayment>()
    lateinit var binding: FragmentAddMoneyBinding
    private val addMoneyViewModel: AddMoneyViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddMoneyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        addMoneyViewModel.getWalletBalance()
        apiResponse()
        setupRv()
        binding.buttonAddMoney.setOnClickListener {
            validation()
        }
        list.add(BeanPayment("50",false))
        list.add(BeanPayment("100",false))
        list.add(BeanPayment("500",false))
        list.add(BeanPayment("1000",false))
        list.add(BeanPayment("2000",false))
        list.add(BeanPayment("3000",false))
        return root
    }

    private fun validation() {
        if (binding.enterAmountEdt.text.toString().trim().isEmpty()) {
            binding.enterAmountEdt.error = "Please Enter Amount"
            binding.enterAmountEdt.requestFocus()
        } else if (binding.enterAmountEdt.text.toString().trim().toDouble() <= 1) {
            binding.enterAmountEdt.error = "Amount should not less 1"
            binding.enterAmountEdt.requestFocus()
        } else {
            addMoneyViewModel.methodRepo.hideSoftKeypad(requireActivity())
            lifecycleScope.launchWhenCreated {
                addMoneyViewModel.methodRepo.dataStore.getEmail().collect { email ->
                    addMoneyViewModel.addMoney(
                        AddMoneyParams(
                            binding.enterAmountEdt.text.toString().trim(),email
                        )
                    )
                }
            }
        }
    }
    private fun setupRv() {
        binding.rvAmount.layoutManager=GridLayoutManager(requireActivity(),3,)
        binding.rvAmount.adapter=AddMoneyAdapter(list){pos ->
            binding.enterAmountEdt.setText(list[pos].amount)
            binding.enterAmountEdt.setSelection(binding.enterAmountEdt.text.toString().length)
        }
    }
    fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            addMoneyViewModel.responseLive.collect { event ->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        when (event.response) {
                            is AddMoneyResponse -> {
                                list.clear()
                                val bundle= bundleOf("amount" to binding.enterAmountEdt.text.toString().trim(),"customerName" to event.response.data.customerName,"transactionId" to event.response.data.transactionId,"send" to false)
                                Navigation.findNavController(requireView()).navigate(R.id.transactionStatusSuccessFragment,bundle)
                                binding.enterAmountEdt.setText("")
                            }
                            is WalletBalance ->{
                                binding.tvAmount.text= "₹ "+event.response.data.amount.toString()
                            }
                        }
                        addMoneyViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        addMoneyViewModel.responseLive.value = ResponseSealed.Empty
                        hideLoadingDialog()
                        if (event.failResponse!!.code == 403) {
                            forcelogout(addMoneyViewModel.methodRepo)
                        }
                        else
                            showCustomToast(event.failResponse.message)

                    }
                    is ResponseSealed.Empty -> {
                        hideLoadingDialog()

                    }
                }
            }
        }
    }
}