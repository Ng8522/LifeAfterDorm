package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lifeafterdorm.databinding.FragmentHomeBinding
import com.google.firebase.database.DatabaseReference


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = arguments?.getString("userId")
        Toast.makeText(requireContext(), userId, Toast.LENGTH_LONG).show()
        binding.userid = userId
    }
}
