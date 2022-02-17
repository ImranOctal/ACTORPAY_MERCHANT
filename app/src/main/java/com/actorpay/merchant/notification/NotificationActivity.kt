package com.actorpay.merchant.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ActivityNotificatioBinding
import com.actorpay.merchant.notification.adapter.NotificationAdapter


class NotificationActivity : AppCompatActivity() {
    private  lateinit var  binding :ActivityNotificatioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_notificatio)
        binding.toolbar.back.setOnClickListener {
            finish()
        }
        binding.toolbar.ToolbarTitle.text = getString(R.string.notification)
        setupRv()

    }
    private fun setupRv() {
        binding.rvNotification.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvNotification.adapter=NotificationAdapter(this)
    }
}