package com.mobile.dataregisteration.apiModel

import com.google.gson.annotations.SerializedName

data class UserRegistrationRequest(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("accesstoken")
    val accesstoken: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phno")
    val phno: String,
    @SerializedName("district")
    val district: String,
    @SerializedName("union")
    val union: String,
    @SerializedName("panchayat")
    val panchayat: String,
    @SerializedName("village")
    val villege: String,
    @SerializedName("aadharCardFrontLink")
    val aadharCardFrontLink: String,
    @SerializedName("aadharCardBackLink")
    val aadharCardBackLink: String,
    @SerializedName("rationCardFrontLink")
    val rationCardFrontLink: String,
    @SerializedName("rationCardBackLink")
    val rationCardBackLink: String,
    @SerializedName("voterCardFrontLink")
    val voterCardFrontLink: String,
    @SerializedName("voterCardBackLink")
    val voterCardBackLink: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("lat_long")
    val latLongitude: String
)

