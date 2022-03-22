package com.actorpay.merchant.ui.requestMoney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentReceiveBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestMoneyParams
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestMoneyResponse
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.ResponseSealed

import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject


class ReceiveFragment : BaseFragment() {

    private val requestMoneyViewModel: RequestMoneyViewModel by inject()
    private lateinit var binding: FragmentReceiveBinding
    private var key = ""
    private var name = ""
    private var contact = ""
    private var type = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString(AppConstance.KEY_KEY)!!
            contact = it.getString(AppConstance.KEY_CONTACT)!!
            name = it.getString(AppConstance.KEY_NAME)!!
            type = it.getString(AppConstance.KEY_TYPE)!!

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receive, container, false)
        apiResponse()
        init()





        return binding.root
    }

    fun init() {
        binding.apply {

           if (key == AppConstance.KEY_MOBILE || key == AppConstance.KEY_EMAIL) {
                cardContact.visibility = View.VISIBLE
                beneficiaryName.text = "Request from $name"
                beneficiaryContact.setText(contact)
            }

            payNow.setOnClickListener {
                var isValidate=true
                val amount = binding.beneficiaryAmount.text.toString().trim()
                val reason = binding.beneficiaryReason.text.toString().trim()
                if (amount.equals("")) {
                    binding.beneficiaryAmount.error = "Please Enter Amount"
                    binding.beneficiaryAmount.requestFocus()
                    isValidate=false
                } else if (amount.toDouble() < 1) {
                    binding.beneficiaryAmount.error = "Amount should not less than 1"
                    binding.beneficiaryAmount.requestFocus()
                    isValidate=false
                }
                if(reason == ""){
                    binding.beneficiaryReason.error = "Please Enter Reason"
                    isValidate=false
                }
                if(isValidate)
                {
                    requestMoneyViewModel.requestMoney(RequestMoneyParams(contact,amount,reason,type))
                }
            }
        }
    }

    fun apiResponse() {

        lifecycleScope.launchWhenStarted {
            requestMoneyViewModel.responseLive.collect { event ->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        when (event.response) {
                            is RequestMoneyResponse ->{
                                CommonDialogsUtils.showCommonDialog(requireActivity(),requestMoneyViewModel.methodRepo,
                                "Money Requested","Your money request has been sent",false,false,true,false,object :CommonDialogsUtils.DialogClick{
                                        override fun onClick() {
                                            Navigation.findNavController(requireView()).popBackStack(R.id.homeFragment,false)
                                        }
                                        override fun onCancel() {

                                        }
                                    })
                            }
                        }
                        requestMoneyViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        requestMoneyViewModel.responseLive.value = ResponseSealed.Empty
                        hideLoadingDialog()
                        if (event.failResponse!!.code == 403) {
                            forcelogout(requestMoneyViewModel.methodRepo)
                        }
                        else{
//                            showCustomToast("Something went wrong, Please check beneficiary contact")
                            showCustomToast(event.failResponse.message)
                        }
                    }
                    is ResponseSealed.Empty -> {
                        hideLoadingDialog()

                    }
                }
            }
        }
    }


}