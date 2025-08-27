package com.example.holly.Presentacion.adapters.Settings

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.holly.Domain.model.Photo
import com.example.holly.R
import com.example.holly.databinding.ItemPhotoBinding

class SettingsVIEWHOLDER (view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemPhotoBinding.bind(view)

     fun bind (photo: Photo) {

         binding.image.load(photo.imageUrl) {
             crossfade(true)
             placeholder(R.drawable.ic_launcher_background)
         }
     }
}