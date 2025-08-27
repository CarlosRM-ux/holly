package com.example.holly.Presentacion.adapters.Settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.holly.Domain.model.Photo
import com.example.holly.R

class SettingADAPTER (private val photos: List<Photo>) : RecyclerView.Adapter<SettingsVIEWHOLDER>() {



    // 2. Infla el layout del elemento de la lista (list item layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsVIEWHOLDER {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return SettingsVIEWHOLDER(view)
    }

    // 3. Asigna los datos a la vista
    override fun onBindViewHolder(holder: SettingsVIEWHOLDER, position: Int) {
        val photo = photos[position]
        holder.bind(photo)
    }

    // 4. Retorna el número de elementos de la lista
    override fun getItemCount() = photos.size
}