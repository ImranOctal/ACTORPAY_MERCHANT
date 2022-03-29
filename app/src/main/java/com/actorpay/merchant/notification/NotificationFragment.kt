package com.actorpay.merchant.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentNotificationBinding
import com.actorpay.merchant.notification.adapter.NotificationAdapter


class NotificationFragment: BaseFragment() {
    private  lateinit var  binding :FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater ,R.layout.fragment_notification, container, false)


        setupRv()


        return binding.root
    }

    private fun setupRv() {
        binding.rvNotification.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvNotification.adapter=NotificationAdapter(requireContext())
    }
}