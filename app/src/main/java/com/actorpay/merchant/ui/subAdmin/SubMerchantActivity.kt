package com.actorpay.merchant.ui.subAdmin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivitySubMerchantBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.DeleteSubMerchant
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.GetAllSubMerchant
import com.actorpay.merchant.repositories.retrofitrepository.models.submerchant.Item
import com.actorpay.merchant.ui.home.HomeActivity
import com.actorpay.merchant.ui.outlet.addoutlet.AddOutletActivity
import com.actorpay.merchant.ui.outlet.response.DeleteOutlet
import com.actorpay.merchant.ui.outlet.updateoutlet.UpdateOutletActivity
import com.actorpay.merchant.ui.subAdmin.adapter.SubMerchantAdapter
import com.actorpay.merchant.ui.subAdmin.addSubMerchant.CreateSubAdminActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.ResponseSealed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SubMerchantActivity : BaseActivity() {
    private lateinit var binding: ActivitySubMerchantBinding
    private val SubviewModel: SubMerchantsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sub_merchant)
        binding.back.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddMerchant.setOnClickListener {
            val intent=Intent(this, CreateSubAdminActivity::class.java)
            resultLauncher.launch(intent)
        }
        SubviewModel.getSubMerchants()
        apiResponse()
    }


    private fun setUpRv(items: List<Item>) {
        binding.rvSubMerchants.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSubMerchants.adapter = SubMerchantAdapter(this, items){
           pos, action ->
            if(action=="delete"){
                var ids = mutableListOf<String>()
                ids.add(items[pos].id)
                CommonDialogsUtils.showCommonDialog(this,
                    SubviewModel.methodRepo, "Delete", "Are you sure you want to delete",
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
            }else if(action=="edit"){
                val intent = Intent(this, CreateSubAdminActivity::class.java)
                intent.putExtra("from","edit")
                intent.putExtra("id",items[pos].id)
                resultUpdateLauncher.launch(intent)
            }


        }
    }

    private fun deleteOutlet(ids: MutableList<String>) {
        SubviewModel.deleteMerchant(ids)

    }

    private fun apiResponse() {
        lifecycleScope.launch {
            SubviewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetAllSubMerchant) {

                            if (it.response.data.items.isNotEmpty()) {
                                setUpRv(it.response.data.items)

                                binding.tvEmptyText.visibility=View.GONE

                            }else{
                               binding.tvEmptyText.visibility=View.VISIBLE

                            }
                        }

                        if (it.response is DeleteSubMerchant) {
                            showCustomAlert(it.response.message, binding.root)
                            SubviewModel.getSubMerchants()
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
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode== Activity.RESULT_OK) {
            SubviewModel.getSubMerchants()
        }
    }

    private var resultUpdateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode==Activity.RESULT_OK) {
            SubviewModel.getSubMerchants()
        }
    }
}