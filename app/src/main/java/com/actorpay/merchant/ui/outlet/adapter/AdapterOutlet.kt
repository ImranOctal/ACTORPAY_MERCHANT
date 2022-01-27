package com.actorpay.merchant.ui.outlet.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemOutletBinding
import com.actorpay.merchant.ui.manageOrder.OrderDetailActivity
import com.actorpay.merchant.ui.outlet.response.OutletItem
import com.actorpay.merchant.ui.outlet.updateoutlet.UpdateOutletActivity
import com.octal.actorpay.repositories.AppConstance.AppConstance


class AdapterOutlet(
    val context: Context,
    private var items: List<OutletItem>,
    val onClick: (pos: Int,action:String) -> Unit
) :
    RecyclerView.Adapter<AdapterOutlet.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_outlet, parent, false)
        val orderBinding = ItemOutletBinding.bind(view)
        return ItemHolder(orderBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }

    inner class ItemHolder(private val outLetBinding: ItemOutletBinding) :
        RecyclerView.ViewHolder(outLetBinding.root) {
        fun bind(position: Int) {


            outLetBinding.apply {
                tvOutletTitle.text=items[position].title
                tvAddress1.text=items[position].addressLine1
                tvOutletAddress2.text=items[position].addressLine2
                tvOutletContact.text=items[position].extensionNumber+items[position].contactNumber


                edit.setOnClickListener {
                    onClick(position,"edit")

                }


                delete.setOnClickListener {
                    onClick(position,"delete")
                }
            }

        }
    }
    fun getFormattedOrderDate(orderDate: String): String? {
        try {
            return  AppConstance.dateFormate4.format(AppConstance.dateFormate3.parse(orderDate)!!)
        }
        catch (e : Exception){
            return orderDate
        }
    }
}
