package com.actorpay.merchant.ui.sendMoney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.AddMoneyResponse
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.TransferMoneyParams
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentPayBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_CONTACT
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_EMAIL
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_KEY
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_MOBILE
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_NAME
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_QR
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.KEY_TYPE
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject


class PayFragment : BaseFragment() {
    private lateinit var binding: FragmentPayBinding
    private val transferMoneyViewModel: TransferMoneyViewModel by inject()

    private var key = ""
    private var name = ""
    private var contact = ""
    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            key = it.getString(KEY_KEY)!!
            contact = it.getString(KEY_CONTACT)!!
            name = it.getString(KEY_NAME)!!
            type = it.getString(KEY_TYPE)!!
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pay, container, false)
        apiResponse()
        binding.apply {
            if (key == KEY_QR) {
                cardContact.visibility = View.GONE
                beneficiaryName.text = "Pay to $contact"
            } else if (key == KEY_MOBILE || key == KEY_EMAIL) {
                cardContact.visibility = View.VISIBLE
                beneficiaryName.text = "Pay to $name"
                beneficiaryContact.setText(contact)
            } else {
                cardContact.visibility = View.VISIBLE
                beneficiaryName.text = "Pay John"
                beneficiaryContact.setText(getString(R.string.enter_mobile_no_or_email))
                payNow.visibility = View.VISIBLE
            }
            payNow.setOnClickListener {
                var isValidate = true
                val amount = binding.beneficiaryAmount.text.toString().trim()
                val reason = binding.beneficiaryReason.text.toString().trim()
                if (amount == "") {
                    binding.beneficiaryAmount.error = "Please Enter Amount"
                    binding.beneficiaryAmount.requestFocus()
                    isValidate = false
                } else if (amount.toDouble() < 1) {
                    binding.beneficiaryAmount.error = "Amount should not less 1"
                    binding.beneficiaryAmount.requestFocus()
                    isValidate = false
                }
                if (reason == "") {
                    binding.beneficiaryReason.error = "Please Enter Reason"
                    isValidate = false
                }
                if (isValidate) {

                    transferMoneyViewModel.transferMoney(TransferMoneyParams(contact,amount,reason,type))

//                    val navOptions= NavOptions.Builder()
//                    navOptions.setPopUpTo(R.id.homeFragment,false)
//                    Navigation.findNavController(requireView()).navigate(R.id.transactionStatusSuccessFragment,null,  navOptions.build())
                }
            }
        }
        return binding.root
    }

    fun apiResponse() {

        lifecycleScope.launchWhenStarted {
            transferMoneyViewModel.responseLive.collect { event ->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        when (event.response) {
                            is AddMoneyResponse -> {

                                val bundle= bundleOf("amount" to binding.beneficiaryAmount.text.toString().trim())
                                Navigation.findNavController(requireView()).navigate(R.id.transactionStatusSuccessFragment,bundle)
                                binding.beneficiaryReason.setText("")
                                binding.beneficiaryAmount.setText("")
                            }
                        }
                        transferMoneyViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        transferMoneyViewModel.responseLive.value = ResponseSealed.Empty
                        hideLoadingDialog()
                        if (event.failResponse!!.code == 403) {
                            forcelogout(transferMoneyViewModel.methodRepo)
                        }
                        else{
//                            showCustomToast("Something went wrong, Please check beneficiary contact")
                            showCustomToast("Don't have sufficient Balance to Transfer")
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