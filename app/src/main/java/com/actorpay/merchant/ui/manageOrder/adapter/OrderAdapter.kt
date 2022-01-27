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
import android.app.Activity
import android.graphics.Color
import android.view.RoundedCorner
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.utils.roundBorderedView
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject


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
                tvPrice.text = AppConstance.dollar + items[position].totalPrice.toString()
                orderStatus.text = items[position].orderStatus.replace("_"," ")
                tvDate.text= getFormattedOrderDate(items[position].createdAt)
                cardView.setOnClickListener {
                    val intent = Intent(context, OrderDetailActivity::class.java)
                    intent.putExtra("data", items[position])
                    (context as Activity).startActivityForResult(intent, 101)
                }
                if(items[position].orderStatus=="CANCELLED"){
                    orderStatus.setTextColor(Color.parseColor(AppConstance.red_color))
                    orderStatus.roundBorderedView(10,AppConstance.white_color,AppConstance.red_color,1)

                }else if(items[position].orderStatus=="PARTIALLY_RETURNED"||items[position].orderStatus=="PARTIALLY_RETURNING"||items[position].orderStatus=="PARTIALLY_CANCELLED"||items[position].orderStatus=="PARTIALLY_CANCELLED"){
                    orderStatus.setTextColor(Color.parseColor(AppConstance.blue_color))
                    orderStatus.roundBorderedView(10,AppConstance.white_color,AppConstance.blue_color,1)
                }else{
                    orderStatus.setTextColor(Color.parseColor(AppConstance.green_color))
                    orderStatus.roundBorderedView(10,AppConstance.white_color,AppConstance.green_color,1)
                }
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
