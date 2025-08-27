package com.example.holly.Presentacion.ui.Match

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.holly.R
import com.example.holly.databinding.FragmentMatchBinding


class MatchFragment : Fragment() {
    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding !!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMatchBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val recycler =  binding.recycler
    }
}