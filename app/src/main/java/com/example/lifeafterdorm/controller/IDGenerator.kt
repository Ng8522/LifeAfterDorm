package com.example.lifeafterdorm.controller

import android.content.Context
import com.google.firebase.database.*
import android.widget.Toast
import com.example.lifeafterdorm.data.User

private lateinit var dbRef : DatabaseReference
fun isUserIDExists(context: Context, callback: (Boolean) -> Unit) {
    dbRef = FirebaseDatabase.getInstance().getReference("User")
    var userExist = false
    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val useridList = mutableListOf<String>()
            if (snapshot.exists()) {
                for (userSnap in snapshot.children) {
                    val userid = userSnap.getValue(User::class.java)?.id
                    userid?.let { useridList.add(it) }
                }
                userExist = useridList.isNotEmpty()
                callback(userExist)
            } else {
                callback(userExist)
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            callback(false)
        }
    })
}

fun getUserId(email: String, callback: (String?) -> Unit) {
    val dbRef = FirebaseDatabase.getInstance().getReference("User")
    dbRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
        ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val userData = dataSnapshot.children.first()
                val id = userData.child("id").getValue(String::class.java)
                callback(id)
            } else {
                callback(null)
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            callback(null)
        }
    })
}

fun readLatestUserIDFromFirebase(context: Context, callback: (String?) -> Unit) {
    dbRef = FirebaseDatabase.getInstance().getReference("User")
    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val useridList = mutableListOf<String>()
            if (snapshot.exists()) {
                for (personSnap in snapshot.children) {
                    val userid = personSnap.getValue(User::class.java)?.id
                    userid?.let { useridList.add(it) }
                }
                val latestUserID = useridList.lastOrNull()
                callback(latestUserID)
            } else {
                callback(null)
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            callback(null)
        }
    })
}



fun incrementID(currentID: String): String {
    val numericPart = currentID.substring(1).toIntOrNull() ?: 0
    val nextNumericPart = numericPart + 1
    return "${currentID[0]}${"%04d".format(nextNumericPart)}"
}

