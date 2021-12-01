package com.actorpay.merchant.repositories.retrofitrepository.models.home

data class ChangePasswordParams(
    val currentPassword:String,
    val newPassword:String,
    val confirmPassword:String,
)