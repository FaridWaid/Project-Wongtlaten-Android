package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Faq
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.modules.pembeli.wishlist.DetailPesananPembeliActivity
import com.wongtlaten.application.modules.penjual.payment.DetailPesananPenjualActivity

class DaftarSaldoPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var rvDaftarSaldo: RecyclerView
    private lateinit var daftarSaldoList: ArrayList<Transaction>
    private lateinit var adapter: DaftarSaldoPenjualAdapter
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_saldo_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarSaldo = findViewById(R.id.rvDaftarSaldo)
        rvDaftarSaldo.setHasFixedSize(true)
        rvDaftarSaldo.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProduk ke dalam "daftarProdukList" sebagai array list
        daftarSaldoList = arrayListOf<Transaction>()

        adapter = DaftarSaldoPenjualAdapter(daftarSaldoList)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListSaldo()

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    private fun showListSaldo() {
        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarSaldoList.clear()
                    for (i in snapshot.children){
                        val transaction = i.getValue(com.wongtlaten.application.core.Transaction::class.java)
                        if (transaction != null && transaction.statusPembayaran == "settlement"){
                            daftarSaldoList.add(transaction)
                        }
                    }

                    adapter = DaftarSaldoPenjualAdapter(daftarSaldoList)
                    adapter.setOnItemClickCallback(object : DaftarSaldoPenjualAdapter.OnItemClickCallback{
                        override fun onItemClicked(data: ArrayList<Transaction>) {
                            Intent(applicationContext, DetailPesananPenjualActivity::class.java).also {
                                it.putExtra(DetailPesananPembeliActivity.EXTRA_TRANSACTION, data)
                                startActivity(it)
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                                finish()
                            }
                        }

                    })
                    rvDaftarSaldo.adapter = adapter

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
                    if (!isConnected(this@DaftarSaldoPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: DaftarSaldoPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}