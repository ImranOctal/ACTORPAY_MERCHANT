package com.octal.actorpayuser.ui.dashboard.bottomnavfragments.wallet.walletuser

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet.WalletData
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.RowWalletUserCreditBinding
import com.actorpay.merchant.databinding.RowWalletUserDebitBinding
import com.actorpay.merchant.repositories.methods.MethodsRepo


class WalletUserAdapter(
    val context: Context,
    val items: MutableList<WalletData>,
    val methodsRepo: MethodsRepo,
    val onClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val bindingCredit = RowWalletUserCreditBinding.inflate(inflater, parent, false)
        val bindingDebit = RowWalletUserDebitBinding.inflate(inflater, parent, false)

        if (viewType == 0)
            return ViewHolderCredit(bindingCredit)
        else
            return ViewHolderDebit(bindingDebit)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewtype=holder.itemViewType
        if (viewtype == 0) {
            val viewHolderCredit=holder as ViewHolderCredit
            viewHolderCredit.bindView(items[position], position)
        }
        else {
            val viewHolderDebit=holder as ViewHolderDebit
            viewHolderDebit.bindView(items[position], position)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        val item=items[position]
        if (item.transactionTypes == "CREDIT")
            return 0
        else
            return 1
    }

    inner class ViewHolderDebit(val binding: RowWalletUserDebitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: WalletData, position: Int) {
//            binding.rowWalletText.text = item.transactionRemark.replace(",", " ")
            binding.rowWalletText.text = "Paid"
            binding.rowWalletTxn.text = item.walletTransactionId
            binding.rowWalletDate.text = methodsRepo.getFormattedOrderDate(item.createdAt)

            binding.rowWalletClick.setOnClickListener {
                onClick(position)
            }
            if (item.transactionTypes == "DEBIT") {
                binding.rowWalletAmount.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorAccent
                    )
                )
                binding.rowWalletAmount.text = "₹ " + item.transactionAmount.toString()
            }
            if (item.transactionTypes == "CREDIT") {
                binding.rowWalletAmount.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green_color
                    )
                )
                binding.rowWalletAmount.text = "₹ " + item.transactionAmount.toString()
            }

            if (item.purchaseType == "TRANSFER") {
//                    binding.rowWalletText.text="Money Transferred Successfully"
            } else if (item.purchaseType == "SHOPPING") {
//                    binding.rowWalletText.text="Online Shopping"
            }
        }
    }

    inner class ViewHolderCredit(val binding: RowWalletUserCreditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: WalletData, position: Int) {
//            binding.rowWalletText.text = item.transactionRemark.replace(",", " ")
            binding.rowWalletText.text = "Payment Received"
            binding.rowWalletTxn.text = item.walletTransactionId
            binding.rowWalletDate.text = methodsRepo.getFormattedOrderDate(item.createdAt)

            binding.rowWalletClick.setOnClickListener {
                onClick(position)
            }
            if (item.transactionTypes == "DEBIT") {
                binding.rowWalletAmount.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorAccent
                    )
                )
                binding.rowWalletAmount.text = "₹ " + item.transactionAmount.toString()
            }
            if (item.transactionTypes == "CREDIT") {
                binding.rowWalletAmount.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green_color
                    )
                )
                binding.rowWalletAmount.text = "₹ " + item.transactionAmount.toString()
            }

            if (item.purchaseType == "TRANSFER") {
//                    binding.rowWalletText.text="Money Transferred Successfully"
            } else if (item.purchaseType == "SHOPPING") {
//                    binding.rowWalletText.text="Online Shopping"
            }
        }
    }
}