package com.actorpay.merchant.ui.requestMoney.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.databinding.RowRequestMoneyRecieveBinding
import com.actorpay.merchant.databinding.RowRequestMoneySendBinding

class RequestMoneyAdapter():RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val bindingRecieve = RowRequestMoneyRecieveBinding.inflate(inflater, parent, false)
        val bindingSend = RowRequestMoneySendBinding.inflate(inflater, parent, false)

        if (viewType == 0)
            return ViewHolderSend(bindingSend)
        else
            return ViewHolderRecieve(bindingRecieve)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewtype=holder.itemViewType
        if (viewtype == 0) {
            val viewHolderSend=holder as ViewHolderSend
            viewHolderSend.bindView(position)
        }
        else {
            val viewHolderRecieve=holder as ViewHolderRecieve
            viewHolderRecieve.bindView(position)
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun getItemViewType(position: Int): Int {
        if (position%2==0)
            return 0
        else
            return 1

     }

    inner class ViewHolderSend(val binding: RowRequestMoneySendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView( position: Int) {
            binding.rowRequestText.text = "Request from you"
            binding.rowRequestAmount.text = "₹ ".plus("100")
        }
    }

    inner class ViewHolderRecieve(val binding: RowRequestMoneyRecieveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(position: Int) {
            binding.rowRequestText.text = "Request from user"
            binding.rowRequestAmount.text = "₹ ".plus("100")
        }
    }
}