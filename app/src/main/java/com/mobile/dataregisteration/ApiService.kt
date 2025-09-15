package com.mobile.dataregisteration

import com.mobile.dataregisteration.apiModel.ApiResponse
import com.mobile.dataregisteration.apiModel.LoginRequest
import com.mobile.dataregisteration.apiModel.LoginResponse
import com.mobile.dataregisteration.apiModel.UploadImageRequest
import com.mobile.dataregisteration.apiModel.UploadImageResponse
import com.mobile.dataregisteration.apiModel.UserRegistrationRequest
import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("upload-photo")
    suspend fun uploadPhoto(@Body request: UploadImageRequest): UploadImageResponse

    @POST("submitForm") // replace with actual API endpoint
    suspend fun registerUser(
        @Body request: UserRegistrationRequest
    ): Response<ApiResponse>
}

