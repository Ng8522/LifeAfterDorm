package com.example.lifeafterdorm

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class NavDrawerActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var myToolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_drawer)


        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open,R.string.menu_close )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayShowHomeEnabled(true)

        myToolbar = findViewById(R.id.navDrawerToolbar)
//        myToolbar.title = "Drawer menu"
        setSupportActionBar(myToolbar)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                   drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(fragment_profile())
                    true
                }

                R.id.nav_favouriteList -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(fragment_favouriteList())
                    true
                }

                R.id.nav_settings -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)

                    true
                }

//                R.id.nav_home -> {
//                    drawerLayout.closeDrawer(GravityCompat.START)
//
//                    replaceFragment(MainFragment())
//
//                    true
//                }
//
                else -> {
                    false
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
//        else {
//            when(item.itemId) {
//                R.id.menuHelp -> replaceFragment(HelpFragment())
//            }
//        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}