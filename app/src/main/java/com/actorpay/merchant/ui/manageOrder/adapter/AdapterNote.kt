package com.actorpay.merchant.ui.manageOrder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ItemNoteBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.order.OrderNotesDto
import com.octal.actorpay.repositories.AppConstance.AppConstance

class AdapterNote(
    val context: Context,
    private var orderNotesDtos: List<OrderNotesDto>,

    ) :
    RecyclerView.Adapter<AdapterNote.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        val itemNote = ItemNoteBinding.bind(view)
        return ItemHolder(itemNote)
    }

    override fun getItemCount(): Int {
        return orderNotesDtos.size
    }
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(position)
    }
    inner class ItemHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val orderNote=orderNotesDtos[position]
            binding.orderNote=orderNote
            binding.orderNoteDesc.text=getFormattedOrderDate(orderNote.createdAt)
            if(orderNote.orderStatus == AppConstance.STATUS_SUCCESS)
                binding.orderNoteDesc.visibility= View.GONE
            if(orderNote.orderStatus == AppConstance.STATUS_SUCCESS || orderNote.orderStatus == AppConstance.STATUS_READY || orderNote.orderStatus == AppConstance.STATUS_DISPATCHED || orderNote.orderStatus == AppConstance.STATUS_DELIVERED) {
                binding.orderNoteStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green_color))
                binding.orderNoteView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green_color))
            }
            else if(orderNote.orderStatus == AppConstance.STATUS_CANCELLED || orderNote.orderStatus == AppConstance.STATUS_PARTIALLY_CANCELLED || orderNote.orderStatus == AppConstance.STATUS_FAILED){
                binding.orderNoteStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                binding.orderNoteView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.red))
            }
            else if(orderNote.orderStatus == AppConstance.STATUS_PENDING || orderNote.orderStatus == AppConstance.STATUS_RETURNED || orderNote.orderStatus == AppConstance.STATUS_RETURNING || orderNote.orderStatus == AppConstance.STATUS_PARTIALLY_RETURNING || orderNote.orderStatus == AppConstance.STATUS_PARTIALLY_RETURNED){
                binding.orderNoteStatus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.primary))
                binding.orderNoteView.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.primary))
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