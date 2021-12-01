package com.octal.actorpay.repositories.retrofitrepository.models.content


data class ContentResponse(val message: String,
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