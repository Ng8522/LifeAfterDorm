package com.example.lifeafterdorm.controller

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.*

private lateinit var dbRef : DatabaseReference

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val phonePattern = "^[0-9]{7,15}\$"
    return phoneNumber.matches(phonePattern.toRegex())
}

fun isPhoneExists(context: Context, phoneNum: String):Boolean {
    var find = false
    dbRef = FirebaseDatabase.getInstance().getReference("User")
    dbRef.orderByChild("phoneNum").equalTo(phoneNum).addListenerForSingleValueEvent(object :
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
