package com.example.holly.Presentacion.ui.settings


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holly.Data.repository.SettingsRepository
import com.example.holly.Domain.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.holly.Domain.model.Result
import javax.inject.Inject



@HiltViewModel
class SetttingsViewModel @Inject constructor(private val repository: SettingsRepository): ViewModel() {

        private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
        val uiState : StateFlow<UiState> = _uiState

        private val _photos = MutableStateFlow<List<Photo>>(emptyList())
        val photo : StateFlow<List<Photo>> = _photos

    init {
        // Carga las fotos del usuario cuando se inicializa el ViewModel
        loadUserPhotos()
    }

    private fun loadUserPhotos() {
        viewModelScope.launch {
            repository.getUserPhotos().collect{ result: Result<List<Photo>> ->
                when (result) {
                    is Result.Success -> _photos.value = result.data
                    is Result.Error -> _uiState.value = UiState.Error(result.message)
                    is Result.Loading -> _uiState.value = UiState.Loading
                }
            }
        }
    }

    fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.uploadPhotoAndSaveUrl(uri).collect { result ->
                when (result) {
                    is Result.Success<*> -> {
                        _uiState.value = UiState.Success("Foto subida exitosamente.")
                        loadUserPhotos() // Recarga las fotos para actualizar la UI
                    }
                    is Result.Error -> _uiState.value = UiState.Error(result.message)
                    is Result.Loading -> _uiState.value = UiState.Loading
                }
            }
        }
    }

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
    }


}

