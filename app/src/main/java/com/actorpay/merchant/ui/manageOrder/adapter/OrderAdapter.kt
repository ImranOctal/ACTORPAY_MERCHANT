package com.actorpay.merchant.ui.manageOrder.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ManageOrderLayoutItemBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.order.Item
import com.actorpay.merchant.ui.manageOrder.OrderDetailActivity
import com.octal.actorpay.repositories.AppConstance.AppConstance


class OrderAdapter(
    val context: Context,
    private var items: ArrayList<Item>,
    val onClick: (pos: Int, status: String) -> Unit
) :
    RecyclerView.Adapter<OrderAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manage_order_layout_item, parent, false)
        val orderBinding = ManageOrderLayoutItemBinding.bind(view)
        return ItemHolder(orderBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }

    inner class ItemHolder(private val orderBinding: ManageOrderLayoutItemBinding) :
        RecyclerView.ViewHolder(orderBinding.root) {
        fun bind(position: Int) {

            orderBinding.apply {
                titleOfOrder.text = items[position].orderNo
                price.text = AppConstance.dollar + items[position].totalPrice.toString()
                OrderType.text = items[position].orderStatus
                date.text= getFormattedOrderDate(items[position].createdAt)

                llRoot.setOnClickListener {
                    val intent = Intent(context, OrderDetailActivity::class.java)
                    intent.putExtra("data", items[position])
                    context.startActivity(intent)
                }
//                ivNext.setOnClickListener {
//                    card.visibility=View.VISIBLE
//                    ivDropDown.visibility=View.VISIBLE
//                    ivNext.visibility=View.GONE
//
//                }
//                ivDropDown.setOnClickListener {
//                    card.visibility=View.GONE
//                    ivDropDown.visibility=View.GONE
//                    ivNext.visibility=View.VISIBLE
//
//                }
//                if(items[position].orderStatus=="SUCCESS"){
//                    list.add("READY")
//                    list.add("CANCELED")
//
//                }else if(items[position].orderStatus=="READY"){
//                    list.add("CANCELED")
//                    list.add("DISPATCHED")
//
//                }else if(items[position].orderStatus=="RETURNING"){
//                    list.add("RETURNED")
//
//                }else{
//                    list.add("")
//                }
//                orderStatusSpinner.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
//                orderStatusSpinner.adapter=OrderStatusAdapter(context,list){
//                    pos,status ->
//                    onClick(pos,status)
//                }
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
