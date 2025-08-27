package com.example.holly.Presentacion.ui.Splash

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow


@HiltViewModel
class SplashViewModel@Inject constructor(private val auth : FirebaseAuth): ViewModel(){

    fun isUserLoggedIn (): Boolean{

        return auth.currentUser != null
    }

}