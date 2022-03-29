package com.actorpay.merchant.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentFaqBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.content.FAQResponse
import com.actorpay.merchant.ui.more.adapter.CustomFAQAdapter
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
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_faq,container,false)
        moreViewModel.getFAQ()
        apiResponse()
        return binding.root

    }

    fun initExpandableList(){

        val adapter= CustomFAQAdapter(moreViewModel.faqList)
        binding.rvFaq.layoutManager= LinearLayoutManager(requireContext())
        binding.rvFaq.adapter=adapter
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