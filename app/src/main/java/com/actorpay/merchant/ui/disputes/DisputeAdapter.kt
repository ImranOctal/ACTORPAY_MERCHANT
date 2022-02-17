package com.octal.actorpayuser.ui.dispute

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.databinding.RowDisputeBinding
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeData

class DisputeAdapter(val items:MutableList<DisputeData>,val methodsRepo: MethodsRepo, val onClick: (position: Int) -> Unit):RecyclerView.Adapter<DisputeAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        val binding= RowDisputeBinding.inflate(layoutInflater,parent,false)


        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(items[position])
    }

    override fun getItemCount(): Int {
       return items.size
    }

    inner class MyViewHolder(val binding:RowDisputeBinding):RecyclerView.ViewHolder(binding.root) {
            fun bindView(item:DisputeData){

                binding.disputedata=item
                binding.createdDate.text=methodsRepo.getFormattedOrderDate(item.createdAt)

                binding.root.setOnClickListener { onClick(adapterPosition) }

            }
    }
}