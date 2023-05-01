package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Faq
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.Users
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates

class DetailAkunPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var referen : DatabaseReference
    private lateinit var reference : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var textUid: TextView
    private lateinit var textNama: TextView
    private lateinit var textJumlahPembelian: TextView
    private lateinit var textJumlahPembayaran: TextView
    private lateinit var textKelamin: TextView
    private lateinit var textEmail: TextView
    private lateinit var textNomor: TextView
    private lateinit var textAlamat: TextView
    private lateinit var textOtp: TextView
    private lateinit var textStatusAkun: TextView
    private lateinit var photoProfil: CircleImageView
    private lateinit var idUsers: String
    private var cekTotal by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_akun_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        idUsers = intent.getStringExtra(EXTRA_ID_USERSS)!!
        // Membuat referen memiliki child userId, yang nantinya akan diisi oleh data user
        referen = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(idUsers)
        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")

        // Mendefinisikan variabel edit text yang nantinya akan berisi inputan user
        textUid = findViewById(R.id.Uid)
        textNama = findViewById(R.id.textNamaPembeli)
        textJumlahPembelian = findViewById(R.id.jumlahPembelian)
        textJumlahPembayaran = findViewById(R.id.jumlahPembayaran)
        textKelamin = findViewById(R.id.JenisKelamin)
        textEmail = findViewById(R.id.Email)
        textNomor = findViewById(R.id.NomorTelepon)
        textAlamat = findViewById(R.id.Alamat)
        textOtp = findViewById(R.id.Otp)
        textStatusAkun = findViewById(R.id.statusAkun)
        photoProfil = findViewById(R.id.ivProfile)
        cekTotal = 0

        // Memanggil fungsi keepData
        keepData()
        keepDataTotalPembayaran()

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    private fun keepDataTotalPembayaran() {
        reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    cekTotal = 0
                    for (i in snapshot.children){
                        val transaction = i.getValue(com.wongtlaten.application.core.Transaction::class.java)
                        if (transaction != null && transaction.statusPembayaran == "settlement" && transaction.idUser == idUsers){
                            cekTotal += transaction.totalPembayaran.toLong()
                        }
                    }

                }
                val formatter: NumberFormat = DecimalFormat("#,###")
                val formattedNumberPrice: String = formatter.format(cekTotal)
                textJumlahPembayaran.text = "Rp. $formattedNumberPrice"
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun keepData() {
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                textUid.text = users.idUsers
                textNama.text = users.username
                if (users.kelamin == ""){
                    textKelamin.text = "belum diisi"
                } else{
                    textKelamin.text = users.kelamin
                }
                textEmail.text = users.email
                if (users.noTelp == ""){
                    textNomor.text = "belum diisi"
                } else{
                    textNomor.text = users.noTelp
                }
                if (users.alamat == ""){
                    textAlamat.text = "belum diisi"
                } else{
                    textAlamat.text = users.alamat
                }
                textOtp.text = users.checkOtp
                textStatusAkun.text = users.status
                textJumlahPembelian.text = "${users.jumlahTransaksi}"
                if (users.photoProfil.isNotEmpty()){
                    Picasso.get().load(users.photoProfil).into(photoProfil)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
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
                    if (!isConnected(this@DetailAkunPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: DetailAkunPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}