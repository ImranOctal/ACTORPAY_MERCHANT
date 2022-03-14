package com.actorpay.merchant.ui.roles

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

import com.actorpay.merchant.databinding.FragmentRoleBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RolesResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenResponse
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RoleFragment : BaseFragment() {
    private lateinit var binding: FragmentRoleBinding
    private var handler: Handler? = null
    private val rolesViewModel: RolesViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_role,container,false)
        apiResponse()
        setAdapter()
        handler=Handler()
        rolesViewModel.getAllRoles()
        binding.btnAddRole.setOnClickListener {
            val bundle= bundleOf("id" to "")
            Navigation.findNavController(requireView()).navigate(R.id.roleDetailFragment,bundle)
        }
        return  binding.root
    }

    fun setAdapter(){
        val adapter= RoleAdapter(rolesViewModel.methodRepo,rolesViewModel.rolesList){
                pos, action ->
                when(action){
                "edit"->{
                    val bundle= bundleOf("id" to rolesViewModel.rolesList[pos].id)
                    Navigation.findNavController(requireView()).navigate(R.id.roleDetailFragment,bundle)
                }
                "delete"->{
                    CommonDialogsUtils.showCommonDialog(requireActivity(),rolesViewModel.methodRepo,"Delete Role","Are you sure?",false,true,true,false,object :
                        CommonDialogsUtils.DialogClick{
                        override fun onClick() {
                            rolesViewModel.deleteRole(rolesViewModel.rolesList[pos].id)
                        }
                        override fun onCancel() {
                        }
                    })
                }
            }
        }
        binding.rvRoles.layoutManager= LinearLayoutManager(requireActivity())
        binding.rvRoles.adapter=adapter
    }

    fun apiResponse(){
        lifecycleScope.launch {
            rolesViewModel.responseLive.collect { event->
                when (event) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (event.response is RolesResponse) {
                            if(event.response.data.items.isNotEmpty()){
                                rolesViewModel.pageNo=event.response.data.pageNumber
                                rolesViewModel.rolesList.clear()
                                rolesViewModel.rolesList.addAll(event.response.data.items)
                                binding.shimmerViewContainer.stopShimmerAnimation()
                                binding.shimmerViewContainer.visibility = View.GONE
                                binding.imageEmpty.visibility = View.GONE
                                binding.tvEmptyText.visibility = View.GONE
                                binding.rvRoles.visibility = View.VISIBLE

                                setAdapter()
                            }else{
                                rolesViewModel.rolesList.clear()
                                binding.shimmerViewContainer.visibility = View.GONE
                                binding.imageEmpty.visibility = View.VISIBLE
                                binding.tvEmptyText.visibility = View.VISIBLE
                                binding.rvRoles.visibility = View.GONE


                            }
                            rolesViewModel.getAllScreen()
                        }
                        else if (event.response is ScreenResponse) {
                            GlobalData.allScreens.clear()
                            GlobalData.allScreens.addAll(event.response.data)
                        }
                        else if (event.response is SuccessResponse) {
                            showCustomToast(event.response.message)
                            rolesViewModel.getAllRoles()
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if(event.failResponse!!.code==403){
                            forcelogout(viewModel.methodRepo)
                        }else{
                            showCustomToast(
                                event.failResponse.message,)
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
        binding.rvRoles.visibility = View.GONE

        binding.shimmerViewContainer.visibility= View.VISIBLE
        binding.shimmerViewContainer.startShimmerAnimation()
    }

    override fun onPause() {
        binding.shimmerViewContainer.visibility= View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()
        super.onPause()
    }


}