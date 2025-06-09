package com.diniauliya0015.assesment3mobpro.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diniauliya0015.assesment3mobpro.network.HewanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    init {
        retrieveData()
    }
    private fun retrieveData(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val result = HewanApi.service.getHewan()
                Log.d("MainViewModel", "Success: $result")
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }
}