package com.actorpay.merchant.ui.manageProduct
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.DialogProductFilterBinding
import com.actorpay.merchant.databinding.FragmentManageProductBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstance
import com.actorpay.merchant.repositories.AppConstance.AppConstanceData
import com.actorpay.merchant.repositories.retrofitrepository.models.permission.PermissionData
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.DataCategory
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.GetAllCategoriesDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct.DeleteProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.Data
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.ui.manageProduct.viewModel.ProductViewModel
import com.actorpay.merchant.ui.paging.MangeAdapter
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.GlobalData.permissionDataList
import com.actorpay.merchant.utils.OnFilterClick
import com.actorpay.merchant.utils.ResponseSealed
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.ext.android.inject

class ManageProductFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, OnFilterClick {
    private lateinit var binding: FragmentManageProductBinding
    private var handler: Handler? = null
    var merchantRole = ""
    var name = ""
    var cat = ""
    var Sub = ""
    var catId = ""
    var SubCatId = ""
    var pos = 0
    var isComing=false
    var catList: MutableList<DataCategory> = ArrayList()
    var subCatList: MutableList<Data> = ArrayList()
    lateinit var productPagingAdapter:MangeAdapter
    var permissionData = PermissionData(false, "5", AppConstance.SCREEN_MANAGE_PRODUCT, false)
    private val productViewModel: ProductViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler()
        setAdapter()
    }
    private fun setAdapter() {
       productPagingAdapter= MangeAdapter(requireActivity(), permissionData, merchantRole) { position: Int, data: String,item ->
            when (data) {
                AppConstanceData.EDIT -> {
                    val bundle= bundleOf(AppConstanceData.PRODUCT_ID to item.productId)
                    bundleOf(AppConstanceData.CATEGORYID to item.categoryId)
                    Navigation.findNavController(requireView()).navigate(R.id.updateProductFragment,bundle)
                }
                AppConstanceData.DELETE -> {
                    CommonDialogsUtils.showCommonDialog(requireActivity(),
                        productViewModel.methodRepo,
                        "Delete",
                        "Are you sure you want to delete?",
                        autoCancelable = false,
                        isCancelAvailable = true,
                        isOKAvailable = true,
                        showClickable = false,
                        callback = object : CommonDialogsUtils.DialogClick {
                            override fun onClick() {
                             productViewModel.deleteProduct(item.productId)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_product, container, false)
        handler = Handler()
        setupRv()
        if(!isComing){
            isComing=!isComing
            binding.shimmerViewContainer.visibility = View.VISIBLE
            binding.shimmerViewContainer.startShimmerAnimation()
        }
        onFilterClick(this)
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

        return binding.root
    }
    private fun clickListener() {
        binding.AddNewProductButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.addNewProductFragment)
        }
    }
    private fun installation() {

        binding.swipeLoad.setOnRefreshListener(this)
        binding.ivSearch.setOnClickListener {
            getProducts( binding.searchEdit.text.toString(), "", true, "", "")
        }
        binding.searchEdit.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getProducts( binding.searchEdit.text.toString(), "", true, "", "")
                    catList.clear()
                true
            } else false
        })
        response()
    }
    private fun getProducts( name: String,categoryNam:String,status:Boolean,subCategoryName:String,merchantId:String){
        lifecycleScope.launchWhenCreated {
            productViewModel.methodRepo.dataStore.getAccessToken().collect { token ->
                productViewModel.getProductsWithPaging(AppConstance.B_Token+token,name, categoryNam, status, subCategoryName, merchantId).collectLatest {
                    productPagingAdapter.submitData(it)
                }
            }
        }
    }
    private fun productFilterBottomSheet() {
        val binding: DialogProductFilterBinding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()), R.layout.dialog_product_filter, null, false)
        val dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        binding.productName.setText(name)
        setCatAdapter(binding.chooseCategory)
        setCatAdapter(binding.chooseSubCategory)
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
                            (view as TextView).setTextColor(requireActivity().resources.getColor(R.color.light_grey));
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
                            (view as TextView).setTextColor(requireActivity().resources.getColor(R.color.light_grey));
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
            catList.clear()

            if (name.isEmpty()) {
                name = binding.productName.text.toString().trim()
            } else {
                name = ""
            }
            getProducts( name, cat, true, Sub, "")
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
        val subCatAdapter: ArrayAdapter<Data> = ArrayAdapter<Data>(requireActivity(), android.R.layout.simple_spinner_item, subCatList)
        subCatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chooseSubCategory.adapter = subCatAdapter
    }
    fun setCatAdapter(chooseCategory: Spinner) {
        val catAdapter: ArrayAdapter<DataCategory> = ArrayAdapter<DataCategory>(requireActivity(), android.R.layout.simple_spinner_item, catList)
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
                               catList.clear()
                               catList.add(DataCategory("", "", "", "", "Please select Category", false))

                            if (it.response.data.isNotEmpty()) {
                                catList.addAll(it.response.data)

                            } else {
                                showCustomAlert(getString(R.string.category_not_found), binding.root)
                            }
                        } else if (it.response is GetSubCatDataDetails) {
                            subCatList.clear()
                            subCatList.add(Data(true, "", "", "", "", "", "Please Select Subcategory"))

                            if (it.response.data.isNotEmpty()) {
                                subCatList.addAll(it.response.data)

                            } else showCustomAlert(
                                getString(R.string.sub_category_not_found),
                                binding.root
                            )
                        }
                        else if (it.response is DeleteProductResponse) {
                            showCustomAlert("Product Deleted Successfully", binding.root)
                            catList.clear()
                            getProducts("","",true,"","")
                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
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
    private fun setupRv() {
        binding.manageProduct.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productPagingAdapter
        }
        productPagingAdapter.addLoadStateListener {
                state->
            when(state.refresh)
            {
                is LoadState.Loading->{
                    showLoadingDialog()

                }
                is LoadState.NotLoading->
                {
                    hideLoadingDialog()
                    productViewModel.getCategory()
                    if(productPagingAdapter.itemCount>0){
                        binding.imageEmpty.visibility=View.GONE
                        binding.emptyText.visibility=View.GONE
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.shimmerViewContainer.stopShimmerAnimation()
                    }
                    else {
                        binding.imageEmpty.visibility=View.VISIBLE
                        binding.emptyText.visibility=View.VISIBLE

                    }
                    binding.shimmerViewContainer.visibility = View.GONE
                    binding.shimmerViewContainer.stopShimmerAnimation()
                }
                is LoadState.Error->{

                    hideLoadingDialog()
                    val errorResponse = (state.refresh as LoadState.Error).error.message!!
                    val json= JSONObject(errorResponse)
                    if (json.has("code") && json.getInt("code") == 403) {
                        forcelogout(productViewModel.methodRepo)
                    }
                }
            }
        }
    }
    override fun onRefresh() {
        binding.swipeLoad.isRefreshing = false
        getProducts("", "", true, "", "")
    }
    override fun onResume() {
        super.onResume()
        getProducts( "", "", true, "", "")

    }
    override fun onPause() {
        binding.shimmerViewContainer.visibility = View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()
        super.onPause()
    }
    override fun onClick() {
        productFilterBottomSheet()
    }
}
