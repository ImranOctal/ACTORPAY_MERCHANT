package com.actorpay.merchant.ui.disputes

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityDisputeBinding
import com.actorpay.merchant.utils.ResponseSealed
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListData
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListResponse
import com.octal.actorpayuser.ui.dispute.DisputeAdapter
import com.octal.actorpayuser.ui.dispute.DisputeFilterDialog
import com.octal.actorpayuser.ui.dispute.DisputeViewModel
import com.techno.taskmanagement.utils.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class DisputeActivity : BaseActivity() {

    private lateinit var binding: ActivityDisputeBinding
    private val disputeViewModel: DisputeViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dispute)

        apiResponse()
        disputeViewModel.getAllDisputes()
        setAdapter()

        binding.ivFilter.setOnClickListener {
            applyFilter()
        }
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setAdapter() {
        val adapter=  DisputeAdapter(disputeViewModel.disputeListData.items,disputeViewModel.methodRepo)
        val layoutManager = LinearLayoutManager(this)

        binding.rvDispute.layoutManager = layoutManager
        val endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener =
            object : EndlessRecyclerViewScrollListener(layoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    if (disputeViewModel.disputeListData.pageNumber < disputeViewModel.disputeListData.totalPages - 1) {
                        disputeViewModel.disputeListData.pageNumber += 1
                        disputeViewModel.getAllDisputes()
                    }
                }
            }
        binding.rvDispute.addOnScrollListener(endlessRecyclerViewScrollListener)
        binding.rvDispute.adapter = adapter
    }

    fun updateUI(disputeListData: DisputeListData) {
        disputeViewModel.disputeListData.pageNumber =
            disputeListData.pageNumber
        disputeViewModel.disputeListData.totalPages =
            disputeListData.totalPages
        disputeViewModel.disputeListData.items.addAll(disputeListData.items)
        binding.rvDispute.adapter?.notifyDataSetChanged()

        if (disputeViewModel.disputeListData.items.size > 0) {
            binding.textEmpty.visibility = View.GONE
        } else {
            binding.textEmpty.visibility = View.VISIBLE
        }
    }



    fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            disputeViewModel.responseLive.collect { event ->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        when (event.response) {
                            is DisputeListResponse -> {
                                updateUI(event.response.data)
                            }
                        }
                        disputeViewModel.responseLive.value = ResponseSealed.Empty
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        disputeViewModel.responseLive.value = ResponseSealed.Empty
                        hideLoadingDialog()
                        if (event.failResponse!!.code == 403) {
                            forcelogout(disputeViewModel.methodRepo)
                        }
                    }
                    is ResponseSealed.Empty -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
    }

    fun applyFilter(){
        DisputeFilterDialog(
            disputeViewModel.disputeListParams,
            this,
            disputeViewModel.methodRepo
        ) {
            disputeViewModel.disputeListParams = it
            disputeViewModel.disputeListData.pageNumber = 0
            disputeViewModel.disputeListData.totalPages = 0
            disputeViewModel.disputeListData.items.clear()
            disputeViewModel.getAllDisputes()

        }.show()
    }
}