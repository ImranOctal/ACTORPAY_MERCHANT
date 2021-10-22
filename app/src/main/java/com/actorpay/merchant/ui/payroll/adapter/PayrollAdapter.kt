package com.actorpay.merchant.ui.payroll.adapter

import android.content.Context
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseAdapter
import com.actorpay.merchant.base.BaseViewHolder
import com.actorpay.merchant.databinding.ManageOrderLayoutItemBinding
import com.actorpay.merchant.databinding.PayrollPaymentItemBinding

class PayrollAdapter ( context: Context) :
    BaseAdapter<String, PayrollPaymentItemBinding>(context, R.layout.payroll_payment_item) {


    override fun onViewHolderBind(
        viewHolder: BaseViewHolder<PayrollPaymentItemBinding>,
        binding: PayrollPaymentItemBinding,
        position: Int,
        data: String
    ) {

    }
}