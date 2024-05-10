package com.example.lifeafterdorm

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.lifeafterdorm.databinding.ActivityNavDrawerBinding
import com.google.android.material.navigation.NavigationView

class NavDrawerActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var myToolbar: Toolbar
    private lateinit var binding:ActivityNavDrawerBinding

    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nav_drawer)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationView)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open,R.string.menu_close )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar?.setDisplayShowHomeEnabled(true)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        myToolbar = findViewById(R.id.navDrawerToolbar)
        myToolbar.title = "Drawer Menu"
        setSupportActionBar(myToolbar)
        myToolbar.setTitleTextColor(Color.WHITE)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_profile -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    replaceFragment(ProfileFragment())
                    true
                }

                R.id.nav_settings -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(SettingsFragment())

                    true
                }

                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(HomeFragment())

                    true
                }

                R.id.nav_favouriteList -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(FavouriteListFragment())

                    true
                }

                R.id.nav_changePassword -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(RecoverPasswordFragment())

                    true
                }

                R.id.nav_community -> {
                    drawerLayout.closeDrawer(GravityCompat.START)

                    replaceFragment(CommunityFragment())

                    true
                }

                R.id.nav_rentalRoom -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    replaceFragment(RentalMainFragment())

                    true
                }

                R.id.nav_logout -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    binding.userId = null
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    true
                }

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
        else {
            when(item.itemId) {
                R.id.menuHelp -> replaceFragment(HelpSupportFragment())
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_header, menu)
        return true
    }

}