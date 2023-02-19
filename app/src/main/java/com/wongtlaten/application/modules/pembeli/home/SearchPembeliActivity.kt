package com.wongtlaten.application.modules.pembeli.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.wongtlaten.application.R

class SearchPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global dari view
    private lateinit var etSearch: EditText
    private lateinit var itemClose: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pembeli)

        itemClose = findViewById(R.id.itemFiturClose)
        etSearch = findViewById(R.id.et_search)
        etSearch.requestFocus()
        etSearch.setFocusableInTouchMode(true)

        var countSearch = etSearch.text.toString()

        if (countSearch.length > 0){
            itemClose.visibility = View.VISIBLE
        }
        if (countSearch.isEmpty()){
            itemClose.visibility = View.INVISIBLE
        }


        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }


    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}