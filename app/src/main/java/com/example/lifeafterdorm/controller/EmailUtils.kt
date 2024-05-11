package com.example.lifeafterdorm.controller

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.*

private lateinit var dbRef : DatabaseReference
private var auth = FirebaseAuth.getInstance()


fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isEmailExists(context: Context, email: String):Boolean {
    var find = false
    dbRef = FirebaseDatabase.getInstance().getReference("User")
    dbRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists()){
                find = true
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()        }
    })
    return find
}

fun sendVerificationEmail(context: Context) {
    val user = Firebase.auth.currentUser
    user!!.sendEmailVerification()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Send verification to your email.", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "Email verification is failed to sent. Please check your email is entered correctly and not fake", Toast.LENGTH_LONG)
            }
        }
}


















