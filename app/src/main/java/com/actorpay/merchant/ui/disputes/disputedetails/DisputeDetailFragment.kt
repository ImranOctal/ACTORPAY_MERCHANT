package com.actorpay.merchant.ui.disputes.disputedetails

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentDisputeDetailBinding

import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.utils.ResponseSealed
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject


class DisputeDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentDisputeDetailBinding
    private val disputeDetailsViewModel: DisputeDetailsViewModel by inject()
    private var disputeID: String =""
    private var disputeCode: String =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_dispute_detail,container,false)
        arguments?.let {
            disputeID = it.getString("disputeId")!!
            disputeCode = it.getString("disputeCode")!!
        }
        if(disputeID != ""){
            disputeDetailsViewModel.disputeListParams.disputeCode=disputeCode
            disputeDetailsViewModel.getAllDisputes()
        }

        else{
            showCustomToast("Something went wrong")
        }

        apiResponse()

        binding.sendMessageIcon.setOnClickListener {
            sendMessage()
        }

        binding.messageRefresh.setDistanceToTriggerSync(50)
        binding.messageRefresh.setOnRefreshListener {
            disputeDetailsViewModel.getAllDisputes()
            binding.messageRefresh.isRefreshing=false
        }
        return binding.root
    }

    fun sendMessage(){
        val message=binding.sendMessageEdt.text.toString().trim()
        if(message == ""){
            return
        }
        binding.sendMessageEdt.setText("")
        disputeDetailsViewModel.sendDisputeMessage(SendMessageParams(disputeID,message))
    }
    fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            disputeDetailsViewModel.responseLive.collect { event ->
                when (event) {
                    is ResponseSealed.Loading -> {
//                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        when (event.response) {
                            is DisputeSingleResponse -> {
                                updateUI(event.response.data)
                            }
                            is DisputeListResponse ->{
                                updateUI(event.response.data.items[0])
                            }
                            is SuccessResponse -> {
//                                showCustomToast(event.response.message)
                                disputeDetailsViewModel.getAllDisputes()
                            }
                        }
                        disputeDetailsViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        disputeDetailsViewModel.responseLive.value = ResponseSealed.Empty
                        hideLoadingDialog()
                        if (event.failResponse!!.code == 403) {
                            forcelogout(disputeDetailsViewModel.methodRepo)
                        }
                    }
                    is ResponseSealed.Empty -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
    }

    fun updateUI(disputeData: DisputeData){
        binding.disputedata=disputeData
        binding.createdDate.text=disputeDetailsViewModel.methodRepo.getFormattedOrderDate(disputeData.createdAt)
        if(disputeData.disputeMessages!=null)
            setAdapter(disputeData.disputeMessages)

        binding.order.setPaintFlags(binding.order.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        binding.order.setOnClickListener {
            val bundle =
                bundleOf("orderNo" to disputeData.orderNo)
            Navigation.findNavController(requireView())
                .navigate(R.id.orderDetailFragment, bundle, null)
        }
    }

    fun setAdapter(disputeMessages:MutableList<DisputeMessage>){
        val adapter=DisputeMessageAdapter(requireActivity(),disputeDetailsViewModel.methodRepo,disputeMessages)
        val layoutManager= LinearLayoutManager(requireActivity())
        layoutManager.reverseLayout=true
        binding.rvMessages.layoutManager=layoutManager
        binding.rvMessages.adapter=adapter
        if(disputeMessages.size>0)
            binding.rvMessages.scrollToPosition(0)
    }
}