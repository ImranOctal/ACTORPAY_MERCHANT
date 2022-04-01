package com.actorpay.merchant.ui.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.actorpay.merchant.R
import com.actorpay.merchant.databinding.FragmentContactUsBinding


class ContactUsFragment : Fragment() {

    private lateinit var binding: FragmentContactUsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_contact_us,container,false)


        binding.contactEmailLayout.setOnClickListener {
            val mailto = "mailto:actorpay@gmail.com"
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Experience with the Actor Pay App")
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            emailIntent.data = Uri.parse(mailto)
            startActivity(emailIntent)
        }
        binding.contactCallLayout.setOnClickListener {
            val u = Uri.parse("tel:+919876543210")
            val i = Intent(Intent.ACTION_DIAL, u)
            startActivity(i)
        }

        return binding.root
    }


}