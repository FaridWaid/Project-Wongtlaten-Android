package com.wongtlaten.application.modules.pembeli.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Customers
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity.Companion.NAMA
import de.hdodenhof.circleimageview.CircleImageView

class UbahDataPribadiPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var referen : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var photoProfil: CircleImageView
    private lateinit var textNama : TextView
    private lateinit var textEmail : TextView
    private lateinit var textAlamat : TextView
    private lateinit var textTelepon : TextView
    private lateinit var textKelamin : TextView
    private lateinit var nama: String
    private lateinit var kelamin: String
    private lateinit var email: String
    private lateinit var telepon: String
    private lateinit var alamat: String
    private lateinit var changeNama: String
    private lateinit var changeKelamin: String
    private lateinit var changeEmail: String
    private lateinit var changeTelepon: String
    private lateinit var changeAlamat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_data_pribadi_pembeli)

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat userIdentity daru auth untuk mendapatkan userid/currrent user
        val userIdentity = auth.currentUser!!

        textNama = findViewById(R.id.Nama)
        textEmail = findViewById(R.id.Email)
        textAlamat = findViewById(R.id.Alamat)
        textTelepon = findViewById(R.id.NomorTelepon)
        textKelamin = findViewById(R.id.JenisKelamin)

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

        textNama.setOnClickListener {
            Intent(applicationContext, UbahNamaPembeliActivity::class.java).also {
                it.putExtra("NAMA", nama)
                it.putExtra("KELAMIN", kelamin)
                it.putExtra("EMAIL", email)
                it.putExtra("TELEPON", telepon)
                it.putExtra("ALAMAT", alamat)
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
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
                Picasso.get().load(customers.photoProfil).into(photoProfil)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        referen.addListenerForSingleValueEvent(menuListener)
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
        const val TEMP_NAMA = "TEMP_NAMA"
        const val TEMP_KELAMIN = "TEMP_KELAMIN"
        const val TEMP_EMAIL = "TEMP_EMAIL"
        const val TEMP_TELEPON = "TEMP_TELEPON"
        const val TEMP_ALAMAT = "TEMP_ALAMAT"
        const val CHANGE_NAMA = "CHANGE_NAMA"
        const val CHANGE_KELAMIN = "CHANGE_KELAMIN"
        const val CHANGE_EMAIL = "CHANGE_EMAIL"
        const val CHANGE_TELEPON = "CHANGE_TELEPON"
        const val CHANGE_ALAMAT = "CHANGE_ALAMAT"
    }

}