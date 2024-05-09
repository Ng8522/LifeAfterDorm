package com.example.lifeafterdorm.controller

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
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
    val user = auth.currentUser
    user?.sendEmailVerification()
        ?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    context,
                    "Verification email sent.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Failed to send verification email. ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}








