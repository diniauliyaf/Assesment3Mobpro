package com.diniauliya0015.assesment3mobpro.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diniauliya0015.assesment3mobpro.model.Resep
import com.diniauliya0015.assesment3mobpro.network.ResepApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.diniauliya0015.assesment3mobpro.network.ApiStatus
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Resep>())
        private set
    var status = MutableStateFlow(ApiStatus.LOADING)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String){
        viewModelScope.launch(Dispatchers.IO){
            status.value = ApiStatus.LOADING
            try {
                data.value = ResepApi.service.getResep(userId)
                status.value = ApiStatus.SUCCESS
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }
    fun saveData(userId: String, judul:String, deskripsi: String, langkah : String, bitmap: Bitmap) {
        viewModelScope.launch (Dispatchers.IO){
            try {
                val result = ResepApi.service.postResep(
                    userId,
                    judul.toRequestBody("text/plan".toMediaTypeOrNull()),
                    deskripsi.toRequestBody("text/plan".toMediaTypeOrNull()),
                    langkah.toRequestBody("text/plan".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteData(userId: String, id: String ) {
        viewModelScope.launch (Dispatchers.IO){
            try {
                val result = ResepApi.service.deleteResep(
                    userId, id)
                if (result.status == "success")
                    retrieveData(userId)
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