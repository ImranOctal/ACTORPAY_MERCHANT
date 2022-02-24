package com.actorpay.merchant.ui.roles.details

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemRolesBinding
import com.actorpay.merchant.databinding.ItemRolesDetailsBinding
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.roles.RoleItem
import com.actorpay.merchant.repositories.retrofitrepository.models.screens.ScreenItem


class RoleDetailsAdapter(
    private val methodsRepo: MethodsRepo,
    private var items: List<ScreenItem>,
    val onClick: (pos: Int,action:String) -> Unit
) :
    RecyclerView.Adapter<RoleDetailsAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_roles_details, parent, false)
        val roleBinding = ItemRolesDetailsBinding.bind(view)
        return ItemHolder(roleBinding)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }
    inner class ItemHolder(private val outLetBinding: ItemRolesDetailsBinding) :
        RecyclerView.ViewHolder(outLetBinding.root) {
        fun bind(position: Int) {
            outLetBinding.apply {
                tvRoleTitle.text=items[position].screenName
                checkRead.isChecked=items[position].read
                checkWrite.isChecked=items[position].write
                    checkRead.setOnCheckedChangeListener { buttonView, isChecked ->
                        items[position].read=isChecked
                }
                checkWrite.setOnCheckedChangeListener { buttonView, isChecked ->
                    items[position].write=isChecked
                    if(isChecked){
                        items[position].read= isChecked
                      }else if(!isChecked){
                        items[position].read=true
                        items[position].write= false
                        checkRead.isClickable=true
                    }
                    if(items[position].write==isChecked&&items[position].read==isChecked){
                        checkRead.isClickable=false
                    }

                    notifyDataSetChanged()
                }
            }
        }
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}
