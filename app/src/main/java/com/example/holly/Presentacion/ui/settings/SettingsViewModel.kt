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
import com.example.holly.Domain.model.rasgosPersonalidad.InterestCategory
import javax.inject.Inject



@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: SettingsRepository): ViewModel() {
//----------------------------------------------------------------------------------------------------------------------------------------
        private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
        val uiState : StateFlow<UiState> = _uiState

        private val _photos = MutableStateFlow<List<Photo>>(emptyList())
        val photo : StateFlow<List<Photo>> = _photos
//-----------------------------------------------------------------------------------------------------------------------------------------
        private val _selectInterest = MutableStateFlow<Set<InterestCategory>>(emptySet())
        val selectedInterests : StateFlow<Set<InterestCategory>> = _selectInterest
//-----------------------------------------------------------------------------------------------------------------------------------------
        private val _description = MutableStateFlow("")
        val description : StateFlow<String> = _description

    init {
        // Carga las fotos del usuario cuando se inicializa el ViewModel
        loadUserPhotos()
        //Carga la lista de intereeses que el usuario selecciono
        loadInterests()
        //Cargar la descripcion del Usuario
        loadDescription()
    }

    //CARGA LAS FOTOS
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

    //SUBE LAS FOTOS
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

 //Seleccion de Intereses

    fun toggleInterest(interest: InterestCategory){
        viewModelScope.launch {

            val currentInterest = _selectInterest.value.toMutableSet()
            val isAdding = !currentInterest.contains(interest)
            if (isAdding) {
                currentInterest.add(interest)
            } else {
                currentInterest.remove(interest)
            }
            _selectInterest.value = currentInterest

            repository.saveInterest(currentInterest).collect { result ->
                when (result) {
                    is Result.Success -> {

                    }

                    is Result.Error -> {
                        _uiState.value = UiState.Error("Error al guardar: ${result.message}")
                    }

                    is Result.Loading -> {

                    }
                }
            }
        }
    }
 //TODO: NO CARGA LOS INTERESES GUARDADOS , VERIFICAR CUAL ES EL ERROR Y CORREGIRLOl
    fun loadInterests(){
        viewModelScope.launch { repository.loadUserInterest().collect {
            result -> when(result){
                is Result.Success ->{
                    _selectInterest.value = result.data
                }
            is Result.Error -> {
                // Maneja el error, por ejemplo, mostrando un mensaje
                _uiState.value = UiState.Error(result.message)
            }
            is Result.Loading -> {
                // Puedes usar este estado para mostrar un spinner en los chips
            }
            }
        } }
    }

    fun loadDescription (){
        viewModelScope.launch {
            repository.loadDescription().collect { result ->
                if (result is Result.Success){
                    _description.value = result.data
                } else if (result is Result.Error){
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun saveDescription(text: String){
        _description.value = text
        viewModelScope.launch {
            repository.saveDescription(text).collect {
        result -> if (result is Result.Error){
            _uiState.value = UiState.Error(result.message)
        }
            }
        }
    }
}

