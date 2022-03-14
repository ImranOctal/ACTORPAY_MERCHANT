package com.actorpay.merchant.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()
fun View.roundBorderedView(cornerRadius: Int, backgroundColor : String, borderColor: String, borderWidth : Int) {
    addOnLayoutChangeListener(object: View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            val shape = GradientDrawable()
            shape.cornerRadius = cornerRadius.toFloat()//measuredHeight / 2f
            shape.setColor(Color.parseColor(backgroundColor))
            shape.setStroke(borderWidth, Color.parseColor(borderColor))
            background = shape
            removeOnLayoutChangeListener(this)
        }
    })
}

