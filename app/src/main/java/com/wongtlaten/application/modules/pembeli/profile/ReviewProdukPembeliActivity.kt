package com.wongtlaten.application.modules.pembeli.profile

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.penjual.home.KomentarProdukPenjualAdapter
import com.wongtlaten.application.modules.penjual.home.TambahFaqPenjualActivity

class ReviewProdukPembeliActivity : AppCompatActivity() {

    private lateinit var reference : DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var userIdentity : FirebaseUser
    private lateinit var daftarTransaksi: ArrayList<Transaction>
    private lateinit var daftarReviewProduk: ArrayList<String>
    private lateinit var reviewNotFound : LinearLayout
    private lateinit var rvDaftarReview : RecyclerView
    private lateinit var adapter: ReviewProdukPembeliAdapter

    override fun onResume() {
        super.onResume()
        auth = FirebaseAuth.getInstance()
        userIdentity = auth.currentUser!!
        showListTransaction(userIdentity.uid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_produk_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        auth = FirebaseAuth.getInstance()
        userIdentity = auth.currentUser!!

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")

        reviewNotFound = findViewById(R.id.reviewNotFound)
        rvDaftarReview = findViewById(R.id.rvDaftarReview)
        daftarTransaksi = arrayListOf()
        daftarReviewProduk = arrayListOf()

        showListTransaction(userIdentity.uid)

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
        
    }

    private fun showListTransaction(idUser: String){
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarTransaksi.clear()
                    daftarReviewProduk.clear()
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)!!
                        if (transaction.idUser == idUser && transaction.statusPembayaran == "settlement" && transaction.jenisTransaksi == "normal" && transaction.statusProduk == "Selesai"){
                            for (j in 0..transaction.produkTransaction.size - 1){
                                if (transaction.produkTransaction[j].statusReview == "none"){
                                    if (daftarTransaksi.filter { it.idTransaksi == transaction.idTransaksi }.size == 0){
                                        daftarTransaksi.add(transaction)
                                    }
                                    daftarReviewProduk.add(transaction.produkTransaction[j].idProduk)
                                }
                            }
                        }
                    }
                }
                if (daftarTransaksi.isNotEmpty()){
                    reviewNotFound.visibility = View.INVISIBLE
                    rvDaftarReview.visibility = View.VISIBLE
                    // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                    rvDaftarReview = findViewById(R.id.rvDaftarReview)
                    rvDaftarReview.setHasFixedSize(true)
                    rvDaftarReview.layoutManager = LinearLayoutManager(this@ReviewProdukPembeliActivity)
                    adapter = ReviewProdukPembeliAdapter(daftarTransaksi, daftarReviewProduk)
                    rvDaftarReview.adapter = adapter
                } else {
                    adapter = ReviewProdukPembeliAdapter(daftarTransaksi, daftarReviewProduk)
                    rvDaftarReview.adapter = adapter
                    reviewNotFound.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        finish()
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
                    if (!isConnected(this@ReviewProdukPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: ReviewProdukPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}