package com.actorpay.merchant.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.WalletBalance
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentHomeBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.ui.addMoney.AddMoneyViewModel
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject


class HomeFragment : BaseFragment() {
    lateinit var binding: FragmentHomeBinding
    var walletBalance=""
    private val homeViewModel: HomeViewModel by inject()
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
        homeViewModel.getById()
        apiResponse()
        return binding.root
    }

    private fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.responseLive.collect { event ->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        when (event.response) {
                            is WalletBalance ->{
                                 getWalletBalance(event.response.data.amount.toString())
                            }
                            is GetUserById->{
                                val data = event.response
                                Log.e("merchantId>>>", data.data.merchantId)
                                viewModel.methodRepo.dataStore.setMerchantId(data.data.merchantId)
                                getUserName(data.data.businessName)
                                homeViewModel.getWalletBalance()
                            }
                        }
                        homeViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        homeViewModel.responseLive.value = ResponseSealed.Empty
                        hideLoadingDialog()
                        if (event.failResponse!!.code == 403) {
                            forcelogout(homeViewModel.methodRepo)
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