package com.actorpay.merchant.ui.wallet

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.WalletFilterDialogBinding


import java.text.DecimalFormat
import java.util.*

class WalletFilterDialog(
    val mContext: Activity,
) : Dialog(mContext) {
    override fun show() {
        super.show()
        window?.setGravity(Gravity.BOTTOM)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DataBindingUtil.inflate<WalletFilterDialogBinding>(
            mContext.layoutInflater,
            R.layout.wallet_filter_dialog,
            null,
            false
        )
        setContentView(binding.root)
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        ArrayAdapter.createFromResource(
            mContext,
            R.array.wallet_txn_type_status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            binding.spinnerStatus.adapter = adapter
        }
        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    (view as TextView).setTextColor(ContextCompat.getColor(mContext, R.color.gray))
                }
            }
        }

        val array = mContext.resources.getStringArray(R.array.wallet_txn_type_status_array).toMutableList()
        binding.startDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(mContext, { _, yearR, monthOfYear, dayOfMonth ->

                val f = DecimalFormat("00")
                val dayMonth = f.format(dayOfMonth)
                val monthYear = f.format(monthOfYear + 1)

                binding.startDate.setText("" + yearR + "-" + (monthYear) + "-" + dayMonth)

            }, year, month, day)
            dpd.show()
            dpd.datePicker.maxDate = Date().time
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }

        binding.endDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(mContext, { _, yearR, monthOfYear, dayOfMonth ->

                val f = DecimalFormat("00")
                val dayMonth = f.format(dayOfMonth)
                val monthYear = f.format(monthOfYear + 1)

                binding.endDate.setText("" + yearR + "-" + (monthYear) + "-" + dayMonth)

            }, year, month, day)

            dpd.show()
            dpd.datePicker.maxDate = Date().time
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }

        binding.apply.setOnClickListener {
            var wallet: String = ""
            var totalFrom: String = ""
            var totalTo: String = ""
            var remark: String = ""
            var transactionType: String? = null
            var startDate: String = ""
            var endDate: String = ""


            wallet = binding.walletId.text.toString().trim()

            totalFrom = binding.totalFrom.text.toString().trim()

            totalTo = binding.totalTo.text.toString().trim()

            remark = binding.remark.text.toString().trim()

            startDate = binding.startDate.text.toString().trim()
            endDate = binding.endDate.text.toString().trim()

            val statusPosition = binding.spinnerStatus.selectedItemPosition
            if (statusPosition != 0) {
                transactionType = array[statusPosition]
            }
            dismiss()
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.reset.setOnClickListener {
            binding.walletId.setText("")
            binding.totalFrom.setText("")
            binding.totalTo.setText("")
            binding.remark.setText("")
            binding.startDate.setText("")
            binding.endDate.setText("")
            binding.spinnerStatus.setSelection(0)
        }


    }
}