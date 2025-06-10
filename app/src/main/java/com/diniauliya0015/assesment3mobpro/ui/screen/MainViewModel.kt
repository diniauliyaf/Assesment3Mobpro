package com.diniauliya0015.assesment3mobpro.ui.screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diniauliya0015.assesment3mobpro.model.Hewan
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diniauliya0015.assesment3mobpro.network.HewanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Hewan>())
        private set
    init {
        retrieveData()
    }
    private fun retrieveData(){
        viewModelScope.launch(Dispatchers.IO){
            try {
                data.value = HewanApi.service.getHewan()
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }
}