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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityManageProductBinding
import com.actorpay.merchant.databinding.DialogProductFilterBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstanceData
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.DataCategory
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.GetAllCategoriesDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct.DeleteProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.GetProductListResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.Data
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.ui.addnewproduct.AddNewProduct
import com.actorpay.merchant.ui.addnewproduct.adapter.CategoryAdapter
import com.actorpay.merchant.ui.addnewproduct.adapter.SubCategoryAdapter
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.home.adapter.ManageProductAdapter
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.updateproduct.UpdateProduct
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData.permissionDataList
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.SCREEN_MANAGE_PRODUCT
import com.techno.taskmanagement.utils.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ManageProductActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: ActivityManageProductBinding
    private var searchRunnable: Runnable? = null
    private var handler: Handler? = null
    private var productListData = ArrayList<Item>()
    private lateinit var catAdapter: CategoryAdapter
    var merchantRole = ""
    var name = ""
    var cat = ""
    var Sub = ""
    var catId = ""
    var SubCatId = ""

    var catList: MutableList<DataCategory> = ArrayList()
    var subCatList: MutableList<Data> = ArrayList()
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    var permissionData = PermissionData(false, "5", SCREEN_MANAGE_PRODUCT, false)
    private val homeviewmodel: HomeViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_product)
        handler = Handler()
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

        clickListner()
        homeviewmodel.getCatogrys()

    }

    private fun clickListner() {
        binding.AddNewProductButton.setOnClickListener {
            val i = Intent(baseContext, AddNewProduct::class.java)
            startActivityForResult(i, 102)
        }
    }

    private fun installation() {
        binding.swipeLoad.setOnRefreshListener(this)
        homeviewmodel.getProductList("0", "", "", true, "", "")
        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 2) {
                    searchRunnable = Runnable {
//                        data = JSONObject()
//                        data.put("name", s.toString())
                        homeviewmodel.getProductList(
                            "0",
                            binding.searchEdit.text.toString(),
                            "",
                            true,
                            "",
                            ""
                        )
                    }
                    handler!!.removeCallbacks(searchRunnable!!)
                    handler!!.postDelayed(searchRunnable!!, 1000)
                }
            }
        })
        val endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener =
            object : EndlessRecyclerViewScrollListener(LinearLayoutManager(this)) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    homeviewmodel.getProductList(page.toString(), "", "", true, "", "")
                }
            }


        binding.manageProduct.addOnScrollListener(endlessRecyclerViewScrollListener)
        binding.ivFilter.setOnClickListener {
            productFilterBottomSheet()
        }
        binding.back.setOnClickListener {
            finish()
        }
        WorkSource()
    }

    private fun productFilterBottomSheet() {
        val binding: DialogProductFilterBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_product_filter, null, false)
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        binding.productName.setText(name)
        catList.add(DataCategory("", "", "", "", "Please select Category", false))
        setCatAdapter(binding.chooseCategory)
        setSubCatAdapter(binding.chooseSubCategory)

        binding.chooseCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    subCatList.clear()
                    subCatList.add(Data(true, "", "", "", "", "", "Please Select Subcategory"))
                    setSubCatAdapter(binding.chooseSubCategory)
                    if (position == 0) {
//                        (view as TextView).setTextColor(this@ManageProductActivity.resources.getColor(R.color.light_grey))
                    } else {

                        catId = catList[position].id
                        cat = catList[position].name
                        homeviewmodel.getSubCatDetalis(catId)

                    }

                }
            }
        binding.chooseSubCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == 0) {
//                        (view as TextView).setTextColor(this@ManageProductActivity.resources.getColor(R.color.light_grey))
                    }else{
                        Sub = subCatList[position].name
                        SubCatId = subCatList[position].id
                    }
                }
            }
//        if(cat.isEmpty()){
//            binding.chooseCategory.hint = "Choose Category"
//        }else{
//            binding.chooseCategory.hint = cat
//        }
//        if(Sub.isEmpty()){
//            binding.chooseSubCategory.hint = "Choose SubCategory"
//
//        }else{
//            binding.chooseSubCategory.hint = Sub
//        }
//        subCategoryAdapter = SubCategoryAdapter(binding.chooseSubCategory)
//        catAdapter = CategoryAdapter(binding.chooseCategory)
//        binding.chooseCategory.setSpinnerAdapter(catAdapter)
//        binding.chooseSubCategory.setSpinnerAdapter(subCategoryAdapter)
//        catAdapter.onSpinnerItemSelectedListener =
//            OnSpinnerItemSelectedListener<DataCategory>() { oldIndex: Int, oldItem: DataCategory?, newIndex: Int, newItem: DataCategory ->
//                cat = newItem.name
//                homeviewmodel.getSubCatDetalis(cat)
//            }
//        subCategoryAdapter.onSpinnerItemSelectedListener =
//            OnSpinnerItemSelectedListener<Data>() { oldIndex: Int, oldItem: Data?, newIndex: Int, newItem: Data ->
//                Sub = newItem.name
//
//            }

        binding.applyFilter.setOnClickListener {
            if (name.isEmpty()) {
                name = binding.productName.text.toString().trim()
            } else {
                name = ""
            }
            homeviewmodel.getProductList("0", name, cat, true, Sub, "")
            dialog.dismiss()
        }
        binding.cancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.reset.setOnClickListener {
            binding.productName.setText("")
            cat = ""
            Sub = ""
        }

        getCategoryReponse()
        dialog.setContentView(binding.root)
        dialog.show()

    }

    fun setSubCatAdapter(chooseSubCategory: Spinner) {
        val catAdapter: ArrayAdapter<Data> = ArrayAdapter<Data>(this, android.R.layout.simple_spinner_item, subCatList)

        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chooseSubCategory.adapter = catAdapter



    }

    fun setCatAdapter(chooseCategory: Spinner) {
        val catAdapter: ArrayAdapter<DataCategory> = ArrayAdapter<DataCategory>(this, android.R.layout.simple_spinner_item, catList)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chooseCategory.adapter = catAdapter

    }


    private fun getCategoryReponse() {
        lifecycleScope.launch {
            homeviewmodel.CatogryLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.CatogrySealed.loading -> {
                        showLoadingDialog()
                    }
                    is HomeSealedClasses.Companion.CatogrySealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetAllCategoriesDetails) {
                            if (it.response.data.size > 0) {
                                catList.addAll(it.response.data)


                            } else {
                                showCustomAlert(
                                    getString(R.string.category_not_found),
                                    binding.root
                                )
                            }

                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }

                    }
                    is HomeSealedClasses.Companion.CatogrySealed.ErrorOnResponse -> {
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
        //SubCategory Loded
        lifecycleScope.launch {
            homeviewmodel.subCatLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.SubCatSealed.loading -> {
                        showLoadingDialog()
                    }
                    is HomeSealedClasses.Companion.SubCatSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetSubCatDataDetails) {
                            if (it.response.data.size > 0) {
                                subCatList.addAll(it.response.data)
                            } else showCustomAlert(
                                getString(R.string.sub_category_not_found),
                                binding.root
                            )
                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }

                    }
                    is HomeSealedClasses.Companion.SubCatSealed.ErrorOnResponse -> {
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
                        } else {
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
                                binding.manageProduct.visibility = View.VISIBLE
                                binding.emptyText.visibility = View.GONE
                                binding.manageProduct.layoutManager = LinearLayoutManager(
                                    this@ManageProductActivity,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                binding.manageProduct.adapter = ManageProductAdapter(
                                    this@ManageProductActivity,
                                    permissionData,
                                    merchantRole,
                                    productListData
                                ) { position: Int, data: String ->
                                    when (data) {
                                        AppConstanceData.EDIT -> {
                                            val i = Intent(baseContext(), UpdateProduct::class.java)
                                            i.putExtra(AppConstanceData.PRODUCT_ID, productListData[position].productId,)
                                            i.putExtra(AppConstanceData.CATEGORYID, productListData[position].categoryId,)
                                            startActivityForResult(i, 102)
                                        }
                                        AppConstanceData.DELETE -> {
                                            CommonDialogsUtils.showCommonDialog(this@ManageProductActivity,
                                                homeviewmodel.methodRepo,
                                                "Delete",
                                                "Are you sure you want to delete?",
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
                                binding.manageProduct.visibility = View.GONE
                                binding.emptyText.visibility = View.VISIBLE
                            }
                        }
                    }
                    is HomeSealedClasses.Companion.ResponseProductListSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if (action.failResponse!!.code == 403) {
                            forcelogout(homeviewmodel.methodRepo)
                        } else {
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
                            homeviewmodel.getProductList("0", "", "", true, "", "")
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
                        } else {
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
        homeviewmodel.getProductList("0", "", "", true, "", "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            homeviewmodel.getProductList("0", "", "", true, "", "")
        }
    }

}