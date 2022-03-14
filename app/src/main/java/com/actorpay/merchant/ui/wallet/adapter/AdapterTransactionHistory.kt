package com.actorpay.merchant.ui.wallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.databinding.ItemTransactionHistoryBinding


class AdapterTransactionHistory(val onClick:(pos:Int)->Unit) : RecyclerView.Adapter<AdapterTransactionHistory.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTransactionHistoryBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {

            onClick(position)
        }

    }
    override fun getItemCount(): Int {
        return 15
    }

    inner class ViewHolder(val binding: ItemTransactionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root){

    }
}