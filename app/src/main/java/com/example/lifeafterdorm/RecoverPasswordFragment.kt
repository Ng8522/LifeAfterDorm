package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class RecoverPasswordFragment : Fragment() {

    private lateinit var myToolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Recover Password"

        return inflater.inflate(R.layout.fragment_recover_password, container, false)
    }
}