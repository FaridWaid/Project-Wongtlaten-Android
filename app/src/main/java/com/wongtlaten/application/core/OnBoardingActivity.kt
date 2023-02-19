package com.wongtlaten.application.core

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.wongtlaten.application.LoginActivity
import com.wongtlaten.application.R

class OnBoardingActivity : AppCompatActivity() {

        // Mendeklarasikan data yang digunakan untuk slider view pager 2
        private val introSlideAdapter = IntroSlideAdapter(
            listOf(
                IntroSlide(
                    "WONGTLATEN.CO",
                    "Menyediakan segala kebutuhan kamu untuk memberikan hadiah kepada orang spesial",
                    R.drawable.giveaway_amico
                ),
                IntroSlide(
                    "INSTALL DAN PESAN",
                    "Cukup pesan dari handphone dan tunggu kadomu dikirim dengan aman sampai ke rumah",
                    R.drawable.gift_amico
                ),
                IntroSlide(
                    "CUSTOM SENDIRI",
                    "Temukan ide kadomu dan buat custom kado terbaikmu sendiri",
                    R.drawable.card_amico
                )
            )
        )

        // Mendefinisikan variabel yang nantinya akan digunakan
        private lateinit var indicatorContainer: LinearLayout
        private lateinit var buttonNext: AppCompatImageView
        // Mendefinisikan variabel global untuk connect ke Firebase
//        private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_on_boarding)

            // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
//            auth = FirebaseAuth.getInstance()

            // Mengambil Id dari view pager 2 dan dijadikan variabel
            val introSlideViewPager: ViewPager2 = findViewById(R.id.introSliderViewPager)
            // Mengset adapter view pager ke "introSlideAdapter"
            introSlideViewPager.adapter = introSlideAdapter

            // Mengambil Id dari view dan dijadikan variabel
            indicatorContainer = findViewById(R.id.indicatorsContainer)
            buttonNext = findViewById(R.id.nextButton)

            // Memanggil fungsi "setupIndicators" dan "setCurrentIndicators"
            // 0 digunakan untuk indicator pertama terletak pada posisi 0
            setupIndicators()
            setCurrentIndicators(0)

            // Menggunakan library dati view pager
            introSlideViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicators(position)
                }
            })

            // Ketika "buttonNext" diklik akan pindah ke data selanjutnya,
            // dan jika sudah berada pada data terakhir maka akan pindah activity ke LoginActivity
            // overridePendingTransition digunakan untuk animasi dari intent
            buttonNext.setOnClickListener {
                if (introSlideViewPager.currentItem + 1 < introSlideAdapter.itemCount){
                    introSlideViewPager.currentItem += 1
                } else{
                    // Pindah ke LoginActivity
                    Intent(applicationContext, LoginActivity::class.java).also {
                        startActivity(it)
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                        finish()
                    }
                }
            }

        }

    // Mendefinisakan fungsi "setupIndicators"
    private fun setupIndicators(){
        // Mendefinisikan varibale "indicators" yang bersisi jumlah dari data image yang ada ada introSlideAdapter
        val indicators = arrayOfNulls<ImageView>(introSlideAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices){
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            indicatorContainer.addView(indicators[i])
        }
    }

    // Mendefinisakan fungsi "setCurrentIndicators"
    private fun setCurrentIndicators(index: Int){
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount){
            val imageView = indicatorContainer[i] as ImageView
            if (i == index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

}