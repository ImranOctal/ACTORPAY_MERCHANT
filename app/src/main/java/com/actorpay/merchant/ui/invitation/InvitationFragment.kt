package com.actorpay.merchant.ui.invitation
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.actorpay.merchant.R
import com.actorpay.merchant.base.BaseFragment
import com.actorpay.merchant.databinding.FragmentInvitationBinding

class InvitationFragment : BaseFragment() {

    lateinit var  binding:FragmentInvitationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_invitation,container,false)
        binding.btnSaveID.setOnClickListener {
            shareLink()
        }
        return  binding.root
    }

    private fun shareLink() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        share.putExtra(Intent.EXTRA_SUBJECT, "ActorPay Merchant")
        share.putExtra(Intent.EXTRA_TEXT, "Download ActorPayMerchant App and earn rewards \n http://ActorPayMerchant"+ "MARTIN MROC 0085")
        startActivity(Intent.createChooser(share, "Share link!"))
    }
}
