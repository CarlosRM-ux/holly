package com.example.holly.Presentacion.ui.RegistrerLogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState{
    object Idle: AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error (val message: String): AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: FirebaseAuth): ViewModel() {

    private val _uiSate = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiSate

    fun loginOrRegistrer(email: String, pasword: String){
        viewModelScope.launch {
            _uiSate.value = AuthUiState.Loading
        }
        try {
            auth.signInWithEmailAndPassword(email,pasword)
            _uiSate.value = AuthUiState.Success
        }catch (e: Exception){
            if (e is FirebaseAuthInvalidUserException){
                try {
                    auth.createUserWithEmailAndPassword(email,pasword)
                    _uiSate.value = AuthUiState.Success
                } catch (e: Exception){
                    _uiSate.value = AuthUiState.Error(e.message ?:
                    "Error al crear cuenta")
                }
            } else if (e is FirebaseAuthInvalidCredentialsException){
                _uiSate.value = AuthUiState.Error("Contrasena incorrecta")
            } else {
                _uiSate.value = AuthUiState.Error(e.message ?:"ocurrio un error desconocido")
            }

        }
    }
}