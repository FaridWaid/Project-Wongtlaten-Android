package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.ReviewProduct
import com.wongtlaten.application.modules.pembeli.home.KomentarProdukPembeliActivity
import com.wongtlaten.application.modules.pembeli.home.KomentarProdukPembeliAdapter
import com.wongtlaten.application.modules.pembeli.wishlist.DaftarKeranjangProdukAdapter
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.properties.Delegates

class KomentarProdukPenjualActivity : AppCompatActivity() {

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
    private lateinit var rvDaftarRating: RecyclerView
    private lateinit var daftarReviewList: ArrayList<ReviewProduct>
    private lateinit var daftarRatingList: ArrayList<ReviewProduct>
    private lateinit var daftarReviewListFilter: ArrayList<ReviewProduct>
    private lateinit var daftarRatingListFilter: ArrayList<ReviewProduct>
    private lateinit var adapterReview: KomentarProdukPenjualAdapter
    private lateinit var adapterRating: RatingProdukPenjualAdapter
    private lateinit var ratingInactive : TextView
    private lateinit var ratingActive : TextView
    private lateinit var komentarInactive : TextView
    private lateinit var komentarActive : TextView
    private lateinit var textNoReview: TextView
    private var cekCheckbox by Delegates.notNull<Int>()
    private lateinit var cekStatusFilter: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_komentar_produk_penjual)

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
        ratingInactive = findViewById(R.id.ratingInactive)
        ratingActive = findViewById(R.id.ratingActive)
        komentarInactive = findViewById(R.id.komentarInactive)
        komentarActive = findViewById(R.id.komentarActive)
        textNoReview = findViewById(R.id.textNoReview)
        cekStatusFilter = "komentar"

        filterRatingInactive.visibility = View.VISIBLE
        filterRatingActive.visibility = View.INVISIBLE

        filterRatingInactive.setOnClickListener {
            filterRating()
        }

        filterRatingActive.setOnClickListener {
            filterRating()
        }

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarReview = findViewById(R.id.rvDaftarReview)
        rvDaftarReview.setHasFixedSize(true)
        rvDaftarReview.layoutManager = LinearLayoutManager(this)
        rvDaftarRating = findViewById(R.id.rvDaftarRating)
        rvDaftarRating.setHasFixedSize(true)
        rvDaftarRating.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProduk ke dalam "daftarFsList" sebagai array list
        daftarReviewList = arrayListOf<ReviewProduct>()
        daftarRatingList = arrayListOf<ReviewProduct>()
        daftarReviewListFilter = arrayListOf<ReviewProduct>()
        daftarRatingListFilter = arrayListOf<ReviewProduct>()

        komentarActive.visibility = View.VISIBLE
        komentarInactive.visibility = View.INVISIBLE
        ratingInactive.visibility = View.VISIBLE
        ratingActive.visibility = View.INVISIBLE

        showListKomentar("komentar")

        ratingInactive.setOnClickListener {
            ratingActive.visibility = View.VISIBLE
            ratingInactive.visibility = View.INVISIBLE
            komentarActive.visibility = View.INVISIBLE
            komentarInactive.visibility = View.VISIBLE
            filterRatingInactive.visibility = View.VISIBLE
            filterRatingActive.visibility = View.INVISIBLE
            textFilterReview.text = "Semua"
            textNoReview.text = "Belum Ada Rating!"
            cekStatusFilter = "rating"
            showListKomentar("rating")
        }
        komentarInactive.setOnClickListener {
            komentarActive.visibility = View.VISIBLE
            komentarInactive.visibility = View.INVISIBLE
            ratingActive.visibility = View.INVISIBLE
            ratingInactive.visibility = View.VISIBLE
            filterRatingInactive.visibility = View.VISIBLE
            filterRatingActive.visibility = View.INVISIBLE
            textFilterReview.text = "Semua"
            textNoReview.text = "Belum Ada Komentar!"
            cekStatusFilter = "komentar"
            showListKomentar("komentar")
        }

        adapterReview = KomentarProdukPenjualAdapter(daftarReviewList)

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
        var dialog = Dialog(this@KomentarProdukPenjualActivity)
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
                showListReviewFilter(cekStatusFilter,"1")
                dialog.dismiss()
            } else if (cekCheckbox == 2){
                textFilterReview.text = "2 - 2,9 Rating"
                showListReviewFilter(cekStatusFilter,"2")
                dialog.dismiss()
            } else if (cekCheckbox == 3){
                textFilterReview.text = "3 - 3,9 Rating"
                showListReviewFilter(cekStatusFilter,"3")
                dialog.dismiss()
            } else if (cekCheckbox == 4){
                textFilterReview.text = "4 - 5 Rating"
                showListReviewFilter(cekStatusFilter,"4")
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showListKomentar(idFilter: String){
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarReviewList.clear()
                    daftarRatingList.clear()
                    var countReview = 0
                    var countRating = 0
                    for (i in snapshot.children){
                        val review = i.getValue(ReviewProduct::class.java)!!
                        if (review != null){
                            countRating += 1
                            daftarRatingList.add(review)
                            if (review.review != ""){
                                daftarReviewList.add(review)
                                countReview += 1
                            }
                        }
                    }
                    textCountReview.text = "$countRating rating - $countReview komentar"
                    komentarActive.text = "Komentar ($countReview)"
                    komentarInactive.text = "Komentar ($countReview)"
                    ratingActive.text = "Rating ($countRating)"
                    ratingInactive.text = "Rating ($countRating)"
                }
                if (idFilter == "komentar"){
                    if (daftarReviewList.isEmpty()){
                        komentarNotFound.visibility = View.VISIBLE
                        textNoReview.text = "Belum Ada Komentar!"
                    } else {
                        komentarNotFound.visibility = View.INVISIBLE
                        mainLayout.visibility = View.VISIBLE
                        rvDaftarRating.visibility = View.INVISIBLE
                        rvDaftarReview.visibility = View.VISIBLE
                        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                        rvDaftarReview = findViewById(R.id.rvDaftarReview)
                        rvDaftarReview.setHasFixedSize(true)
                        rvDaftarReview.layoutManager = LinearLayoutManager(this@KomentarProdukPenjualActivity)
                        adapterReview = KomentarProdukPenjualAdapter(daftarReviewList)
                        adapterReview.setOnItemClickCallback(object : KomentarProdukPenjualAdapter.OnItemClickCallback{
                            override fun onItemClicked() {
                                showListKomentar("komentar")
                                filterRatingInactive.visibility = View.VISIBLE
                                filterRatingActive.visibility = View.INVISIBLE
                                textFilterReview.text = "Semua"
                            }

                        })
                        rvDaftarReview.adapter = adapterReview
                    }
                } else if (idFilter == "rating"){
                    if (daftarRatingList.isEmpty()){
                        komentarNotFound.visibility = View.VISIBLE
                        textNoReview.text = "Belum Ada Rating!"
                    } else {
                        komentarNotFound.visibility = View.INVISIBLE
                        mainLayout.visibility = View.VISIBLE
                        rvDaftarReview.visibility = View.INVISIBLE
                        rvDaftarRating.visibility = View.VISIBLE
                        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                        rvDaftarRating = findViewById(R.id.rvDaftarRating)
                        rvDaftarRating.setHasFixedSize(true)
                        rvDaftarRating.layoutManager = LinearLayoutManager(this@KomentarProdukPenjualActivity)
                        adapterRating = RatingProdukPenjualAdapter(daftarRatingList)
                        adapterRating.setOnItemClickCallback(object : RatingProdukPenjualAdapter.OnItemClickCallback{
                            override fun onItemClicked() {
                                showListKomentar("rating")
                                filterRatingInactive.visibility = View.VISIBLE
                                filterRatingActive.visibility = View.INVISIBLE
                                textFilterReview.text = "Semua"
                            }

                        })
                        rvDaftarRating.adapter = adapterRating
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showListReviewFilter(idFilterReview: String, idFilterRating: String){
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarReviewListFilter.clear()
                    daftarRatingListFilter.clear()
                    for (i in snapshot.children){
                        val review = i.getValue(ReviewProduct::class.java)!!
                        if (review != null){
                            if (idFilterRating == "4"){
                                if (review.review != "" && review.rating >= 4 && review.rating <= 5){
                                    daftarReviewListFilter.add(review)
                                    daftarRatingListFilter.add(review)
                                } else if (review.review == "" && review.rating >= 4 && review.rating <= 5){
                                    daftarRatingListFilter.add(review)
                                }
                            } else if (idFilterRating == "3"){
                                if (review.review != "" && review.rating >= 3 && review.rating < 4){
                                    daftarReviewListFilter.add(review)
                                    daftarRatingListFilter.add(review)
                                } else if (review.review == "" && review.rating >= 3 && review.rating < 4){
                                    daftarRatingListFilter.add(review)
                                }
                            } else if (idFilterRating == "2"){
                                if (review.review != "" && review.rating >= 2 && review.rating < 3){
                                    daftarReviewListFilter.add(review)
                                    daftarRatingListFilter.add(review)
                                } else if (review.review == "" && review.rating >= 2 && review.rating < 3){
                                    daftarRatingListFilter.add(review)
                                }
                            } else if (idFilterRating == "1"){
                                if (review.review != "" && review.rating >= 0 && review.rating < 2){
                                    daftarReviewListFilter.add(review)
                                    daftarRatingListFilter.add(review)
                                } else if (review.review == "" && review.rating >= 0 && review.rating < 2){
                                    daftarRatingListFilter.add(review)
                                }
                            }
                        }
                    }
                }
                if (idFilterReview == "komentar"){
                    komentarNotFound.visibility = View.INVISIBLE
                    mainLayout.visibility = View.VISIBLE
                    rvDaftarRating.visibility = View.INVISIBLE
                    rvDaftarReview.visibility = View.VISIBLE
                    // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                    rvDaftarReview = findViewById(R.id.rvDaftarReview)
                    rvDaftarReview.setHasFixedSize(true)
                    rvDaftarReview.layoutManager = LinearLayoutManager(this@KomentarProdukPenjualActivity)
                    adapterReview = KomentarProdukPenjualAdapter(daftarReviewListFilter)
                    adapterReview.setOnItemClickCallback(object : KomentarProdukPenjualAdapter.OnItemClickCallback{
                        override fun onItemClicked() {
                            showListKomentar("komentar")
                            filterRatingInactive.visibility = View.VISIBLE
                            filterRatingActive.visibility = View.INVISIBLE
                            textFilterReview.text = "Semua"
                        }

                    })
                    rvDaftarReview.adapter = adapterReview
                } else if (idFilterReview == "rating"){
                    komentarNotFound.visibility = View.INVISIBLE
                    mainLayout.visibility = View.VISIBLE
                    rvDaftarReview.visibility = View.INVISIBLE
                    rvDaftarRating.visibility = View.VISIBLE
                    // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                    rvDaftarRating = findViewById(R.id.rvDaftarRating)
                    rvDaftarRating.setHasFixedSize(true)
                    rvDaftarRating.layoutManager = LinearLayoutManager(this@KomentarProdukPenjualActivity)
                    adapterRating = RatingProdukPenjualAdapter(daftarRatingListFilter)
                    adapterRating.setOnItemClickCallback(object : RatingProdukPenjualAdapter.OnItemClickCallback{
                        override fun onItemClicked() {
                            showListKomentar("rating")
                            filterRatingInactive.visibility = View.VISIBLE
                            filterRatingActive.visibility = View.INVISIBLE
                            textFilterReview.text = "Semua"
                        }

                    })
                    rvDaftarRating.adapter = adapterRating
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
                    if (!isConnected(this@KomentarProdukPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: KomentarProdukPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}