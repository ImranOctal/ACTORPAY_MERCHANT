package com.actorpay.merchant.ui.outlet

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

import com.actorpay.merchant.databinding.FragmentOutletBinding
import com.actorpay.merchant.ui.outlet.adapter.AdapterOutlet
import com.actorpay.merchant.ui.outlet.response.DeleteOutlet
import com.actorpay.merchant.ui.outlet.response.GetOutlet
import com.actorpay.merchant.ui.outlet.response.OutletItem
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.ResponseSealed
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class OutletFragment : BaseFragment() {
    private lateinit var binding: FragmentOutletBinding
    private var handler: Handler? = null
    private val outletViewModel: OutletViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_outlet,container,false)
        binding.shimmerViewContainer.visibility=View.VISIBLE
        handler=Handler()
        binding.btnAddOutlet.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.addOutletFragment)
        }
        Places.initialize(
            requireActivity(), "AIzaSyBn9ZKmXc-MN12Fap0nUQotO6RKtYJEh8o"
        )

        handler!!.postDelayed({ //Do something after delay
            outletViewModel.getOutlet()
        }, 1000)
        apiResponse()
        return  binding.root

    }

    fun apiResponse() {
        lifecycleScope.launch {
            outletViewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetOutlet) {
                            if (it.response.data.items.isNotEmpty()) {
                                setupRv(it.response.data.items)

                                binding.tvDataNotFound.visibility = View.GONE
                                binding.imageEmpty.visibility = View.GONE

                            } else {
                                setupRv(mutableListOf())
                                binding.tvDataNotFound.visibility = View.VISIBLE
                                binding.imageEmpty.visibility = View.VISIBLE
                            }

                            binding.shimmerViewContainer.stopShimmerAnimation()
                            binding.shimmerViewContainer.visibility = View.GONE
                        }
                        if (it.response is DeleteOutlet) {
                            showCustomAlert(it.response.message, binding.root)
                            outletViewModel.getOutlet()
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (it.failResponse!!.code == 403) {
                            forcelogout(outletViewModel.methodRepo)
                        } else {
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
    private fun setupRv(items: List<OutletItem>) {
        binding.rvOutlet.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvOutlet.adapter = AdapterOutlet(requireActivity(),items) { pos, action ->
            if (action == "delete") {
                var ids = mutableListOf<String>()
                ids.add(items[pos].id)

                CommonDialogsUtils.showCommonDialog(requireActivity(),
                    outletViewModel.methodRepo,
                    "Delete",
                    "Are you sure you want to delete?",
                    autoCancelable = false,
                    isCancelAvailable = true,
                    isOKAvailable = true,
                    showClickable = false,
                    callback = object : CommonDialogsUtils.DialogClick {
                        override fun onClick() {
                            deleteOutlet(ids)
                        }
                        override fun onCancel() {
                        }
                    }
                )
            } else if (action == "edit") {
                val bundle= bundleOf("id" to items[pos].id )
                Navigation.findNavController(requireView()).navigate(R.id.updateOutletFragment,bundle)
            }
        }
    }
    private fun deleteOutlet(ids: MutableList<String>) {
        outletViewModel.deleteOutlet(ids)
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