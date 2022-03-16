package com.actorpay.merchant.ui.sendMoney

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentTransferMoneyBinding
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.number


class TransferMoneyFragment : BaseFragment() {
    private lateinit var binding: FragmentTransferMoneyBinding
    private var permissions = Manifest.permission.CAMERA
    lateinit var codeScanner: CodeScanner
    private val CONTACT_PICKER_RESULT = 1001
    private val RESULT_OK = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transfer_money, container, false)
        init()
        codeScanner = CodeScanner(requireContext(), binding.codeScannerView)
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                binding.scan.visibility = View.VISIBLE
                codeScanner.stopPreview()
                showCustomToast("Scan result: ${it.text}")

            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            requireActivity().runOnUiThread {
                showCustomToast("Camera initialization error: ${it.message}")
            }
        }

        binding.ivContact.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(i, CONTACT_PICKER_RESULT)
        }


        binding.scan.setOnClickListener {
            if (!viewModel.methodRepo.checkPermission(requireActivity(), permissions)) {
                permReqLauncher.launch(permissions)
            } else {
                binding.scan.visibility = View.GONE
                codeScanner.startPreview()
            }
        }


        binding.emailNumberField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Navigation.findNavController(requireView()).navigate(R.id.payFragment)
                return@setOnEditorActionListener true;
            }
            return@setOnEditorActionListener false;
        }
        return binding.root
    }


    private val permReqLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission) {
                binding.scan.visibility = View.GONE
                codeScanner.startPreview()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), permissions
                    )
                ) {
                    Toast.makeText(
                        requireContext(), "Permission Denied, Go to setting to give access",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }

            }
        }

    fun init() {
        makeSelected(0)
        binding.walletToWalletBtn.setOnClickListener {
            makeSelected(0)
        }
        binding.walletToBankBtn.setOnClickListener {
            makeSelected(1)
        }
    }

    private fun makeSelected(i: Int) {
        if(i==0){
            binding.walletToWalletBtn.setBackgroundResource(R.drawable.round_wallet_bg)
            binding.walletToBankBtn.setBackgroundResource(R.drawable.round_wallet_blue_bg)
            binding.layoutScanQr.visibility = View.VISIBLE
            binding.layoutBank.visibility = View.GONE

        }else{
            binding.walletToWalletBtn.setBackgroundResource(R.drawable.round_wallet_blue_bg)
            binding.walletToBankBtn.setBackgroundResource(R.drawable.round_wallet_bg)
            binding.layoutScanQr.visibility = View.GONE
            binding.layoutBank.visibility = View.VISIBLE
        }
    }


    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CONTACT_PICKER_RESULT -> if (resultCode == Activity.RESULT_OK) {
                val uri: Uri?
                val cursor1: Cursor
                val cursor2: Cursor
                var TempNumberHolder: String?
                val TempContactID: String
                var IDresult: String? = ""
                val IDresultHolder: Int
                uri = data!!.data
                cursor1 = requireActivity().contentResolver.query(uri!!, null, null, null, null)!!
                if (cursor1.moveToFirst()) {
                    TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID))
                    IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    IDresultHolder = Integer.valueOf(IDresult)
                    if (IDresultHolder == 1) {
                        cursor2 = requireActivity().contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null)!!
                        while (cursor2.moveToNext()) {
                            TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            binding.emailNumberField.setText(TempNumberHolder)
                        }
                    }
                }
            }
        }

    }
}