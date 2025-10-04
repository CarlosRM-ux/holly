package com.example.holly.Presentacion.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.holly.Domain.model.rasgosPersonalidad.InterestCategory
import com.example.holly.Presentacion.adapters.Settings.SettingADAPTER
import com.example.holly.databinding.FragmentSettingsBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding : FragmentSettingsBinding? =null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var adapterSettings: SettingADAPTER
    private var saveDescriptionJob: Job?= null




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
        initAdapter()

        observeViewModel()
        observeInterests()
        setupDescriptionBinding()

    }

    private fun botones() {
        binding.agregar.setOnClickListener { pickMedia.launch(PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.ImageOnly)) }

    }

    private fun initAdapter(){
        adapterSettings = SettingADAPTER(emptyList())
        binding.photosRecyclerView.apply {
            layoutManager = GridLayoutManager(context,1, GridLayoutManager.HORIZONTAL,false)
            adapter = adapterSettings
        }
    }

    private fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photo.collectLatest { photos ->
                Log.d("SettingsFragment", "Nueva lista de fotos recibida: ${photos.size} fotos")
                adapterSettings.updatePhotos(photos)
            }
        }
    }


    private fun observeInterests() {
        // Observa los cambios en los intereses seleccionados
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedInterests.collectLatest { selectedInterests ->

                binding.interestsChipGroup.removeAllViews()
                InterestCategory.entries.forEach { interest ->
                    val chip = Chip(requireContext(),).apply {
                        // Actualiza el estado del chip (seleccionado o no)
                        text = interest.name
                        isCheckable = true

                        isChecked = selectedInterests.contains(interest)
                        setOnClickListener { viewModel.toggleInterest(interest) } //Le pasa el boton seleccionado al_selectdInterests
                    // y lo guarda en firebase
                    }
                    binding.interestsChipGroup.addView(chip)

                }
            }
        }
    }

    private fun setupDescriptionBinding(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.description.collectLatest { desc ->
                if (binding.segundo.text.toString() != desc){
                binding.segundo.setText(desc)
                }
            }

        }
        // 2. Implementar el guardado automático con debounce
        binding.segundo.doOnTextChanged { text, _, _, _ ->
            // Cancela el trabajo de guardado anterior si el usuario escribe de nuevo
            saveDescriptionJob?.cancel()

            // Crea un nuevo trabajo que espera 500ms antes de guardar
            saveDescriptionJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500) // DEBOUNCE: Espera medio segundo
                val newText = text?.toString() ?: ""
                viewModel.saveDescription(newText)
            }
        }

    }

}