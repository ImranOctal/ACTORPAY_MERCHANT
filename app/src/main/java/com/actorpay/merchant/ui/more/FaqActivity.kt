package com.actorpay.merchant.ui.more


import android.os.Bundle
import android.widget.Adapter

import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityFaqBinding
import com.actorpay.merchant.ui.more.adapter.CustomExpandableListAdapter
import com.octal.actorpay.repositories.retrofitrepository.models.content.FAQResponse
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject

class FaqActivity : BaseActivity() {
    private val moreViewModel: MoreViewModel by inject()
    private lateinit var binding:ActivityFaqBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_faq)
        moreViewModel.getFAQ()
        binding.back.setOnClickListener {
            onBackPressed()
        }
        apiResponse()

    }

    fun initExpandableList(){
        val adapter: ExpandableListAdapter = CustomExpandableListAdapter(this ,moreViewModel.faqList)
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
    override fun onBackPressed() {
        finish()
    }
}