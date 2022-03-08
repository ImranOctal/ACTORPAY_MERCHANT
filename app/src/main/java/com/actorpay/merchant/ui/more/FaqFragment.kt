package com.actorpay.merchant.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentFaqBinding
import com.actorpay.merchant.ui.more.adapter.CustomExpandableListAdapter
import com.actorpay.merchant.repositories.retrofitrepository.models.content.FAQResponse
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject


class FaqFragment : BaseFragment() {
    private val moreViewModel: MoreViewModel by inject()
    private lateinit var binding: FragmentFaqBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_faq,container,false)
        moreViewModel.getFAQ()
        apiResponse()
        return binding.root

    }

    fun initExpandableList(){
        val adapter: ExpandableListAdapter = CustomExpandableListAdapter(requireActivity() ,moreViewModel.faqList)
        binding.expendableList.setAdapter(adapter)
        binding.expendableList.setOnGroupExpandListener (object :
            ExpandableListView.OnGroupExpandListener {
            var previousGroup = -1
            var flag = false
            override fun onGroupExpand(groupPosition: Int) {
                if (groupPosition != previousGroup && flag) {
                    binding.expendableList.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;

                flag = true;
            }
        })
    }
    private fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            moreViewModel.miscResponseLive.collect { action ->
                when (action) {
                    is MoreViewModel.ResponseMiscSealed.loading -> {
                        showLoadingDialog()
                    }
                    is  MoreViewModel.ResponseMiscSealed.Success -> {
                        hideLoadingDialog()
                        if (action.response is FAQResponse) {
                            initExpandableList()
                        }
                    }
                    is MoreViewModel.ResponseMiscSealed.ErrorOnResponse -> {
                        if(action.failResponse!!.code==403){
                            forcelogout(moreViewModel.methodRepo)
                        }else{
                            showCustomAlert(
                                action.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    else -> hideLoadingDialog()
                }
            }
        }
    }
}