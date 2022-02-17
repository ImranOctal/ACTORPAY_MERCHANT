package com.actorpay.merchant.repositories.methods
/*
* © Copyright Ishant Sharma
* Android Developer
* JAVA/KOTLIN
* */

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.actorpay.merchant.database.datastore.DataStoreBase
import com.actorpay.merchant.ui.home.HomeActivity
import com.octal.actorpay.repositories.AppConstance.AppConstance
import java.io.UnsupportedEncodingException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class MethodsRepo(private  var context: Context,  var dataStore: DataStoreBase
) {
    private var  progressDialog:Dialog?=null

    fun isValidEmail(email: String): Boolean {
        val EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches()
    }

    fun isValidPhoneNumber(phone: String): Boolean {
        val mobilePattern = "[0-9]{10}"
        return Pattern.matches(mobilePattern, phone)
    }

    fun isValidName(name: String):Boolean{
        val pattern = Pattern.compile("^[a-zA-Z\\s]*$")
        val matcher: Matcher = pattern.matcher(name)
        return matcher.matches()
    }
    fun isValidPassword(password: String):Boolean{
        val pattern = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$")
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }
     fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
         val network = cm!!.activeNetwork
         val capabilities = cm.getNetworkCapabilities(network)
         return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
             NetworkCapabilities.TRANSPORT_CELLULAR
         ))
    }
    fun getFormattedDate(context: Context?, smsTimeInMilis: Long): String? {
        val formatter = SimpleDateFormat("yyyy-MM-dd , h:mm aa", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = smsTimeInMilis
        return formatter.format(calendar.time)
    }
    fun hideSoftKeypad(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
    fun getDeviceWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
    fun getDeviceHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun getFormattedDate(context: Context?, smsTimeInMilis: Long, Format: String?): String? {
        val formatter = SimpleDateFormat(Format,Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = smsTimeInMilis
        return formatter.format(calendar.time)
    }

    fun ConverMillsTo(mills: Long, youFormat: String?): String? {
        val date = Date(mills)
        val formatter: DateFormat = SimpleDateFormat(youFormat,Locale.ENGLISH)
        return formatter.format(date)
    }

    fun getMillsFromDateandTime(dateOrTime: String, format: String?): Long {
        val sdf = SimpleDateFormat(format,Locale.ENGLISH)
        return try {
          val date = sdf.parse(dateOrTime)
            date!!.time
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }
    fun Base64Encode(text: String): String? {
        var encrpt = ByteArray(0)
        try {
            encrpt = text.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return Base64.encodeToString(encrpt, Base64.DEFAULT)
    }

    fun Base64Decode(base64: String?): String? {
        val decrypt = Base64.decode(base64, Base64.DEFAULT)
        var text: String? = null
        try {
            text = String(decrypt, charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return text
    }
    fun setBackGround(context: Context?, view: View, @DrawableRes drawable: Int) {
        view.setBackground(ContextCompat.getDrawable(context!!, drawable))
    }

    fun makeTextLink(textView: TextView, str: String, underlined: Boolean, color: Int?, action: (() -> Unit)? = null) {
        val spannableString = SpannableString(textView.text)
        val textColor = color ?: textView.currentTextColor
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                action?.invoke()
            }
            override fun updateDrawState(drawState: TextPaint) {
                super.updateDrawState(drawState)
                drawState.isUnderlineText = underlined
                drawState.color = textColor
            }
        }
        val index = spannableString.indexOf(str)
        spannableString.setSpan(clickableSpan, index, index + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }

    fun checkPermission(activity: Activity,permission:String):Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) != PackageManager.PERMISSION_DENIED
    }

    fun getFormattedOrderDate(orderDate: String): String? {
        try {
            return  AppConstance.dateFormate4.format(AppConstance.dateFormate3.parse(orderDate)!!)
        }
        catch (e : Exception){
            return orderDate
        }
    }
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
    fun Activity.transparentStatusBar() {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        this.window.statusBarColor = Color.TRANSPARENT
    }

    private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun Activity.setStatusBarTransparent(color: String, isDark: Boolean) {
        this.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (isDark) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.statusBarColor = Color.parseColor(color)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.parseColor(color)
        }
    }

}