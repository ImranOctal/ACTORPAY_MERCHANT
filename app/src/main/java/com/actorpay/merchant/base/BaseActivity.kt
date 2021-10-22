package com.actorpay.merchant.base


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.actorpay.merchant.R
import com.actorpay.merchant.repositories.methods.MethodsRepo
import com.actorpay.merchant.viewmodel.ActorPayViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.regex.Pattern

abstract class BaseActivity : AppCompatActivity() {
     val viewModel: ActorPayViewModel by  inject()
     val methods by inject<MethodsRepo>()
    val CLICK_TIME = 1000L
    private var doubleBackToExitPressedOnce = false
    private lateinit var snackBar: Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = this.resources.getColor(R.color.primary)
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

    /**
     * override onBackPressed
     *
     */
/*    @Override
    override fun onBackPressed() {
       *//* val fm = supportFragmentManager
        val backStackCount = fm.backStackEntryCount

        if (backStackCount == 0) {
            finish()
        } else if (backStackCount > 1) {
            val backStackEntry = fm.getBackStackEntryAt(backStackCount - 1)
            val frag = fm.findFragmentByTag(backStackEntry.name)

            if (frag!!.childFragmentManager.backStackEntryCount > 1) {
                frag.childFragmentManager.popBackStack()
            } else {
                fm.popBackStack()
            }

        } else {
            val backStackEntry = fm.getBackStackEntryAt(fm.backStackEntryCount - 1)
            val frag = fm.findFragmentByTag(backStackEntry.name)
            if (frag!!.childFragmentManager.backStackEntryCount > 1) {
                frag.childFragmentManager.popBackStack()
            } else {
                finish()
            }
        }*//*
     onBackPressed()
    }*/

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
        if (isRetryOptionAvailable) {
            snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
                .setAction(button) { listener.onClickRetry() }
        } else {
            snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        }
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.getView()
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }

    open fun showCustomAlert(msg: String?, v: View?) {
        snackBar = Snackbar.make(v!!, msg!!, Snackbar.LENGTH_LONG)
        snackBar.setActionTextColor(Color.BLUE)
        val snackBarView: View = snackBar.getView()
        val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackBar.show()
    }


    protected fun showSnackBar(message: String?) {
        Snackbar.make(view, message!!, Snackbar.LENGTH_SHORT).show()
    }

    private val view: View
        private get() = findViewById(R.id.content)


}