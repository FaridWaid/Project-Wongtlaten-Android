package com.wongtlaten.application.modules.pembeli.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wongtlaten.application.R
import com.wongtlaten.application.modules.pembeli.customize.CustomizePembeliFragment
import com.wongtlaten.application.modules.pembeli.profile.ProfilePembeliFragment
import com.wongtlaten.application.modules.pembeli.wishlist.WishlistPembeliFragment
import me.ibrahimsn.lib.SmoothBottomBar

class HomePembeliActivity : AppCompatActivity() {

    private lateinit var navBottom : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_pembeli)

        navBottom = findViewById(R.id.nav_bottom)

        // Mendefinisikan NavController yang nantinya akan digunakan untuk control fragment
        val navController: NavController = findNavController(R.id.nav_host_fragment)
        navBottom.setupWithNavController(navController)

    }

}