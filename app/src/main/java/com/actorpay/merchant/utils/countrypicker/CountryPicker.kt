package com.actorpay.merchant.utils.countrypicker

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.actorpay.merchant.R
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.repositories.retrofitrepository.models.auth.CountryItem


import java.util.*

class CountryPicker(val context: Context, val methodsRepo: MethodsRepo, val list:MutableList<CountryItem>, val onClick:(position:Int)->Unit) {

    fun show(){

        val filterList= mutableListOf<CountryItem>()
        filterList.addAll(list)

        val dialog = Dialog(context)
        dialog.apply {
//            window?.attributes?.windowAnimations = R.style.DialogAnimationTheme
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.country_picker)
            window!!.setLayout(
                (methodsRepo.getDeviceWidth(context) / 100) * 95,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
//            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(true)
        }.also { dialog.show() }




        val btn = dialog.findViewById<TextView>(R.id.search_spinner_button)
        btn.setOnClickListener { dialog.dismiss() }

        val adapter = CountryPickerAdapter(filterList,context){
            dialog.dismiss()
            onClick(it)

        }
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.searchListRecyclerview)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter=adapter

        val editText = dialog.findViewById<EditText>(R.id.search_query)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(ss: CharSequence, start: Int, before: Int, count: Int) {
                val filterdNames = ArrayList<CountryItem>()

                for (s in list) {
                    //if the existing elements contains the search input
                    if (s.country.lowercase(Locale.getDefault()).contains(ss.toString()
                            .lowercase(Locale.getDefault()))) {
                        //adding the element to filtered list
                        filterdNames.add(s)
                    }
                }
                filterList.clear()
                filterList.addAll(filterdNames)
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun afterTextChanged(ss: Editable) {}
        })
    }
}