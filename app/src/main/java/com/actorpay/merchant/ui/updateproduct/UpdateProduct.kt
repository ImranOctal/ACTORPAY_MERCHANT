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
import com.actorpay.merchant.databinding.ActivityAddNewProductBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.ui.home.HomeViewModel
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.manageOrder.adapter.OrderAdapter
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.bumptech.glide.Glide
import com.octal.actorpay.repositories.AppConstance.AppConstance
import com.octal.actorpay.repositories.AppConstance.AppConstance.Companion.GALLERY
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException
import java.util.ArrayList
import android.content.Intent.getIntent
import androidx.core.net.toFile
import com.actorpay.merchant.databinding.ActivityEditProductBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.profile.ProfileReesponse
import com.octal.actorpay.repositories.retrofitrepository.models.content.ProductResponse
import org.json.JSONObject


class UpdateProduct : BaseActivity() {
    private lateinit var binding: ActivityEditProductBinding
    private val homeviewmodel: HomeViewModel by inject()
    var PERMISSIONS = Manifest.permission.READ_EXTERNAL_STORAGE
    var prodImage:File?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_product)
        Installation()
    }

    private fun Installation() {
        homeviewmodel.getProduct("dfsdf")
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.ToolbarTitle.text = getString(R.string.updateProduct)
        ClickListners()
        apiResponse()
    }

    private fun ClickListners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
        binding.updateProduct.setOnClickListener {
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

        if (binding.chooseCategory.selectedIndex == -1) {
            isValidate = false
            binding.errorOnCat.visibility = View.VISIBLE
        }
         else
        binding.errorOnCat.visibility = View.GONE

        if (binding.chooseSubCategory.selectedIndex == -1) {
            isValidate = false
            binding.errorOnSubcat.visibility = View.VISIBLE
        }
        else
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


           val name=binding.productNameEdit.text.toString().trim()
           val price=binding.actualPrice.text.toString().trim()
           val dealPrice=binding.dealPrice.text.toString().trim()
           val desc=binding.description.text.toString().trim()
           val qaunt=binding.quantity.text.toString().trim()
           val catIndex=binding.chooseCategory.selectedIndex
           val subCatIndex=binding.chooseSubCategory.selectedIndex
            val cat=resources.getStringArray(R.array.productCat).get(catIndex)
            val subCat=resources.getStringArray(R.array.productCat).get(subCatIndex)

            val productJson=JSONObject()
            productJson.put("productId","")
            productJson.put("name","name")
            productJson.put("description",desc)
            productJson.put("categoryId","1")
            productJson.put("subCategoryId","2")
            productJson.put("actualPrice",price)
            productJson.put("dealPrice",dealPrice)
            productJson.put("productPictureUrl","String")
            productJson.put("merchantId",0)

            homeviewmodel.updateProduct("product Id",productJson.toString(),prodImage!!)

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

            homeviewmodel.homeResponseLive.collect {
                when (it) {
                    is HomeViewModel.ResponseHomeSealed.loading -> {
                        homeviewmodel.methodRepo.showLoadingDialog(this@UpdateProduct)
                    }
                    is HomeViewModel.ResponseHomeSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (it.response is SuccessResponse) {
                            CommonDialogsUtils.showCommonDialog(
                                this@UpdateProduct,
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
                                                this@UpdateProduct,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finishAffinity()
                                    }

                                    override fun onCancel() {

                                    }
                                }
                            )
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
                    is HomeViewModel.ResponseHomeSealed.ErrorOnResponse -> {
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