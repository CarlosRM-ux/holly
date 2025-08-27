package com.example.holly.Presentacion.ui.Match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holly.Data.repository.UsuarioMatch
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MatchViewModel @Inject constructor() : ViewModel() {
    private val _users = MutableStateFlow<List<UsuarioMatch>>(emptyList())
    val users: StateFlow<List<UsuarioMatch>> = _users

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {  }
    }
}