package com.actorpay.merchant.ui.addnewproduct


import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityAddNewProductBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.products.addNewProduct.AddNewProductResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.Data
import com.actorpay.merchant.repositories.retrofitrepository.models.products.categories.GetAllCategoriesDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.GetSubCatDataDetails
import com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory.Item
import com.actorpay.merchant.repositories.retrofitrepository.models.taxation.GetCurrentTaxDetail
import com.actorpay.merchant.ui.addnewproduct.adapter.CategoryAdapter
import com.actorpay.merchant.ui.addnewproduct.adapter.SubCategoryAdapter
import com.actorpay.merchant.ui.addnewproduct.adapter.TaxAdapter
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.bumptech.glide.Glide
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException


class AddNewProduct : BaseActivity() {
    private lateinit var binding: ActivityAddNewProductBinding
    private lateinit var catAdapter: CategoryAdapter
    private lateinit var taxAdapter: TaxAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private val homeviewmodel: HomeViewModel by inject()
    var PERMISSIONS = Manifest.permission.READ_EXTERNAL_STORAGE
    var prodImage: File? = null
    var taxId: String = ""
    var catId = ""
    var SubCatId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_product)
        Installation()

    }

    private fun Installation() {
        binding.toolbar.back.visibility = View.VISIBLE
        homeviewmodel.getCatogrys()
        homeviewmodel.getSubCatDetalis()
        homeviewmodel.getTaxationDetails()
        catAdapter = CategoryAdapter(binding.chooseCategory)
        taxAdapter = TaxAdapter(binding.taxData)

        subCategoryAdapter = SubCategoryAdapter(binding.chooseSubCategory)
        catAdapter.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener<Data>() { oldIndex: Int, oldItem: Data?, newIndex: Int, newItem: Data ->
                catId = newItem.id
            }
        taxAdapter.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener<com.actorpay.merchant.repositories.retrofitrepository.models.taxation.Data>() { oldIndex: Int, oldItem: com.actorpay.merchant.repositories.retrofitrepository.models.taxation.Data?, newIndex: Int, newItem: com.actorpay.merchant.repositories.retrofitrepository.models.taxation.Data ->
                taxId = newItem.id
            }
        subCategoryAdapter.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener<Item>() { oldIndex: Int, oldItem: Item?, newIndex: Int, newItem: Item ->
                SubCatId = newItem.id
            }

        binding.chooseCategory.setSpinnerAdapter(catAdapter)
        binding.taxData.setSpinnerAdapter(taxAdapter)
        binding.chooseSubCategory.setSpinnerAdapter(subCategoryAdapter)
        binding.toolbar.ToolbarTitle.text = getString(R.string.addNewProduct)
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
        if (prodImage == null) {
            showCustomToast("Please Select Product Image")

        } else if (binding.productNameEdit.text.toString().trim().isEmpty()) {
            binding.productNameEdit.error = getString(R.string.product_empty)
            binding.productNameEdit.requestFocus()

        } else if (binding.productNameEdit.text.toString().trim().length < 3) {
            binding.productNameEdit.error = getString(R.string.prod_name_empty)
            binding.productNameEdit.requestFocus()

        } else if (catId == "") {
            showCustomToast("Please Select Category")
        } else if (SubCatId == "") {
            showCustomToast("Please Select SubCategory")
        } else if (binding.actualPrice.text.toString().trim().isEmpty()) {
            binding.actualPrice.error = getString(R.string.price_empty)
            binding.actualPrice.requestFocus()

        } else if (binding.actualPrice.text.toString().trim().toFloat() < 1) {
            binding.actualPrice.error = getString(R.string.prod_price_empty)
            binding.actualPrice.requestFocus()
        } else if (binding.dealPrice.text.toString().trim().isEmpty()) {
            binding.dealPrice.error = getString(R.string.deal_price_empty)
            binding.dealPrice.requestFocus()
        } else if (binding.dealPrice.text.toString().trim().toFloat() < 1) {
            binding.dealPrice.error = getString(R.string.deal_price_length)
            binding.dealPrice.requestFocus()
        }
        else if (taxId=="") {
            showCustomToast("Please Select SubCategory")
        }

        else if (binding.quantity.text.toString().trim().isEmpty()) {
            binding.quantity.error = getString(R.string.prod_quant_empty)
            binding.quantity.requestFocus()

        } else if (binding.quantity.text.toString().trim().toInt() < 1) {
            binding.quantity.error = getString(R.string.prod_quant_length)
            binding.quantity.requestFocus()
        } else if (binding.description.text.toString().trim().isEmpty()) {
            binding.description.error = getString(R.string.prod_desc_empty)
            binding.description.requestFocus()
        }else{
                lifecycleScope.launch {
                    viewModel.methodRepo.dataStore.getMerchantId().collect { merchantId ->
                        val name = binding.productNameEdit.text.toString().trim()
                        val price = binding.actualPrice.text.toString().trim()
                        val dealPrice = binding.dealPrice.text.toString().trim()
                        val desc = binding.description.text.toString().trim()
                        val qaunt = binding.quantity.text.toString().trim()
                        val productJson = JSONObject()
//                  productJson.put("productId", "")
                        productJson.put("name", name)
                        productJson.put("description", desc)
                        productJson.put("categoryId", catId)
                        productJson.put("subCategoryId", SubCatId)
                        productJson.put("actualPrice", price)
                        productJson.put("dealPrice", dealPrice)
                        productJson.put("merchantId", merchantId)
                        productJson.put("stockCount", qaunt)
                        productJson.put("taxId", taxId)
                        homeviewmodel.addProduct(productJson.toString(), prodImage!!)
                        Log.e("merchantId>>", merchantId)
                    }
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
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS)) {
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
                File(getCacheDir(), queryName(getContentResolver(), sourceUri))
            )
            val options: UCrop.Options = UCrop.Options();
            options.setCompressionQuality(80);
            options.setToolbarColor(ContextCompat.getColor(this, R.color.black));
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
            options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));
            options.withAspectRatio(1f, 1f);
            val uCrop = UCrop.of(sourceUri, destinationUri).withOptions(options)
            val intent = uCrop.getIntent(this)
            croporResult.launch(intent)
        }

        val croporResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data

                    if (data != null) {
                        val resultUri = UCrop.getOutput(data)
                        prodImage = resultUri?.toFile()
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
            val returnCursor: Cursor? = resolver.query(uri, null, null, null, null)
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
                homeviewmodel.addProductByIDLive.collect {
                    when (it) {
                        is HomeSealedClasses.Companion.ResponseAddProductSealed.loading -> {
                            showLoadingDialog()
                        }
                        is HomeSealedClasses.Companion.ResponseAddProductSealed.Success -> {
                            hideLoadingDialog()
                            if (it.response is AddNewProductResponse) {
                                CommonDialogsUtils.showCommonDialog(
                                    this@AddNewProduct,
                                    homeviewmodel.methodRepo,
                                    "Add New Product",
                                    it.response.message,
                                    autoCancelable = false,
                                    isCancelAvailable = false,
                                    isOKAvailable = true,
                                    showClickable = false,
                                    callback = object : CommonDialogsUtils.DialogClick {
                                        override fun onClick() {
                                            setResult(Activity.RESULT_OK)
                                            finish()
                                        }

                                        override fun onCancel() {

                                        }
                                    }
                                )
                            } else {
                                showCustomAlert(
                                    getString(R.string.please_try_after_sometime),
                                    binding.root
                                )
                            }

                        }
                        is HomeSealedClasses.Companion.ResponseAddProductSealed.ErrorOnResponse -> {
                            hideLoadingDialog()
                            hideLoadingDialog()
                            if(it.failResponse!!.code==403){
                                forcelogout(homeviewmodel.methodRepo)
                            }else{
                                showCustomAlert(
                                    it.failResponse.message,
                                    binding.root
                                )
                            }
                        }
                        else -> {
                            hideLoadingDialog()
                        }
                    }

                }
            }

            //Category Loded
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
                                if (it.response.data.items.size > 0) {
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


            //tax Loded
            lifecycleScope.launch {
                homeviewmodel.taxListLive.collect {
                    when (it) {
                        is HomeSealedClasses.Companion.TaxationSealed.loading -> {
                            showLoadingDialog()
                        }
                        is HomeSealedClasses.Companion.TaxationSealed.Success -> {
                            hideLoadingDialog()
                            if (it.response is GetCurrentTaxDetail) {
                                if (it.response.data.size > 0) {
                                    taxAdapter.setItems(itemList = it.response.data)
                                } else
                                    showCustomAlert(getString(R.string.tax_not_found), binding.root)


                            } else {
//                            showCustomAlert(getString(R.string.please_try_after_sometime), binding.root)
                            }

                        }
                        is HomeSealedClasses.Companion.TaxationSealed.ErrorOnResponse -> {
                            hideLoadingDialog()
//                        showCustomAlert(it.failResponse!!.message, binding.root)
                        }
                        else -> {
                            hideLoadingDialog()
                        }
                    }

                }
            }
        }
    }