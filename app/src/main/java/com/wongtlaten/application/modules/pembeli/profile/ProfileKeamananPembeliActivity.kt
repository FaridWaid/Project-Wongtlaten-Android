package com.wongtlaten.application.modules.pembeli.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.core.Customers


class ProfileKeamananPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var ref : DatabaseReference
    private lateinit var nextUbahPassword : AppCompatImageView
    private lateinit var switchCheck : SwitchCompat
    private lateinit var check : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_keamanan_pembeli)

        // Mengisi variabel auth dengan fungsi yang ada pada FirebaseAuth
        auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!
        // Membuat database baru dengan reference users dan dimasukkan ke dalam variabel ref
        ref = FirebaseDatabase.getInstance().getReference("dataAkunCustomer").child(userIdentity.uid)

        nextUbahPassword = findViewById(R.id.iconNextUbahPassword)
        switchCheck = findViewById(R.id.switchCheck)
        check = ""

        switchCheck.setChecked(false)

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val customers = dataSnapshot.getValue(Customers::class.java)!!
                if (customers.checkOtp == "active"){
                    switchCheck.setChecked(true)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        ref.addListenerForSingleValueEvent(menuListener)

        switchCheck.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                check = "active"
                updateSwitchCheck(check)
            } else {
                check = "inactive"
                updateSwitchCheck(check)
            }
        })

        nextUbahPassword.setOnClickListener {
            Intent(applicationContext, UbahPasswordPembeliActivity::class.java).also {
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

    fun updateSwitchCheck(check: String){
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val customers = snapshot.getValue(Customers::class.java)!!
                val customerUpdate = Customers(customers.idCustomers!!, customers.username, customers.kelamin, customers.alamat, customers.email, customers.photoProfil, customers.noTelp, customers.jumlahTransaksi, customers.accessLevel, customers.token, customers.status, check)
                ref.setValue(customerUpdate).addOnCompleteListener {
                    if (it.isSuccessful){

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}