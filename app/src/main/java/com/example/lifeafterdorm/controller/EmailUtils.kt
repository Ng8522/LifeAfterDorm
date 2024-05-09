package com.example.lifeafterdorm.controller

import android.util.Patterns
import com.google.firebase.database.*

private lateinit var dbRef : DatabaseReference

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isEmailExists(email: String, callback: (Boolean) -> Unit) {
    dbRef = FirebaseDatabase.getInstance().getReference("User")
    dbRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            callback(snapshot.exists())
        }
        override fun onCancelled(error: DatabaseError) {
            callback(false)
        }
    })
}





