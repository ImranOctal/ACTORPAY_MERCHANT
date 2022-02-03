package com.actorpay.merchant.ui.manageProduct

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityManageProductBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstanceData
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct.DeleteProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.GetProductListResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.ui.addnewproduct.AddNewProduct
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.home.adapter.ManageProductAdapter
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.updateproduct.UpdateProduct
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData.permissionDataList
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_PRODUCT
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_SUB_MERCHANT
import com.techno.taskmanagement.utils.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.ArrayList

class ManageProductActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: ActivityManageProductBinding
    private var searchRunnable: Runnable? = null
    private var handler: Handler? = null
    private var productListData = ArrayList<Item>()

    var permissionData=PermissionData(false,"5",SCREEN_MANAGE_PRODUCT,false)

    private val homeviewmodel: HomeViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_product)
        handler = Handler()
        searchRunnable = Runnable {
        }



        installation()

        permissionDataList.forEach {
            if(it.screenName==permissionData.screenName){
                permissionData.read=it.read
                permissionData.write=it.write
            }
        }

        if(permissionData.write){
            binding.AddNewProductButton.visibility=View.VISIBLE
        }else{
            binding.AddNewProductButton.visibility=View.GONE
        }
        clickListner()

    }

    private fun clickListner() {
       binding.AddNewProductButton.setOnClickListener {
           val i=Intent(baseContext,AddNewProduct::class.java)
           startActivityForResult(i,102)
       }
    }

    private fun installation() {
        binding.swipeLoad.setOnRefreshListener(this)
        homeviewmodel.getProductList("0", "")
        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 2) {
                    searchRunnable = Runnable {
//                        data = JSONObject()
//                        data.put("name", s.toString())
                        homeviewmodel.getProductList("0", binding.searchEdit.text.toString())
                    }
                    handler!!.removeCallbacks(searchRunnable!!)
                    handler!!.postDelayed(searchRunnable!!, 1000)
                }
            }
        })

        val endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener =
            object : EndlessRecyclerViewScrollListener(LinearLayoutManager(this)) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    homeviewmodel.getProductList(page.toString(),"")
                }
            }
        binding.manageProduct.addOnScrollListener(endlessRecyclerViewScrollListener)
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.manage_product)

        binding.toolbar.back.setOnClickListener {
            finish()
        }

        WorkSource()
    }
    fun WorkSource() {
        lifecycleScope.launchWhenStarted {
            homeviewmodel.homeResponseLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.ResponseHomeSealed.loading -> {
                        showLoadingDialog()
                    }
                    is HomeSealedClasses.Companion.ResponseHomeSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is SuccessResponse) {
                            CommonDialogsUtils.showCommonDialog(
                                this@ManageProductActivity,
                                homeviewmodel.methodRepo,
                                "Signed Up",
                                it.response.message,
                                autoCancelable = false,
                                isCancelAvailable = false,
                                isOKAvailable = true,
                                showClickable = false,
                                callback = object : CommonDialogsUtils.DialogClick {
                                    override fun onClick() {
                                        startActivity(
                                            Intent(
                                                this@ManageProductActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finishAffinity()
                                    }

                                    override fun onCancel() {

                                    }
                                }
                            )
                        } else showCustomAlert(
                            getString(R.string.please_try_after_sometime),
                            binding.root
                        )
                    }
                    is HomeSealedClasses.Companion.ResponseHomeSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (it.failResponse!!.code == 403) {
                            forcelogout(homeviewmodel.methodRepo)
                        }else{
                            showCustomAlert(
                                it.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    else -> hideLoadingDialog()
                }
            }
        }
        //productListLivedata

        lifecycleScope.launchWhenStarted {
            homeviewmodel.productListLive.collect { action ->
                when (action) {
                    is HomeSealedClasses.Companion.ResponseProductListSealed.loading -> {
//                        binding.emptyText.visibility = View.VISIBLE
                        showLoadingDialog()
                    }
                    is HomeSealedClasses.Companion.ResponseProductListSealed.Success -> {
                        hideLoadingDialog()
                        binding.swipeLoad.isRefreshing = false
                        if (action.response is GetProductListResponse) {
//                            binding.emptyText.visibility = View.GONE
                            if (action.response.data.items.size > 0) {
                                productListData = action.response.data.items
                                binding.manageProduct.visibility=View.VISIBLE
                                binding.emptyText.visibility = View.GONE
                                binding.manageProduct.layoutManager=LinearLayoutManager(  this@ManageProductActivity,LinearLayoutManager.VERTICAL,false)
                                binding.manageProduct.adapter = ManageProductAdapter(  this@ManageProductActivity,permissionData,productListData) { position: Int, data: String ->
                                    when (data) {
                                        AppConstanceData.EDIT -> {
                                            val i = Intent(baseContext(), UpdateProduct::class.java)
                                            i.putExtra(AppConstanceData.PRODUCT_ID,productListData[position].productId)
                                            startActivityForResult(i,102)
                                        }
                                        AppConstanceData.DELETE -> {
                                            CommonDialogsUtils.showCommonDialog(this@ManageProductActivity,
                                                homeviewmodel.methodRepo, "Delete", "Are you sure you want to delete",
                                                autoCancelable = false,
                                                isCancelAvailable = true,
                                                isOKAvailable = true,
                                                showClickable = false,
                                                callback = object : CommonDialogsUtils.DialogClick {
                                                    override fun onClick() {
                                                        homeviewmodel.deleteProduct(productListData[position].productId)
                                                    }

                                                    override fun onCancel() {

                                                    }
                                                }
                                            )
                                        }
                                        AppConstanceData.ROOT -> {
                                        }
                                    }
                                }
                            } else {
                                binding.manageProduct.visibility=View.GONE
                                binding.emptyText.visibility = View.VISIBLE
                            }
                        }
                    }
                    is HomeSealedClasses.Companion.ResponseProductListSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (action.failResponse!!.code == 403) {
                            forcelogout(homeviewmodel.methodRepo)
                        }else{
                            binding.swipeLoad.isRefreshing = false
                            showCustomAlert(
                                action.failResponse.message,
                                binding.root
                            )
                        }
//                        binding.emptyText.visibility = View.VISIBLE
//                        binding.swipeLoad.isRefreshing = false
//                        hideLoadingDialog()
//                        showCustomAlert(action.failResponse!!.message, binding.root)
//                        if (action.failResponse.message.contains("End of input at")) {
////                            logOutDirect()
//                        }
                    }
                    else -> hideLoadingDialog()
                }
            }
        }
        //Delete Product

        lifecycleScope.launchWhenStarted {
            homeviewmodel.deleteproductLive.collect { action ->
                when (action) {
                    is HomeSealedClasses.Companion.ResponseDeleteSealed.loading -> {
                        binding.emptyText.visibility = View.VISIBLE
                        showLoadingDialog()
                    }
                    is HomeSealedClasses.Companion.ResponseDeleteSealed.Success -> {
                        hideLoadingDialog()
                        if (action.response is DeleteProductResponse) {
                            showCustomAlert("Product Deleted Successfully", binding.root)
                            homeviewmodel.getProductList("0", "")
                        } else {
                            binding.emptyText.visibility = View.VISIBLE
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }
                    }
                    is HomeSealedClasses.Companion.ResponseDeleteSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (action.failResponse!!.code == 403) {
                            forcelogout(homeviewmodel.methodRepo)
                        }else{
                            showCustomAlert(
                                action.failResponse.message,
                                binding.root
                            )
                        }
                        /* if (action.failResponse.message.contains("End of input at")) {
                             logOutDirect()
                         }*/
                    }
                    else -> hideLoadingDialog()
                }
            }
        }
    }

    override fun onRefresh() {
        binding.swipeLoad.isRefreshing = true
        homeviewmodel.getProductList("0", "")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102&&resultCode== Activity.RESULT_OK) {
            homeviewmodel.getProductList("0","")
        }
    }
}