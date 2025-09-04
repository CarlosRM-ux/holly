package com.example.holly.Presentacion.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.holly.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment @Inject constructor(): Fragment() {
    private var _binding : FragmentSettingsBinding? =null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return  binding.root
    }

    val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                println("Uri de la imagen seleccionada: $uri")
                viewModel.uploadPhoto(uri)
            }else {
                println("no selecciono una imagen")
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        botones()

    }

    private fun botones() {
        binding.agregar.setOnClickListener { pickMedia.launch(PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    }
}