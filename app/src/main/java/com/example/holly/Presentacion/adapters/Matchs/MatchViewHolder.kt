package com.example.holly.Presentacion.adapters.Matchs

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.holly.Data.repository.UsuarioMatch
import com.example.holly.R
import com.example.holly.databinding.ItemMatchBinding

class MatchViewHolder(view : View): RecyclerView.ViewHolder(view) {
    private  val binding = ItemMatchBinding.bind(view)

     fun  bind ( usuarioMatch: UsuarioMatch){

        binding.userNameTextView.text = usuarioMatch.nombre
        binding.userImageView.load(usuarioMatch.photos){
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
        }

    }

}