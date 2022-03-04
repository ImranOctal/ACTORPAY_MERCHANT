package com.actorpay.merchant.ui.profile

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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment

import com.actorpay.merchant.databinding.FragmentProfileBinding
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.GetUserById
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getUserById.MerchantSettingsDTO
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.utils.ResponseSealed
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.File
import java.io.IOException


class ProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by inject()

    var PERMISSIONS = Manifest.permission.READ_EXTERNAL_STORAGE
    var prodImage: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false)
        initialisation()
        return  binding.root
    }

    private fun initialisation() {
        clickListeners()
        getProfile()
        apiResponse()
    }
    fun apiResponse() {
        lifecycleScope.launch {
            profileViewModel.profileResponseLive.collect {
                when (it) {
                    is ResponseSealed.Loading -> {
                        showLoadingDialog()
                    }
                    is ResponseSealed.Success -> {
                        hideLoadingDialog()
                        if (it.response is GetUserById) {
                            updateUI(it.response)
                        } else if (it.response is SuccessResponse) {
                            CommonDialogsUtils.showCommonDialog(
                                requireActivity(), profileViewModel.methodRepo, getString(
                                    R.string.profile_update
                                ), it.response.message
                            )
                        } else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }
                    }
                    is ResponseSealed.ErrorOnResponse -> {
                        hideLoadingDialog()
                        if(it.failResponse!!.code==403){
                            forcelogout(profileViewModel.methodRepo)
                        }else{
                            showCustomAlert(
                                it.failResponse.message,
                                binding.root
                            )
                        }
                    }
                    is ResponseSealed.Empty -> {
                        hideLoadingDialog()
                    }
                }
            }
        }
    }

    fun updateUI(reesponse2: GetUserById){

        binding.emailEdit.setText(reesponse2.data.email)
        binding.businessName.setText(reesponse2.data.businessName)
        binding.shopAddress.setText(reesponse2.data.shopAddress)
        binding.address.setText(reesponse2.data.fullAddress)
        binding.shopAct.setText(reesponse2.data.licenceNumber)
        binding.mobileNumber.setText(reesponse2.data.contactNumber)
        binding.textView.text = reesponse2.data.businessName
        profileViewModel.merchantSettingsDTOList.clear()
        profileViewModel.merchantSettingsDTOList.addAll(reesponse2.data.merchantSettingsDTOS)

        var adminCommission="0"
        var returnFee="0"
        var cancellationFee="0"
        var returnDays="0"
        profileViewModel.merchantSettingsDTOList.forEach {
            if(it.paramName == "return-fee")
                returnFee=it.paramValue
            else if(it.paramName == "cancellation-fee")
                cancellationFee=it.paramValue

            else if(it.paramName == "return-days")
                returnDays=it.paramValue
            else if(it.paramName == "admin-commission")
                adminCommission=it.paramValue
        }

        binding.adminCommission.setText(adminCommission)
        binding.cancellation.setText(cancellationFee)
        binding.returnedt.setText(returnFee)
        binding.returnedD.setText(returnDays)



        try {
            var extContact = reesponse2.data.extensionNumber
            if (extContact.isNotEmpty()) {
                extContact = extContact.replace("+", "")
                binding.codePicker.text=extContact
            }
        } catch (e: Exception) {
            Log.d("Profile Fragment", "apiResponse: ${e.message}")
        }
    }

    fun validate() {
        val returnFee=binding.returnedt.text.toString()
        val cancellationFee=binding.cancellation.text.toString()
        var isRerunFee=false
        var isCancelFee=false
        try {
            val returnF=returnFee.toDouble()
            if(returnF >=1 && returnF <=99)
                isRerunFee=true
        }
        catch (e:Exception){ }
        try {
            val cancellationF=cancellationFee.toDouble()
            if(cancellationF >=1 && cancellationF <=99)
                isCancelFee=true
        }
        catch (e:Exception){ }
        if (binding.emailEdit.text.toString().length < 3 || !profileViewModel.methodRepo.isValidEmail(binding.emailEdit.text.toString())) {
            binding.errorOnEmail.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(requireActivity(), binding.emailLay, R.drawable.btn_search_outline)
        }
        else if (binding.businessName.text.toString().trim().isEmpty()) {
            binding.businessName.error = getString(R.string.business_empty)
            binding.businessName.requestFocus()
        } else if (binding.businessName.text.toString().trim().length < 3) {
            binding.businessName.error = getString(R.string.error_business)
            binding.businessName.requestFocus()

        } else if (binding.mobileNumber.text.toString().trim().length < 6) {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility = View.GONE
            binding.errorOnMobile.visibility = View.VISIBLE
            profileViewModel.methodRepo.setBackGround(
                requireActivity(),
                binding.emailLay,
                R.drawable.btn_outline_gray
            )
            profileViewModel.methodRepo.setBackGround(
                requireActivity(),
                binding.businessLay,
                R.drawable.btn_outline_gray
            )
            profileViewModel.methodRepo.setBackGround(
                requireActivity(),
                binding.mobileLay,
                R.drawable.btn_search_outline
            )

        } else if (binding.mobileNumber.text.toString().trim()[0].toString() == "0") {
            binding.errorOnEmail.visibility = View.GONE
            binding.errorOnBusinessName.visibility = View.GONE
            binding.errorOnMobile.visibility = View.VISIBLE
            binding.errorOnMobile.text = getString(R.string.mobile_not_start_with_0)
            profileViewModel.methodRepo.setBackGround(
                requireActivity(),
                binding.emailLay,
                R.drawable.btn_outline_gray
            )
            profileViewModel.methodRepo.setBackGround(
                requireActivity(),
                binding.businessLay,
                R.drawable.btn_outline_gray
            )
            profileViewModel.methodRepo.setBackGround(
                requireActivity(),
                binding.mobileLay,
                R.drawable.btn_search_outline
            )


        } else if (binding.shopAddress.text.toString().trim().isEmpty()) {
            binding.shopAddress.error = getString(R.string.shop_address_empty)
            binding.shopAddress.requestFocus()

        } else if (binding.shopAddress.text.toString().trim().length < 3) {
            binding.shopAddress.error = getString(R.string.error_shop_address)
            binding.shopAddress.requestFocus()

        } else if (binding.address.text.toString().trim().isEmpty()) {
            binding.address.error = getString(R.string.address_empty)
            binding.address.requestFocus()

        } else if (binding.address.text.toString().trim().length < 3) {
            binding.address.error = getString(R.string.address_error)
            binding.address.requestFocus()

        } else if (binding.shopAct.text.toString().trim().isEmpty()) {
            binding.shopAct.error = getString(R.string.shop_act_empty)
            binding.shopAct.requestFocus()
        } else if (binding.shopAct.text.toString().trim().length < 3) {
            binding.shopAct.error = getString(R.string.shop_Act_length)
            binding.shopAct.requestFocus()
        }
        else if(!isRerunFee){
            binding.returnedt.error = getString(R.string.error_percentage_numeric)
            binding.returnedt.requestFocus()
        }
        else if(!isCancelFee){
            binding.cancellation.error = getString(R.string.error_percentage_numeric)
            binding.cancellation.requestFocus()
        }
        else {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val email = binding.emailEdit.text.toString().trim()
        val contactNumber = binding.mobileNumber.text.toString().trim()
//        val ext = binding.profileCcp.selectedCountryCodeWithPlus
        val shopAddress = binding.shopAddress.text.toString().trim()
        val fullAddress = binding.address.text.toString().trim()
        val businessName = binding.businessName.text.toString().trim()
        val licenceNumber = binding.shopAct.text.toString().trim()
        val returnFee = binding.returnedt.text.toString().trim()
        val cancellationFee = binding.cancellation.text.toString().trim()
        val returnDays = binding.returnedD.text.toString().trim()
        val merchantSettingsDTOList= mutableListOf<MerchantSettingsDTO>()


        profileViewModel.merchantSettingsDTOList.forEach {
            if(it.paramName == "return-fee"){
                it.paramValue=returnFee
                merchantSettingsDTOList.add(it)
            }
            else if(it.paramName == "cancellation-fee"){
                it.paramValue=cancellationFee
                merchantSettingsDTOList.add(it)
            }

            else if(it.paramName == "return-days"){
                it.paramValue=returnDays
                merchantSettingsDTOList.add(it)
            }

            else if(it.paramName == "admin-commission"){
                merchantSettingsDTOList.add(it)
            }
        }

        profileViewModel.saveProfile(email, shopAddress, fullAddress, businessName, licenceNumber,merchantSettingsDTOList)
    }

    private fun getProfile() {
        profileViewModel.getProfile()
    }

    private fun clickListeners() {
        binding.btnSaveProfile.setOnClickListener {
            validate()
        }
        binding.adminCommissionLay.setOnClickListener {
            showCustomToast("Your are not allowed to change admin commission")
        }

        binding.imgCamelMDPRDet.setOnClickListener {
            if (!profileViewModel.methodRepo.checkPermission(
                     requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                permReqLauncher.launch(PERMISSIONS)
            } else {
                fetchImage()
            }
        }
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission) {
                fetchImage()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), PERMISSIONS)) {
                    showCustomToast("Permission Denied, Go to setting to give access")
                } else {
                    showCustomToast("Permission Denied")
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
            File(requireActivity().cacheDir, queryName(requireActivity().contentResolver, sourceUri))
        )
        val options: UCrop.Options = UCrop.Options();
        options.setCompressionQuality(80);
        options.setToolbarColor(ContextCompat.getColor(requireActivity(), R.color.black));
        options.setStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.black));
        options.setToolbarWidgetColor(ContextCompat.getColor(requireActivity(), R.color.white));
        options.withAspectRatio(1f, 1f);
        val uCrop = UCrop.of(sourceUri, destinationUri).withOptions(options)
        val intent = uCrop.getIntent(requireActivity())
        croporResult.launch(intent)
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
    val croporResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val resultUri = UCrop.getOutput(data)
                    prodImage = resultUri?.toFile()
                    Glide.with(this).load(resultUri).error(R.drawable.demo).into(binding.imgCamelMDPRDet)
//                val bitmap = (mBinding.profilePic.getDrawable() as BitmapDrawable).bitmap
//                val file = UtilityHelper.createFile(this, bitmap)

                }
            }
        }
}