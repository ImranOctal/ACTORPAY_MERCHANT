package com.octal.actorpayuser.ui.request_money

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentRequestMoneyDetailsBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestMoneyData
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestProcessResponse
import com.actorpay.merchant.ui.requestMoney.RequestMoneyViewModel
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.ResponseSealed


import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject


class RequestMoneyDetailsFragment : BaseFragment() {

    lateinit var binding: FragmentRequestMoneyDetailsBinding
    lateinit var requestMoneyData: RequestMoneyData
    private val requestMoneyViewModel: RequestMoneyViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater,
            R.layout.fragment_request_money_details,container,false)

        arguments.let {
            requestMoneyData=requireArguments().getSerializable("item") as RequestMoneyData
        }

        updateUI(requestMoneyData)
        apiResponse()

        binding.btnDone.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }






        return binding.root
    }

    fun updateUI(requestMoneyData:RequestMoneyData){
        if(requestMoneyData.createdAt != null)
            binding.rowRequestDate.text=requestMoneyViewModel.methodRepo.getFormattedOrderDate(requestMoneyData.createdAt!!)
        else{
            binding.rowRequestDate.visibility=View.GONE
            binding.rowRequestDateText.visibility=View.GONE
        }

        binding.rowRequestAmount.text = "₹ ".plus(requestMoneyData.amount)

        if(requestMoneyData.requestMoneyStatus == "MONEY_REQUESTED"){
            binding.rowRequestStatus.text="Requesting"
            binding.pay.visibility= View.VISIBLE
            binding.decline.visibility= View.VISIBLE
            binding.btnDone.visibility= View.GONE
        }
        else if(requestMoneyData.requestMoneyStatus == "REQUEST_DECLINED"){

            binding.rowRequestStatus.text="Request declined"
            binding.pay.visibility= View.GONE
            binding.decline.visibility= View.GONE
            binding.btnDone.visibility= View.VISIBLE
        }
        else if(requestMoneyData.requestMoneyStatus == "REQUEST_ACCEPTED"){
            binding.rowRequestStatus.text="Paid"
            binding.pay.visibility= View.GONE
            binding.decline.visibility= View.GONE
            binding.btnDone.visibility= View.VISIBLE
        }

        lifecycleScope.launchWhenCreated {
            requestMoneyViewModel.methodRepo.dataStore.getUserId().collect { userId ->
                if(requestMoneyData.userId == userId){
                    binding.rowRequestText.text="Request from you"
                    binding.pay.visibility= View.GONE
                    binding.decline.visibility= View.GONE
                    binding.btnDone.visibility= View.VISIBLE
                    binding.rowRequestToUser.visibility=View.VISIBLE
                    binding.rowRequestToUserText.visibility=View.VISIBLE
                    binding.rowRequestToUser.text=requestMoneyData.toUserName.replace(" ,","")
                }
                else{
                    binding.rowRequestText.text="Request from ${requestMoneyData.userName.replace(" ,","")}"
                    binding.rowRequestToUser.visibility=View.GONE
                    binding.rowRequestToUserText.visibility=View.GONE
                }
            }
        }

        binding.pay.setOnClickListener {
            processRequest(true,requestMoneyData.requestId)
        }

        binding.decline.setOnClickListener {
            processRequest(false,requestMoneyData.requestId)

        }

    }

    fun processRequest(isAccept:Boolean,requestId:String){
        var title=""
        if(isAccept)
            title="Pay User"
        else
            title="Decline Request"
        CommonDialogsUtils.showCommonDialog(requireActivity(),requestMoneyViewModel.methodRepo,title,"Are you sure?",true,true,
        true,false,object :CommonDialogsUtils.DialogClick{
                override fun onClick() {
                    requestMoneyViewModel.processRequest(isAccept,requestId)
                }
                override fun onCancel() {

                }
            })


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

                            is RequestProcessResponse ->{
                              updateUI(event.response.data.requestMoneyDTO)
                            }
                        }
                        requestMoneyViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        requestMoneyViewModel.responseLive.value = ResponseSealed.Empty
                        hideLoadingDialog()
                        if (event.failResponse!!.code == 403) {
                            forcelogout(requestMoneyViewModel.methodRepo)
                        } else
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