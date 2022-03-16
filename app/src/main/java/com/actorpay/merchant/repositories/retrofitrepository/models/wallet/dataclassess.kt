package ccom.actorpay.merchant.repositories.retrofitrepository.models.wallet

import java.io.Serializable


data class AddMoneyResponse(
    var message: String,
    var data: AddMoneyData,
    var status: String,
    var httpStatus: String
)

data class AddMoneyData(
    var walletBalance:Double,
    var transferredAmount:Double,
    var customerName:String,
    var transactionType:String,
    var transactionId:String
)



data class AddMoneyParams(
    var amount:String?
)

data class TransferMoneyParams(
    var userIdentity:String,
    var amount:String,
    var transactionRemark:String,
    var beneficiaryUserType:String="merchant"
)

data class WallletMoneyParams(
    var walletTransactionId:String="",
    var transactionAmountTo:String="",
    var transactionAmountFrom:String="",
    var transactionRemark:String="",
    var startDate: String = "",
    var endDate: String = "",
    var toUser: String = "",
    var transactionTypes: String? = null,
    var purchaseType: String? = null,
)


data class WalletHistoryResponse(

    var message: String,
    var data: WalletListData,
    var status: String,
    var httpStatus: String

)


data class WalletListData(
    var totalPages: Int,
    var totalItems: Int,
    var items: MutableList<WalletData>,
    var pageNumber: Int,
    val pageSize: Int
)

data class WalletData(

    var createdAt: String,
    var updatedAt: String,
    var walletTransactionId: String,
    var transactionAmount: Double,
    var transactionTypes: String,
    var userType: String,
    var userId: String,
    var walletId: String,
    var adminCommission: Double,
    var transferAmount: Double,
    var purchaseType: String,
    var transactionRemark: String,
    var toUserName: String,
    var toUser: String,
    var toEmail: String,
    var toMobileNo: String,
    var percentage: Double,
    ): Serializable


data class WalletBalance(
    val `data`: WalletBalanceData,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class WalletBalanceData(
    val active: Boolean,
    val amount: Double,
    val createdAt: String,
    val updatedAt: Any,
    val userId: String,
    val userType: String
)