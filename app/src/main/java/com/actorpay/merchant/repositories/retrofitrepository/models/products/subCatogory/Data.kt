package com.actorpay.merchant.repositories.retrofitrepository.models.products.subCatogory

data class Data(
    val active: Boolean,
    val categoryId: String,
    val categoryName: String,
    val createdAt: String,
    val description: String,
    val id: String,
    var name: String
    ){

    override fun toString(): String {
        return name

    }

}