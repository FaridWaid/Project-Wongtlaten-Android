package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.pembeli.customize.CustomizeProdukPembeliActivity
import com.wongtlaten.application.modules.pembeli.home.DetailProdukPembeliActivity
import com.wongtlaten.application.modules.pembeli.home.DetailProdukPembeliActivity.Companion.EXTRA_ID_PRODUCT
import com.wongtlaten.application.modules.pembeli.wishlist.DetailPesananPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliAdapter
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity

class RiwayatTransaksiPembeliActivity : AppCompatActivity() {

    private lateinit var reference : DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var userIdentity : FirebaseUser
    private lateinit var cardInformation: RelativeLayout
    private lateinit var daftarTransaksi: ArrayList<Transaction>
    private lateinit var rvDaftarProduk : RecyclerView
    private lateinit var adapter: RiwayatTransaksiPembeliAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_transaksi_pembeli)

        auth = FirebaseAuth.getInstance()
        userIdentity = auth.currentUser!!

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")

        cardInformation = findViewById(R.id.cardInformation)
        rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
        daftarTransaksi = arrayListOf()

        cardInformation.setOnClickListener {
            Intent(applicationContext, PembayaranPembeliActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
            }
        }

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
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)!!
                        if (transaction.idUser == idUser && transaction.statusPembayaran != "pending"){
                            daftarTransaksi.add(transaction)
                        }
                    }
                }
                rvDaftarProduk.visibility = View.VISIBLE
                // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
                rvDaftarProduk = findViewById(R.id.rvDaftarProduk)
                rvDaftarProduk.setHasFixedSize(true)
                rvDaftarProduk.layoutManager = LinearLayoutManager(this@RiwayatTransaksiPembeliActivity)
                adapter = RiwayatTransaksiPembeliAdapter(daftarTransaksi)
                adapter.setOnItemClickCallback(object : RiwayatTransaksiPembeliAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: ArrayList<Transaction>) {
                        Intent(applicationContext, DetailPesananPembeliActivity::class.java).also {
                            it.putExtra(DetailPesananPembeliActivity.EXTRA_TRANSACTION, data)
                            startActivity(it)
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                            finish()
                        }
                    }

                    override fun btnBeliClicked(data: ArrayList<Transaction>) {
                        if (data[0].jenisTransaksi == "custom"){
                            Intent(applicationContext, CustomizeProdukPembeliActivity::class.java).also {
                                startActivity(it)
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                                finish()
                            }
                        } else if (data[0].jenisTransaksi == "normal"){
                            Intent(applicationContext, DetailProdukPembeliActivity::class.java).also {
                                it.putExtra(EXTRA_ID_PRODUCT, data[0].produkTransaction[0].idProduk)
                                startActivity(it)
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                                finish()
                            }
                        }
                    }

                })
                rvDaftarProduk.adapter = adapter
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


}