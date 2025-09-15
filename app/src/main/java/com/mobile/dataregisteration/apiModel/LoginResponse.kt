package com.mobile.dataregisteration.apiModel

data class LoginResponse(
    val status: Boolean,
    val user_id: Int,
    val message: String,
    val accesstoken: String,
    val data: UserData
)

data class UserData(
    val username: String,
    val email: String,
    val phone: String?
)

