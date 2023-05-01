package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Products
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat

class PreviewProdukActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_produk)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

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

        val idProduct = intent.getStringExtra(EXTRA_ID_PRODUCT)!!
        reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val produk = dataSnapshot.getValue(Products::class.java)!!

                typeProduk.text = produk.kategoriProduct
                namaProduk.text = produk.namaProduct
                ratingBar.rating = produk.ratingProduct
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.CEILING
                textRate.setText("(${df.format(produk.ratingProduct).toDouble()})")
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

        textBacaSelengkapnya.setOnClickListener {
            Intent(applicationContext, PreviewDeskripsiProdukActivity::class.java).also {
                it.putExtra("EXTRA_ID_PRODUCT", idProduct)
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
            }
        }

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
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
    }

    // Fungsi ini digunakan untuk menampilkan dialog peringatan tidak tersambung ke internet,
    // jika tetep tidak connect ke internet maka tetap looping dialog tersebut
    private fun showInternetDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            // Menambahkan title dan pesan ke dalam alert dialog
            setTitle("PERINGATAN!")
            setMessage("Tidak ada koneksi internet, mohon nyalakan mobile data/wifi anda terlebih dahulu")
            setIcon(R.drawable.ic_alert)
            setCancelable(false)
            setPositiveButton(
                "Coba lagi",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (!isConnected(this@PreviewProdukActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: PreviewProdukActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}

