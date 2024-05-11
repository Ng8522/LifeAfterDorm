package com.example.lifeafterdorm.controller

import android.content.Context
import android.widget.Toast
import com.example.lifeafterdorm.data.User
import com.google.firebase.database.*

private lateinit var dbRef : DatabaseReference

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val phonePattern = "^[0-9]{7,15}\$"
    return phoneNumber.matches(phonePattern.toRegex())
}

fun isPhoneExists(context: Context, phoneNumber: String) : Boolean {
    dbRef = FirebaseDatabase.getInstance().getReference("User")
    var phoneExist = false
    dbRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                for (phoneSnap in snapshot.children) {
                    val user = phoneSnap.getValue(User::class.java)
                    if(user != null && user.phoneNum == phoneNumber){
                        phoneExist = true
                        break
                    }
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
        }
    })
    return false
}
