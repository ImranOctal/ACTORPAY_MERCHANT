package com.actorpay.merchant.ui.manageOrder.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemOrderDetailBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderItemDto
import com.actorpay.merchant.ui.manageOrder.OrderDetailActivity
import com.bumptech.glide.Glide
import com.octal.actorpay.repositories.AppConstance.AppConstance

import org.json.JSONArray
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
          var list=ArrayList<String>()
          var orderItemId = mutableListOf<String>()
        fun bind(position: Int) {

            orderDetailBinding.apply {
                Glide.with(context).load(data[position].image) .placeholder(R.drawable.logo).into(productImage)
                productTitle.text=data[position].productName
                actualPriceText.text=AppConstance.rupee+data[position].productPrice.toString()
                actualQuantityText.text="Quantity: "+data[position].productQty.toString()
                orderItemStatus.text="Status: "+data[position].orderItemStatus
                orderItemId.add(data[position].orderItemId)
                when (data[position].orderItemStatus) {
                    AppConstance.STATUS_SUCCESS -> {
                        list.add(AppConstance.STATUS_READY)
                        list.add(AppConstance.STATUS_CANCELLED)
                    }
                    AppConstance.STATUS_READY -> {
                        list.add(AppConstance.STATUS_CANCELLED)
                        list.add(AppConstance.STATUS_DISPATCHED)
                    }
                    AppConstance.STATUS_RETURNING -> {
                        list.add(AppConstance.STATUS_RETURN_ACCEPT)
                        list.add(AppConstance.STATUS_RETURN_DECLINE)
                    }
                    AppConstance.STATUS_RETURN_ACCEPT , AppConstance.STATUS_RETURN_DECLINE -> {
                        list.add(AppConstance.STATUS_RETURNED)
                    }
                    AppConstance.STATUS_DISPATCHED -> {
                        list.add(AppConstance.STATUS_DELIVERED)
                    }
                    else -> {
                        ivMenu.visibility=View.GONE
                    }
                }
                ivMenu.setOnClickListener {
                    (context as OrderDetailActivity).dialog(list,ivMenu,orderItemId)
                }

            }

        }
    }
}
