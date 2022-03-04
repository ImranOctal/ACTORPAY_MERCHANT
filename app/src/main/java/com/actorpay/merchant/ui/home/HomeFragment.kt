package com.actorpay.merchant.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentHomeBinding


class HomeFragment : BaseFragment() {
    lateinit var binding: FragmentHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        listener()
        return binding.root
    }

    private fun listener() {
        binding.cvEarnMoney.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.earnFragment)
        }

        binding.cvProduct.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.manageProductFragment)
        }

        binding.cvOrder.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.manageOrderFragment)
        }

        binding.cvOutlet.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.outletFragment)
        }

        binding.cvRole.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.roleFragment)
        }

        binding.cvSubMerchant.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.subMerchantFragment)
        }

        binding.cvProfile.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.cvReport.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.payrollFragment)
        }
    }
}