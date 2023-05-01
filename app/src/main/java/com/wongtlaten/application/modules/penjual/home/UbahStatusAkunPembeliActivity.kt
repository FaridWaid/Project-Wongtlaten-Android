package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.core.Users
import kotlin.properties.Delegates

class UbahStatusAkunPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference: DatabaseReference
    private lateinit var idUsers: String
    private lateinit var uidPembeli: TextView
    private lateinit var namaPembeli: TextView
    private lateinit var etStatus: TextInputEditText
    private lateinit var statusContainer: TextInputLayout
    private lateinit var btnUbah: Button
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_status_akun_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        idUsers = intent.getStringExtra(UbahStatusOtpPembeliActivity.EXTRA_ID_USERSS)!!

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(idUsers)

        uidPembeli = findViewById(R.id.uidPembeli)
        namaPembeli = findViewById(R.id.namaPembeli)
        etStatus = findViewById(R.id.etStatus)
        statusContainer = findViewById(R.id.statusContainer)
        btnUbah = findViewById(R.id.btnUbah)
        checkClick = true

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                etStatus.setText(users.status)
                uidPembeli.text = users.idUsers
                namaPembeli.text = users.username
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        reference.addListenerForSingleValueEvent(menuListener)

        // Memanggil fungsi "namaProdukFocusListener", "hargaProdukFocusListener", "promoProdukFocusListener"
        statusFocusListener()

        btnUbah.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                val status = etStatus.text.toString().trim().toLowerCase()

                statusContainer.helperText = validStatus()
                val validStatus = statusContainer.helperText == null

                if (validStatus) {
                    if (status == "active" || status == "inactive"){
                        loadingBar(6000)
                        val menuListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val users = dataSnapshot.getValue(Users::class.java)!!
                                val usersUpdate = Users(users.idUsers, users.username, users.kelamin, users.alamat, users.email, users.photoProfil, users.noTelp, users.jumlahTransaksi, users.accessLevel, users.token, status, users.checkOtp)
                                reference.setValue(usersUpdate).addOnCompleteListener {
                                    if (it.isSuccessful){
                                        alertDialog("BERHASIL!", "Status akun berhasil di ubah!", true)
                                        checkClick = true
                                    } else{
                                        alertDialog("Gagal Mengubah Status Akun!", "${it.exception?.message}", false)
                                        checkClick = true
                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                // handle error
                            }
                        }
                        reference.addListenerForSingleValueEvent(menuListener)
                    } else{
                        alertDialog("GAGAL!", "Status Akun harus active/inactive!", false)
                        checkClick = true
                    }
                } else {
                    loadingBar(1000)
                    alertDialog("GAGAL!", "Status Akun akun gagal di ubah!", false)
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
    private fun statusFocusListener() {
        // Memastikan apakah etNamaProduk sudah sesuai dengan format pengisian
        etStatus.setOnFocusChangeListener { _, focused ->
            if(!focused){
                statusContainer.helperText = validStatus()
            }
        }
    }

    // Membuat fungsi "validNamaProduk"
    private fun validStatus(): String? {
        val status = etStatus.text.toString().toLowerCase()
        // Jika namaProduk kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (status.isEmpty()){
            return "Status harus ditambahkan terlebih dahulu!"
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

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object{
        const val EXTRA_ID_USERSS = "EXTRA_ID_USERSS"
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
                    if (!isConnected(this@UbahStatusAkunPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahStatusAkunPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}