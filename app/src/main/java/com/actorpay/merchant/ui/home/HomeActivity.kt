package com.actorpay.merchant.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityHomeBinding
import com.actorpay.merchant.repositories.AppConstance.AppConstanceData
import com.actorpay.merchant.repositories.AppConstance.AppConstanceData.PRODUCT_ID
import com.actorpay.merchant.repositories.retrofitrepository.models.SuccessResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.GetProductListResponse
import com.actorpay.merchant.repositories.retrofitrepository.models.products.getProductList.Item
import com.actorpay.merchant.ui.profile.ProfileActivity
import com.actorpay.merchant.ui.addnewproduct.AddNewProduct
import com.actorpay.merchant.ui.home.adapter.ManageProductAdapter
import com.actorpay.merchant.ui.home.models.sealedclass.HomeSealedClasses
import com.actorpay.merchant.ui.login.LoginActivity
import com.actorpay.merchant.ui.manageOrder.ManageOrderActivity
import com.actorpay.merchant.ui.payroll.PayRollActivity
import com.actorpay.merchant.ui.subAdmin.CreateSubAdminActivity
import com.actorpay.merchant.ui.updateproduct.UpdateProduct
import com.actorpay.merchant.utils.CommonDialogsUtils
import com.octal.actorpay.repositories.AppConstance.AppConstance
import com.techno.taskmanagement.utils.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.ext.android.inject
import java.util.ArrayList
import android.text.Editable

import android.text.TextWatcher
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.actorpay.merchant.repositories.retrofitrepository.models.products.deleteProduct.DeleteProductResponse


class HomeActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ManageProductAdapter
    private var doubleBackToExitPressedOnce = false
    private val homeviewmodel: HomeViewModel by inject()
    private var productListData = ArrayList<Item>()
    private lateinit var data: JSONObject
    private var handler: Handler? = null
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        handler = Handler()
        searchRunnable = Runnable {

        }
        initialisation()
    }

    private fun initialisation() {
        binding.swipeLoad.setOnRefreshListener(this)
        adapter = ManageProductAdapter(this) { position: Int, data: String ->
            when (data) {
                AppConstanceData.EDIT -> switchActivity(
                    Intent(
                        baseContext(),
                        UpdateProduct::class.java
                    ).putExtra(PRODUCT_ID, productListData.get(position).productId)
                )

                AppConstanceData.DELETE -> homeviewmodel.deleteProduct(productListData.get(position).productId)

                AppConstanceData.ROOT -> {

                }
            }
        }
        binding.manageProduct.adapter = adapter
        homeviewmodel.getProductList("0", data = JSONObject())
        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 2) {
                    searchRunnable = Runnable {
                        data = JSONObject()
                        data.put("name", s.toString())
                        homeviewmodel.getProductList("0", data = data)
                    }
                    handler!!.removeCallbacks(searchRunnable!!)
                    handler!!.postDelayed(searchRunnable!!, 1000)
                }
            }
        })
        val endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener =
            object : EndlessRecyclerViewScrollListener(LinearLayoutManager(this)) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    homeviewmodel.getProductList(page.toString(), data = JSONObject())
                }
            }
        binding.manageProduct.addOnScrollListener(endlessRecyclerViewScrollListener)
        adapter.UpdateList(productListData)
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hamburger))
        binding.toolbar.ToolbarTitle.text = getString(R.string.manage_product)
        clickListeners()
        WorkSource()
        lifecycleScope.launchWhenCreated {

            homeviewmodel.methodRepo.dataStore.getBussinessName().collect { businessName ->
                binding.headerTitle.userProfileName.text = "$businessName"
            }
        }
    }

    private fun clickListeners() {
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.toolbar.back.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START, true)
            }
        }
        binding.AddNewProductButton.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), AddNewProduct::class.java))
        }
        binding.profileLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ProfileActivity::class.java))
        }
        binding.myOrderLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), ManageOrderActivity::class.java))
        }
        binding.reportsLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), PayRollActivity::class.java))
        }
        binding.merchatLay.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawers()
            }
            switchActivity(Intent(baseContext(), CreateSubAdminActivity::class.java))
        }
        binding.changePasswordLay.setOnClickListener {
            changePasswordUi()
        }
        binding.footer.btnHomeLogout.setOnClickListener {
            logOut()
        }
    }

    fun changePasswordUi() {
        ChangePasswordDialog().show(this, homeviewmodel.methodRepo) { oldPassword, newPassword ->
            homeviewmodel.changePassword(oldPassword, newPassword)
        }
    }


    fun WorkSource() {
        lifecycleScope.launchWhenStarted {
            homeviewmodel.homeResponseLive.collect {
                when (it) {
                    is HomeSealedClasses.Companion.ResponseHomeSealed.loading -> {
                        homeviewmodel.methodRepo.showLoadingDialog(this@HomeActivity)
                    }
                    is HomeSealedClasses.Companion.ResponseHomeSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (it.response is SuccessResponse) {
                            CommonDialogsUtils.showCommonDialog(
                                this@HomeActivity,
                                homeviewmodel.methodRepo,
                                "Signed Up",
                                it.response.message,
                                autoCancelable = false,
                                isCancelAvailable = false,
                                isOKAvailable = true,
                                showClickable = false,
                                callback = object : CommonDialogsUtils.DialogClick {
                                    override fun onClick() {
                                        startActivity(
                                            Intent(
                                                this@HomeActivity,
                                                LoginActivity::class.java
                                            )
                                        )
                                        finishAffinity()
                                    }

                                    override fun onCancel() {

                                    }
                                }
                            )
                        } else showCustomAlert(
                            getString(R.string.please_try_after_sometime),
                            binding.root
                        )


                    }
                    is HomeSealedClasses.Companion.ResponseHomeSealed.ErrorOnResponse -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.failResponse!!.message,
                            binding.root
                        )
                    }
                    else -> homeviewmodel.methodRepo.hideLoadingDialog()

                }

            }
        }
        lifecycleScope.launchWhenStarted {
            homeviewmodel.productListLive.collect { action ->
                when (action) {
                    is HomeSealedClasses.Companion.ResponseProductListSealed.loading -> {
                        binding.emptyText.visibility = View.VISIBLE

                        homeviewmodel.methodRepo.showLoadingDialog(this@HomeActivity)
                    }

                    is HomeSealedClasses.Companion.ResponseProductListSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        binding.swipeLoad.isRefreshing = false
                        if (action.response is GetProductListResponse) {
                            binding.emptyText.visibility = View.GONE
                            adapter.UpdateList(action.response.data.items)
                            productListData = action.response.data.items
                        } else {
                            binding.emptyText.visibility = View.VISIBLE
                            showCustomAlert(getString(R.string.please_try_after_sometime), binding.root)
                        }
                    }
                    is HomeSealedClasses.Companion.ResponseProductListSealed.ErrorOnResponse -> {
                        binding.emptyText.visibility = View.VISIBLE
                        binding.swipeLoad.isRefreshing = false
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        showCustomAlert(action.failResponse!!.message, binding.root)
                        if (action.failResponse.message.contains("End of input at")) {
                            logOutDirect()
                        }
                    }
                    else -> homeviewmodel.methodRepo.hideLoadingDialog()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            homeviewmodel.deleteproductLive.collect { action ->
                when (action) {
                    is HomeSealedClasses.Companion.ResponseDeleteSealed.loading -> {
                        binding.emptyText.visibility = View.VISIBLE
                        homeviewmodel.methodRepo.showLoadingDialog(this@HomeActivity)
                    }

                    is HomeSealedClasses.Companion.ResponseDeleteSealed.Success -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        if (action.response is DeleteProductResponse) {
                            homeviewmodel.getProductList("0", data = JSONObject())
                        } else {
                            binding.emptyText.visibility = View.VISIBLE
                            showCustomAlert(getString(R.string.please_try_after_sometime), binding.root)
                        }
                    }
                    is HomeSealedClasses.Companion.ResponseDeleteSealed.ErrorOnResponse -> {
                        homeviewmodel.methodRepo.hideLoadingDialog()
                        showCustomAlert(action.failResponse!!.message, binding.root)
                        if (action.failResponse.message.contains("End of input at")) {
                            logOutDirect()
                        }
                    }
                    else -> homeviewmodel.methodRepo.hideLoadingDialog()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }
        doubleBackToExitPressedOnce = true
        showCustomAlert(getString(R.string.press_back_again), binding.root)
        //Toast.makeText(this, "Press back again", Toast.LENGTH_SHORT)
        lifecycleScope.launch(Dispatchers.Default) {
            delay(2000)
            doubleBackToExitPressedOnce = false
        }
    }

    override fun onRefresh() {
        binding.swipeLoad.isRefreshing=true
        homeviewmodel.getProductList("0", data = JSONObject())
    }


}