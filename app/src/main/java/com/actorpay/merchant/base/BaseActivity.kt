package com.actorpay.merchant.base


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.actorpay.merchant.R
import com.actorpay.merchant.database.prefrence.SharedPre
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.actorpay.merchant.viewmodel.ActorPayViewModel
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {
     val viewModel: ActorPayViewModel by  inject()
     val sharedPre: SharedPre by  inject()
     val methods by inject<MethodsRepo>()
    val CLICK_TIME = 1000L
    private lateinit var snackBar: Snackbar
    private var  progressDialog: Dialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
    }
    fun baseContext():Context{
        return this@BaseActivity
    }



    fun setFragment(resourceView: Int, fragment: Fragment, addToBackStackFlag: Boolean) {
        //
        val fragmentManager: FragmentManager = supportFragmentManager
        val mFragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val currentFragment = fragmentManager.findFragmentById(resourceView)

        if (currentFragment == null) {
            if (fragmentManager.fragments.size != 0) {
                if (addToBackStackFlag) {
                    mFragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
                }

                mFragmentTransaction.replace(resourceView, fragment)
                mFragmentTransaction.commit()
            }
        } else {
            if (!(currentFragment.javaClass.simpleName.equals(fragment.javaClass.simpleName))) {
                if (fragmentManager.fragments.size != 0) {
                    if (addToBackStackFlag) {
                        mFragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
                    }
                    mFragmentTransaction.replace(resourceView, fragment)
                    mFragmentTransaction.commit()
                }
            }

        }
    }


    fun switchActivity( intent: Intent){
        startActivity(intent)
    }

    open fun showCustomAlert(
        msg: String?,
        v: View?,
        button: String?,
        isRetryOptionAvailable: Boolean,
        listener: RetrySnackBarClickListener
    ) {
        snackBar = if (isRetryOptionAvailable) {
            Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
                .setAction(button) { listener.onClickRetry() }
        } else {
            Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        }
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.view
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    open fun showCustomAlert(msg: String?, v: View?) {
        snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.view
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }
    fun logOut(){
        CommonDialogsUtils.showCommonDialog(this,viewModel.methodRepo, getString(R.string.log_out),
            getString(R.string.are_you_sure),
            autoCancelable = true,
            isCancelAvailable = true,
            isOKAvailable = true,
            showClickable = false,
            callback = object : CommonDialogsUtils.DialogClick {
                override fun onClick() {
                    lifecycleScope.launchWhenCreated {
                        delay(2000)
                        viewModel.methodRepo.dataStore.logOut()
                        startActivity(Intent(this@BaseActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                }

                override fun onCancel() {
                }
            })
    }
    fun logOutDirect(){
        lifecycleScope.launchWhenCreated {
            delay(2000)
            viewModel.methodRepo.dataStore.logOut()
            startActivity(Intent(this@BaseActivity, LoginActivity::class.java))
            finishAffinity()
        }
    }
    fun showCustomToast(msg: String) {
        val myToast = Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        )
        myToast.setGravity(Gravity.BOTTOM, 0, 0)
        myToast.show()
    }

    protected fun showSnackBar(message: String?) {
        Snackbar.make(view, message!!, Snackbar.LENGTH_SHORT).show()
    }

    private val view: View
        get() = findViewById(R.id.content)

    fun showLoadingDialog() {
        if(progressDialog==null){
            progressDialog = Dialog(this)
            if (progressDialog!!.window != null) {
                val window = progressDialog!!.window
                window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                window.setGravity(Gravity.CENTER)
                progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            progressDialog!!.setContentView(R.layout.progress_dialog)
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
        }
        else{
            progressDialog!!.show()
        }

    }
    fun hideLoadingDialog() {
        if(progressDialog!=null){
            progressDialog!!.dismiss()
        }
    }

}