package com.octal.actorpayuser.ui.dispute

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
import com.actorpay.merchant.databinding.DisputeFilterDialogBinding
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.octal.actorpayuser.repositories.retrofitrepository.models.dispute.DisputeListParams
import java.text.DecimalFormat
import java.util.*

class DisputeFilterDialog(
    private val params: DisputeListParams,
    val mContext: Activity,
    val methodsRepo: MethodsRepo,
    val onClick: (DisputeListParams) -> Unit
) : Dialog(mContext) {
    override fun show() {
        super.show()
        window?.setGravity(Gravity.BOTTOM)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(R.layout.order_filter_dialog)
        val binding = DataBindingUtil.inflate<DisputeFilterDialogBinding>(
            mContext.layoutInflater,
            R.layout.dispute_filter_dialog,
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
            R.array.dispute_status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Apply the adapter to the spinner
            binding.spinnerStatus.adapter = adapter
        }
        binding.spinnerStatus.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position==0){
                    (view as TextView).setTextColor(ContextCompat.getColor(mContext,R.color.gray))
                }
            }

        }

            binding.disputeCode.setText(params.disputeCode)
            binding.startDate.setText(params.startDate)
            binding.endDate.setText(params.endDate)

        val array = mContext.resources.getStringArray(R.array.dispute_status_array).toMutableList()
        if (params.status != "") {
            if (array.contains(params.status)) {
                val pos = array.indexOfFirst { it.equals(params.status) }
                binding.spinnerStatus.setSelection(pos)
            }
        }

        binding.startDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(mContext,  { _, yearR, monthOfYear, dayOfMonth ->

                val f =  DecimalFormat("00")
                val dayMonth=f.format(dayOfMonth)
                val monthYear=f.format(monthOfYear+1)

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


            val dpd = DatePickerDialog(mContext,  { _, yearR, monthOfYear, dayOfMonth ->

                val f =  DecimalFormat("00")
                val dayMonth=f.format(dayOfMonth)
                val monthYear=f.format(monthOfYear+1)

                binding.endDate.setText("" + yearR + "-" + (monthYear) + "-" + dayMonth)

            }, year, month, day)

            dpd.show()
            dpd.datePicker.maxDate = Date().time
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }

        binding.apply.setOnClickListener {
            var disputeCode: String = ""
            var startDate: String = ""
            var endDate: String = ""
            var status: String = ""
            if ((binding.disputeCode.text.toString().trim() == "").not())
                disputeCode = binding.disputeCode.text.toString().trim()
            if ((binding.startDate.text.toString().trim() == "").not())
                startDate = binding.startDate.text.toString().trim()
            if ((binding.endDate.text.toString().trim() == "").not())
                endDate = binding.endDate.text.toString().trim()


            val statusPosition = binding.spinnerStatus.selectedItemPosition
            if (statusPosition != 0) {
                status = array[statusPosition]
                status=status
            }
            onClick(
                DisputeListParams(
                    startDate,
                    endDate,
                    status,
                    disputeCode
                )
            )
            dismiss()
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.reset.setOnClickListener {
            binding.disputeCode.setText("")
            binding.startDate.setText("")
            binding.endDate.setText("")
            binding.spinnerStatus.setSelection(0)

        }


    }
}