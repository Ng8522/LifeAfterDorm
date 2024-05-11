package com.example.lifeafterdorm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.lifeafterdorm.data.User
import com.example.lifeafterdorm.databinding.FragmentProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private lateinit var myToolbar: Toolbar
    private lateinit var binding:FragmentProfileBinding
    private lateinit var dbRef : DatabaseReference
    private  lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val myToolbar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.navDrawerToolbar)
        myToolbar.title = "Profile"
        val userId = arguments?.getString("userId")
        binding.userid = userId
        val view = binding.root
        if (userId != null) {
            getUserData(userId)
            getProfileImage(userId)
        }
        return view
    }

    private fun getUserData(id:String){
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val user = userSnap.getValue(User::class.java)
                        if (user != null) {
                            if(user.id == id){
                                binding.tvProfileUserName.text = user.name

                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getProfileImage(id:String){
        val imageName = "$id.png"
        val imageRef = FirebaseStorage.getInstance().reference.child("user_image/$imageName")
        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.ivProfile)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error: $exception", Toast.LENGTH_LONG).show()
            }
    }
}