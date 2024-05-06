package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lifeafterdorm.databinding.FragmentExploreBinding

class Explore_Fragment : Fragment() {
    private lateinit var binding: FragmentExploreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.btnExplore.setOnClickListener {
            findNavController().navigate(R.id.action_exploreFragment_to_loginFragment)
        }
        return view
    }
}
