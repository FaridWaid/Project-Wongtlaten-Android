package com.wongtlaten.application.modules.pembeli.home

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity
import com.wongtlaten.application.modules.penjual.home.PreviewDeskripsiProdukActivity
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity
import com.wongtlaten.application.modules.penjual.home.RecomViewPagerAdapter
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates

class DetailProdukPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    private lateinit var typeProduk : TextView
    private lateinit var namaProduk : TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var textRate : TextView
    private lateinit var deskripsiProduk : TextView
    private lateinit var totalPrice : TextView
    private lateinit var totalPriceSlash : TextView
    private lateinit var totalPricePromo : TextView
    private lateinit var textBacaSelengkapnya : TextView
    private lateinit var recomViewPager: ViewPager2
    private lateinit var daftarRecomList: ArrayList<Products>
    private lateinit var adapterRecom: RecomViewPagerAdapter
    private lateinit var loveInactive: ImageView
    private lateinit var loveActive: ImageView
    private lateinit var itemFiturPlus : CardView
    private lateinit var itemFiturMinus : CardView
    private lateinit var totalProduk : TextView
    private var countTotalProduk by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk_pembeli)

        typeProduk = findViewById(R.id.typeProduk)
        namaProduk = findViewById(R.id.nameProduk)
        ratingBar = findViewById(R.id.ratingBar)
        textRate = findViewById(R.id.textRate)
        deskripsiProduk = findViewById(R.id.deskripsiProduk)
        totalPrice = findViewById(R.id.totalPrice)
        totalPriceSlash = findViewById(R.id.totalPriceSlash)
        totalPricePromo = findViewById(R.id.totalPricePromo)
        textBacaSelengkapnya = findViewById(R.id.textBacaSelengkapnya)
        recomViewPager = findViewById(R.id.recomViewPager)
        recomViewPager.clipToPadding = false
        recomViewPager.clipChildren = false
        recomViewPager.offscreenPageLimit = 3
        recomViewPager.getChildAt(0)
        recomViewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        loveInactive = findViewById(R.id.loveInactivated)
        loveActive = findViewById(R.id.loveActivated)
        itemFiturPlus = findViewById(R.id.itemFiturPlus)
        itemFiturMinus = findViewById(R.id.itemFiturMinus)
        totalProduk = findViewById(R.id.totalProduk)
        countTotalProduk = 1

        loveInactive.visibility = View.VISIBLE
        loveInactive.setOnClickListener {
            loveActive.visibility = View.VISIBLE
            loveInactive.visibility = View.INVISIBLE
        }
        loveActive.setOnClickListener {
            loveInactive.visibility = View.VISIBLE
            loveActive.visibility = View.INVISIBLE
        }

        itemFiturPlus.setOnClickListener {
            countTotalProduk += 1
            totalProduk.text = "$countTotalProduk"
        }
        itemFiturMinus.setOnClickListener {
            countTotalProduk -= 1
            if (countTotalProduk <= 0){
                alertDialog("GAGAL!", "Minimal pembelian produk harus 1", false)
                countTotalProduk += 1
            } else {
                totalProduk.text = "$countTotalProduk"
            }
        }

        val idProduct = intent.getStringExtra(PreviewProdukActivity.EXTRA_ID_PRODUCT)!!
        reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val produk = dataSnapshot.getValue(Products::class.java)!!

                typeProduk.text = produk.kategoriProduct
                namaProduk.text = produk.namaProduct
                ratingBar.rating = produk.ratingProduct
                textRate.setText("(${produk.ratingProduct})")
                deskripsiProduk.text = produk.deskripsiProduct
                val formatter: NumberFormat = DecimalFormat("#,###")
                val price = produk.hargaProduct
                val promo = 100 - produk.hargaPromoProduct
                val totalPromo = ((promo.toFloat()/100) * produk.hargaProduct)
                val formattedNumberPrice: String = formatter.format(price)
                val formattedNumberPrice2: String = formatter.format(totalPromo.toLong())
                if (produk.jenisProduct == "flash sale"){
                    totalPriceSlash.paintFlags = totalPriceSlash.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
                    totalPriceSlash.visibility = View.VISIBLE
                    totalPriceSlash.text = "Rp. ${formattedNumberPrice}"
                    totalPricePromo.visibility = View.VISIBLE
                    totalPricePromo.text = "Rp. ${formattedNumberPrice2}"
                } else {
                    totalPrice.visibility = View.VISIBLE
                    totalPrice.text = "Rp. ${formattedNumberPrice}"
                }

                val imageSlider = findViewById<ImageSlider>(R.id.imageSlider)
                val imageList = ArrayList<SlideModel>()

                imageList.add(SlideModel(produk.photoProduct1))
                imageList.add(SlideModel(produk.photoProduct2))
                imageList.add(SlideModel(produk.photoProduct3))
                imageList.add(SlideModel(produk.photoProduct4))

                imageSlider.setImageList(imageList, ScaleTypes.FIT)

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        reference.addListenerForSingleValueEvent(menuListener)

        // Memasukkan data DaftarProduk ke dalam "daftarRecomList" sebagai array list
        daftarRecomList = arrayListOf<Products>()
        // Memanggil fungsi "showListProduk" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProduk()

        textBacaSelengkapnya = findViewById(R.id.textBacaSelengkapnya)
        textBacaSelengkapnya.setOnClickListener {
            Intent(applicationContext, DetailDeskripsiProdukPembeliActivity::class.java).also {
                it.putExtra("EXTRA_ID_PRODUCT", idProduct)
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
            }
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

    // Membuat fungsi "alertDialog" dengan parameter title, message, dan backActivity
    // Fungsi ini digunakan untuk menampilkan alert dialog
    private fun alertDialog(title: String, message: String, backActivity: Boolean){
        // Membuat variabel yang berisikan AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle(title)
            setMessage(message)
            window.setBackgroundDrawableResource(android.R.color.background_light)
            setCancelable(false)
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (backActivity){
                        onBackPressed()
                    }
                })
        }
        alertDialog.show()
    }

    // Membuat fungsi "showListProduk" yang digunakan untuk menampilkan data dari database ke dalam,
    // recyclerview
    private fun showListProduk() {
        val ref = FirebaseDatabase.getInstance().getReference("dataProduk")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarRecomList.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(Products::class.java)
                        if (products?.jenisProduct == "popular"){
                            daftarRecomList.add(products)
                        }
                    }

                    adapterRecom = RecomViewPagerAdapter(daftarRecomList)
                    recomViewPager.adapter = adapterRecom

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object{
        const val EXTRA_ID_PRODUCT = "EXTRA_ID_PRODUCT"
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}