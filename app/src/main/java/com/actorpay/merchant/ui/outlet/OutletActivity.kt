package com.actorpay.merchant.ui.outlet
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityOutletBinding
import com.actorpay.merchant.ui.outlet.adapter.AdapterOutlet
import com.actorpay.merchant.ui.outlet.addoutlet.AddOutletActivity
import com.actorpay.merchant.ui.outlet.response.DeleteOutlet
import com.actorpay.merchant.ui.outlet.response.GetOutlet
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OutletActivity : BaseActivity() {
    private lateinit var binding: ActivityOutletBinding
    private val outletViewModel: OutletViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_outlet)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddOutlet.setOnClickListener {
            val intent=Intent(this,AddOutletActivity::class.java)
            resultLauncher.launch(intent)
        }
        outletViewModel.getOutlet()
        apiResponse()
    }
    fun apiResponse(){
        lifecycleScope.launch {
            outletViewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetOutlet) {
                            if(it.response.data.items.isNotEmpty()){
                                binding.rvOutlet.layoutManager=LinearLayoutManager(this@OutletActivity,LinearLayoutManager.VERTICAL,false)
                                binding.rvOutlet.adapter=AdapterOutlet(this@OutletActivity,it.response.data.items){
                                    pos ->
                                    var ids = mutableListOf<String>()
                                    ids.add(it.response.data.items[pos].id)
                                    CommonDialogsUtils.showCommonDialog(this@OutletActivity,
                                        outletViewModel.methodRepo, "Delete", "Are you sure you want to delete",
                                        autoCancelable = false,
                                        isCancelAvailable = true,
                                        isOKAvailable = true,
                                        showClickable = false,
                                        callback = object : CommonDialogsUtils.DialogClick {
                                            override fun onClick() {
                                                deleteOutlet(ids)
                                            }

                                            override fun onCancel() {

                                            }
                                        }
                                    )
                                }

                            }else{
                                showCustomToast("Data not found")
                            }

                        } else {

                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        hideLoadingDialog()
                    }
                }
            }
        }

        lifecycleScope.launch {
            outletViewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is DeleteOutlet) {

                            showCustomAlert(it.response.message, binding.root)

                            outletViewModel.getOutlet()
                        } else {


                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
    }


    private fun deleteOutlet(ids: MutableList<String>) {
        outletViewModel.deleteOutlet(ids)
    }
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
       if(it.resultCode==Activity.RESULT_OK) {
           outletViewModel.getOutlet()
       }
    }
}
