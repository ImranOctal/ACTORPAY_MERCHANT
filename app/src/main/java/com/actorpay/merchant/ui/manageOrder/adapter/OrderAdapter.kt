package com.actorpay.merchant.ui.manageOrder.adapter

import android.content.Context
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseAdapter
import com.actorpay.merchant.base.BaseViewHolder
import com.actorpay.merchant.databinding.ManageOrderLayoutItemBinding
import java.util.ArrayList

class OrderAdapter( context: Context) :BaseAdapter<String,ManageOrderLayoutItemBinding>(context, R.layout.manage_order_layout_item){


    override fun onViewHolderBind(
        viewHolder: BaseViewHolder<ManageOrderLayoutItemBinding>,
        binding: ManageOrderLayoutItemBinding,
        position: Int,
        data: String
    ) {

    }
}