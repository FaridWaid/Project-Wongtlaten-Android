package com.wongtlaten.application.modules.pembeli.profile

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.core.LoadingDialog
import de.hdodenhof.circleimageview.CircleImageView

class ProfileDataPribadiPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var referen : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var textUid: TextView
    private lateinit var textNama: TextView
    private lateinit var textKelamin: TextView
    private lateinit var textEmail: TextView
    private lateinit var textNomor: TextView
    private lateinit var textAlamat: TextView
    private lateinit var photoProfil: CircleImageView
    private lateinit var btnUbahDataPribadi : Button
    private lateinit var nama : String
    private lateinit var kelamin : String
    private lateinit var email : String
    private lateinit var telepon : String
    private lateinit var alamat : String
    private lateinit var change : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_data_pribadi_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat userIdentity daru auth untuk mendapatkan userid/currrent user
        val userIdentity = auth.currentUser!!

        // Membuat referen memiliki child userId, yang nantinya akan diisi oleh data user
        referen = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(userIdentity.uid)

        // Mendefinisikan variabel edit text yang nantinya akan berisi inputan user
        textUid = findViewById(R.id.Uid)
        textNama = findViewById(R.id.Nama)
        textKelamin = findViewById(R.id.JenisKelamin)
        textEmail = findViewById(R.id.Email)
        textNomor = findViewById(R.id.NomorTelepon)
        textAlamat = findViewById(R.id.Alamat)
        photoProfil = findViewById(R.id.ivProfile)
        nama = ""
        kelamin = ""
        email = ""
        telepon = ""
        alamat = ""

        try {
            change = intent.getStringExtra(CHANGE)!!
        } catch (e: Exception){
            change = "false"
        }

        if (change == "true"){
            alertDialog("Konfirmasi!", "Data pribadi anda berhasil diubah!", false)
        }

        // Memanggil fungsi loadingBar dan mengeset time = 2000
        loadingBar(2000)

        // Memanggil fungsi keepData
        keepData()

        btnUbahDataPribadi = findViewById(R.id.btnUbahDataPribadi)
        btnUbahDataPribadi.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            Intent(applicationContext, UbahDataPribadiPembeliActivity::class.java).also {
                it.putExtra("NAMA", nama)
                it.putExtra("KELAMIN", kelamin)
                it.putExtra("EMAIL", email)
                it.putExtra("TELEPON", telepon)
                it.putExtra("ALAMAT", alamat)
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                finish()
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

    fun keepData() {
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                textUid.text = users.idUsers
                textNama.text = users.username
                nama = users.username
                if (users.kelamin == ""){
                    textKelamin.text = "belum diisi"
                    kelamin = "belum diisi"
                } else{
                    textKelamin.text = users.kelamin
                    kelamin = users.kelamin
                }
                textEmail.text = users.email
                email = users.email
                if (users.noTelp == ""){
                    textNomor.text = "belum diisi"
                    telepon = "belum diisi"
                } else{
                    textNomor.text = users.noTelp
                    telepon = users.noTelp
                }
                if (users.alamat == ""){
                    textAlamat.text = "belum diisi"
                    alamat = "belum diisi"
                } else{
                    textAlamat.text = users.alamat
                    alamat = users.alamat
                }
                Picasso.get().load(users.photoProfil).into(photoProfil)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
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
            setCancelable(false)
            window.setBackgroundDrawableResource(android.R.color.background_light)
            setCancelable(false)
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                    if (backActivity){
                        onBackPressed()
                    }
                    // Memanggil fungsi keepData
                    keepData()
                })
        }
        alertDialog.show()
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
        const val CHANGE = "CHANGE"
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
                    if (!isConnected(this@ProfileDataPribadiPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: ProfileDataPribadiPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}