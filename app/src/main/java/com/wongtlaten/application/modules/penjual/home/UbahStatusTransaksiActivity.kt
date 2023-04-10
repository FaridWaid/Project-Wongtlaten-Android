package com.wongtlaten.application.modules.penjual.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.penjual.payment.DetailPesananPenjualActivity

class UbahStatusTransaksiActivity : AppCompatActivity() {

    private lateinit var idTransaksi: String
    private lateinit var checkbox1 : CheckBox
    private lateinit var checkbox2 : CheckBox
    private lateinit var checkbox3 : CheckBox
    private lateinit var btnTerapkanActivated : Button
    private lateinit var btnTerapkanInactivated : Button
    private lateinit var statusTransaksi: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_status_transaksi)

        idTransaksi = intent.getStringExtra(EXTRA_ID)!!

        statusTransaksi = ""
        checkbox1 = findViewById(R.id.checkbox1)
        checkbox2 = findViewById(R.id.checkbox2)
        checkbox3 = findViewById(R.id.checkbox3)
        btnTerapkanActivated = findViewById(R.id.btnTerapkanActivated)
        btnTerapkanInactivated = findViewById(R.id.btnTerapkanInactivated)

        btnTerapkanInactivated.visibility = View.VISIBLE

        checkbox1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkbox2.isChecked = false
                checkbox3.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
                statusTransaksi = "Belum Diproses"
            } else {
                btnTerapkanInactivated.visibility = View.VISIBLE
                btnTerapkanActivated.visibility = View.INVISIBLE
            }
        }

        checkbox2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkbox1.isChecked = false
                checkbox3.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
                statusTransaksi = "Proses"
            } else {
                btnTerapkanInactivated.visibility = View.VISIBLE
                btnTerapkanActivated.visibility = View.INVISIBLE
            }
        }

        checkbox3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkbox1.isChecked = false
                checkbox2.isChecked = false
                btnTerapkanActivated.visibility = View.VISIBLE
                btnTerapkanInactivated.visibility = View.INVISIBLE
                statusTransaksi = "Selesai"
            } else {
                btnTerapkanInactivated.visibility = View.VISIBLE
                btnTerapkanActivated.visibility = View.INVISIBLE
            }
        }

        btnTerapkanActivated.setOnClickListener {
            val referenceStatus = FirebaseDatabase.getInstance().getReference("dataTransaksi").child(idTransaksi)
            val menuListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val transaction = dataSnapshot.getValue(Transaction::class.java)!!
                    var updateTransaction = Transaction(transaction.idUser, transaction.idTransaksi, transaction.jenisTransaksi, transaction.namePenerima, transaction.kotaTujuan, transaction.kodePos, transaction.alamatLengkap, transaction.teleponPenerima, transaction.totalBerat, transaction.jumlahOngkir, transaction.totalPembayaran, transaction.typePembayaran, transaction.waktuTransaksi, transaction.waktuPengiriman, transaction.statusPembayaran, statusTransaksi, transaction.kurir, transaction.resiPengiriman, transaction.catatanGiftcard, transaction.pdfUrl, transaction.produkTransaction)
                    referenceStatus.setValue(updateTransaction).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this@UbahStatusTransaksiActivity,"Status produk berhasil di ubah!", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                            finish()
                        } else{
                            Toast.makeText(this@UbahStatusTransaksiActivity,"Status produk gagal di ubah!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }
            referenceStatus.addListenerForSingleValueEvent(menuListener)
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

    companion object{
        const val EXTRA_ID = "EXTRA_ID"
    }

}