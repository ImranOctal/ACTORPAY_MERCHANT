package com.actorpay.merchant.ui.commission

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.databinding.ItemCommissionBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.commission.CommissionItem
import java.text.DecimalFormat


class CommissionAdapter(
    private var items: ArrayList<CommissionItem>
) :
    RecyclerView.Adapter<CommissionAdapter.ItemHolder>() {
    var decimalFormat: DecimalFormat = DecimalFormat("0.00")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_commission, parent, false)
        val orderBinding = ItemCommissionBinding.bind(view)
        return ItemHolder(orderBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)

    }

    inner class ItemHolder(private val itemBinding: ItemCommissionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(position: Int) {

            itemBinding.apply {
                tvCommissionTitle.text = items[position].orderNo
                tvOrderStatus.text = items[position].orderStatus.replace("_"," ")
                merchantEarnings.text= decimalFormat.format(items[position].merchantEarnings)
                commissionPercentage.text=items[position].commissionPercentage.toString()
                commissionAmt.text=items[position].actorCommissionAmt.toString()
                settlementStatus.text=items[position].settlementStatus

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
