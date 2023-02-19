package com.wongtlaten.application.modules.penjual.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wongtlaten.application.R

class HomePenjualActivity : AppCompatActivity() {

    private lateinit var navBottom : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_penjual)

        navBottom = findViewById(R.id.nav_bottom)

        // Mendefinisikan NavController yang nantinya akan digunakan untuk control fragment
        val navController: NavController = findNavController(R.id.nav_host_fragment)
        navBottom.setupWithNavController(navController)

    }
}