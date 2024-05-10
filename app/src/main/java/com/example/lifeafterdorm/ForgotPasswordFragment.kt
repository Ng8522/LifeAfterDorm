package com.example.lifeafterdorm

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.lifeafterdorm.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private var auth = FirebaseAuth.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        val pageTitleTextView = view.findViewById<TextView>(R.id.pageTitle)
        if (pageTitleTextView != null) {
            pageTitleTextView.text = "Forgot Password"
        }

        view.findViewById<ImageView>(R.id.viewback).setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnSubmit.setOnClickListener {
            val email = binding.tfForgotPassword.text.toString().trim()
            if(email.isNotEmpty()){
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    task ->
                    if(task.isSuccessful){
                        Toast.makeText(
                            requireContext(),
                            "Email Verification sent.",
                            Toast.LENGTH_LONG)
                        findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                    }
                }
            }else{
                showErrorDialog("Please fill in your email.")
            }
        }

        return view
    }

    private fun showErrorDialog(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Error")
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


}


