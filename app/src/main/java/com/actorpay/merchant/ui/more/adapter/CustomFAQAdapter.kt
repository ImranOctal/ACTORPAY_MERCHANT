package com.actorpay.merchant.ui.more.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.RowItemFaqBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.content.FAQResponseData


class CustomFAQAdapter(
    private val list: List<FAQResponseData>,
) : RecyclerView.Adapter<CustomFAQAdapter.MyViewoHolder>() {

    var oldViewHolder:MyViewoHolder?=null
    var myViewHolder2:MyViewoHolder?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewoHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val binding=DataBindingUtil.inflate<RowItemFaqBinding>(layoutInflater, R.layout.row_item_faq,parent,false)

        return MyViewoHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewoHolder, position: Int) {
        holder.bindData(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewoHolder(val binding: RowItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(position: Int){
            val item=list[position]
            binding.faqQuestion.text=item.question


            binding.faqAnswer.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(item.answer.trim(), Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(item.answer.trim())
            }

            if(item.isExpand) {
                binding.faqAnswer.visibility = View.VISIBLE
                binding.faqIcon.rotation=180f
            }
            else {
                binding.faqAnswer.visibility = View.GONE
                binding.faqIcon.rotation=0f
            }

            binding.faqQuestion.setOnClickListener {
                updateUI(item)
            }
            binding.faqIcon.setOnClickListener {
                updateUI(item)
            }



        }

        fun updateUI(item: FAQResponseData){
            if(item.isExpand){
                binding.faqAnswer.animate()
                    .translationY(0f)
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            binding.faqAnswer.setVisibility(View.GONE)
                        }
                    })
                list[position].isExpand= false
                notifyItemRangeChanged(0,list.size)
            }
            else{
                binding.faqAnswer.animate()
                    .translationY(binding.faqAnswer.getHeight().toFloat())
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            binding.faqAnswer.setVisibility(View.VISIBLE)
                        }
                    })

                closeAll()
                list[position].isExpand= true
                notifyItemRangeChanged(0,list.size)
            }
        }

    }

    fun closeAll(){
        list.forEach { it.isExpand=false }
    }

}