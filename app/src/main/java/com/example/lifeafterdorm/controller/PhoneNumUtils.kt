package com.example.lifeafterdorm.controller

import com.google.firebase.database.*

private lateinit var dbRef : DatabaseReference

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val phonePattern = "^[0-9]{7,15}\$"
    return phoneNumber.matches(phonePattern.toRegex())
}

fun isPhoneExists(phone: String, callback: (Boolean) -> Unit) {
    dbRef = FirebaseDatabase.getInstance().getReference("User")
    dbRef.orderByChild("phoneNum").equalTo(phone).addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            callback(snapshot.exists())
        }
        override fun onCancelled(error: DatabaseError) {
            callback(false)
        }
    })
}
