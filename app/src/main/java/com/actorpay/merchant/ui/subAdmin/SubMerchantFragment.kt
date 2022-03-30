package com.actorpay.merchant.ui.subAdmin


import android.os.Bundle
import android.os.Handler

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

import com.actorpay.merchant.databinding.FragmentSubMerchantBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.DataCategory
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.DeleteSubMerchant
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.GetAllSubMerchant
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.Item
import com.actorpay.merchant.ui.subAdmin.adapter.SubMerchantAdapter
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData.permissionDataList
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class SubMerchantFragment : BaseFragment() {
    var merchantRole=""
    private var handler: Handler? = null
    private lateinit var binding: FragmentSubMerchantBinding
    private val SubviewModel: SubMerchantsViewModel by inject()
    var permissionData= PermissionData(false,"5", AppConstance.SCREEN_SUB_MERCHANT,false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_sub_merchant,container,false)
        binding.shimmerViewContainer.visibility=View.VISIBLE
        binding.shimmerViewContainer.startShimmerAnimation();
        handler=Handler()
        SubviewModel.getSubMerchants()
        apiResponse()


        permissionDataList.forEach {
            if(it.screenName==permissionData.screenName){
                permissionData.read=it.read
                permissionData.write=it.write
            }
        }
        lifecycleScope.launch {
            viewModel.methodRepo.dataStore.getRole().collect { role ->
                merchantRole=role
                if(merchantRole!="MERCHANT"){
                    if(permissionData.write){
                        binding.btnAddMerchant.visibility=View.VISIBLE
                    }else{
                        binding.btnAddMerchant.visibility=View.GONE
                    }
                }else{
                    binding.btnAddMerchant.visibility=View.VISIBLE
                }
            }
        }
        binding.btnAddMerchant.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.addSubMerchantFragment)
        }
        return binding.root
    }

    private fun setUpRv(items: List<Item>) {
        binding.rvSubMerchants.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvSubMerchants.adapter = SubMerchantAdapter(requireActivity(),permissionData,merchantRole, items){
                pos, action ->
            if(action=="delete"){
                var ids = mutableListOf<String>()
                ids.add(items[pos].id)
                CommonDialogsUtils.showCommonDialog(requireActivity(),
                    SubviewModel.methodRepo, "Delete", "Are you sure you want to delete?",
                    autoCancelable = false,
                    isCancelAvailable = true,
                    isOKAvailable = true,
                    showClickable = false,
                    callback = object : CommonDialogsUtils.DialogClick {
                        override fun onClick() {
                            SubviewModel.deleteMerchant(ids)
                        }
                        override fun onCancel() {
                        }
                    }
                )
            }else if(action=="edit"){
                val bundle= bundleOf("from" to "edit","id" to items[pos].id,"gender" to items[pos].gender,"roleId" to items[pos].roleId)
                Navigation.findNavController(requireView()).navigate(R.id.addSubMerchantFragment,bundle)
            }
        }
    }
    private fun apiResponse() {
        lifecycleScope.launch {
            SubviewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetAllSubMerchant) {
                            if (it.response.data.items.isNotEmpty()) {
                                setUpRv(it.response.data.items)
                                binding.tvEmptyText.visibility=View.GONE
                                binding.imageEmpty.visibility=View.GONE
                                binding.rvSubMerchants.visibility=View.VISIBLE


                            }else{
                                setUpRv(mutableListOf())
                                binding.tvEmptyText.visibility=View.VISIBLE
                                binding.imageEmpty.visibility=View.VISIBLE
                                binding.rvSubMerchants.visibility=View.GONE
                            }

                            binding.shimmerViewContainer.stopShimmerAnimation()
                            binding.shimmerViewContainer.visibility = View.GONE

                        }else  if (it.response is DeleteSubMerchant) {
                            showCustomAlert(it.response.message, binding.root)
                            SubviewModel.getSubMerchants()
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if(it.failResponse!!.code==403){
                            forcelogout(viewModel.methodRepo)
                        }else{
                            showCustomAlert(
                                it.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    else -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation();
    }
    override fun onPause() {
        binding.shimmerViewContainer.visibility=View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation();
        super.onPause()
    }
}