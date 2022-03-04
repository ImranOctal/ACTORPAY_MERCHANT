package com.actorpay.merchant.ui.more

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.actorpay.merchant.R

import com.actorpay.merchant.databinding.FragmentMoreBinding
import com.actorpay.merchant.ui.content.ContentActivity
import com.actorpay.merchant.ui.content.ContentViewModel

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_more,container,false)
        clickListners()
        return binding.root
    }
    private fun clickListners() {
        binding.apply {
            aboutUsText.setOnClickListener {
                ContentViewModel.type=1
                startActivity(Intent(requireActivity(), ContentActivity::class.java))
            }
            privacyText.setOnClickListener {
                ContentViewModel.type=2
                startActivity(Intent(requireActivity(), ContentActivity::class.java))
            }
            tcText.setOnClickListener {
                ContentViewModel.type=3
                startActivity(Intent(requireActivity(), ContentActivity::class.java))
            }
            faqText.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(R.id.faqFragment)
            }

        }
    }
}