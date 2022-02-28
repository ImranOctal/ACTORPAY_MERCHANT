package com.actorpay.merchant.ui.commission

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityCommissionBinding
import com.actorpay.merchant.databinding.DialogCommissionFilterBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.commission.CommissionResponse
import com.actorpay.merchant.utils.ResponseSealed
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import java.text.DecimalFormat
import java.util.*

class CommissionActivity : BaseActivity() {
    private lateinit var binding: ActivityCommissionBinding
    private val commissionViewModel: CommissionViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_commission)
        commissionViewModel.getAllCommissions()
        binding.shimmerViewContainer.visibility=View.VISIBLE
        binding.back.setOnClickListener {
            finish()
        }
        binding.ivFilter.setOnClickListener {
            filterBottomsheet()
        }
        apiResponse()
        setAdapter()
    }

    private fun setAdapter() {
        binding.rvCommission.layoutManager =
            LinearLayoutManager(this@CommissionActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvCommission.adapter = CommissionAdapter(commissionViewModel.commissionList)
    }

    private fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            commissionViewModel.responseLive.collect { action ->
                when (action) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (action.response is CommissionResponse) {
                            if (commissionViewModel.pageNo == 0) {
                                commissionViewModel.commissionList.clear()
                            }
                            commissionViewModel.commissionList.addAll(action.response.data.items)
                            binding.rvCommission.adapter?.notifyDataSetChanged()
                            if (commissionViewModel.commissionList.size > 0) {
                                binding.rvCommission.visibility = View.VISIBLE
                                binding.emptyText.visibility = View.GONE

                            } else {
                                binding.emptyText.visibility = View.VISIBLE
                                binding.rvCommission.visibility = View.GONE
                            }

                            binding.shimmerViewContainer.stopShimmerAnimation()
                            binding.shimmerViewContainer.visibility = View.GONE
                        }
                    }

                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (action.failResponse!!.code == 403) {
                            forcelogout(commissionViewModel.methodRepo)
                        } else {
                            showCustomAlert(action.failResponse.message, binding.root)
                        }
                    }
                    else -> hideLoadingDialog()
                }
            }
        }
    }

    private fun filterBottomsheet() {
        val binding: DialogCommissionFilterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.dialog_commission_filter,
            null,
            false
        )
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        binding.orderNumber.setText(commissionViewModel.commissionParams.orderNo)
        binding.merchantName.setText(commissionViewModel.commissionParams.merchantName)
        binding.merchantEarningFrom.setText(commissionViewModel.commissionParams.merchantEarningsRangeFrom)
        binding.merchantEarningTo.setText(commissionViewModel.commissionParams.merchantEarningsRangeTo)
        binding.merchantCommissionFrom.setText(commissionViewModel.commissionParams.actorCommissionAmtRangeFrom)
        binding.merchantCommissionTo.setText(commissionViewModel.commissionParams.actorCommissionAmtRangeTo)
        binding.startDate.setText(commissionViewModel.commissionParams.startDate)
        binding.endDate.setText(commissionViewModel.commissionParams.endDate)

        ArrayAdapter.createFromResource(
            this,
            R.array.status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                    (view as TextView).setTextColor(
                        ContextCompat.getColor(
                            this@CommissionActivity,
                            R.color.gray
                        )
                    )
                }
            }
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.settlement_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerSettlement.adapter = adapter
        }


        binding.spinnerSettlement.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (position == 0) {
                        (view as TextView).setTextColor(
                            ContextCompat.getColor(
                                this@CommissionActivity,
                                R.color.gray
                            )
                        )
                    }
                }

            }

        val status_array = this.resources.getStringArray(R.array.status_array).toMutableList()
        if (status_array.contains(
                commissionViewModel.commissionParams.orderStatus.replace(
                    "_",
                    " "
                )
            )
        ) {
            val pos =
                status_array.indexOfFirst {
                    it.equals(
                        commissionViewModel.commissionParams.orderStatus.replace(
                            "_",
                            " "
                        )
                    )
                }
            binding.spinnerStatus.setSelection(pos)
        }

        val settlement_array =
            this.resources.getStringArray(R.array.settlement_array).toMutableList()
        if (settlement_array.contains(commissionViewModel.commissionParams.settlementStatus)) {
            val pos =
                settlement_array.indexOfFirst { it.equals(commissionViewModel.commissionParams.settlementStatus) }
            binding.spinnerSettlement.setSelection(pos)
        }



        binding.applyFilter.setOnClickListener {
            commissionViewModel.commissionParams.orderNo = binding.orderNumber.text.toString()
            commissionViewModel.commissionParams.merchantName = binding.merchantName.text.toString()
            commissionViewModel.commissionParams.merchantEarningsRangeFrom =
                binding.merchantEarningFrom.text.toString()
            commissionViewModel.commissionParams.merchantEarningsRangeTo =
                binding.merchantEarningTo.text.toString()
            commissionViewModel.commissionParams.actorCommissionAmtRangeFrom =
                binding.merchantCommissionFrom.text.toString()
            commissionViewModel.commissionParams.actorCommissionAmtRangeTo =
                binding.merchantCommissionTo.text.toString()
            commissionViewModel.commissionParams.startDate = binding.startDate.text.toString()
            commissionViewModel.commissionParams.endDate = binding.endDate.text.toString()
            val statusPosition = binding.spinnerStatus.selectedItemPosition
            var status = ""
            if (statusPosition != 0) {
                status = status_array[statusPosition]
                status = status.replace(" ", "_")
            }
            val settlementPosition = binding.spinnerSettlement.selectedItemPosition
            var settlement = ""
            if (settlementPosition != 0) {
                settlement = settlement_array[settlementPosition]
            }

            commissionViewModel.commissionParams.orderStatus = status
            commissionViewModel.commissionParams.settlementStatus = settlement
            commissionViewModel.pageNo = 0
            commissionViewModel.getAllCommissions()

            dialog.dismiss()
        }


        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.startDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this, { view, yearR, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val f = DecimalFormat("00");
                val dayMonth = f.format(dayOfMonth)
                val monthYear = f.format(monthOfYear + 1)
                binding.startDate.setText("$yearR-$monthYear-$dayMonth")

            }, year, month, day)
            dpd.show()
            dpd.getDatePicker().setMaxDate(Date().time)
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        binding.endDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this, { view, yearR, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val f = DecimalFormat("00");
                val dayMonth = f.format(dayOfMonth)
                val monthYear = f.format(monthOfYear + 1)
                binding.endDate.setText("$yearR-$monthYear-$dayMonth")
            }, year, month, day)
            dpd.show()
            dpd.datePicker.maxDate = Date().time
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        binding.reset.setOnClickListener {
            binding.orderNumber.setText("")
            binding.merchantName.setText("")
            binding.merchantEarningFrom.setText("")
            binding.merchantEarningTo.setText("")
            binding.merchantCommissionFrom.setText("")
            binding.merchantCommissionTo.setText("")
            binding.startDate.setText("")
            binding.endDate.setText("")
            binding.spinnerStatus.setSelection(0)
            binding.spinnerSettlement.setSelection(0)
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation();


    }

    override fun onPause() {
        binding.shimmerViewContainer.visibility=View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation();
        super.onPause()
    }

}