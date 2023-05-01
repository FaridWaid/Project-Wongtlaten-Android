package com.wongtlaten.application.modules.penjual.payment

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.penjual.home.UbahStatusTransaksiActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

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
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tanggal_pengiriman_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        idTransaksi = intent.getStringExtra(EXTRA_ID)!!
        tanggalPengiriman = findViewById(R.id.tanggalPengiriman)
        btnUbahTanggal = findViewById(R.id.btnUbahTanggal)
        etWaktuPengiriman = findViewById(R.id.etWaktuPengiriman)
        etNomorResi = findViewById(R.id.etNomorResi)
        btnUbah = findViewById(R.id.btnUbah)
        statusProduk = ""
        statusPembayaran = ""
        updateDate = ""
        checkClick = true

        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR, year)
            myCalender.set(Calendar.MONTH, month)
            myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val format = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(format, Locale.US)
            updateDate = sdf.format(myCalender.time)
            tanggalPengiriman.setText(updateDate)
            checkClick = true
        }

        btnUbahTanggal.setOnClickListener {
            if (checkClick){
                checkClick = false
                DatePickerDialog(this, datePicker, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH),
                    myCalender.get(Calendar.DAY_OF_MONTH)).show()
            } else{
                return@setOnClickListener
            }
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

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick){
                checkClick = false
                if (statusPembayaran != "settlement"){
                    Toast.makeText(this@TanggalPengirimanPenjualActivity, "Tanggal Pengiriman gagal di ubah, status pembayaran produk masih belum lunas!", Toast.LENGTH_SHORT).show()
                    checkClick = true
                    return@setOnClickListener
                } else if (statusProduk != "Selesai") {
                    Toast.makeText(this@TanggalPengirimanPenjualActivity, "Tanggal Pengiriman gagal di ubah, status produk masih belum selesai!", Toast.LENGTH_SHORT).show()
                    checkClick = true
                    return@setOnClickListener
                } else{
                    val waktuPengiriman = etWaktuPengiriman.text.toString().trim()
                    val nomorResi = etNomorResi.text.toString().trim()

                    if (updateDate.length <= 0){
                        Toast.makeText(this@TanggalPengirimanPenjualActivity, "Masukkan tanggal pengiriman terlebih dahulu!", Toast.LENGTH_SHORT).show()
                        checkClick = true
                        return@setOnClickListener
                    }

                    if (waktuPengiriman.isEmpty()){
                        etWaktuPengiriman.error = "Masukkan waktu pengiriman terlebih dahulu!"
                        etWaktuPengiriman.requestFocus()
                        checkClick = true
                        return@setOnClickListener
                    }
                    if (nomorResi.isEmpty()){
                        etNomorResi.error = "Masukkan nomor resi terlebih dahulu!"
                        etNomorResi.requestFocus()
                        checkClick = true
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
                                    checkClick = true
                                    onBackPressed()
                                    finish()
                                } else{
                                    Toast.makeText(this@TanggalPengirimanPenjualActivity,"Tanggal pengiriman produk gagal di ubah!", Toast.LENGTH_SHORT).show()
                                    checkClick = true
                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // handle error
                        }
                    }
                    referenceStatus.addListenerForSingleValueEvent(menuListener)
                }
            } else{
                return@setOnClickListener
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
                    if (!isConnected(this@TanggalPengirimanPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: TanggalPengirimanPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}