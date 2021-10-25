package com.actorpay.merchant.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityHomeBinding
import com.actorpay.merchant.ui.addnewproduct.AddNewProduct
import com.actorpay.merchant.ui.home.adapter.ManageProductAdapter
import com.actorpay.merchant.ui.manageOrder.ManageOrderActivity
import com.actorpay.merchant.ui.payroll.PayRollActivity
import com.actorpay.merchant.ui.subAdmin.CreateSubAdminActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList

class HomeActivity : BaseActivity(){
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ManageProductAdapter
    private var doubleBackToExitPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_home)
        initiliation()
    }



    private fun initiliation() {
        adapter = ManageProductAdapter(this)
        adapter.UpdateList(ArrayList<String>())
        binding.manageProduct.adapter = adapter
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.back.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.hamburger))
        binding.toolbar.ToolbarTitle.text = getString(R.string.manage_product)
        ClickListners()

    }
    private fun ClickListners() {
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.back.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
                binding.drawerLayout.closeDrawers()
            }else{
                binding.drawerLayout.openDrawer(Gravity.LEFT,true)
            }
        }
        binding.AddNewProductButton.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), AddNewProduct::class.java))
        }
        binding.profileLay.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageOrderActivity::class.java))
        }
        binding.myOrderLay.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageOrderActivity::class.java))
        }

        binding.reportsLay.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), PayRollActivity::class.java))
        }
        binding.merchatLay.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), CreateSubAdminActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }
        doubleBackToExitPressedOnce = true
        showCustomAlert("Press back again",binding.root);
        //Toast.makeText(this, "Press back again", Toast.LENGTH_SHORT)
        lifecycleScope.launch(Dispatchers.Default) {
            delay(2000)
            doubleBackToExitPressedOnce = false
        }
    }
}