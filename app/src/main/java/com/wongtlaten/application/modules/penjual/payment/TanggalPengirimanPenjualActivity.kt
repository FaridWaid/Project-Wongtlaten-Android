package com.wongtlaten.application.modules.penjual.payment

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.penjual.home.UbahStatusTransaksiActivity
import java.text.SimpleDateFormat
import java.util.*

class TanggalPengirimanPenjualActivity : AppCompatActivity() {

    private lateinit var idTransaksi: String
    private lateinit var tanggalPengiriman : TextView
    private lateinit var btnUbahTanggal : Button
    private lateinit var etWaktuPengiriman : EditText
    private lateinit var etNomorResi : EditText
    private lateinit var btnUbah : Button
    private lateinit var updateDate : String
    private lateinit var statusProduk : String
    private lateinit var statusPembayaran : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanggal_pengiriman_penjual)

        idTransaksi = intent.getStringExtra(EXTRA_ID)!!
        tanggalPengiriman = findViewById(R.id.tanggalPengiriman)
        btnUbahTanggal = findViewById(R.id.btnUbahTanggal)
        etWaktuPengiriman = findViewById(R.id.etWaktuPengiriman)
        etNomorResi = findViewById(R.id.etNomorResi)
        btnUbah = findViewById(R.id.btnUbah)
        statusProduk = ""
        statusPembayaran = ""
        updateDate = ""

        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val format = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(format, Locale.US)
            updateDate = sdf.format(myCalender.time)
            tanggalPengiriman.setText(updateDate)
        }

        btnUbahTanggal.setOnClickListener {
            DatePickerDialog(this, datePicker, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)).show()
        }

        val referenceStatus = FirebaseDatabase.getInstance().getReference("dataTransaksi").child(idTransaksi)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val transaction = dataSnapshot.getValue(Transaction::class.java)!!
                statusPembayaran = transaction.statusPembayaran
                statusProduk = transaction.statusProduk
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referenceStatus.addListenerForSingleValueEvent(menuListener)

        btnUbah.setOnClickListener {
            if (statusPembayaran != "settlement"){
                Toast.makeText(this@TanggalPengirimanPenjualActivity, "Tanggal Pengiriman gagal di ubah, status pembayaran produk masih belum lunas!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (statusProduk != "Selesai") {
                Toast.makeText(this@TanggalPengirimanPenjualActivity, "Tanggal Pengiriman gagal di ubah, status produk masih belum selesai!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else{
                val waktuPengiriman = etWaktuPengiriman.text.toString().trim()
                val nomorResi = etNomorResi.text.toString().trim()

                if (updateDate.length <= 0){
                    Toast.makeText(this@TanggalPengirimanPenjualActivity, "Masukkan tanggal pengiriman terlebih dahulu!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (waktuPengiriman.isEmpty()){
                    etWaktuPengiriman.error = "Masukkan waktu pengiriman terlebih dahulu!"
                    etWaktuPengiriman.requestFocus()
                    return@setOnClickListener
                }
                if (nomorResi.isEmpty()){
                    etNomorResi.error = "Masukkan nomor resi terlebih dahulu!"
                    etNomorResi.requestFocus()
                    return@setOnClickListener
                }

                var waktuPengirimanInput = "$updateDate $waktuPengiriman"
                val referenceStatus = FirebaseDatabase.getInstance().getReference("dataTransaksi").child(idTransaksi)
                val menuListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val transaction = dataSnapshot.getValue(Transaction::class.java)!!
                        var updateTransaction = Transaction(transaction.idUser, transaction.idTransaksi, transaction.jenisTransaksi, transaction.namePenerima, transaction.kotaTujuan, transaction.kodePos, transaction.alamatLengkap, transaction.teleponPenerima, transaction.totalBerat, transaction.jumlahOngkir, transaction.totalPembayaran, transaction.typePembayaran, transaction.waktuTransaksi, waktuPengirimanInput, transaction.statusPembayaran, transaction.statusProduk, transaction.kurir, nomorResi, transaction.catatanGiftcard, transaction.pdfUrl, transaction.produkTransaction)
                        referenceStatus.setValue(updateTransaction).addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(this@TanggalPengirimanPenjualActivity,"Tanggal pengiriman produk berhasil di ubah!", Toast.LENGTH_SHORT).show()
                                onBackPressed()
                                finish()
                            } else{
                                Toast.makeText(this@TanggalPengirimanPenjualActivity,"Tanggal pengiriman produk gagal di ubah!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // handle error
                    }
                }
                referenceStatus.addListenerForSingleValueEvent(menuListener)
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

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object{
        const val EXTRA_ID = "EXTRA_ID"
    }

}