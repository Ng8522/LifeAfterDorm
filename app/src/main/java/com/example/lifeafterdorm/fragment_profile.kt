package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.navigation.NavigationView

class fragment_profile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
//        val navigationView : NavigationView.OnNavigationItemSelectedListener
//        val btnNext: Button = view.findViewById(R.id.nav_profile)
//        btnNext.setOnClickListener() {
//            val fragment = fragment_profile()
//            val transaction = activity?.supportFragmentManager?.beginTransaction()
//            transaction?.replace(R.id.fragmentContainer, fragment)
//            transaction?.addToBackStack(null)
//            transaction?.commit()
//        }
        return view
    }

}