package com.actorpay.merchant.ui.addMoney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.GridLayoutManager
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
        list.add(BeanPayment("50",false))
        list.add(BeanPayment("100",false))
        list.add(BeanPayment("500",false))
        list.add(BeanPayment("1000",false))
        list.add(BeanPayment("2000",false))
        list.add(BeanPayment("3000",false))
        return root
    }

    private fun setupRv() {
        binding.rvAmount.layoutManager=GridLayoutManager(requireActivity(),3,)
        binding.rvAmount.adapter=AddMoneyAdapter(list)

    }
}