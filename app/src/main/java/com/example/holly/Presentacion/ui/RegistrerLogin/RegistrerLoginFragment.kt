package com.example.holly.Presentacion.ui.RegistrerLogin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.holly.R
import com.example.holly.databinding.FragmentRegistrerLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrerLoginFragment @Inject constructor() : Fragment() {
    private  var _binding: FragmentRegistrerLoginBinding? = null
    private val binding get() = _binding !!
    private lateinit var auth: FirebaseAuth
    private val authViewModel : AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrerLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        init()
        observeUiState()
    }

    private fun init() {
        binding.button.setOnClickListener {
            val email = binding.editTextUsername.text.toString()
            val pasword = binding.editTextPassword.text.toString()

            if (email.isEmpty() || pasword.isEmpty()) {
                Toast.makeText(
                    requireContext(), "Porfavor completa los campos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            authViewModel.loginOrRegistrer(email, pasword)
        }

    }

    private fun observeUiState(){
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    authViewModel.uiState.collect { uiState ->
                        when (uiState){
                            is AuthUiState.Idle -> {

                            }
                            is AuthUiState.Loading -> {
                                Toast.makeText(requireContext(),"Cargando...", Toast.LENGTH_SHORT).show()
                            }

                            is AuthUiState.Success -> {
                                Toast.makeText(requireContext(),"Bienvenido", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_registrerLoginFragment_to_homeActivity)
                            }

                            is AuthUiState.Error -> {
                                Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}