package com.actorpay.merchant.ui.manageProduct

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityManageProductBinding
import com.actorpay.merchant.databinding.DialogProductFilterBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_PRODUCT
import com.actorpay.merchant.repositories.AppConstance.AppConstanceData
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.DataCategory
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.GetAllCategoriesDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct.DeleteProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.GetProductListResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.Data
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.ui.addnewproduct.AddNewProduct
import com.actorpay.merchant.ui.home.adapter.ManageProductAdapter
import com.actorpay.merchant.ui.manageProduct.viewModel.ProductViewModel
import com.actorpay.merchant.ui.updateproduct.UpdateProduct
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData.permissionDataList
import com.actorpay.merchant.utils.ResponseSealed
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.techno.taskmanagement.utils.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ManageProductActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: ActivityManageProductBinding
    private var searchRunnable: Runnable? = null
    private var handler: Handler? = null
    private var productListData = ArrayList<Item>()
    var merchantRole = ""
    var name = ""
    var cat = ""
    var Sub = ""
    var catId = ""
    var SubCatId = ""
    var pos = 0
    var catList: MutableList<DataCategory> = ArrayList()
    var subCatList: MutableList<Data> = ArrayList()
    var permissionData = PermissionData(false, "5", SCREEN_MANAGE_PRODUCT, false)
    private val productViewModel: ProductViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_product)
        handler = Handler()
        binding.shimmerViewContainer.visibility=View.VISIBLE
        searchRunnable = Runnable {
        }
        installation()
        permissionDataList.forEach {
            if (it.screenName == permissionData.screenName) {
                permissionData.read = it.read
                permissionData.write = it.write
            }
        }
        lifecycleScope.launch {
            viewModel.methodRepo.dataStore.getRole().collect { role ->
                merchantRole = role
                if (merchantRole != "MERCHANT") {
                    if (permissionData.write) {
                        binding.AddNewProductButton.visibility = View.VISIBLE
                    } else {
                        binding.AddNewProductButton.visibility = View.GONE
                    }
                } else {
                    binding.AddNewProductButton.visibility = View.VISIBLE
                }
            }
        }
        clickListener()
    }

    private fun clickListener() {
        binding.AddNewProductButton.setOnClickListener {
            val i = Intent(baseContext, AddNewProduct::class.java)
            startActivityForResult(i, 102)
        }
    }
    private fun installation() {
        binding.swipeLoad.setOnRefreshListener(this)

        handler!!.postDelayed({ //Do something after delay

            productViewModel.getProductList("0", "", "", true, "", "")

        }, 3000)

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 2) {
                    searchRunnable = Runnable {
                        productViewModel.getProductList("0", binding.searchEdit.text.toString(), "", true, "", "")
                    }
                    handler!!.removeCallbacks(searchRunnable!!)
                    handler!!.postDelayed(searchRunnable!!, 1000)
                }
            }
        })
        val endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener =
            object : EndlessRecyclerViewScrollListener(LinearLayoutManager(this)) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    productViewModel.getProductList(page.toString(), "", "", true, "", "")
                }
            }
        binding.manageProduct.addOnScrollListener(endlessRecyclerViewScrollListener)
        binding.ivFilter.setOnClickListener {
            productFilterBottomSheet()
        }
        binding.back.setOnClickListener {
            finish()
        }

        response()

    }
    private fun productFilterBottomSheet() {
        val binding: DialogProductFilterBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_product_filter, null, false)
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        binding.productName.setText(name)
        setCatAdapter(binding.chooseCategory)
        setSubCatAdapter(binding.chooseSubCategory)
        binding.chooseCategory.setSelection(pos)
        binding.chooseCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    subCatList.clear()
                    subCatList.add(Data(true, "", "", "", "", "", "Please Select Subcategory"))
                    setSubCatAdapter(binding.chooseSubCategory)
                    if (position == 0) {
                        try {
                            (view as TextView).setTextColor(this@ManageProductActivity.resources.getColor(R.color.light_grey));
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        pos = binding.chooseCategory.selectedItemPosition
                        catId = catList[position].id
                        cat = catList[position].name
                        productViewModel.getSubCatDetalis(catId)
                    }
                }
            }
        binding.chooseSubCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == 0) {
                        try {
                            (view as TextView).setTextColor(this@ManageProductActivity.resources.getColor(R.color.light_grey));
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Sub = subCatList[position].name
                        SubCatId = subCatList[position].id
                    }
                }
            }
        binding.applyFilter.setOnClickListener {
            if (name.isEmpty()) {
                name = binding.productName.text.toString().trim()
            } else {
                name = ""
            }
            productViewModel.getProductList("0", name, cat, true, Sub, "")
            dialog.dismiss()
        }
        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.reset.setOnClickListener {
            binding.productName.setText("")
            binding.chooseCategory.setSelection(0)
            cat = ""
            Sub = ""
        }

        dialog.setContentView(binding.root)
        dialog.show()
    }
    fun setSubCatAdapter(chooseSubCategory: Spinner) {
        val subCatAdapter: ArrayAdapter<Data> =
            ArrayAdapter<Data>(this, android.R.layout.simple_spinner_item, subCatList)
        subCatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chooseSubCategory.adapter = subCatAdapter
    }
    fun setCatAdapter(chooseCategory: Spinner) {
        val catAdapter: ArrayAdapter<DataCategory> =
            ArrayAdapter<DataCategory>(this, android.R.layout.simple_spinner_item, catList)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chooseCategory.adapter = catAdapter
    }

    private fun response() {
        lifecycleScope.launch {
            productViewModel.responseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetAllCategoriesDetails) {
                            catList.add(DataCategory("", "", "", "", "Please select Category", false))
                            if (it.response.data.isNotEmpty()) {
                                catList.addAll(it.response.data)

                            } else {
                                showCustomAlert(
                                    getString(R.string.category_not_found),
                                    binding.root
                                )
                            }

                        } else if(it.response is GetSubCatDataDetails){
                            if (it.response.data.isNotEmpty()) {
                                subCatList.addAll(it.response.data)

                            } else showCustomAlert(getString(R.string.sub_category_not_found),
                                binding.root
                            )

                        }

                       else  if(it.response is GetProductListResponse){
                           productViewModel.getCategory()
                            if (it.response.data.items.size > 0) {
                                productListData = it.response.data.items
                                binding.manageProduct.visibility = View.VISIBLE
                                binding.emptyText.visibility = View.GONE
                                binding.imageEmpty.visibility = View.GONE

                                setupRv(productListData)

                            } else {
                                binding.manageProduct.visibility = View.GONE
                                binding.emptyText.visibility = View.VISIBLE
                                binding.imageEmpty.visibility = View.VISIBLE
                            }

                            binding.shimmerViewContainer.stopShimmerAnimation()
                            binding.shimmerViewContainer.visibility = View.GONE

                        }else if(it.response is DeleteProductResponse){
                            showCustomAlert("Product Deleted Successfully", binding.root)
                            productViewModel.getProductList("0", "", "", true, "", "")
                        }
                        else {
                            showCustomAlert(getString(R.string.please_try_after_sometime), binding.root)
                        }

                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        showCustomAlert(it.failResponse!!.message, binding.root)
                    }
                    else -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
    }

    private fun setupRv(productListData: java.util.ArrayList<Item>) {
        binding.manageProduct.layoutManager = LinearLayoutManager(this@ManageProductActivity, LinearLayoutManager.VERTICAL, false)
        binding.manageProduct.adapter = ManageProductAdapter(this@ManageProductActivity, permissionData, merchantRole, productListData) { position: Int, data: String ->
            when (data) {
                AppConstanceData.EDIT -> {
                    val i = Intent(baseContext(), UpdateProduct::class.java)
                    i.putExtra(AppConstanceData.PRODUCT_ID, productListData[position].productId)
                    i.putExtra(AppConstanceData.CATEGORYID, productListData[position].categoryId)
                    startActivityForResult(i, 102)
                }
                AppConstanceData.DELETE -> {
                    CommonDialogsUtils.showCommonDialog(this@ManageProductActivity,
                        productViewModel.methodRepo,
                        "Delete",
                        "Are you sure you want to delete?",
                        autoCancelable = false,
                        isCancelAvailable = true,
                        isOKAvailable = true,
                        showClickable = false,
                        callback = object : CommonDialogsUtils.DialogClick {
                            override fun onClick() {
                                productViewModel.deleteProduct(productListData[position].productId)
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
    }

    override fun onRefresh() {
        binding.swipeLoad.isRefreshing = true
        productViewModel.getProductList("0", "", "", true, "", "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            productViewModel.getProductList("0", "", "", true, "", "")
            catList.clear()

        }
    }

    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation()


    }

    override fun onPause() {
        binding.shimmerViewContainer.visibility=View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()
        super.onPause()
    }

}