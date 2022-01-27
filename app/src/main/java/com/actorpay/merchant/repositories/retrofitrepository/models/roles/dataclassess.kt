package com.actorpay.merchant.repositories.retrofitrepository.models.roles


data class RolesResponse(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class Data(
    val items: List<RoleItem>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)

data class RoleItem(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: String,
    val screenAccessPermission: Any?,
)

data class GetRolesParams(
    val name: String,
    val description: String,
)