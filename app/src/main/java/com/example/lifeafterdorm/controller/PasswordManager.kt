package com.example.lifeafterdorm.controller

import com.google.firebase.database.DatabaseReference
import java.security.MessageDigest


private lateinit var dbRef : DatabaseReference
fun passwordFormat(password:String):Boolean{
    val passwordPattern = "(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}"
    return password.matches(passwordPattern.toRegex())
}

fun sha256(base: String): String {
    return try {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(base.toByteArray(charset("UTF-8")))
        val hexString = StringBuffer()
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        hexString.toString()
    } catch (ex: Exception) {
        throw RuntimeException(ex)
    }
}



