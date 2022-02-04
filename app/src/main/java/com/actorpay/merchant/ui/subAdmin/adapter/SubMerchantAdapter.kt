package com.actorpay.merchant.ui.subAdmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemSubmerchantBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.Item
import com.bumptech.glide.Glide


class SubMerchantAdapter(
    val context: Context,
    private  var permissionData: PermissionData,
    private var merchantRole:String,
    private val items: List<Item>,
    val onClick: (pos: Int, action: String) -> Unit
    ) :
    RecyclerView.Adapter<SubMerchantAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_submerchant, parent, false)
        val subMerchantBinding = ItemSubmerchantBinding.bind(view)
        return ItemHolder(subMerchantBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }

    inner class ItemHolder(private val subMerchantBinding: ItemSubmerchantBinding) :
        RecyclerView.ViewHolder(subMerchantBinding.root) {
        fun bind(position: Int) {
            subMerchantBinding.apply {
                titleOfMerchant.text=items[position].firstName+" "+items[position].lastName
                tvEmail.text=items[position].email
                tvContact.text=items[position].extensionNumber+items[position].contactNumber
                Glide.with(root).load(items[position].profilePicture).placeholder(R.drawable.logo).into(ivProfile)

                delete.setOnClickListener {
                    onClick(position,"delete")
                }
                edit.setOnClickListener {
                    onClick(position,"edit")
                }
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

                 }

            }

        }
    }
//    fun getFormattedOrderDate(orderDate: String): String? {
//        try {
//            return  AppConstance.dateFormate4.format(AppConstance.dateFormate3.parse(orderDate)!!)
//        }
//        catch (e : Exception){
//            return orderDate
//        }
//    }

