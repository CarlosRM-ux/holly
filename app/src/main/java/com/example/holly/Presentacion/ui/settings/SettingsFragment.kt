package com.example.holly.Presentacion.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.holly.Domain.model.rasgosPersonalidad.InterestCategory
import com.example.holly.Presentacion.adapters.Settings.SettingADAPTER
import com.example.holly.databinding.FragmentSettingsBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding : FragmentSettingsBinding? =null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var adapterSettings: SettingADAPTER




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

        setupInterestsUI()
        observeInterests()

        botones()
        initAdapter()
        observeViewModel()


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

    private fun setupInterestsUI() {
        // Itera sobre el enum y crea un Chip por cada interés
        InterestCategory.entries.forEach { interest ->
            val chip = Chip(requireContext()).apply {
                text = interest.name // El texto del chip es el nombre de la constante
                isCheckable = true
                setOnClickListener {
                    viewModel.toggleInterest(interest) // Llama al ViewModel en cada click
                }
            }
            binding.interestsChipGroup.addView(chip) // Añade el chip al ChipGroup
        }
    }
    //TODO: Corregir el timing de observeInterest para que se actualizen los botones cuando se guarden y posteriormente se puedan ver
    private fun observeInterests() {
        // Observa los cambios en los intereses seleccionados
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedInterests.collectLatest { selectedInterests ->
                binding.interestsChipGroup.children.forEach { view ->
                    val chip = view as Chip
                    // Actualiza el estado del chip (seleccionado o no)

                    val interest = InterestCategory.valueOf(chip.text.toString())
                    chip.isChecked = selectedInterests.contains(interest)
                }
            }
        }
    }
}