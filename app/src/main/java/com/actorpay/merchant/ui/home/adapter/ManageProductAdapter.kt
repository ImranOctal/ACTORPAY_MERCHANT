package com.actorpay.merchant.ui.home.adapter

import android.content.Context
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseAdapter
import com.actorpay.merchant.base.BaseViewHolder
import com.actorpay.merchant.databinding.ManageProductItemBinding

class ManageProductAdapter ( context: Context,val onClick:(action:Action,position:Int)->Unit) :
    BaseAdapter<String, ManageProductItemBinding>(context, R.layout.manage_product_item) {


    override fun onViewHolderBind(
        viewHolder: BaseViewHolder<ManageProductItemBinding>,
        binding: ManageProductItemBinding,
        position: Int,
        data: String
    ) {
            binding.root.setOnClickListener {
                onClick(Action.ActionView,position)
            }
            binding.delete.setOnClickListener {
                onClick(Action.ActionDelete,position)
            }
    }
}

enum class Action{
    ActionView,
    ActionDelete
}