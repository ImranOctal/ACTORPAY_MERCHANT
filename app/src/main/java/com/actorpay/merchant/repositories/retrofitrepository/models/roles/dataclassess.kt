package com.actorpay.merchant.repositories.retrofitrepository.models.roles


data class RolesResponse(
    val `data`: Data,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class SingleRoleResponse(
    val `data`: RoleItem,
    val httpStatus: String,
    val message: String,
    val status: String
)

data class Data(
    val items: ArrayList<RoleItem>,
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
    val screenAccessPermission: MutableList<ScreenAccessPermission>?,
)

data class ScreenAccessPermission(
    val screenId: String,
    val screenName: String,
    val read: Boolean,
    val write: Boolean,
)

data class GetRolesParams(
    val name: String,
    val description: String,
)

data class  SendRolesParmas(
    val id: String?,
    val name: String,
    val description: String,
    val screenAccessPermission: MutableList<ScreenAccessPermission>
)

data class DeleteRolesParams(
    val ids: MutableList<String>
)