package com.actorpay.merchant.ui.manageOrder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemOrderDetailBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderItemDto
import com.bumptech.glide.Glide
import com.octal.actorpay.repositories.AppConstance.AppConstance


class OrderDetailAdapter(val context: Context, private var  data: List<OrderItemDto>) :
    RecyclerView.Adapter<OrderDetailAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail, parent, false)
        val orderDetailBinding = ItemOrderDetailBinding.bind(view)
        return ItemHolder(orderDetailBinding)
    }
    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }
    inner class ItemHolder(private val orderDetailBinding: ItemOrderDetailBinding) :
        RecyclerView.ViewHolder(orderDetailBinding.root) {
        fun bind(position: Int) {
            orderDetailBinding.apply {
                Glide.with(context).load(data[position].image).into(productImage)
                productTitle.text=data[position].productName
                actualPriceText.text=AppConstance.rupee+data[position].productPrice.toString()
                actualQuantityText.text="Quantity: "+data[position].productQty.toString()

                if(data[position].orderItemStatus=="SUCCESS"||data[position].orderItemStatus=="READY"){
                    orderItemStatus.visibility=View.VISIBLE

                }else{

                    orderItemStatus.visibility=View.VISIBLE
                    orderItemStatus.text="RETURNED"
                }

            }
        }
    }


}
