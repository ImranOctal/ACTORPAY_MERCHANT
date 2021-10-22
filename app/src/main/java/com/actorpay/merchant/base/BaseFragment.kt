package com.actorpay.merchant.base

import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.actorpay.merchant.R

 open class BaseFragment : Fragment() {
    fun showCustomToast(msg: String) {
        val myToast = Toast.makeText(
            activity,
            msg,
            Toast.LENGTH_SHORT
        )
        myToast.setGravity(Gravity.CENTER, 0, 0)
        myToast.show()
    }


    fun showMsg(connected: Boolean) {
        if (connected == false) {
            val myToast = Toast.makeText(
                requireContext(),
                getString(R.string.not_connected_internet),
                Toast.LENGTH_SHORT
            )
            myToast.setGravity(Gravity.CENTER, 0, 0)
            myToast.show()
            //showAlertBar(getString(R.string.not_connected_internet))
        }
    }
}