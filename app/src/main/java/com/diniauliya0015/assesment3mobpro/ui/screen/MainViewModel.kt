package com.diniauliya0015.assesment3mobpro.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diniauliya0015.assesment3mobpro.model.Hewan
import com.diniauliya0015.assesment3mobpro.network.HewanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Hewan>())
        private set
    var status = MutableStateFlow(HewanApi.ApiStatus.LOADING)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    init {
        retrieveData()
    }
    fun retrieveData(){
        viewModelScope.launch(Dispatchers.IO){
            status.value = HewanApi.ApiStatus.LOADING
            try {
                data.value = HewanApi.service.getHewan()
                status.value = HewanApi.ApiStatus.SUCCES
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = HewanApi.ApiStatus.FAILED
            }
        }
    }

    fun saveData(userId: String, nama:String, namaLatin: String, bitmap: Bitmap) {
        viewModelScope.launch (Dispatchers.IO){
            try {
                val result = HewanApi.service.postHewan(
                    userId,
                    nama.toRequestBody("text/plan".toMediaTypeOrNull()),
                    namaLatin.toRequestBody("text/plan".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                if (result.status == "succes")
                    retrieveData()
                else
                    throw Exception(result.message)
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }
    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody
        )
    }
    fun clearMessage() { errorMessage.value = null }
}