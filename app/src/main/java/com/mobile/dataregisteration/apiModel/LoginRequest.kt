package com.mobile.dataregisteration.apiModel

data class LoginRequest( val device_id: String,
                         val device_type: String = "Android",
                         val userName: String,
                         val password: String)
