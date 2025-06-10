package com.diniauliya0015.assesment3mobpro.ui.screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diniauliya0015.assesment3mobpro.model.Hewan
import com.diniauliya0015.assesment3mobpro.network.HewanApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Hewan>())
        private set
    var status = MutableStateFlow(HewanApi.ApiStatus.LOADING)
        private set
    init {
        retrieveData()
    }
    private fun retrieveData(){
        viewModelScope.launch(Dispatchers.IO){
            status.value = HewanApi.ApiStatus.LOADING
            try {
                data.value = HewanApi.service.getHewan()
                status.value = HewanApi.ApiStatus.SUCCES
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }
}