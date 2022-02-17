package com.actorpay.merchant.ui.disputes.disputedetails

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityDisputeDetailsBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.utils.ResponseSealed
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class DisputeDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityDisputeDetailsBinding
    private val disputeDetailsViewModel: DisputeDetailsViewModel by inject()
    private var disputeID: String =""
    private var disputeCode: String =""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dispute_details)

        intent?.let {
            disputeID = it.getStringExtra("disputeId")!!
            disputeCode = it.getStringExtra("disputeCode")!!
        }
        if(disputeID != ""){
            disputeDetailsViewModel.disputeListParams.disputeCode=disputeCode
            disputeDetailsViewModel.getAllDisputes()
//            disputeDetailsViewModel.getDispute(disputeID)
        }
        else{
            showCustomToast("Something went wrong")
        }

        apiResponse()
        binding.sendMessageIcon.setOnClickListener {
            sendMessage()
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.messageRefresh.setDistanceToTriggerSync(50)
        binding.messageRefresh.setOnRefreshListener {
            disputeDetailsViewModel.getAllDisputes()
            binding.messageRefresh.isRefreshing=false
        }
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
    }

    fun setAdapter(disputeMessages:MutableList<DisputeMessage>){
        val adapter=DisputeMessageAdapter(this,disputeDetailsViewModel.methodRepo,disputeMessages)
        val layoutManager= LinearLayoutManager(this)
        layoutManager.reverseLayout=true
        binding.rvMessages.layoutManager=layoutManager
        binding.rvMessages.adapter=adapter
        if(DisputeDetailsViewModel.disputeData!!.disputeMessages.size>0)
            binding.rvMessages.scrollToPosition(0)

    }


}