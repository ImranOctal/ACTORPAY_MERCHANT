package com.actorpay.merchant.ui.addMoney

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemAddMoneyBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.repositories.retrofitrepository.models.payment.BeanPayment


class AddMoneyAdapter(private  var list: MutableList<BeanPayment> ,val onClick:(pos:Int)->Unit) : RecyclerView.Adapter<AddMoneyAdapter.ItemHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMoneyAdapter.ItemHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_add_money,parent,false)
        val binding=ItemAddMoneyBinding.bind(view)
        return  ItemHolder(binding)
    }
    override fun onBindViewHolder(holder: AddMoneyAdapter.ItemHolder, position: Int) {
        holder.bind(position)

    }
    inner class ItemHolder(private val binding: ItemAddMoneyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                amount50.text=AppConstance.rupee+list[position].amount
                for(i in list[position].amount.indices){
                    if(list[position].isSelected){
                        amount50.setBackgroundResource(R.drawable.bg_round_corner)
                        amount50.setTextColor(Color.WHITE)
                    }else{
                        amount50.setBackgroundResource(R.drawable.orderstatus_bg)
                        amount50.setTextColor(Color.parseColor("#0077b6"))
                    }
                }

                amount50.setOnClickListener {
//                    for( i in list.indices){
//                        list[i].isSelected=i==position
//                    }
                    onClick(position)
                    notifyDataSetChanged()
                }

            }
        }
    }
    override fun getItemCount(): Int {
        return  list.size
    }
}