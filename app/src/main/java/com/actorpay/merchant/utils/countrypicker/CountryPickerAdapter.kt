package com.actorpay.merchant.utils.countrypicker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.databinding.CountryListitemBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.CountryItem
import com.bumptech.glide.Glide


class CountryPickerAdapter(val list:MutableList<CountryItem>, private val context: Context,val onClick:(position:Int)->Unit):RecyclerView.Adapter<CountryPickerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CountryListitemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(list[position])
    }

    override fun getItemCount(): Int {
       return list.size
    }

    inner class MyViewHolder(val countryListitemBinding: CountryListitemBinding):RecyclerView.ViewHolder(countryListitemBinding.root) {
            fun bindView(item: CountryItem){
                countryListitemBinding.code.text=item.countryCode
                countryListitemBinding.listitem.text=item.country
                Glide.with(context).load(item.countryFlag).into(countryListitemBinding.ivFlag)
                countryListitemBinding.root.setOnClickListener {
                    onClick(adapterPosition)
                }
            }
    }
}