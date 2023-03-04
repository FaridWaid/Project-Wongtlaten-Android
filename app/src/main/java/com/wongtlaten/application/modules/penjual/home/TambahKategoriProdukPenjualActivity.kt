package com.wongtlaten.application.modules.penjual.home

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.AttemptLogin
import com.wongtlaten.application.core.CategoryListener
import com.wongtlaten.application.core.CategoryProduct
import com.wongtlaten.application.core.Customers
import java.util.*
import java.util.Locale.filter
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class TambahKategoriProdukPenjualActivity : AppCompatActivity(), CategoryListener {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    // Mendefinisikan variabel global dari view
    private lateinit var rvDaftarKategori: RecyclerView
    private lateinit var daftarKategoriList: ArrayList<CategoryProduct>
    private lateinit var daftarCheckedList: ArrayList<String>
    private lateinit var adapter: DaftarKategoriProdukAdapter
    private lateinit var addProduk : AppCompatImageView
    private lateinit var btnTerapkanInactive : Button
    private lateinit var btnTerapkanActive : Button
    private lateinit var etSearch: EditText
    private var checkCategory by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_kategori_produk_penjual)

        reference = FirebaseDatabase.getInstance().getReference("dataKategoriProduk")

        addProduk = findViewById(R.id.plusButton)
        btnTerapkanInactive = findViewById(R.id.btnTerapkanInactivated)
        btnTerapkanActive = findViewById(R.id.btnTerapkanActivated)
        checkCategory = false

        btnTerapkanInactive.visibility = View.VISIBLE

        addProduk.setOnClickListener {
            showDialogAdd()
        }

        // Mendefinisikan variabel "rvDaftarKategori" yang berupa recyclerview
        rvDaftarKategori = findViewById(R.id.rvDaftarKategori)
        rvDaftarKategori.setHasFixedSize(true)
        rvDaftarKategori.layoutManager = LinearLayoutManager(this)

        // Memasukkan data DaftarSampah ke dalam "daftarKategoriList" sebagai array list
        daftarKategoriList = arrayListOf<CategoryProduct>()
        daftarCheckedList = arrayListOf<String>()
        // Memanggil fungsi "showListKategori" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListKategori()

        adapter = DaftarKategoriProdukAdapter(daftarKategoriList, daftarCheckedList, this)
        rvDaftarKategori.adapter = adapter

        daftarCheckedList = adapter.getSelected()

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

        btnTerapkanActive.setOnClickListener {
            if (daftarCheckedList.size > 1){
                alertDialog("PERINGATAN!", "Kategori yang dapat ditambahkan hanya boleh satu!", false)
            } else {
                var intent = Intent()
                intent.putExtra("CATEGORY", daftarCheckedList[0])
                setResult(RESULT_OK, intent);
                finish()
                onBackPressed()
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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

    private fun showDialogAdd(){
        var dialog = Dialog(this@TambahKategoriProdukPenjualActivity)
        dialog.requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_layout_kategori)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        var btnClose = dialog.findViewById(R.id.closeButton) as AppCompatImageView
        var etCategory = dialog.findViewById(R.id.etKategori) as EditText
        var btnSimpan = dialog.findViewById(R.id.btnSimpan) as Button



        btnSimpan.setOnClickListener {
            reference = FirebaseDatabase.getInstance().getReference("dataKategoriProduk")
            var isNewKategori = randomCode()
            val namaCategory = etCategory.text.toString().trim()
            val newKategori = CategoryProduct(isNewKategori, namaCategory)
            reference.child(isNewKategori).setValue(newKategori).addOnCompleteListener {
                if (it.isSuccessful){
                    dialog.dismiss()
                    Toast.makeText(this@TambahKategoriProdukPenjualActivity, "Kategori baru berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                } else {
                    dialog.dismiss()
                    Toast.makeText(this@TambahKategoriProdukPenjualActivity, "Kategori baru gagal ditambahkan!", Toast.LENGTH_SHORT).show()
                }

            }
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun randomCode(): String{
        val rnd = Random()
        val kodeKategori = rnd.nextInt(999)
        while (kodeKategori.toString().length < 3){
            val kodeKategori = rnd.nextInt(999999)
        }

        val newKategori = "CAT$kodeKategori"
        return newKategori
    }

    private fun showListKategori() {
        reference = FirebaseDatabase.getInstance().getReference("dataKategoriProduk")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarKategoriList.clear()
                    for (i in snapshot.children){
                        val kategori = i.getValue(CategoryProduct::class.java)
                        if (kategori != null){
                            daftarKategoriList.add(kategori)
                        }
                    }

                    adapter = DaftarKategoriProdukAdapter(daftarKategoriList, daftarCheckedList, this@TambahKategoriProdukPenjualActivity)
                    rvDaftarKategori.adapter = adapter

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    // Membuat fungsi "filter" yang digunakan untuk memfilter recyclerView,
    private fun filter(text: String) {
        // mendefiniskan variabel "filteredNames" yang berisi arraylist dari data users
        val filteredNames = ArrayList<CategoryProduct>()
        // setiap data yang ada pada daftarKategoriList disamakan dengan filteredNames
        daftarKategoriList.filterTo(filteredNames) {
            // jika namaCategory sama dengan text input yang dimasukkan oleh user
            it.namaCategory.toLowerCase().contains(text.toLowerCase())
        }
        // maka akan memenaggil fungsi filterlist dari adapter dan hanyak menampilkan data yang cocok
        adapter!!.filterList(filteredNames)
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    override fun onQuantityChange(arrayList: ArrayList<String>) {
        if (daftarCheckedList.isEmpty()){
            btnTerapkanActive.visibility = View.INVISIBLE
            btnTerapkanInactive.visibility = View.VISIBLE
        } else {
            btnTerapkanActive.visibility = View.VISIBLE
            btnTerapkanInactive.visibility = View.INVISIBLE
        }
    }

}