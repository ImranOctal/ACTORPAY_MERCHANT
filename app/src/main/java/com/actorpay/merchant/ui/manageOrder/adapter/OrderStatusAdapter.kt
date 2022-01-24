package com.actorpay.merchant.ui.manageOrder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemOrderStatusBinding
import com.actorpay.merchant.ui.manageOrder.OrderDetailActivity
import org.json.JSONArray

//val onClick:(pos:Int,status:String)->Unit

class OrderStatusAdapter(
    val context: Context,
    private var list: List<String>,
    private var  orderItemId: MutableList<String>,
    private var mpopup: PopupWindow
) :
    RecyclerView.Adapter<OrderStatusAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_status, parent, false)
        val orderStatus = ItemOrderStatusBinding.bind(view)
        return ItemHolder(orderStatus)
    }

    override fun getItemCount(): Int {
        return list.size


    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)

    }

    inner class ItemHolder(private val orderStatus: ItemOrderStatusBinding) :
        RecyclerView.ViewHolder(orderStatus.root) {
        fun bind(position: Int) {
            orderStatus.apply {
                tvStatus.text=list[position]
                tvStatus.setOnClickListener {
                    mpopup.dismiss()
                    (context as OrderDetailActivity).cancelBottomSheet(list[position],orderItemId)

                }

            }
        }
    }


}
