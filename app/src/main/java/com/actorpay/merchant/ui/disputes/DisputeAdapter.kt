package com.actorpay.merchant.ui.disputes

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.databinding.RowDisputeBinding
import com.actorpay.merchant.repositories.AppConstance.Clicks
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeData

class DisputeAdapter(val items:MutableList<DisputeData>,val methodsRepo: MethodsRepo, val onClick: (click: Clicks, position: Int) -> Unit):RecyclerView.Adapter<DisputeAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        val binding= RowDisputeBinding.inflate(layoutInflater,parent,false)


        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(items[position],position)
    }

    override fun getItemCount(): Int {
       return items.size
    }

    inner class MyViewHolder(val binding:RowDisputeBinding):RecyclerView.ViewHolder(binding.root) {
            fun bindView(item:DisputeData,position: Int){

                binding.disputedata=item
                binding.createdDate.text=methodsRepo.getFormattedOrderDate(item.createdAt)


                binding.order.setPaintFlags(binding.order.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
                binding.order.setOnClickListener {
                    onClick(Clicks.Success,position)
                }
                binding.root.setOnClickListener {
                    onClick(Clicks.Root,position)
                }

            }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}