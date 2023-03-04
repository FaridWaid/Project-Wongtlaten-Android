package com.wongtlaten.application.modules.penjual.profile

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Customers
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.modules.pembeli.profile.ProfileDataPribadiPembeliActivity
import com.wongtlaten.application.modules.pembeli.profile.UbahDataPribadiPembeliActivity
import de.hdodenhof.circleimageview.CircleImageView

class ProfileDataPribadiPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var referen : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var textUid: TextView
    private lateinit var textNama: TextView
    private lateinit var textKelamin: TextView
    private lateinit var textEmail: TextView
    private lateinit var textNomor: TextView
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
        setContentView(R.layout.activity_profile_data_pribadi_penjual)

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Membuat userIdentity daru auth untuk mendapatkan userid/currrent user
        val userIdentity = auth.currentUser!!

        // Membuat referen memiliki child userId, yang nantinya akan diisi oleh data user
        referen = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child(userIdentity.uid)

        // Mendefinisikan variabel edit text yang nantinya akan berisi inputan user
        textUid = findViewById(R.id.Uid)
        textNama = findViewById(R.id.Nama)
        textKelamin = findViewById(R.id.JenisKelamin)
        textEmail = findViewById(R.id.Email)
        textNomor = findViewById(R.id.NomorTelepon)
        photoProfil = findViewById(R.id.ivProfile)
        nama = ""
        kelamin = ""
        email = ""
        telepon = ""
        alamat = ""

        try {
            change = intent.getStringExtra(ProfileDataPribadiPembeliActivity.CHANGE)!!
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
            Intent(applicationContext, UbahDataPribadiPenjualActivity::class.java).also {
                it.putExtra("NAMA", nama)
                it.putExtra("KELAMIN", kelamin)
                it.putExtra("EMAIL", email)
                it.putExtra("TELEPON", telepon)
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
                Picasso.get().load(customers.photoProfil).into(photoProfil)
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

}