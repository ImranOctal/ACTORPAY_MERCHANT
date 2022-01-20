package com.actorpay.merchant.utils

import android.os.SystemClock
import android.util.Log
import android.view.View

abstract class SingleClickListener @JvmOverloads constructor(protected var defaultInterval: Int = 5000) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            Log.d("System Click ", "onClick: in")
            return
        }
        Log.d("System Click ", "onClick: out")
        lastTimeClicked = SystemClock.elapsedRealtime()
        performClick(v)
    }

    abstract fun performClick(v: View?)
}