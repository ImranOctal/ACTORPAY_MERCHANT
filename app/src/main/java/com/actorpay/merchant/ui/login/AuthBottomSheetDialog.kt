package com.actorpay.merchant.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.actorpay.merchant.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



/**
 * Created by Arun on 07-10-2021
 */
class AuthBottomSheetDialog(val callback:()-> Unit): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.layout_auth_verificaition, container, false)
        val button = v.findViewById<Button>(R.id.btn_auth)
//        val title = v.findViewById<TextView>(R.id.title)
//        val subtitle = v.findViewById<TextView>(R.id.subtitle)

//        title.text =  "Sign in with your biometric?"
//        subtitle.text =  "Use your biometric to authenticate with Blytics."

        button.setOnClickListener {
            callback()
        }
        return v
    }

}