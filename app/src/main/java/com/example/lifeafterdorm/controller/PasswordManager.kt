package com.example.lifeafterdorm.controller

import com.google.firebase.database.DatabaseReference
import java.security.MessageDigest


private lateinit var dbRef : DatabaseReference
fun passwordFormat(password:String):Boolean{
    val passwordPattern = "(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}"
    return password.length >= 8 && password.matches(passwordPattern.toRegex())
}





