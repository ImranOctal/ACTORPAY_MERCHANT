package com.actorpay.merchant.ui.requestMoney

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentRequestMoneyBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.repositories.AppConstance.Clicks
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.UserDetailsResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.GetAllRequestMoneyResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestMoneyListData
import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestProcessResponse
import com.actorpay.merchant.ui.requestMoney.adapter.RequestMoneyAdapter
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.OnFilterClick
import com.actorpay.merchant.utils.ResponseSealed
import com.techno.taskmanagement.utils.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.flow.collect


import org.koin.android.ext.android.inject


class RequestMoneyFragment : BaseFragment(), OnFilterClick {
    private lateinit var binding: FragmentRequestMoneyBinding
    private val requestMoneyViewModel: RequestMoneyViewModel by inject()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestMoneyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        requestMoneyViewModel.requestMoneyListData.pageNumber = 0
        requestMoneyViewModel.requestMoneyListData.items.clear()
        requestMoneyViewModel.getAllRequest()
        apiResponse()
        setAdapter()
        onFilterClick(this)
        binding.emailNumberField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validate(true, "","")
                return@setOnEditorActionListener true;
            }
            return@setOnEditorActionListener false;
        }

        binding.messageRefresh.setDistanceToTriggerSync(50)
        binding.messageRefresh.setOnRefreshListener {
            requestMoneyViewModel.requestMoneyListData.pageNumber = 0
            requestMoneyViewModel.requestMoneyListData.items.clear()
            requestMoneyViewModel.getAllRequest()
            binding.messageRefresh.isRefreshing=false
        }
        return root
    }

    fun setAdapter() {
        lifecycleScope.launchWhenCreated {
            requestMoneyViewModel.methodRepo.dataStore.getUserId().collect { userId ->
                val adapter = RequestMoneyAdapter(userId, requestMoneyViewModel.methodRepo, requestMoneyViewModel.requestMoneyListData.items) { click,position->
                    when(click){
                        Clicks.PAY ->{
                            processRequest(true,requestMoneyViewModel.requestMoneyListData.items[position].requestId)
                        }
                        Clicks.DECLINE->{
                            processRequest(false,requestMoneyViewModel.requestMoneyListData.items[position].requestId)
                        }
                        Clicks.Root->{
                            val bundle= bundleOf("item" to requestMoneyViewModel.requestMoneyListData.items[position])
                            Navigation.findNavController(requireView()).navigate(R.id.requestMoneyDetailsFragment,bundle)
                        }
                        else -> Unit
                    }
                }
                binding.rvRequestMoney.adapter = adapter
                val linearLayoutManager = LinearLayoutManager(requireContext())
                linearLayoutManager.reverseLayout = true
                val endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener =
                    object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                        override fun onLoadMore(page: Int, totalItemsCount: Int) {
                            if (requestMoneyViewModel.requestMoneyListData.pageNumber < requestMoneyViewModel.requestMoneyListData.totalPages - 1) {
                                requestMoneyViewModel.requestMoneyListData.pageNumber += 1
                                requestMoneyViewModel.getAllRequest()

                            }
                        }
                    }
                binding.rvRequestMoney.layoutManager = linearLayoutManager
                binding.rvRequestMoney.addOnScrollListener(endlessRecyclerViewScrollListener)
            }
        }
    }

    fun processRequest(isAccept:Boolean,requestId:String){
        var title=""
        if(isAccept)
            title="Pay User"
        else
            title="Decline Request"
        CommonDialogsUtils.showCommonDialog(requireActivity(),requestMoneyViewModel.methodRepo,title,"Are you sure?",true,true,
            true,false,object : CommonDialogsUtils.DialogClick{
                override fun onClick() {

                    requestMoneyViewModel.processRequest(isAccept,requestId)
                }
                override fun onCancel() {

                }
            })
    }

    fun validate(checkAPI: Boolean, name: String,type:String,destintation:Int=0) {
        requestMoneyViewModel.methodRepo.hideSoftKeypad(requireActivity())
        val contact = binding.emailNumberField.text.toString().trim()
        if (requestMoneyViewModel.methodRepo.isValidEmail(contact)) {
            if (checkAPI)
                requestMoneyViewModel.userExists(contact)
            else {
                val bundle = bundleOf(
                        AppConstance.KEY_KEY to AppConstance.KEY_EMAIL,
                        AppConstance.KEY_CONTACT to contact,
                        AppConstance.KEY_NAME to name,
                        AppConstance.KEY_TYPE to type
                    )
                Navigation.findNavController(requireView())
                    .navigate(destintation, bundle)
            }
        } else if (requestMoneyViewModel.methodRepo.isValidPhoneNumber(contact)) {
            if (checkAPI)
                requestMoneyViewModel.userExists(contact)
            else {
                val bundle = bundleOf(
                        AppConstance.KEY_KEY to AppConstance.KEY_MOBILE,
                        AppConstance.KEY_CONTACT to contact,
                        AppConstance.KEY_NAME to name,
                        AppConstance.KEY_TYPE to type
                    )
                Navigation.findNavController(requireView())
                    .navigate(destintation, bundle)
            }
        } else {
            binding.emailNumberField.error = "Please enter valid input"
        }
    }
    fun updateUI(requestMoneyListData: RequestMoneyListData) {
        requestMoneyViewModel.requestMoneyListData.pageNumber =
            requestMoneyListData.pageNumber
        requestMoneyViewModel.requestMoneyListData.totalPages =
            requestMoneyListData.totalPages
        requestMoneyViewModel.requestMoneyListData.items.addAll(requestMoneyListData.items)

        binding.rvRequestMoney.adapter?.notifyDataSetChanged()

        if (requestMoneyListData.pageNumber == 0)
            binding.rvRequestMoney.scrollToPosition(0)

        if (requestMoneyViewModel.requestMoneyListData.items.size > 0) {
            binding.imageEmpty.visibility = View.GONE
            binding.textEmpty.visibility = View.GONE
        } else {
            binding.imageEmpty.visibility = View.VISIBLE
            binding.textEmpty.visibility = View.VISIBLE
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
                            is UserDetailsResponse -> {
                                if(event.response.data.customerDetails!=null)
                                    validate(
                                        false,
                                        event.response.data.customerDetails.firstName + " " + event.response.data.customerDetails.lastName,"customer",R.id.receiveFragment
                                    )
                                else{
                                    validate(
                                        false,
                                        event.response.data.merchantDetails!!.businessName,"merchant",R.id.receiveFragment
                                    )
                                }
                            }
                            is GetAllRequestMoneyResponse -> {
                                updateUI(event.response.data)
                            }
                            is RequestProcessResponse ->{
                                requestMoneyViewModel.requestMoneyListData.pageNumber = 0
                                requestMoneyViewModel.requestMoneyListData.items.clear()
                                requestMoneyViewModel.getAllRequest()
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
                        else if(event.failResponse.message.contains("User is not found")){
                            validate(false, "","",R.id.invitationFragment)
                        }
                        else
                            showCustomToast(event.failResponse.message)
                    }
                    is ResponseSealed.Empty -> {
                        hideLoadingDialog()

                    }
                }
            }
        }
    }

    override fun onClick() {
        RequestFilterDialog(
            requestMoneyViewModel.requestMoneyParams,
            requireActivity(),
            requestMoneyViewModel.methodRepo) {
            requestMoneyViewModel.requestMoneyParams = it
            requestMoneyViewModel.requestMoneyListData.pageNumber = 0
            requestMoneyViewModel.requestMoneyListData.totalPages = 0
            requestMoneyViewModel.requestMoneyListData.items.clear()
            requestMoneyViewModel.getAllRequest()

        }.show()
    }

}