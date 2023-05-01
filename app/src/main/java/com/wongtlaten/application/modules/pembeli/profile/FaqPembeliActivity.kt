package com.wongtlaten.application.modules.pembeli.profile

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Faq
import com.wongtlaten.application.modules.penjual.home.DaftarFaqPenjualAdapter

class FaqPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var rvDaftarFaq: RecyclerView
    private lateinit var daftarFaqList: ArrayList<Faq>
    private lateinit var adapter: FaqPembeliAdapter
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        reference = FirebaseDatabase.getInstance().getReference("dataFaq")

        // Mendefinisikan variabel "rvDaftarProduk" yang berupa recyclerview
        rvDaftarFaq = findViewById(R.id.rvDaftarFaq)
        rvDaftarFaq.setHasFixedSize(true)
        rvDaftarFaq.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarProduk ke dalam "daftarProdukList" sebagai array list
        daftarFaqList = arrayListOf<Faq>()

        adapter = FaqPembeliAdapter(daftarFaqList)

        // Memanggil fungsi "showListProduct" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListFaq()

        // Mendefinisikan variabel "etSearch", ketika memasukkan query ke etSearch maka akan memanggil fungsi filter
        // terdapat closeSearch digunakan untuk menghapus query/inputan
        // overridePendingTransition digunakan untuk animasi dari intent
        etSearch = findViewById(R.id.et_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(editable: Editable?) {
                filter(editable.toString())
            }

        })

    }

    private fun showListFaq() {
        reference = FirebaseDatabase.getInstance().getReference("dataFaq")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarFaqList.clear()
                    for (i in snapshot.children){
                        val faq = i.getValue(Faq::class.java)
                        if (faq != null){
                            daftarFaqList.add(faq)
                        }
                    }

                    adapter = FaqPembeliAdapter(daftarFaqList)
                    rvDaftarFaq.adapter = adapter

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    // Membuat fungsi "filter" yang digunakan untuk memfilter recyclerView,
    private fun filter(text: String) {
        // mendefiniskan variabel "filteredNames" yang berisi arraylist dari data users
        val filteredNames = ArrayList<Faq>()
        // setiap data yang ada pada daftarProdukList disamakan dengan filteredNames
        daftarFaqList.filterTo(filteredNames) {
            // jika namaProduk sama dengan text input yang dimasukkan oleh user
            it.pertanyaan.toLowerCase().contains(text.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredNames)
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
                    if (!isConnected(this@FaqPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: FaqPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}