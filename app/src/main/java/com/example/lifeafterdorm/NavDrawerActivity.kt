package com.example.lifeafterdorm

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.lifeafterdorm.data.User
import com.example.lifeafterdorm.databinding.ActivityNavDrawerBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class NavDrawerActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var myToolbar: Toolbar
    private lateinit var binding:ActivityNavDrawerBinding
    private lateinit var dbRef : DatabaseReference

    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nav_drawer)
        val userLoginId: String? = intent.getStringExtra("userid")
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open,R.string.menu_close )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        dbRef = FirebaseDatabase.getInstance().reference

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        myToolbar = findViewById(R.id.navDrawerToolbar)
        myToolbar.title = "Drawer Menu"
        setSupportActionBar(myToolbar)
        myToolbar.setTitleTextColor(Color.WHITE)
        drawerHeaderData(this, userLoginId)
        getProfileImage(this, userLoginId)
        navigationView.setNavigationItemSelectedListener { menuItem ->
             when (menuItem.itemId) {
             R.id.nav_profile -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                replaceFragment(ProfileFragment(), userLoginId)
                true
                }

                R.id.nav_settings -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    replaceFragment(SettingsFragment(), userLoginId)

                    true
                }

                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(HomeFragment(), userLoginId)

                    true
                }

                R.id.nav_favouriteList -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(FavouriteListFragment(), userLoginId)

                    true
                }

                R.id.nav_changePassword -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(RecoverPasswordFragment(), userLoginId)

                    true
                }

                R.id.nav_community -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(CommunityFragment(), userLoginId)

                    true
                }

                R.id.nav_rentalRoom -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    replaceFragment(RentalMainFragment(), userLoginId)

                    true
                }

                R.id.nav_logout -> {
                    outNavMain("Log your account out")
                    true
                }

                else -> {
                  false
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, userId: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment.apply {
            arguments = Bundle().apply {
                putString("userId", userId)
            }
        })
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        else {
            when(item.itemId) {
                R.id.menuHelp -> replaceFragment(HelpSupportFragment(), intent.getStringExtra("userid"))
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_header, menu)
        return true
    }

    private fun drawerHeaderData(context:Context,id:String?){
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        dbRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(
            object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val username: String? = userSnapshot.child("name").getValue(String::class.java)
                        val tvName: TextView = findViewById(R.id.tvName)
                        username?.let {
                            tvName.text = "Welcome $it"
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    outNavMain("Data read error")
                }
            }
        )
    }

    private fun getProfileImage(context: Context, id:String?){
        val imageName = "$id.png"
        val imageRef = FirebaseStorage.getInstance().reference.child("user_image/$imageName")
        imageRef.downloadUrl
            .addOnSuccessListener { uri ->
                val profileImg: ImageView = findViewById(R.id.imgProfile)
                Glide.with(context)
                    .load(uri)
                    .into(profileImg)
            }
            .addOnFailureListener { exception ->
                outNavMain("Data read error")
            }
    }

    private fun outNavMain(msg:String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Log out")
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            drawerLayout.closeDrawer(GravityCompat.START)
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
        alertDialogBuilder.setCancelable(false)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}






