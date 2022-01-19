package com.actorpay.merchant.ui.home.adapter

import android.content.Context
import android.view.View
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseAdapter
import com.actorpay.merchant.base.BaseViewHolder
import com.actorpay.merchant.databinding.ManageProductItemBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.bumptech.glide.Glide
import com.octal.actorpay.repositories.AppConstance.AppConstance

class ManageProductAdapter(context: Context, val onClick:(position: Int, data: String)->Unit) : BaseAdapter<Item, ManageProductItemBinding>(context, R.layout.manage_product_item) {


    override fun onViewHolderBind(viewHolder: BaseViewHolder<ManageProductItemBinding>, binding: ManageProductItemBinding, position: Int, data: Item) {

        Glide.with(binding.root).load(data.image).placeholder(R.drawable.demo).into(binding.productImage)
        binding.titleOfOrder.text = data.name
        binding.status.visibility=View.GONE
        binding.actualPrice.text = data.actualPrice.toString()
        binding.date.text = getFormattedOrderDate(data.createdAt)
        binding.dealPrice.text = AppConstance.rupee+data.dealPrice.toString()
        binding.root.setOnClickListener {
            onClick(position,"root")
        }
        binding.delete.setOnClickListener {
            onClick(position,"delete")
        }
        binding.edit.setOnClickListener {
            onClick(position,"edit")
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

