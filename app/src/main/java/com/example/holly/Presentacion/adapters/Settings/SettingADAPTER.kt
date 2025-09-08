package com.example.holly.Presentacion.adapters.Settings

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.holly.Domain.model.Photo
import com.example.holly.R

class SettingADAPTER (private var photos: List<Photo>) : RecyclerView.Adapter<SettingsVIEWHOLDER>() {

     fun updatePhotos(newPhotos: List<Photo>){
         Log.d("SettingADAPTER", "Actualizando adaptador con ${newPhotos.size} fotos")
         this.photos = newPhotos
        notifyDataSetChanged()
    }


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