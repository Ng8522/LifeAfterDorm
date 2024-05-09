package com.example.lifeafterdorm.controller

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

private lateinit var dbRef : DatabaseReference

fun passwordFormat(password:String):Boolean{
    val passwordPattern = "(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}"
    return password.matches(passwordPattern.toRegex())
}

fun generateSalt(): ByteArray {
    val random = SecureRandom()
    val salt = ByteArray(16)
    random.nextBytes(salt)
    return salt
}

fun hashPassword(password: String, salt: ByteArray): String {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(salt)
    val hashedPassword = md.digest(password.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(hashedPassword, Base64.DEFAULT)
}

fun verifyPassword(password: String, hashedPassword: String, salt: ByteArray): Boolean {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(salt)
    val hashedInput = md.digest(password.toByteArray(Charsets.UTF_8))
    val hashedInputBase64 = Base64.encodeToString(hashedInput, Base64.DEFAULT)
    return hashedInputBase64 == hashedPassword
}

fun isPasswordExists(email: String, callback: (String) -> Unit) {
    dbRef = FirebaseDatabase.getInstance().getReference("User")
    dbRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val userData = dataSnapshot.children.first()
                val password = userData.child("password").getValue(String::class.java)
                if (password != null) {
                    callback(password)
                }else{
                    callback("")
                }
            } else {
                callback("")
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            callback("")
        }
    })
}



