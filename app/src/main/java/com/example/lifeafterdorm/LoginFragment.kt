package com.example.lifeafterdorm

import android.app.AlertDialog
import android.content.Intent
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
import com.example.lifeafterdorm.controller.getUserId
import com.example.lifeafterdorm.controller.sha256
import com.example.lifeafterdorm.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private var auth = FirebaseAuth.getInstance()


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

        val spanForgotPass = SpannableString(binding.tvForgotPassword.text)
        val clickToForgotPass = object  : ClickableSpan(){
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
        }
        spanForgotPass.setSpan(clickToForgotPass, 0, spanForgotPass.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvForgotPassword.text = spanForgotPass
        binding.tvForgotPassword.movementMethod = LinkMovementMethod.getInstance()

        binding.btnLogin.setOnClickListener {
            val errorMsg = ArrayList<String>()
            val email:String = binding.ptEmail.text.toString().trim()
            val password:String = binding.ptPassword.text.toString().trim()
            if(email.isNotEmpty()&&password.isNotEmpty()){
                if (errorMsg.isEmpty()) {
                    auth.signInWithEmailAndPassword(email, sha256(password))
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                getUserId(email){
                                        getId ->
                                    if(!getId.isNullOrBlank()){
                                        binding.userId = getId
                                        Toast.makeText(requireContext(), "Sign in successful! Your user id is ${binding.userId}", Toast.LENGTH_SHORT).show()
                                        SuccessLoginBox()
                                    }else{
                                        FailedLoginBox("Your id not found. Please register.")
                                    }
                                }
                            } else {
                                FailedLoginBox("Please check your email and password is correct or " +
                                        "already registered.")
                            }
                        }
                } else {
                    FailedLoginBox(errorMsg.joinToString("\n"))
                }
            }else{
                FailedLoginBox("Please fill in all mandatory fields.")
            }

        }
        return view
    }

    private fun SuccessLoginBox() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Success")
        alertDialogBuilder.setMessage("Login successful!")
        alertDialogBuilder.setPositiveButton("Let's Go") { _, _ ->
            val intent = Intent(requireContext(), NavDrawerActivity::class.java)
            startActivity(intent)
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun FailedLoginBox(errorMsg:String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Failed")
        alertDialogBuilder.setMessage(errorMsg)
        alertDialogBuilder.setPositiveButton("Try Again") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}