package com.actorpay.merchant.ui.addMoney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentAddMoneyBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.payment.BeanPayment

class AddMoneyFragment : BaseFragment() {
    val list = mutableListOf<BeanPayment>()
    lateinit var binding: FragmentAddMoneyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddMoneyBinding.inflate(inflater, container, false)
        val root: View = binding.root
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
        if (binding.enterAmountEdt.text.toString().isEmpty()) {
            binding.enterAmountEdt.error = "Please Enter Amount"
            binding.enterAmountEdt.requestFocus()
        } else if (binding.enterAmountEdt.text.toString()=="0") {
            binding.enterAmountEdt.error = "Amount should not less 1"
            binding.enterAmountEdt.requestFocus()
        } else{
            list.clear()
            val bundle= bundleOf("amount" to binding.enterAmountEdt.text.toString())
            Navigation.findNavController(requireView()).navigate(R.id.paymentFragment,bundle)
       }
    }
    private fun setupRv() {
        binding.rvAmount.layoutManager=GridLayoutManager(requireActivity(),3,)
        binding.rvAmount.adapter=AddMoneyAdapter(list){pos ->
            binding.enterAmountEdt.setText(list[pos].amount)
        }
    }
}