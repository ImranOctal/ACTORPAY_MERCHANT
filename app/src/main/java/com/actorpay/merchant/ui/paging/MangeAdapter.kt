package com.actorpay.merchant.ui.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ManageProductItemBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.bumptech.glide.Glide
import java.text.DecimalFormat

class MangeAdapter(
    val context: Context,
    private var permissionData: PermissionData,
    private var merchantRole: String,
    val onClick: (pos: Int, status: String,item:Item) -> Unit,

) : PagingDataAdapter<Item,MangeAdapter.LoadStateViewHolder>(CharacterComparator) {
    var decimalFormat: DecimalFormat = DecimalFormat("0.00")
    inner class LoadStateViewHolder(val binding: ManageProductItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: Item,position: Int) = with(binding) {
            binding.apply {
                Glide.with(root).load(item.image).placeholder(R.drawable.logo)
                    .into(productImage)
                titleOfOrder.text = item.name
                status.visibility = View.GONE
                actualPrice.text = AppConstance.rupee+item.actualPrice.toString()
                date.text = getFormattedOrderDate(item.createdAt)
                dealPrice.text = AppConstance.rupee+decimalFormat.format(item.dealPrice)
                if (merchantRole != "MERCHANT") {
                    if (permissionData.write) {
                        edit.visibility = View.VISIBLE
                        delete.visibility = View.VISIBLE
                    } else {
                        //subMerchant
//                       edit.visibility = View.GONE
//                       delete.visibility = View.GONE
                        edit.visibility = View.VISIBLE
                        delete.visibility = View.VISIBLE
                    }
                } else {
                    edit.visibility = View.VISIBLE
                    delete.visibility = View.VISIBLE
                }
                root.setOnClickListener {
                    onClick(position, "root",item)
                }
                delete.setOnClickListener {
                    onClick(position, "delete",item)
                }
                edit.setOnClickListener {
                    onClick(position, "edit",item)
                }
            }

            }
        }

    object CharacterComparator : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) =
            oldItem.productId == newItem.productId

        override fun areContentsTheSame(oldItem: Item, newItem: Item) =
            oldItem == newItem
    }
    override fun onBindViewHolder(holder: LoadStateViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it,position)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadStateViewHolder {
        return  LoadStateViewHolder(
            ManageProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }
    fun getFormattedOrderDate(orderDate: String): String? {
        try {
            return AppConstance.dateFormate4.format(AppConstance.dateFormate3.parse(orderDate)!!)
        } catch (e: Exception) {
            return orderDate
        }
    }
}
