package com.mobile.dataregisteration.apiModel

data class UploadImageRequest(
    val user_id: Int,
    val imageFile: String,  // Base64 encoded string
    val imageName: String , // e.g., "ravcardFront-1234567890"
    val Phno : String
)

data class UploadImageResponse(
    val imageURL: String,
    val status: String
)