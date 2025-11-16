package com.example.pizzahub_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.network.HereRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HereViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = HereRepository(application.applicationContext)

    private val _mapImageUrl = MutableStateFlow<String?>(null)
    val mapImageUrl: StateFlow<String?> = _mapImageUrl

    private val _etaMinutes = MutableStateFlow<Int?>(null)
    val etaMinutes: StateFlow<Int?> = _etaMinutes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadRouteAndMap(originLat: Double, originLon: Double, destLat: Double, destLon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Usar HERE SDK para obtener la ruta
                val r = repo.getRoute(originLat, originLon, destLat, destLon)
                if (r.isSuccess) {
                    val (route, summary) = r.getOrNull() ?: return@launch
                    _etaMinutes.value = (summary.durationSeconds / 60).toInt()

                    // El HERE SDK ya no necesita URLs de mapas estáticos
                    // La ruta se dibuja directamente en MapView usando MapPolyline
                    _mapImageUrl.value = null
                } else {
                    _error.value = r.exceptionOrNull()?.message ?: "Failed to get route"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error interno"
                android.util.Log.e("HereViewModel", "Unexpected error loading route", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
