package com.mobile.dataregisteration.apiModel

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
    // Add other fields if returned by your API
)

