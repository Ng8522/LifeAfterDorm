package com.example.lifeafterdorm.controller

import android.content.Context
import android.widget.Toast
import com.example.lifeafterdorm.data.User
import com.google.firebase.database.*

private lateinit var dbRef : DatabaseReference

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val phonePattern = "^[0-9]{10,15}\$"
    return phoneNumber.matches(phonePattern.toRegex())
}




