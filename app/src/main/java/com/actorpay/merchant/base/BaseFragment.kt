package com.actorpay.merchant.base

import android.view.View
import androidx.fragment.app.Fragment
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.ui.home.HomeActivity
import com.actorpay.merchant.utils.OnFilterClick
import com.actorpay.merchant.viewmodel.ActorPayViewModel
import org.koin.android.ext.android.inject

open class BaseFragment : Fragment() {

    val methods by inject<MethodsRepo>()
    val viewModel: ActorPayViewModel by inject()
    fun showLoadingDialog() {
        (requireActivity() as BaseActivity).showLoadingDialog()

    }

    fun hideLoadingDialog() {
        (requireActivity() as BaseActivity).hideLoadingDialog()

    }

    fun forcelogout(methodRepo: MethodsRepo) {

        (requireActivity() as BaseActivity).forcelogout(methodRepo)
    }


    fun showCustomToast(msg: String) {
        (requireActivity() as BaseActivity).showCustomToast(msg)

    }


    fun showCustomAlert(msg: String?, v: View?) {
        (requireActivity() as BaseActivity).showCustomAlert(msg, v)

    }

    fun onFilterClick(filterClick: OnFilterClick) {
        (requireActivity() as HomeActivity).onFilterClick(filterClick)
    }

    fun updateToolbarText(title: String) {
        (requireActivity() as HomeActivity).updateToolbarText(title)
    }

    fun getWalletBalance(balance: String) {
        (requireActivity() as HomeActivity).getWalletBalance(balance)
    }
    fun getUserName(name: String) {
        (requireActivity() as HomeActivity).getUserName(name)
    }


}