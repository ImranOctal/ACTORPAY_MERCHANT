package com.actorpay.merchant.ui.disputes

import android.os.Bundle
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
import com.actorpay.merchant.databinding.FragmentDisputeBinding
import com.actorpay.merchant.repositories.AppConstance.Clicks
import com.actorpay.merchant.ui.disputes.disputedetails.DisputeDetailsViewModel
import com.actorpay.merchant.utils.OnFilterClick
import com.actorpay.merchant.utils.ResponseSealed
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListData
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListResponse
import com.octal.actorpayuser.ui.dispute.DisputeFilterDialog
import com.techno.taskmanagement.utils.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class DisputeFragment : BaseFragment(), OnFilterClick {
    private lateinit var binding: FragmentDisputeBinding
    private val disputeViewModel: DisputeViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_dispute,container,false)
        apiResponse()
        disputeViewModel.getAllDisputes()
        setAdapter()
        onFilterClick(this)
        return  binding.root
    }
    private fun setAdapter() {
        val adapter=  DisputeAdapter(disputeViewModel.disputeListData.items,disputeViewModel.methodRepo){
                action,position ->
            when(action){
                Clicks.Root->{
                    DisputeDetailsViewModel.disputeData=disputeViewModel.disputeListData.items[position]
                    val bundle= bundleOf("disputeId" to disputeViewModel.disputeListData.items[position].disputeId,"disputeCode" to disputeViewModel.disputeListData.items[position].disputeCode)
                    Navigation.findNavController(requireView()).navigate(R.id.disputeDetailFragment,bundle)
                }
                Clicks.Success->{
                    val bundle =
                        bundleOf("orderNo" to disputeViewModel.disputeListData.items[position].orderNo)
                    Navigation.findNavController(requireView())
                        .navigate(R.id.orderDetailFragment, bundle, null)
                }
            }

        }
        val layoutManager = LinearLayoutManager(requireActivity())
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
        disputeViewModel.disputeListData.pageNumber = disputeListData.pageNumber
        disputeViewModel.disputeListData.totalPages = disputeListData.totalPages
        disputeViewModel.disputeListData.items.clear()
        disputeViewModel.disputeListData.items.addAll(disputeListData.items)
        binding.rvDispute.adapter?.notifyDataSetChanged()

        if (disputeViewModel.disputeListData.items.size > 0) {
            binding.tvEmptyText.visibility = View.GONE
            binding.imageEmpty.visibility = View.GONE
        } else {
            binding.tvEmptyText.visibility = View.VISIBLE
            binding.imageEmpty.visibility = View.VISIBLE
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
            disputeViewModel.disputeListParams, requireActivity(), disputeViewModel.methodRepo) {
            disputeViewModel.disputeListParams = it
            disputeViewModel.disputeListData.pageNumber = 0
            disputeViewModel.disputeListData.totalPages = 0
            disputeViewModel.disputeListData.items.clear()
            disputeViewModel.getAllDisputes()

        }.show()
    }

    override fun onClick() {
        applyFilter()
    }
}