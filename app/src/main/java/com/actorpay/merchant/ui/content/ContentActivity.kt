package com.actorpay.merchant.ui.content

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseActivity
import com.actorpay.merchant.databinding.ActivityContentBinding
import com.octal.actorpay.repositories.retrofitrepository.models.content.ContentResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ContentActivity : BaseActivity() {

    private val contentViewModel: ContentViewModel by inject()
    private lateinit var binding: ActivityContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_content)


        apiResponse()
        installation()

    }


    private fun installation() {
        contentViewModel.getContent(ContentViewModel.type.toString())
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun apiResponse() {
        lifecycleScope.launch {
            contentViewModel.contentResponseLive.collect{
                when(it){
                    is ContentViewModel.ResponseContentSealed.loading->{
                        contentViewModel.methodRepo.showLoadingDialog(this@ContentActivity)
                    }
                    is ContentViewModel.ResponseContentSealed.Success -> {
                        contentViewModel.methodRepo.hideLoadingDialog()
                        if (it.response is ContentResponse) {
                            binding.toolbar.ToolbarTitle.text=it.response.data.title
                            binding.contentWebview.loadDataWithBaseURL("",it.response.data.contents,"text/html","UTF-8","")

                        }
                        else {
                            showCustomAlert(
                                getString(R.string.please_try_after_sometime),
                                binding.root
                            )
                        }

                    }
                    is ContentViewModel.ResponseContentSealed.ErrorOnResponse -> {
                        contentViewModel.methodRepo.hideLoadingDialog()
                        showCustomAlert(
                            it.message!!.message,
                            binding.root
                        )
                    }
                    else -> {
                        contentViewModel.methodRepo.hideLoadingDialog()
                    }
                }

            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}