package com.actorpay.merchant.ui.requestMoney

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentRequestMoneyBinding
import com.actorpay.merchant.ui.requestMoney.adapter.RequestMoneyAdapter
import com.actorpay.merchant.utils.OnFilterClick


class RequestMoneyFragment : BaseFragment(),OnFilterClick {
    private val CONTACT_PICKER_RESULT = 1001
    private lateinit var binding: FragmentRequestMoneyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRequestMoneyBinding.inflate(inflater, container, false)
        val root: View = binding.root
        onFilterClick(this)
        setupRv()

        binding.emailNumberField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return@setOnEditorActionListener true;
            }
            return@setOnEditorActionListener false;
        }

        binding.ivContact.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(i, CONTACT_PICKER_RESULT)

        }

        return root
    }
    private fun setupRv() {
        binding.rvRequestMoney.layoutManager=LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.rvRequestMoney.adapter=RequestMoneyAdapter()
    }

    override fun onClick() {
        RequestFilterDialog(requireActivity()).show()
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