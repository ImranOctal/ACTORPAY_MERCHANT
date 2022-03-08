package com.actorpay.merchant.repositories.retrofitrepository.models.content


data class ContentResponse(

    val message: String,
    val status: String,
    val `data`: ContentResponseData
)

data class ContentResponseData(
    val id: String,
    val cmsType: Long,
    val title: String,
    val contents: String,
    val metaTitle: String,
    val metaKeyword: String,
    val metaData: String,
    val updatedAt: String
)

data class FAQResponseData(
    val id: String,
    val question: String,
    val answer: String,
    val updatedAt: String
)

data class FAQResponse(
    val message: String,
    val data: List<FAQResponseData> = arrayListOf(),
    val status: String,
    val httpStatus: String
)