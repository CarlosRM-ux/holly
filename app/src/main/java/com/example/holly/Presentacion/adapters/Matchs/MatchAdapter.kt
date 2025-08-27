package com.example.holly.Presentacion.adapters.Matchs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.holly.Data.repository.UsuarioMatch
import com.example.holly.Presentacion.adapters.Matchs.MatchViewHolder
import com.example.holly.R

class MatchAdapter(private val usuario : List<UsuarioMatch>): RecyclerView.Adapter<MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match,parent,false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val usuaroActual = usuario[position]
        holder.bind(usuaroActual)
    }

    override fun getItemCount() = usuario.size
}