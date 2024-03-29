package com.wongtlaten.application.modules.pembeli.profile

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.wongtlaten.application.LoginActivity
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.core.LoadingDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.properties.Delegates

class UbahDataPribadiPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var referen : DatabaseReference
    private lateinit var imageUri: Uri
    // Mendefinisikan variabel global dari view
    private lateinit var photoProfil: CircleImageView
    private lateinit var textUbahPhotoProfil : TextView
    private lateinit var textNama : TextView
    private lateinit var textEmail : TextView
    private lateinit var textAlamat : TextView
    private lateinit var textTelepon : TextView
    private lateinit var textKelamin : TextView
    private lateinit var btnSimpan : Button
    private lateinit var nama: String
    private lateinit var kelamin: String
    private lateinit var email: String
    private lateinit var telepon: String
    private lateinit var alamat: String
    private lateinit var oldEmail: String
    private var changeImage by Delegates.notNull<Boolean>()
    private var changeEmail by Delegates.notNull<Boolean>()
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_data_pribadi_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat userIdentity daru auth untuk mendapatkan userid/currrent user
        val userIdentity = auth.currentUser!!
        referen = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(userIdentity.uid)

        textNama = findViewById(R.id.Nama)
        textEmail = findViewById(R.id.Email)
        textAlamat = findViewById(R.id.Alamat)
        textTelepon = findViewById(R.id.NomorTelepon)
        textKelamin = findViewById(R.id.JenisKelamin)
        textUbahPhotoProfil = findViewById(R.id.textUbahPhotoProfil)
        photoProfil = findViewById(R.id.ivProfile)
        btnSimpan = findViewById(R.id.btnSimpanPerubahan)
        changeImage = false
        changeEmail = false
        checkClick = true

        nama = intent.getStringExtra(NAMA)!!
        kelamin = intent.getStringExtra(KELAMIN)!!
        email = intent.getStringExtra(EMAIL)!!
        telepon = intent.getStringExtra(TELEPON)!!
        alamat = intent.getStringExtra(ALAMAT)!!

        textNama.text = nama
        textEmail.text = email
        textAlamat.text = alamat
        textTelepon.text = telepon
        textKelamin.text = kelamin

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = dataSnapshot.getValue(Users::class.java)!!
                oldEmail = users.email
                if (users.photoProfil != ""){
                    Picasso.get().load(users.photoProfil).into(photoProfil)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referen.addListenerForSingleValueEvent(menuListener)


        textNama.setOnClickListener {
            Intent(applicationContext, UbahNamaPembeliActivity::class.java).also {
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

        textEmail.setOnClickListener {
            Intent(applicationContext, UbahEmailPembeliActivity::class.java).also {
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

        textAlamat.setOnClickListener {
            Intent(applicationContext, UbahAlamatPembeliActivity::class.java).also {
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

        textTelepon.setOnClickListener {
            Intent(applicationContext, UbahTeleponPembeliActivity::class.java).also {
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

        textKelamin.setOnClickListener {
            Intent(applicationContext, UbahKelaminPembeliActivity::class.java).also {
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

        // Ketika "textUbahPhotoProfil" di klik, maka akan menjalankan fungsi pickImageGallery()
        textUbahPhotoProfil.setOnClickListener {
            pickImageGallery()
        }

        btnSimpan.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                var onChange = true
                if (kelamin == "belum diisi" || telepon == "belum diisi" || alamat == "belum diisi"){
                    onChange = false
                    alertDialog("Gagal Mengubah Data Pribadi!", "Silakan lengkapi data pribadi anda terlebih dahulu!", false)
                    checkClick = true
                }
                if (onChange){
                    if (oldEmail != email){
                        loadingBar(4000)
                        changeEmail = true
                        userIdentity.let {
                            userIdentity.updateEmail(email).addOnCompleteListener {
                                userIdentity.sendEmailVerification().addOnCompleteListener {
                                    updateData(changeImage)
                                    Intent(this@UbahDataPribadiPembeliActivity, LoginActivity::class.java).also { intent ->
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("CHANGE_EMAIL", "true")
                                        startActivity(intent)
                                        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
                                        val loading = LoadingDialog(this@UbahDataPribadiPembeliActivity)
                                        loading.startDialog()
                                        loading.isDissmis()
                                        finish()
                                        checkClick = true
                                    }
                                }
                            }
                        }
                    } else{
                        updateData(changeImage)
                        Intent(this@UbahDataPribadiPembeliActivity, ProfileDataPribadiPembeliActivity::class.java).also  { intent ->
                            intent.putExtra("CHANGE", "true")
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                            val loading = LoadingDialog(this@UbahDataPribadiPembeliActivity)
                            loading.startDialog()
                            loading.isDissmis()
                            finish()
                            checkClick = true
                        }
                    }
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

    fun updateData(changeImage: Boolean){
        val userIdentity = auth.currentUser!!
        if (changeImage == false){
            // Mengupdate child yang ada pada reference dengan inputan baru,
            val menuListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val users = dataSnapshot.getValue(Users::class.java)!!
                    val usersUpdate = Users(userIdentity.uid, nama, kelamin, alamat, email, users.photoProfil, telepon, users.jumlahTransaksi, users.accessLevel, users.token, users.status, users.checkOtp)
                    referen.setValue(usersUpdate).addOnCompleteListener {
                        if (it.isSuccessful){
                        } else{
                            alertDialog("Gagal Mengubah Data Pribadi!", "${it.exception?.message}", false)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle error
                }
            }

            referen.addListenerForSingleValueEvent(menuListener)
        } else {
            val refImage = FirebaseStorage.getInstance().reference.child("imgProfilCustomer/${userIdentity.uid}")
            refImage.putFile(imageUri).addOnSuccessListener {
                var downloadUrl: Uri? = null
                refImage.downloadUrl.addOnSuccessListener { it1 ->
                    downloadUrl = it1
                    // Mengupdate child yang ada pada reference dengan inputan baru,
                    val menuListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val users = dataSnapshot.getValue(Users::class.java)!!
                            val usersUpdate = Users(userIdentity.uid, nama, kelamin, alamat, email, downloadUrl.toString(), telepon, users.jumlahTransaksi, users.accessLevel, users.token, users.status, users.checkOtp)
                            referen.setValue(usersUpdate).addOnCompleteListener {
                                if (it.isSuccessful){
                                } else{
                                    alertDialog("Gagal Mengubah Data Pribadi!", "${it.exception?.message}", false)
                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            // handle error
                        }
                    }
                    referen.addListenerForSingleValueEvent(menuListener)
                }
            }
        }
    }

    // Membuat fungsi "pickImageGallery",
    // Fungsi ini digunakan untuk memilik photo dari gallery
    fun pickImageGallery() {
        val inten = Intent(Intent.ACTION_PICK)
        inten.type = "image/*"
        startActivityForResult(inten, IMAGE_REQUEST_CODE)
    }

    // Memanggi fungsi turunan "onActivityResult", fungsi ini berjalan ketika activity dibuka
    // Fungsi ini digunakan untuk mengambil image yang telah dipilih di gallery dan dipasang ke photoprofil,
    // dan dimasukkan ke dalam database img, dengan id dari user
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            photoProfil.setImageURI(data?.data)
            imageUri = Uri.parse("${data?.data}")
            changeImage = true
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

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    companion object {
        const val NAMA = "NAMA"
        const val KELAMIN = "KELAMIN"
        const val EMAIL = "EMAIL"
        const val TELEPON = "TELEPON"
        const val ALAMAT = "ALAMAT"
        val IMAGE_REQUEST_CODE = 100
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
                    if (!isConnected(this@UbahDataPribadiPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahDataPribadiPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}