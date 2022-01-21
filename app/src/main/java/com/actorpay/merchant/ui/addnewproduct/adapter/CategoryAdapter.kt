package com.actorpay.merchant.ui.addnewproduct.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.Data
import com.skydoves.powerspinner.*
import com.skydoves.powerspinner.databinding.ItemDefaultPowerSpinnerLibraryBinding

class CategoryAdapter(powerSpinnerView: PowerSpinnerView
) : RecyclerView.Adapter<CategoryAdapter.IconSpinnerViewHolder>(),
    PowerSpinnerInterface<Data> {

    override var index: Int = powerSpinnerView.selectedIndex?:0
    override val spinnerView: PowerSpinnerView = powerSpinnerView
    override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<Data>? = null
    private val compoundPadding: Int = 12
    private var spinnerItems: MutableList<Data> = arrayListOf()

    init {
        this.spinnerView.compoundDrawablePadding = compoundPadding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconSpinnerViewHolder {
        val binding =
            ItemDefaultPowerSpinnerLibraryBinding.inflate(
                LayoutInflater.from(parent.context), parent,
                false
            )
        return IconSpinnerViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener
                notifyItemSelected(position)
            }
        }
    }

    override fun onBindViewHolder(holder: IconSpinnerViewHolder, position: Int) {
        holder.bind(spinnerItems[position], spinnerView)

        holder.itemView.setOnClickListener {
            notifyItemSelected(position)
        }

    }

    override fun setItems(itemList: List<Data>) {
        this.spinnerItems.clear()
       this.spinnerItems= itemList.toMutableList()
        notifyDataSetChanged()
    }

    override fun notifyItemSelected(index: Int) {
        if (index == -1) return
        val item = spinnerItems[index]
        val oldIndex = this.index
        this.index = index
        this.spinnerView.notifyItemSelected(index, item.name)
        this.onSpinnerItemSelectedListener?.onItemSelected(
            oldIndex = oldIndex,
            oldItem = oldIndex.takeIf { it >= 0 }?.let { spinnerItems[oldIndex] },
            newIndex = index,
            newItem = item
        )
    }

    override fun getItemCount() = this.spinnerItems.size

    class IconSpinnerViewHolder(private val binding: ItemDefaultPowerSpinnerLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Data, spinnerView: PowerSpinnerView) {
            binding.itemDefaultText.apply {
                text = item.name
                gravity = spinnerView.gravity
                setTextSize(TypedValue.COMPLEX_UNIT_PX, spinnerView.textSize)
                setTextColor(spinnerView.currentTextColor)
            }
            binding.root.setPadding(
                spinnerView.paddingLeft,
                spinnerView.paddingTop,
                spinnerView.paddingRight,
                spinnerView.paddingBottom
            )
        }
    }
}
