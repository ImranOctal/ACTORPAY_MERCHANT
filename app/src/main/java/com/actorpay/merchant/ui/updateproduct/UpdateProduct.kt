package com.actorpay.merchant.ui.updateproduct


import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import androidx.core.net.toFile
import com.actorpay.merchant.databinding.ActivityAddNewProductBinding
import com.actorpay.merchant.databinding.ActivityEditProductBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstanceData
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.Data
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.GetAllCategoriesDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductById.success.GetProductDataById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.Item
import com.actorpay.merchant.repositories.retrofitrepository.models.taxation.GetCurrentTaxDetail
import com.actorpay.merchant.ui.addnewproduct.adapter.CategoryAdapter
import com.actorpay.merchant.ui.addnewproduct.adapter.SubCategoryAdapter
import com.actorpay.merchant.ui.addnewproduct.adapter.TaxAdapter
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.octal.actorpay.repositories.retrofitrepository.models.content.ProductResponse
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import org.json.JSONObject


class UpdateProduct : BaseActivity() {
    private lateinit var binding: ActivityAddNewProductBinding
    private lateinit var catAdapter: CategoryAdapter
    private lateinit var taxAdapter: TaxAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private val homeviewmodel: HomeViewModel by inject()
    private  var categoryList: List<Data>?=null
    private  var subCategoryList: List<Item>?=null
    private  var taxList: List<com.actorpay.merchant.repositories.retrofitrepository.models.taxation.Data>?=null
    private var response:GetProductDataById?=null
    var PERMISSIONS = Manifest.permission.READ_EXTERNAL_STORAGE
    var prodImage: File? = null
    var taxId:String=""
    var catId=""
    var SubCatId=""
    var productId=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_product)
        binding.addProduct.setText(getString(R.string.updated_product))
        productId= intent.getStringExtra(AppConstanceData.PRODUCT_ID).toString()
        Installation()
    }

    private fun Installation() {

        homeviewmodel.getProduct(productId)
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.updateProduct)
        binding.toolbar.back.visibility = View.VISIBLE
        homeviewmodel.getCatogrys()
        homeviewmodel.getSubCatDetalis()
        homeviewmodel.getTaxationDetails()
        catAdapter = CategoryAdapter(binding.chooseCategory)
        taxAdapter = TaxAdapter(binding.taxData)
        subCategoryAdapter = SubCategoryAdapter(binding.chooseSubCategory)
        catAdapter.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener<Data>() { oldIndex: Int, oldItem: Data?, newIndex: Int, newItem: Data ->
                catId=newItem.id
            }
        taxAdapter.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener<com.actorpay.merchant.repositories.retrofitrepository.models.taxation.Data>() { oldIndex: Int, oldItem: com.actorpay.merchant.repositories.retrofitrepository.models.taxation.Data?, newIndex: Int, newItem: com.actorpay.merchant.repositories.retrofitrepository.models.taxation.Data ->
                taxId=newItem.id
            }
        subCategoryAdapter.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener<Item>() { oldIndex: Int, oldItem: Item?, newIndex: Int, newItem: Item ->
                SubCatId=newItem.id
            }

        binding.chooseCategory.setSpinnerAdapter(catAdapter)
        binding.taxData.setSpinnerAdapter(taxAdapter)
        binding.chooseSubCategory.setSpinnerAdapter(subCategoryAdapter)
        ClickListners()
        apiResponse()
    }

    private fun ClickListners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
        binding.addProduct.setOnClickListener {
            validate()
        }
        binding.uploadImage.setOnClickListener {
            if (!homeviewmodel.methodRepo.checkPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {

                permReqLauncher.launch(PERMISSIONS)
            } else {
                fetchImage()
            }
        }

    }


    fun validate() {
        var isValidate = true

        if (binding.productNameEdit.text.toString().trim().length < 3) {
            isValidate = false
            binding.errorOnName.visibility = View.VISIBLE
        }
        else
            binding.errorOnName.visibility = View.GONE

        if (catId=="") {
            isValidate = false
            binding.errorOnCat.visibility = View.VISIBLE
        } else
            binding.errorOnCat.visibility = View.GONE

        if (SubCatId=="") {
            isValidate = false
            binding.errorOnSubcat.visibility = View.VISIBLE
        } else
            binding.errorOnSubcat.visibility = View.GONE

        if (binding.actualPrice.text.toString().trim() =="" || binding.actualPrice.text.toString().trim().toFloat() < 1) {
            isValidate = false
            binding.errorOnActualPrice.visibility = View.VISIBLE
        }
        else
            binding.errorOnActualPrice.visibility = View.GONE

        if (binding.dealPrice.text.toString().trim() =="" || binding.dealPrice.text.toString().trim().toFloat() < 1) {
            isValidate = false
            binding.errorOndealPrice.visibility = View.VISIBLE
        }
        else
            binding.errorOndealPrice.visibility = View.GONE

        if (binding.quantity.text.toString().trim() =="" || binding.quantity.text.toString().trim().toInt() < 1) {
            isValidate = false
            binding.errorOnquantity.visibility = View.VISIBLE
        }
        else
            binding.errorOnquantity.visibility = View.GONE

        if (binding.description.text.toString().trim() =="") {
            isValidate = false
            binding.errorOnDescription.visibility = View.VISIBLE
        }
        else
            binding.errorOnDescription.visibility = View.GONE

        if(isValidate){

            if(prodImage==null)
            {
                showCustomToast("Please Select Product Image")
                return
            }
            if (taxId == "") {
                isValidate = false
                binding.errorOntaxData.visibility = View.VISIBLE
                binding.errorOntaxData.text=getString(R.string.error_on_tax)
            } else
                binding.errorOntaxData.visibility = View.GONE

            lifecycleScope.launch {
                val name = binding.productNameEdit.text.toString().trim()
                val price = binding.actualPrice.text.toString().trim()
                val dealPrice = binding.dealPrice.text.toString().trim()
                val desc = binding.description.text.toString().trim()
                val qaunt = binding.quantity.text.toString().trim()

                val productJson = JSONObject()
                productJson.put("productId", "")
                productJson.put("name", name)
                productJson.put("description", desc)
                productJson.put("categoryId", catId)
                productJson.put("subCategoryId", SubCatId)
                productJson.put("actualPrice", price)
                productJson.put("dealPrice", dealPrice)
                productJson.put("merchantId",viewModel.methodRepo.dataStore.getUserId())
                productJson.put("stockCount", qaunt)
                productJson.put("taxId", taxId)
                homeviewmodel.updateProduct(productId,productJson.toString(),prodImage!!)

            }
        }
    }

    fun fetchImage() {

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        galleryForResult.launch(galleryIntent)
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->

            if (permission) {
                fetchImage()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this, PERMISSIONS
                    )
                ) {
                    showCustomToast("Permission Denied, Go to setting to give access")
                } else {
                    showCustomToast("Permission Denied")
                }

            }
        }

    val galleryForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    try {
                        val contentURI = data.data
                        cropImage(contentURI!!)

                    } catch (e: IOException) {
                        e.printStackTrace()

                    }

                }

            }
        }

    private fun cropImage(sourceUri: Uri) {
        val destinationUri: Uri = Uri.fromFile(
            File(
                getCacheDir(),
                queryName(getContentResolver(), sourceUri)
            )
        )
        val options: UCrop.Options = UCrop.Options();
        options.setCompressionQuality(80);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.black));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));

        options.withAspectRatio(1f, 1f);

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)

        val intent = uCrop.getIntent(this)
        cropResult.launch(intent)


    }

    val cropResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                if (data != null) {
                    val resultUri = UCrop.getOutput(data)

                    prodImage=resultUri?.toFile()

                    binding.image.visibility = View.VISIBLE
                    binding.uploadImage.text = getString(R.string.edit_image)
                    Glide.with(this).load(resultUri).error(R.drawable.logo)
                        .into(binding.image)
//                val bitmap = (mBinding.profilePic.getDrawable() as BitmapDrawable).bitmap
//                val file = UtilityHelper.createFile(this, bitmap)

                }
            }
        }

    private fun queryName(resolver: ContentResolver, uri: Uri): String {
        val returnCursor: Cursor? =
            resolver.query(uri, null, null, null, null);

        returnCursor.let {

            val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            val name: String = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        }
    }


    private fun apiResponse() {
        lifecycleScope.launch {
            homeviewmodel.getProductByIDLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.ResponseGetProductSealed.loading -> {
                        homeviewmodel.methodRepo.showLoadingDialog(this@UpdateProduct)
                    }
                    is HomeSealedClasses.Companion.ResponseGetProductSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (it.response is GetProductDataById) {
                            response=it.response
                            Glide.with(binding.root).load(it.response.data.image).placeholder(R.drawable.demo).into(binding.image)
                            binding.actualPrice.setText(it.response.data.actualPrice.toString())
                            binding.dealPrice.setText(it.response.data.dealPrice.toString())
                            binding.description.setText(it.response.data.description.toString())
                            binding.quantity.setText(it.response.data.quantity.toString())
                            binding.productNameEdit.setText(it.response.data.name.toString())
                            if(categoryList!=null && categoryList!!.size>0){
                                for((index, value) in categoryList!!.withIndex()){
                                    if(value.id.equals(it.response.data.categoryId)){
                                        binding.chooseCategory.notifyItemSelected(index,value.name)
                                    }
                                }
                            }
                            if(subCategoryList!=null && subCategoryList!!.size>0){
                                for((index, value) in subCategoryList!!.withIndex()){
                                    if(value.id.equals(it.response.data.categoryId)){
                                        binding.chooseSubCategory.notifyItemSelected(index,value.name)
                                    }
                                }
                            }
                            if(taxList!=null && taxList!!.size>0){
                                for((index, value) in taxList!!.withIndex()){
                                    if(value.id.equals(it.response.data.categoryId)){
                                        binding.chooseSubCategory.notifyItemSelected(index,value.taxPercentage.toString())
                                    }
                                }
                            }

                        }
                        else if(it.response is ProductResponse){
                            CommonDialogsUtils.showCommonDialog(
                                this@UpdateProduct,
                                homeviewmodel.methodRepo,
                                "Get Prododuct",
                                it.response.message,
                                autoCancelable = false,
                                isCancelAvailable = false,
                                isOKAvailable = true,
                                showClickable = false,
                                callback = object : CommonDialogsUtils.DialogClick {
                                    override fun onClick() {

                                    }

                                    override fun onCancel() {

                                    }
                                })
                        }
                        else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }

                    }
                    is HomeSealedClasses.Companion.ResponseGetProductSealed.ErrorOnResponse -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                    }
                }

            }
        }
        //Category Loded
        lifecycleScope.launch {
            homeviewmodel.CatogryLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.CatogrySealed.loading -> {
                        homeviewmodel.methodRepo.showLoadingDialog(this@UpdateProduct)
                    }
                    is HomeSealedClasses.Companion.CatogrySealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (it.response is GetAllCategoriesDetails) {
                            if (it.response.data.size > 0) {
                                categoryList=it.response.data
                                if(response!=null){
                                    if(categoryList!=null && categoryList!!.size>0){
                                        for((index, value) in categoryList!!.withIndex()){
                                            if(value.id.equals(response!!.data.categoryId)){
                                                binding.chooseCategory.setText(value.name)
                                            }
                                        }
                                    }
                                }
                                catAdapter.setItems(itemList = it.response.data)

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
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                    }
                }

            }
        }

        //SubCategory Loded
        lifecycleScope.launch {
            homeviewmodel.subCatLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.SubCatSealed.loading -> {
                        homeviewmodel.methodRepo.showLoadingDialog(this@UpdateProduct)
                    }
                    is HomeSealedClasses.Companion.SubCatSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (it.response is GetSubCatDataDetails) {
                            if (it.response.data.items.size > 0) {
                                subCategoryList=it.response.data.items
                                if(response!=null){
                                    if(subCategoryList!=null && subCategoryList!!.size>0){
                                        for((index, value) in subCategoryList!!.withIndex()){
                                            if(value.id.equals(response!!.data.categoryId)){
                                                binding.chooseSubCategory.setText(value.name)
                                            }
                                        }
                                    }
                                }
                                subCategoryAdapter.setItems(itemList = it.response.data.items)
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
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                    }
                }

            }
        }

        //tax Loded
        lifecycleScope.launch {
            homeviewmodel.taxListLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.TaxationSealed.loading -> {
                        homeviewmodel.methodRepo.showLoadingDialog(this@UpdateProduct)
                    }
                    is HomeSealedClasses.Companion.TaxationSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (it.response is GetCurrentTaxDetail) {
                            if (it.response.data.size > 0) {
                                taxList=it.response.data
                                taxAdapter.setItems(itemList = it.response.data)
                                if(response!=null && taxList!=null && taxList!!.size>0){
                                    for((index, value) in taxList!!.withIndex()){
                                        if(value.id.equals(response!!.data.categoryId)){
                                            binding.chooseSubCategory.setText(value.taxPercentage.toString())
                                        }
                                    }
                                }
                            } else showCustomAlert(getString(R.string.tax_not_found), binding.root)
                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }

                    }
                    is HomeSealedClasses.Companion.TaxationSealed.ErrorOnResponse -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                    }
                }

            }
        }
    }
}