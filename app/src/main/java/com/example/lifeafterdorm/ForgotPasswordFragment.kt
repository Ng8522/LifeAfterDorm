package com.example.lifeafterdorm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.lifeafterdorm.databinding.FragmentForgotPasswordBinding


class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        val pageTitleTextView = view.findViewById<TextView>(R.id.pageTitle)

        if (pageTitleTextView != null) {
            pageTitleTextView.text = "Forgot Password"
        }

        view.findViewById<ImageView>(R.id.viewback).setOnClickListener{
            findNavController().navigateUp()
        }

        return view
    }

}


