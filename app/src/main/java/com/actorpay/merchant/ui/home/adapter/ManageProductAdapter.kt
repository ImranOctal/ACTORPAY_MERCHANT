package com.actorpay.merchant.ui.home.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ManageProductItemBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.bumptech.glide.Glide
import com.actorpay.merchant.repositories.AppConstance.AppConstance

class ManageProductAdapter(
    val context: Context,
    private var permissionData: PermissionData,
    private var merchantRole: String,
    private var items: ArrayList<Item>,
    val onClick: (pos: Int, status: String) -> Unit
) :
    RecyclerView.Adapter<ManageProductAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manage_product_item, parent, false)
        val productBinding = ManageProductItemBinding.bind(view)
        return ItemHolder(productBinding)
    }

    override fun getItemCount(): Int {



        return items.size


    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }

    inner class ItemHolder(private val productBinding: ManageProductItemBinding) :
        RecyclerView.ViewHolder(productBinding.root) {
        fun bind(position: Int) {
            productBinding.apply {
                Glide.with(root).load(items[position].image).placeholder(R.drawable.logo)
                    .into(productImage)
                titleOfOrder.text = items[position].name
                status.visibility = View.GONE
                actualPrice.text = items[position].actualPrice.toString()
                date.text = getFormattedOrderDate(items[position].createdAt)
                dealPrice.text = AppConstance.rupee + items[position].dealPrice.toString()

                if (merchantRole != "MERCHANT") {
                    if (permissionData.write) {
                        edit.visibility = View.VISIBLE
                        delete.visibility = View.VISIBLE
                    } else {
                        edit.visibility = View.GONE
                        delete.visibility = View.GONE
                    }
                } else {
                    edit.visibility = View.VISIBLE
                    delete.visibility = View.VISIBLE
                }

                root.setOnClickListener {
                    onClick(position, "root")
                }
                delete.setOnClickListener {
                    onClick(position, "delete")
                }
                edit.setOnClickListener {
                    onClick(position, "edit")
                }
            }
        }
    }

    fun getFormattedOrderDate(orderDate: String): String? {
        try {
            return AppConstance.dateFormate4.format(AppConstance.dateFormate3.parse(orderDate)!!)
        } catch (e: Exception) {
            return orderDate
        }
    }
}
