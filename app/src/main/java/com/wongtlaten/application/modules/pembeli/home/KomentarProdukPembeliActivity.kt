package com.wongtlaten.application.modules.pembeli.home

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.ReviewProduct
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.pembeli.profile.ReviewProdukPembeliAdapter
import com.wongtlaten.application.modules.pembeli.wishlist.DetailPesananPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliAdapter
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity
import com.wongtlaten.application.modules.penjual.home.TambahFaqPenjualActivity
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates

class KomentarProdukPembeliActivity : AppCompatActivity() {

    private lateinit var reference: DatabaseReference
    private lateinit var komentarNotFound: LinearLayout
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var ratingBar: RatingBar
    private lateinit var allRate: TextView
    private lateinit var textRate: TextView
    private lateinit var textCountReview: TextView
    private lateinit var filterRatingActive: TextView
    private lateinit var filterRatingInactive: TextView
    private lateinit var textFilterReview: TextView
    private lateinit var rvDaftarReview: RecyclerView
    private lateinit var daftarReviewList: ArrayList<ReviewProduct>
    private lateinit var daftarReviewListFilter: ArrayList<ReviewProduct>
    private lateinit var adapterReview: KomentarProdukPembeliAdapter
    private var cekCheckbox by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_komentar_produk_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        val idProduct = intent.getStringExtra(EXTRA_ID_PRODUCT)!!

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataReviewProduk").child(idProduct)

        komentarNotFound = findViewById(R.id.komentarNotFound)
        ratingBar = findViewById(R.id.ratingBar)
        allRate = findViewById(R.id.allRate)
        textRate = findViewById(R.id.textRate)
        textCountReview = findViewById(R.id.textCountReview)
        filterRatingActive = findViewById(R.id.filterRatingActive)
        filterRatingInactive = findViewById(R.id.filterRatingInactive)
        textFilterReview = findViewById(R.id.textFilterReview)
        mainLayout = findViewById(R.id.mainLayout)

        filterRatingInactive.visibility = View.VISIBLE
        filterRatingActive.visibility = View.INVISIBLE

        filterRatingInactive.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            filterRating()
        }

        filterRatingActive.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            filterRating()
        }

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarReview = findViewById(R.id.rvDaftarReview)
        rvDaftarReview.setHasFixedSize(true)
        rvDaftarReview.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProduk ke dalam "daftarFsList" sebagai array list
        daftarReviewList = arrayListOf<ReviewProduct>()
        daftarReviewListFilter = arrayListOf<ReviewProduct>()

        adapterReview = KomentarProdukPembeliAdapter(daftarReviewListFilter)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListReview()

        val referenceProduk = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val produk = dataSnapshot.getValue(Products::class.java)!!
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.CEILING
                allRate.setText("${df.format(produk.ratingProduct).toDouble()}")
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referenceProduk.addListenerForSingleValueEvent(menuListener)

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    private fun filterRating() {
        var dialog = Dialog(this@KomentarProdukPembeliActivity)
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_filter_review)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
        var checkbox1 = dialog.findViewById(R.id.checkbox1) as CheckBox
        var checkbox2 = dialog.findViewById(R.id.checkbox2) as CheckBox
        var checkbox3 = dialog.findViewById(R.id.checkbox3) as CheckBox
        var checkbox4 = dialog.findViewById(R.id.checkbox4) as CheckBox
        var btnTerapkanInactivated = dialog.findViewById(R.id.btnTerapkanInactivated) as Button
        var btnTerapkanActivated = dialog.findViewById(R.id.btnTerapkanActivated) as Button

        btnTerapkanInactivated.visibility = View.VISIBLE
        btnTerapkanActivated.visibility = View.INVISIBLE

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        cekCheckbox = 0

        checkbox1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cekCheckbox = 1
                checkbox2.isChecked = false
                checkbox3.isChecked = false
                checkbox4.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanActivated.visibility = View.INVISIBLE
                btnTerapkanInactivated.visibility = View.VISIBLE
            }
        }

        checkbox2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cekCheckbox = 2
                checkbox1.isChecked = false
                checkbox3.isChecked = false
                checkbox4.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanActivated.visibility = View.INVISIBLE
                btnTerapkanInactivated.visibility = View.VISIBLE
            }
        }

        checkbox3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cekCheckbox = 3
                checkbox1.isChecked = false
                checkbox2.isChecked = false
                checkbox4.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanActivated.visibility = View.INVISIBLE
                btnTerapkanInactivated.visibility = View.VISIBLE
            }
        }

        checkbox4.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cekCheckbox = 4
                checkbox1.isChecked = false
                checkbox2.isChecked = false
                checkbox3.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
            } else {
                btnTerapkanActivated.visibility = View.INVISIBLE
                btnTerapkanInactivated.visibility = View.VISIBLE
            }
        }

        btnTerapkanActivated.setOnClickListener {
            filterRatingActive.visibility = View.VISIBLE
            filterRatingInactive.visibility = View.INVISIBLE
            if (cekCheckbox == 1){
                textFilterReview.text = "0 - 1,9 Rating"
                showListReviewFilter("1")
                dialog.dismiss()
            } else if (cekCheckbox == 2){
                textFilterReview.text = "2 - 2,9 Rating"
                showListReviewFilter("2")
                dialog.dismiss()
            } else if (cekCheckbox == 3){
                textFilterReview.text = "3 - 3,9 Rating"
                showListReviewFilter("3")
                dialog.dismiss()
            } else if (cekCheckbox == 4){
                textFilterReview.text = "4 - 5 Rating"
                showListReviewFilter("4")
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showListReview(){
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarReviewList.clear()
                    var countReview = 0
                    var countRating = 0
                    for (i in snapshot.children){
                        val review = i.getValue(ReviewProduct::class.java)!!
                        if (review != null){
                            countRating += 1
                            if (review.review != ""){
                                daftarReviewList.add(review)
                                countReview += 1
                            }
                        }
                    }
                    textCountReview.text = "$countRating rating - $countReview komentar"
                }
                if (daftarReviewList.isEmpty()){
                    komentarNotFound.visibility = View.VISIBLE
                } else {
                    komentarNotFound.visibility = View.INVISIBLE
                    mainLayout.visibility = View.VISIBLE
                    rvDaftarReview.visibility = View.VISIBLE
                    // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                    rvDaftarReview = findViewById(R.id.rvDaftarReview)
                    rvDaftarReview.setHasFixedSize(true)
                    rvDaftarReview.layoutManager = LinearLayoutManager(this@KomentarProdukPembeliActivity)
                    adapterReview = KomentarProdukPembeliAdapter(daftarReviewList)
                    rvDaftarReview.adapter = adapterReview
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showListReviewFilter(idFilter: String){
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarReviewListFilter.clear()
                    for (i in snapshot.children){
                        val review = i.getValue(ReviewProduct::class.java)!!
                        if (review != null){
                            if (idFilter == "4"){
                                if (review.review != "" && review.rating >= 4 && review.rating <= 5){
                                    daftarReviewListFilter.add(review)
                                }
                            } else if (idFilter == "3"){
                                if (review.review != "" && review.rating >= 3 && review.rating < 4){
                                    daftarReviewListFilter.add(review)
                                }
                            } else if (idFilter == "2"){
                                if (review.review != "" && review.rating >= 2 && review.rating < 3){
                                    daftarReviewListFilter.add(review)
                                }
                            } else if (idFilter == "1"){
                                if (review.review != "" && review.rating >= 0 && review.rating < 2){
                                    daftarReviewListFilter.add(review)
                                }
                            }
                        }
                    }
                }
                komentarNotFound.visibility = View.INVISIBLE
                mainLayout.visibility = View.VISIBLE
                rvDaftarReview.visibility = View.VISIBLE
                // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                rvDaftarReview = findViewById(R.id.rvDaftarReview)
                rvDaftarReview.setHasFixedSize(true)
                rvDaftarReview.layoutManager = LinearLayoutManager(this@KomentarProdukPembeliActivity)
                adapterReview = KomentarProdukPembeliAdapter(daftarReviewListFilter)
                rvDaftarReview.adapter = adapterReview
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
                    if (!isConnected(this@KomentarProdukPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: KomentarProdukPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}