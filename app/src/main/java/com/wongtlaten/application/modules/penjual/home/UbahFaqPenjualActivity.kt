package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Faq
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.core.Users
import kotlin.properties.Delegates

class UbahFaqPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference: DatabaseReference
    private lateinit var idFaq: String
    private lateinit var etJudul: TextInputEditText
    private lateinit var judulContainer: TextInputLayout
    private lateinit var etDeskripsi: TextInputEditText
    private lateinit var deskripsiContainer: TextInputLayout
    private lateinit var btnUbah: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_faq_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        idFaq = intent.getStringExtra(EXTRA_ID_FAQ)!!

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataFaq").child(idFaq)

        etJudul = findViewById(R.id.etJudul)
        judulContainer = findViewById(R.id.judulContainer)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        deskripsiContainer = findViewById(R.id.deskripsiContainer)
        btnUbah = findViewById(R.id.btnUbah)
        checkClick = true

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val faq = dataSnapshot.getValue(Faq::class.java)!!
                etJudul.setText(faq.pertanyaan)
                etDeskripsi.setText(faq.jawaban)
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        reference.addListenerForSingleValueEvent(menuListener)

        // Memanggil fungsi "namaProdukFocusListener", "hargaProdukFocusListener", "promoProdukFocusListener"
        judulFocusListener()
        deskripsiFocusListener()

        btnUbah.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                val judul = etJudul.text.toString().trim()
                val deskripsi = etDeskripsi.text.toString().trim()

                judulContainer.helperText = validJudul()
                deskripsiContainer.helperText = validDeskripsi()
                val validJudul = judulContainer.helperText == null
                val validDeskripsi = deskripsiContainer.helperText == null

                if (validJudul && validDeskripsi) {
                    addNewFaq(judul, deskripsi)
                    loadingBar(6000)
                } else {
                    loadingBar(1000)
                    alertDialog("GAGAL!", "Bantuan baru gagal ditambahkan, silakan isi judul dan deskripsi bantuan terlebih dahulu!", false)
                    checkClick = true
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

    private fun addNewFaq(judul: String, deskripsi: String) {
        val faqUpdate = Faq(idFaq, deskripsi, judul)
        reference.setValue(faqUpdate).addOnCompleteListener {
            if (it.isSuccessful) {
                val loading = LoadingDialog(this@UbahFaqPenjualActivity)
                loading.startDialog()
                loading.isDissmis()
                alertDialog("BERHASIL!", "Bantuan berhasil diubah!", true)
                checkClick = true
            } else {
                val loading = LoadingDialog(this@UbahFaqPenjualActivity)
                loading.startDialog()
                loading.isDissmis()
                alertDialog("GAGAL!", "Bantuan gagal diubah!", false)
                checkClick = true
            }
        }
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object{
        const val EXTRA_ID_FAQ = "EXTRA_ID_FAQ"
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

    // Membuat fungsi "namaProdukFocusListener"
    private fun judulFocusListener() {
        // Memastikan apakah etNamaProduk sudah sesuai dengan format pengisian
        etJudul.setOnFocusChangeListener { _, focused ->
            if(!focused){
                judulContainer.helperText = validJudul()
            }
        }
    }

    // Membuat fungsi "validNamaProduk"
    private fun validJudul(): String? {
        val judul = etJudul.text.toString()
        // Jika namaProduk kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (judul.isEmpty()){
            return "Pertanyaan harus ditambahkan terlebih dahulu!"
        }
        return null
    }

    // Membuat fungsi "namaProdukFocusListener"
    private fun deskripsiFocusListener() {
        // Memastikan apakah etNamaProduk sudah sesuai dengan format pengisian
        etDeskripsi.setOnFocusChangeListener { _, focused ->
            if(!focused){
                deskripsiContainer.helperText = validDeskripsi()
            }
        }
    }

    // Membuat fungsi "validNamaProduk"
    private fun validDeskripsi(): String? {
        val deskripsi = etDeskripsi.text.toString()
        // Jika namaProduk kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (deskripsi.isEmpty()){
            return "Jawaban pertanyaan harus ditambahkan terlebih dahulu!"
        }
        return null
    }

    // Membuat fungsi "loadingBar" dengan parameter time,
    // Fungsi ini digunakan untuk menampilkan loading dialog
    private fun loadingBar(time: Long) {
        val loading = LoadingDialog(this)
        loading.startDialog()
        val handler = Handler()
        handler.postDelayed(object: Runnable{
            override fun run() {
                loading.isDissmis()
            }

        }, time)
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
                    if (!isConnected(this@UbahFaqPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahFaqPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}