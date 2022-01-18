package com.actorpay.merchant.ui.more

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.ActivityMoreBinding
import com.actorpay.merchant.ui.content.ContentActivity
import com.actorpay.merchant.ui.content.ContentViewModel
import org.koin.android.ext.android.inject

class MoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_more)
        clickListners()
        binding.toolbar.ToolbarTitle.text = getString(R.string.more)
        binding.toolbar.back.setOnClickListener {
            onBackPressed()
        }
    }
    private fun clickListners() {
        binding.apply {
            aboutUsText.setOnClickListener {
                ContentViewModel.type=1
                startActivity(Intent(this@MoreActivity, ContentActivity::class.java))
            }
            privacyText.setOnClickListener {
                ContentViewModel.type=2
                startActivity(Intent(this@MoreActivity, ContentActivity::class.java))
            }
            tcText.setOnClickListener {
                ContentViewModel.type=3
                startActivity(Intent(this@MoreActivity, ContentActivity::class.java))
            }
            faqText.setOnClickListener {
                startActivity(Intent(this@MoreActivity, FaqActivity::class.java))
            }

        }
    }
}