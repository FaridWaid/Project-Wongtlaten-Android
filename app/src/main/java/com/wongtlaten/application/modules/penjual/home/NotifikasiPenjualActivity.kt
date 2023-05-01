package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.FCMSend
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.Users
import kotlin.properties.Delegates

class NotifikasiPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference: DatabaseReference
    private lateinit var referenceAnggota: DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var daftarAnggotaList: ArrayList<String>
    private lateinit var autoComplete: AutoCompleteTextView
    private lateinit var etJudul: EditText
    private lateinit var etPesan: EditText
    private lateinit var btnKirim: Button
    private lateinit var tokenUser: String
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifikasi_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        // Mendefinisikan variabel yang nantinya akan berisi inputan user
        etJudul = findViewById(R.id.etJudul)
        etPesan = findViewById(R.id.etPesan)
        btnKirim = findViewById(R.id.btnKirim)
        tokenUser = ""
        checkClick = true

        // Memanggil kelas FCMSend untuk dimasukkan ke dalam variabel fcm
        val fcm = FCMSend()

        // Memasukkan data ke dalam array list
        daftarAnggotaList = arrayListOf<String>()

        // Mendefinisikan reference sebagai FirebaseDatabase
        reference = FirebaseDatabase.getInstance().reference
        referenceAnggota = FirebaseDatabase.getInstance().getReference("dataAkunUser")

        // Mendifinisika "arrayAdapterAnggota" sebagai Array Adapter
        val arrayAdapterAnggota = android.widget.ArrayAdapter(this, R.layout.dropdown_item, daftarAnggotaList)
        // Memanggil fungsi "getDataList" yang datanya akan dimasukkan ke dalam daftarAnggotaList
        getDataList("dataAkunUser", "username", daftarAnggotaList)
        // Mendifinisikan autoComplete dan menggunakan "arrayAdapterAnggota" sebagai adapter
        autoComplete = findViewById(R.id.autoCompleteTextViewPembeli)
        autoComplete.setAdapter(arrayAdapterAnggota)

        // Jika btnKirim di klik maka akan menjalankan aksi
        btnKirim.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick){
                checkClick = false
                // Membuat variabel baru yang berisi inputan user
                val dropDownAnggotaInput = autoComplete.text.toString().trim().toLowerCase()
                val titleInput = etJudul.text.toString().trim()
                val messageInput = etPesan.text.toString().trim()

                // Jika titleInput kosong maka akan muncul error harus isi terlebih dahulu
                if (titleInput.isEmpty()){
                    checkClick = true
                    etJudul.error = "Masukkan judul notifikasi terlebih dahulu!"
                    etJudul.requestFocus()
                    return@setOnClickListener
                }

                // Jika messageInput kosong maka akan muncul error harus isi terlebih dahulu
                if (messageInput.isEmpty()){
                    checkClick = true
                    etPesan.error = "Masukkan pesan notifikasi terlebih dahulu!"
                    etPesan.requestFocus()
                    return@setOnClickListener
                }

                // jika titleInput dan messageInpt tidak kosong maka akan menjalankan aksi
                if (!titleInput.equals("") && !messageInput.equals("")){
                    // Jika dropDownAnggotaInput memliki value semua anggota, maka akan mengirimkan notifikasi
                    // ke semua pengguna dengan token masing - masing, jika berhasil maka akan menampilkan alert
                    // dialog berhasil, jika gagal akan menampilkan alert dialog gagal
                    if (dropDownAnggotaInput == "semua pembeli"){
                        referenceAnggota.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (i in snapshot.children){
                                    val users = i.getValue(Users::class.java)!!
                                    tokenUser = users.token
                                    fcm.pushNotification(this@NotifikasiPenjualActivity, tokenUser, titleInput, messageInput)
                                }
                                alertDialog("Konfirmasi", "Pesan notifikasi berhasil dikirim!", true)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                alertDialog("Konfirmasi", "Pesan notifikasi gagal dikirim!", false)
                                checkClick = true
                            }

                        })
                        // Jika dropDownAnggotaInput tidak memiliki value semua anggota, maka akan mengirimkan notifikasi
                        // kepada anggota yang dipilih, jika berhasil maka akan menampilkan alert dialog berhasil, dan jika
                        // gagal akan menampilkan alert dialog gagal
                    } else{
                        referenceAnggota.orderByChild("username").equalTo(dropDownAnggotaInput).addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (i in snapshot.children){
                                    val users = i.getValue(Users::class.java)!!
                                    tokenUser = users.token
                                    fcm.pushNotification(this@NotifikasiPenjualActivity, tokenUser, titleInput, messageInput)
                                    alertDialog("Konfirmasi", "Pesan notifikasi berhasil dikirim!", true)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                alertDialog("Konfirmasi", "Pesan notifikasi gagal dikirim!", false)
                                checkClick = true
                            }

                        })
                    }
                }
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

    // Fungsi "getDataList" untuk mendapat value/data
    private fun getDataList(child: String, path: String, list: ArrayList<String>) {
        reference.child(child).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    list.clear()
                    for (i in snapshot.children){
                        val users = i.getValue(Users::class.java)!!
                        if (users.accessLevel != "penjual"){
                            val suggestion: String = i.child(path).getValue(String::class.java)!!.toUpperCase()
                            list.add(suggestion)
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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
                    if (!isConnected(this@NotifikasiPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: NotifikasiPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}