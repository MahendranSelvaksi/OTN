package com.mobile.dataregisteration

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.dataregisteration.apiModel.LoginRequest
import com.mobile.dataregisteration.apiModel.LoginResponse
import com.mobile.dataregisteration.apiModel.UploadImageRequest
import com.mobile.dataregisteration.apiModel.UserData
import com.mobile.dataregisteration.apiModel.UserRegistrationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStore: DataStore<Preferences>) :
    ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    var latlong by mutableStateOf("")

    var name by mutableStateOf("")
    var phone by mutableStateOf("")

    var nameError by mutableStateOf<String?>(null)
    var phoneError by mutableStateOf(false)

    var showSuccessDialog by mutableStateOf(false)
    var successMessage by mutableStateOf("")

    var aadharFrontPhoto by mutableStateOf<Bitmap?>(null)
    var aadharBackPhoto by mutableStateOf<Bitmap?>(null)
    var rationCardFrontPhoto by mutableStateOf<Bitmap?>(null)
    var rationCardBackPhoto by mutableStateOf<Bitmap?>(null)
    var voterIdFrontPhoto by mutableStateOf<Bitmap?>(null)
    var voterIdBackPhoto by mutableStateOf<Bitmap?>(null)

    var selectedDistrict by mutableStateOf<District?>(null)
    var selectedUnion by mutableStateOf<Union?>(null)
    var selectedPanchayat by mutableStateOf<Panchayat?>(null)
    var selectedVillage by mutableStateOf<Village?>(null)


    val isFormValid: Boolean
        get() = name.isNotBlank()
                && phone.matches(Regex("^[0-9]{10}$"))
                && !selectedDistrict?.districtName.isNullOrBlank()
                && !selectedUnion?.unionName.isNullOrBlank()
                && !selectedPanchayat?.panchayatName.isNullOrBlank()
                && !selectedVillage?.villageName.isNullOrBlank()
                && aadharFrontPhoto != null
                && aadharBackPhoto != null
                && rationCardFrontPhoto != null
                && rationCardBackPhoto != null
                && voterIdFrontPhoto != null
                && voterIdBackPhoto != null

    val loginResponseFlow: StateFlow<LoginResponse?> = dataStore.data
        .map { prefs ->
            val isLoggedIn = prefs[LoginKeys.STATUS] ?: false
            if (!isLoggedIn) return@map null

            LoginResponse(
                status = isLoggedIn,
                user_id = prefs[LoginKeys.USER_ID] ?: -1,
                message = prefs[LoginKeys.MESSAGE] ?: "",
                accesstoken = prefs[LoginKeys.ACCESS_TOKEN] ?: "",
                data = UserData(
                    username = prefs[LoginKeys.USERNAME] ?: "",
                    email = prefs[LoginKeys.EMAIL] ?: "",
                    phone = prefs[LoginKeys.PHONE]
                )
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            null
        )

    fun login(deviceId: String, userName: String, password: String) {
        viewModelScope.launch {
            Log.w("Success", "User name :::: $userName")
            _uiState.value = LoginUiState.Loading
            try {
                val request = LoginRequest(
                    device_id = deviceId,
                    userName = userName,
                    password = password
                )
                val response = RetrofitClient.api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = LoginUiState.Success(response.body()!!)
                } else {
                    _uiState.value = LoginUiState.Error(
                        response.errorBody()?.string() ?: "Unknown error"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    // Retrieve user ID as StateFlow
    val userId: StateFlow<Int?> = dataStore.data
        .map { it[LoginKeys.USER_ID] }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Retrieve user ID as StateFlow
    val accessToken: StateFlow<String?> = dataStore.data
        .map { it[LoginKeys.ACCESS_TOKEN] }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    suspend fun saveLoginResponse(context: Context, loginResponse: LoginResponse) {
        context.loginDataStore.edit { prefs ->
            prefs[LoginKeys.STATUS] = loginResponse.status
            prefs[LoginKeys.USER_ID] = loginResponse.user_id
            prefs[LoginKeys.MESSAGE] = loginResponse.message
            prefs[LoginKeys.ACCESS_TOKEN] = loginResponse.accesstoken
            prefs[LoginKeys.USERNAME] = loginResponse.data.username
            prefs[LoginKeys.EMAIL] = loginResponse.data.email
            prefs[LoginKeys.PHONE] = loginResponse.data.phone ?: ""
        }
    }

    /* fun uploadPhoto(file: File, description: String) {
         viewModelScope.launch {
             val result = repository.uploadPhoto(file, description)
             result.onSuccess { url ->
                 // Use the image URL
             }.onFailure { e ->
                 // Show error
             }
         }
     }*/

    private val _uploadedUrls = MutableLiveData<List<String>>()
    val uploadedUrls: LiveData<List<String>> get() = _uploadedUrls

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading


    fun uploadPhotos(
        userId: Int, bitmaps: List<ImageUploadMap>?,
        requestData: UserRegistrationRequest,
        onResult: (Boolean, String) -> Unit,
    ) {
        viewModelScope.launch {
            _isUploading.value = true
            val passURL = mutableListOf<String>()
            try {
                var urls = uploadPhotosSequentially(userId, bitmaps)
                passURL.addAll(urls)
               // _uploadedUrls.postValue(urls)
            } catch (e: Exception) {
                e.printStackTrace()
                _uploadedUrls.postValue(emptyList())
            }
            val finalRequest = requestData.copy(
                aadharCardFrontLink = passURL[0],
                aadharCardBackLink = passURL[1],
                rationCardFrontLink = passURL[2],
                rationCardBackLink = passURL[3],
                voterCardFrontLink = passURL[4],
                voterCardBackLink = passURL[5]
            )
            try {
                val response = RetrofitClient.api.registerUser(finalRequest)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    onResult(true , body.message)
                } else {
                    onResult(false, "API error: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Unknown error")
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun clearForm() {
        name = ""
        phone = ""
        selectedDistrict = null
        selectedUnion = null
        selectedPanchayat = null
        selectedVillage = null
        aadharFrontPhoto = null
        aadharBackPhoto = null
        rationCardFrontPhoto = null
        rationCardBackPhoto = null
        voterIdFrontPhoto = null
        voterIdBackPhoto = null
    }

    private suspend fun uploadPhotosSequentially(
        userId: Int,
        bitmaps: List<ImageUploadMap>?,
    ): List<String> {
        val uploadedUrls = mutableListOf<String>()

        if (bitmaps != null) {
            for ((index, map) in bitmaps.withIndex()) {
                val base64Image = bitmapToBase64(map.bitmap)
                val imageName = map.nameOfThePhoto // customize as needed
                Log.w("Success", "Name === $userId :::: $imageName")
                val request = UploadImageRequest(
                    user_id = userId,
                    imageFile = base64Image,
                    imageName = imageName,
                    Phno = phone
                )

                // val response = apiService.uploadPhoto(request)
                val response = RetrofitClient.api.uploadPhoto(request)

                if (response.status == "success") {
                    uploadedUrls.add(response.imageURL)
                } else {
                    // Handle failed upload if needed
                }
            }
        }

        return uploadedUrls
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.edit { it.clear() } // Clear all saved data
        }
    }

}
