package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CommunityFragment : Fragment() {

    private lateinit var myToolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Community"

        val view = inflater.inflate(R.layout.fragment_community, container, false)
        return view
    }
}