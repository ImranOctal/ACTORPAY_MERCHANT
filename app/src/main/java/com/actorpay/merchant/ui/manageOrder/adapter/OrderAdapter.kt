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
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import android.app.Activity
import android.graphics.Color
import android.view.View
import com.actorpay.merchant.utils.roundBorderedView
import com.bumptech.glide.Glide


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
                tvPrice.text ="Price: "+ AppConstance.rupee + items[position].totalPrice.toString()
                orderStatus.text = items[position].orderStatus.replace("_"," ")
                tvDate.text= getFormattedOrderDate("Order Date: "+items[position].createdAt)
                cardView.setOnClickListener {
                    val intent = Intent(context, OrderDetailActivity::class.java)
                    intent.putExtra("data", items[position])
                    (context as Activity).startActivityForResult(intent, 101)
                }
                if(items[position].orderStatus=="CANCELLED"){
                    orderStatus.setTextColor(Color.parseColor(AppConstance.red_color))
                    orderStatus.roundBorderedView(12,
                        AppConstance.white_color,
                        AppConstance.red_color,1)

                }else if(items[position].orderStatus=="PARTIALLY_RETURNED"||items[position].orderStatus=="PARTIALLY_RETURNING"||items[position].orderStatus=="PARTIALLY_CANCELLED"||items[position].orderStatus=="PARTIALLY_CANCELLED"){
                    orderStatus.setTextColor(Color.parseColor(AppConstance.blue_color))
                    orderStatus.roundBorderedView(12,
                        AppConstance.white_color,
                        AppConstance.blue_color,1)
                }else{
                    orderStatus.setTextColor(Color.parseColor(AppConstance.green_color))
                    orderStatus.roundBorderedView(12,
                        AppConstance.white_color,
                        AppConstance.green_color,1)
                }

                if(items[position].orderItemDtos.size>3){
                    productImage1.visibility=View.VISIBLE
                    productImage2.visibility=View.VISIBLE
                    productImage3.visibility= View.VISIBLE
                    productImage4.visibility=View.VISIBLE

                }
                else if(items[position].orderItemDtos.size>2){
                   productImage1.visibility=View.VISIBLE
                    productImage2.visibility=View.VISIBLE
                    productImage3.visibility=View.VISIBLE
                }
                else if(items[position].orderItemDtos.size>1){
                    productImage1.visibility=View.VISIBLE
                    productImage2.visibility=View.VISIBLE
                }
                else if(items[position].orderItemDtos.isNotEmpty()){
                    productImage1.visibility=View.VISIBLE
                }
                try {
                    Glide.with(root).load(items[position].orderItemDtos[0].image).error(R.drawable.logo).into(productImage1)
                    Glide.with(root).load(items[position].orderItemDtos[1].image).error(R.drawable.logo).into(productImage2)
                    Glide.with(root).load(items[position].orderItemDtos[2].image).error(R.drawable.logo).into(productImage3)
                    Glide.with(root).load(items[position].orderItemDtos[3].image).error(R.drawable.logo).into(productImage4)
                }
                catch (e:Exception){
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
