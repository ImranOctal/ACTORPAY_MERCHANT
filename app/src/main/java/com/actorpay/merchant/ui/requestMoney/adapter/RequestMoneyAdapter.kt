package com.actorpay.merchant.ui.requestMoney.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.databinding.RowRequestMoneyRecieveBinding
import com.actorpay.merchant.databinding.RowRequestMoneySendBinding
import com.actorpay.merchant.repositories.AppConstance.Clicks
import com.actorpay.merchant.repositories.methods.MethodsRepo

import com.actorpay.merchant.repositories.retrofitrepository.models.wallet.RequestMoneyData

class RequestMoneyAdapter(val myUserId:String, val methodsRepo: MethodsRepo, val items:MutableList<RequestMoneyData>, val onClick:(Clicks, Int)->Unit):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        return items.size
    }

    override fun getItemViewType(position: Int): Int {

        val item=items[position]
        if (item.userId == myUserId)
            return 0
        else
            return 1
    }

    inner class ViewHolderSend(val binding: RowRequestMoneySendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView( position: Int) {
            val item=items[position]

            binding.rowRequestText.text = "Request from ${item.userName.replace(" ,","")}"
            binding.rowRequestAmount.text = "₹ ".plus(item.amount)
            if(item.createdAt!=null)
            binding.rowRequestDate.text=methodsRepo.getFormattedOrderDate(item.createdAt!!)

            if(item.expired) {
                binding.rowRequestStatus.text = "Request Expired"
            }
            else if(item.requestMoneyStatus == "MONEY_REQUESTED"){
                binding.rowRequestStatus.text="Requested"
            }
            else if(item.requestMoneyStatus == "REQUEST_DECLINED"){
                binding.rowRequestStatus.text="Request declined"
            }
            else if(item.requestMoneyStatus == "REQUEST_ACCEPTED"){
                binding.rowRequestStatus.text="Paid"
            }

            binding.rowWalletClick.setOnClickListener {
                onClick(Clicks.Root,position)
            }
        }
    }

    inner class ViewHolderRecieve(val binding: RowRequestMoneyRecieveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView( position: Int) {
            val item=items[position]
//            binding.rowWalletText.text = item.transactionRemark.replace(",", " ")
            binding.rowRequestText.text = "Request from user"
            binding.rowRequestAmount.text = "₹ ".plus(item.amount)
            if(item.createdAt!=null)
            binding.rowRequestDate.text=methodsRepo.getFormattedOrderDate(item.createdAt!!)

            if(item.expired) {
                binding.pay.visibility= View.GONE
                binding.decline.visibility= View.GONE
                binding.rowRequestStatus.visibility= View.VISIBLE
                binding.rowRequestStatus.text = "Request Expired"
            }
           else if(item.requestMoneyStatus == "MONEY_REQUESTED"){
                binding.pay.visibility= View.VISIBLE
                binding.decline.visibility= View.VISIBLE
                binding.rowRequestStatus.visibility= View.GONE
            }
            else if(item.requestMoneyStatus == "REQUEST_DECLINED"){
                binding.pay.visibility= View.GONE
                binding.decline.visibility= View.GONE
                binding.rowRequestStatus.visibility= View.VISIBLE
                binding.rowRequestStatus.text="Request declined"
            }
            else if(item.requestMoneyStatus == "REQUEST_ACCEPTED"){
                binding.pay.visibility= View.GONE
                binding.decline.visibility= View.GONE
                binding.rowRequestStatus.visibility= View.VISIBLE
                binding.rowRequestStatus.text="Paid"
            }

            binding.rowWalletClick.setOnClickListener {
                onClick(Clicks.Root,position)
            }
            binding.decline.setOnClickListener {
                onClick(Clicks.DECLINE,position)
            }
            binding.pay.setOnClickListener {
                onClick(Clicks.PAY,position)
            }

        }
    }

}