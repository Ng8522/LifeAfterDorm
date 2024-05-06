package com.example.lifeafterdorm

import android.app.AlertDialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.lifeafterdorm.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        val spanRegister = SpannableString(binding.tvRegister.text)
        val clickToRegister = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
        spanRegister.setSpan(clickToRegister, 0, spanRegister.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvRegister.text = spanRegister
        binding.tvRegister.movementMethod = LinkMovementMethod.getInstance()

        val spanForgotPass = SpannableString(binding.tvForgortPassword.text)
        val clickToForgotPass = object  : ClickableSpan(){
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
        }
        spanForgotPass.setSpan(clickToForgotPass, 0, spanForgotPass.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvForgortPassword.text = spanForgotPass
        binding.tvForgortPassword.movementMethod = LinkMovementMethod.getInstance()


        binding.btnLogin.setOnClickListener {
            SuccessLoginBox()
        }
        return view
    }

    private fun SuccessLoginBox() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Success")
        alertDialogBuilder.setMessage("Login successful!")
        alertDialogBuilder.setPositiveButton("Let's Go") { _, _ ->
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun FailedLoginBox() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Failed")
        alertDialogBuilder.setMessage("Failed to login!Please try again.")
        alertDialogBuilder.setPositiveButton("Try Again") { _, _ ->
            Toast.makeText(requireContext(), "Failed to login! Please try again.", Toast.LENGTH_SHORT).show()
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}