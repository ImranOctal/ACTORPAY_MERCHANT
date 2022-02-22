package com.actorpay.merchant.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemNotificationBinding

class NotificationAdapter(
    val context: Context,
) :
    RecyclerView.Adapter<NotificationAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        val notificationBinding = ItemNotificationBinding.bind(view)
        return ItemHolder(notificationBinding)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }

    inner class ItemHolder(private val notificationBinding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(notificationBinding.root) {
        fun bind(position: Int) {
            notificationBinding.apply {
            }
        }
    }
}
