package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.ForgotPasswordActivity
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Customers
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_data_pribadi_pembeli)

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat userIdentity daru auth untuk mendapatkan userid/currrent user
        val userIdentity = auth.currentUser!!

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

        // Membuat referen memiliki child userId, yang nantinya akan diisi oleh data user
        referen = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child(userIdentity.uid)

        // Memanggil fungsi loadingBar dan mengeset time = 4000
        loadingBar(1000)

        // Memanggil fungsi keepData
        keepData()

        btnUbahDataPribadi = findViewById(R.id.btnUbahDataPribadi)
        btnUbahDataPribadi.setOnClickListener {
            Intent(applicationContext, UbahDataPribadiPembeliActivity::class.java).also {
                it.putExtra("NAMA", nama)
                it.putExtra("KELAMIN", kelamin)
                it.putExtra("EMAIL", email)
                it.putExtra("TELEPON", telepon)
                it.putExtra("ALAMAT", alamat)
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
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

    private fun keepData() {
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val customers = dataSnapshot.getValue(Customers::class.java)!!
                textUid.text = customers.idCustomers
                textNama.text = customers.username
                nama = customers.username
                if (customers.kelamin == ""){
                    textKelamin.text = "belum diisi"
                    kelamin = "belum diisi"
                } else{
                    textKelamin.text = customers.kelamin
                    kelamin = customers.kelamin
                }
                textEmail.text = customers.email
                email = customers.email
                if (customers.noTelp == ""){
                    textNomor.text = "belum diisi"
                    telepon = "belum diisi"
                } else{
                    textNomor.text = customers.noTelp
                    telepon = customers.noTelp
                }
                if (customers.alamat == ""){
                    textAlamat.text = "belum diisi"
                    alamat = "belum diisi"
                } else{
                    textAlamat.text = customers.alamat
                    alamat = customers.alamat
                }
                Picasso.get().load(customers.photoProfil).into(photoProfil)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
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

}