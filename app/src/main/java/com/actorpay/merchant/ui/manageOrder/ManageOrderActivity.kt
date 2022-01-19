package com.actorpay.merchant.ui.manageOrder


import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ActivityManageOrderBinding
import com.actorpay.merchant.databinding.DialogFilterBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.order.BeanViewAllOrder
import com.actorpay.merchant.repositories.retrofitrepository.models.order.Item
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.ui.manageOrder.adapter.OrderAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class ManageOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageOrderBinding
    private val homeviewmodel: HomeViewModel by inject()
    var startDate=""
    var endDate=""
    var merchantIid=""
    var deliveryStatus=""
    var orderStatus=""
    var customerEmail=""
    var orderNo=""
    private var order = ArrayList<Item>()
    private var list=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_order)
        Installation()
        homeviewmodel.getAllOrder(startDate,endDate,merchantIid,orderStatus,customerEmail,orderNo)
    }

    private fun setupRv(orderList: ArrayList<Item>) {
        binding.manageOrder.layoutManager = LinearLayoutManager(this@ManageOrderActivity, LinearLayoutManager.VERTICAL, false)
        binding.manageOrder.adapter = OrderAdapter(this@ManageOrderActivity,
            orderList
        ) { position, status ->
                updateStatus(position, orderList, status)
            }
    }
    private fun Installation() {
        binding.back.setOnClickListener {
            finish()
        }

        binding.ivFilter.setOnClickListener {
            filterBottomsheet()
        }
        apiResponse()
    }

    private fun filterBottomsheet() {
        val binding: DialogFilterBinding =
            DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_filter, null, false)
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        binding.applyFilter.setOnClickListener {
            if (orderNo.isEmpty()) {
                orderNo = binding.orderNumber.text.toString()

            } else {
                orderNo = ""

            }
            if (startDate.isEmpty()) {
                startDate = ""

            }
            if (endDate.isEmpty()) {
                endDate = ""

            }
            if (customerEmail.isEmpty()) {
                customerEmail = binding.merchant.text.toString()
            } else {
                customerEmail=""

            }

            homeviewmodel.getAllOrder(startDate, endDate, merchantIid, orderStatus, customerEmail, orderNo)
            dialog.dismiss()
        }
        ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item).also { adapter ->

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerStatus.adapter = adapter
        }
        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if(position>0){
                    orderStatus = binding.spinnerStatus.selectedItem.toString()
                }


                if (position == 0) {
                    (view as TextView).setTextColor(this@ManageOrderActivity.resources.getColor(R.color.light_grey))

                }
            }
         }
        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.startDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this,  { view, yearR, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val f =  DecimalFormat("00");
                val dayMonth=f.format(dayOfMonth)
                val monthYear=f.format(monthOfYear+1)
                binding.startDate.setText("$yearR-$monthYear-$dayMonth")
                startDate= "$year-$monthYear-$dayMonth"

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
            val dpd = DatePickerDialog(this,  { view, yearR, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val f =  DecimalFormat("00");
                val dayMonth=f.format(dayOfMonth)
                val monthYear=f.format(monthOfYear+1)
                binding.endDate.setText("$yearR-$monthYear-$dayMonth")
                endDate= "$year-$monthYear-$dayMonth"
            }, year, month, day)
            dpd.show()
            dpd.datePicker.maxDate = Date().time
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        binding.reset.setOnClickListener {
            binding.orderNumber.setText("")
            binding.merchant.setText("")
            binding.startDate.setText("")
            binding.endDate.setText("")
        }
        dialog.setContentView(binding.root)
        dialog.show()
    }
    private fun apiResponse() {
        lifecycleScope.launchWhenStarted {
            homeviewmodel.getAllOrder.collect { action ->
                when (action) {
                    is HomeSealedClasses.Companion.ResponseSealed.loading -> {
                        homeviewmodel.methodRepo.showLoadingDialog(this@ManageOrderActivity)
                    }
                    is HomeSealedClasses.Companion.ResponseSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (action.response is BeanViewAllOrder) {

                            if(action.response.data.items.size>0){
                                order=action.response.data.items
                                binding.manageOrder.visibility = View.VISIBLE
                                binding.emptyText.visibility = View.GONE
                                setupRv(order)
                            }else{
                                binding.emptyText.visibility = View.VISIBLE
                                binding.manageOrder.visibility = View.GONE
                            }
                        }
                    }
                    is HomeSealedClasses.Companion.ResponseSealed.ErrorOnResponse -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                    }
                    else -> homeviewmodel.methodRepo.hideLoadingDialog()
                }
            }
        }
    }
    private fun updateStatus(position: Int, items: ArrayList<Item>, status: String) {
//        homeviewmodel.updateStatus(items[position].orderNo,status)
    }
}