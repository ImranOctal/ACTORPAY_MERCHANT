package com.actorpay.merchant.ui.roles

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemRolesBinding
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RoleItem


class RoleAdapter(
    private val methodsRepo: MethodsRepo,
    private var items: List<RoleItem>,
    val onClick: (pos: Int,action:String) -> Unit
) :
    RecyclerView.Adapter<RoleAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_roles, parent, false)
        val roleBinding = ItemRolesBinding.bind(view)
        return ItemHolder(roleBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {

        holder.bind(position)

    }

    inner class ItemHolder(private val outLetBinding: ItemRolesBinding) :
        RecyclerView.ViewHolder(outLetBinding.root) {
        fun bind(position: Int) {


            outLetBinding.apply {
                tvRoleTitle.text=items[position].name
                tvDesc.text=items[position].description
                tvRoleDate.text=methodsRepo.getFormattedOrderDate(items[position].createdAt)

                root.setOnClickListener {
                    onClick(position,"edit")
                }
                edit.setOnClickListener {
                    onClick(position,"edit")
                }
                delete.setOnClickListener {
                    onClick(position,"delete")
                }
            }

        }
    }

}
