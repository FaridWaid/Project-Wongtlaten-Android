package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.core.Products
import kotlin.properties.Delegates

class TambahKustomisasiProdukPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference: DatabaseReference
    private lateinit var imageProduk1: RoundedImageView
    private lateinit var imageUri1: Uri
    private lateinit var tambahFoto : TextView
    private lateinit var hapusFoto : TextView
    private lateinit var etNamaProduk: TextInputEditText
    private lateinit var namaProdukContainer: TextInputLayout
    private lateinit var etHargaProduk: TextInputEditText
    private lateinit var hargaProdukContainer: TextInputLayout
    private lateinit var etStok: EditText
    private lateinit var etBerat: EditText
    private lateinit var etPanjang: EditText
    private lateinit var etLebar: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var btnSimpan : Button
    private lateinit var idProduk : String
    private lateinit var image1 : String
    private var countUpload by Delegates.notNull<Int>()
    private var countPhoto by Delegates.notNull<Int>()
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_kustomisasi_produk_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize")
        idProduk = ""

        // Memanggil value/child terakhir dari database daftarpengumulan untuk mendifinisikan idProduk yang terbaru
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countChild = snapshot.childrenCount.toInt()
                if (countChild == 0){
                    idProduk = "PCT00001"
                } else {
                    val lastChild = reference.limitToLast(1)
                    lastChild.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var lastIdProduct = snapshot.getValue().toString()
                            var newIdProduct = lastIdProduct.substring(4, 9).toLong()
                            newIdProduct += 1
                            if (newIdProduct < 10){
                                idProduk = "PCT0000${newIdProduct}"
                            } else if (newIdProduct < 100){
                                idProduk = "PCT000${newIdProduct}"
                            } else if (newIdProduct < 1000){
                                idProduk = "PCT00${newIdProduct}"
                            } else if (newIdProduct < 10000){
                                idProduk = "PCT0${newIdProduct}"
                            } else if (newIdProduct < 100000){
                                idProduk = "PCT${newIdProduct}"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        imageProduk1 = findViewById(R.id.imageProduk1)
        tambahFoto = findViewById(R.id.textTambahFoto)
        hapusFoto = findViewById(R.id.hapusFoto)
        etNamaProduk = findViewById(R.id.etNamaProduk)
        namaProdukContainer = findViewById(R.id.NamaProdukContainer)
        etHargaProduk = findViewById(R.id.etHargaProduk)
        hargaProdukContainer = findViewById(R.id.HargaProdukContainer)
        etStok = findViewById(R.id.etStok)
        etBerat = findViewById(R.id.etBeratProduk)
        etPanjang = findViewById(R.id.etPanjangProduk)
        etLebar = findViewById(R.id.etLebarProduk)
        etDeskripsi = findViewById(R.id.etDeskripsiProduk)
        btnSimpan = findViewById(R.id.btnSimpan)
        countPhoto = 0
        countUpload = 0
        image1 = ""
        imageUri1 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
        checkClick = true

        tambahFoto.setOnClickListener {
            if (countPhoto >= 1){
                alertDialog("PERINGATAN!", "Foto produk yang dapat ditambahkan hanya 1 foto!", false)
            } else {
                pickImageGallery()
            }
        }

        hapusFoto.setOnClickListener {
            if (countPhoto == 1){
                imageUri1 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
                imageProduk1.setImageURI(imageUri1)
                countPhoto -= 1
                hapusFoto.visibility = View.INVISIBLE
            }

        }

        // Memanggil fungsi "namaProdukFocusListener", "hargaProdukFocusListener", "promoProdukFocusListener"
        namaProdukFocusListener()
        hargaProdukFocusListener()

        btnSimpan.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (checkClick) {
                checkClick = false

                if (countPhoto == 0){
                    alertDialog("PERINGATAN!", "Silakan pilih foto produk terlebih dahulu!", false)
                    checkClick = true
                } else {

                    // Membuat variabel baru yang berisi inputan user
                    val namaProduk = etNamaProduk.text.toString().trim()
                    val hargaProduk = etHargaProduk.text.toString().trim()
                    val stokInput = etStok.text.toString().trim()
                    val beratInput = etBerat.text.toString().trim()
                    val panjangInput = etPanjang.text.toString().trim()
                    val lebarInput = etLebar.text.toString().trim()
                    val deskripsiInput = etDeskripsi.text.toString().trim()

                    // Memastikan lagi apakah format yang diinputkan oleh user sudah benar
                    namaProdukContainer.helperText = validNamaProduk()
                    hargaProdukContainer.helperText = validHargaProduk()

                    // Jika sudah benar, maka helper pada edittext diisikan dengan null
                    val validNamaProduk = namaProdukContainer.helperText == null
                    val validHargaProduk = hargaProdukContainer.helperText == null

                    // Jika semua sudah diisi maka akan melakukan "addNewProduct"
                    if (validNamaProduk && validHargaProduk) {
                        // Jika stokInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (stokInput.isEmpty()){
                            etStok.error = "Masukkan jumlah stok produk terlebih dahulu!"
                            etStok.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        // Jika beratInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (beratInput.isEmpty()){
                            etBerat.error = "Masukkan berat produk terlebih dahulu!"
                            etBerat.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        // Jika beratInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (panjangInput.isEmpty()){
                            etPanjang.error = "Masukkan panjang produk terlebih dahulu!"
                            etPanjang.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        // Jika beratInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (panjangInput.toFloat() > 25.0F){
                            etPanjang.error = "Panjang produk tidak boleh lebih dari 25cm!"
                            etPanjang.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        // Jika beratInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (lebarInput.isEmpty()){
                            etLebar.error = "Masukkan lebar produk terlebih dahulu!"
                            etLebar.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        // Jika beratInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (lebarInput.toFloat() > 10.0F){
                            etLebar.error = "Lebar produk tidak boleh lebih dari 10cm!"
                            etLebar.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        // Jika deskripsiInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (deskripsiInput.isEmpty()){
                            etDeskripsi.error = "Masukkan deskripsi produk terlebih dahulu!"
                            etDeskripsi.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        var kategoriProduk = ""
                        if (panjangInput.toFloat() <= 10.0F && lebarInput.toFloat() <= 7.0F){
                            kategoriProduk = "kecil"
                        } else if (panjangInput.toFloat() <= 20.0F && lebarInput.toFloat() <= 10.0F){
                            kategoriProduk = "sedang"
                        } else if (panjangInput.toFloat() <= 25.0F && lebarInput.toFloat() <= 10.0F){
                            kategoriProduk = "besar"
                        }

                        // Memanggil fungsi "addNewProduct" dengan membawa berbagai variabel,
                        // Fungsi ini digunakan untuk nemabahkan produk baru
                        addNewProduct(imageUri1, namaProduk, hargaProduk, stokInput, beratInput, panjangInput, lebarInput, deskripsiInput, kategoriProduk)
                        loadingBar(12000)
                    }else {
                        loadingBar(1000)
                        alertDialog("GAGAL!", "Produk baru gagal ditambahkan, silakan isi nama dan harga produk terlebih dahulu!", false)
                        checkClick = true
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

    private fun addNewProduct(imageUri1: Uri, namaProduk: String, hargaProduk: String, stokInput: String, beratInput: String, panjangInput: String, lebarInput: String, deskripsiInput: String, dropDownKategoriInput: String) {
        var ref = FirebaseStorage.getInstance().reference.child("imgProductCustomize/${idProduk}")
        ref.putFile(imageUri1).addOnSuccessListener {
            FirebaseStorage.getInstance().reference.child("imgProductCustomize/${idProduk}").downloadUrl.addOnSuccessListener {
                image1 = it.toString()
                countUpload += 1
                val productUpdate = CustomizeProducts(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), beratInput.toInt(), panjangInput.toFloat(), lebarInput.toFloat(), dropDownKategoriInput, deskripsiInput, image1, "active")
                reference.child("$idProduk").setValue(productUpdate).addOnCompleteListener {
                    if (it.isSuccessful){
                        val loading = LoadingDialog(this@TambahKustomisasiProdukPenjualActivity)
                        loading.startDialog()
                        loading.isDissmis()
                        alertDialog("BERHASIL!", "Produk baru berhasil ditambahkan!", true)
                        checkClick = true
                    } else {
                        val loading = LoadingDialog(this@TambahKustomisasiProdukPenjualActivity)
                        loading.startDialog()
                        loading.isDissmis()
                        alertDialog("GAGAL!", "Produk baru gagal ditambahkan!", false)
                        checkClick = true
                    }
                }
            }
        }
    }

    // Membuat fungsi "pickImageGallery",
    // Fungsi ini digunakan untuk memilik photo dari gallery
    private fun pickImageGallery() {
        val inten = Intent(Intent.ACTION_PICK)
        inten.type = "image/*"
        startActivityForResult(inten, IMAGE_REQUEST_CODE)
    }

    // Memanggi fungsi turunan "onActivityResult", fungsi ini berjalan ketika activity dibuka
    // Fungsi ini digunakan untuk mengambil image yang telah dipilih di gallery dan dipasang ke photoProduct,
    // dan dimasukkan ke dalam database img, dengan id dari user
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            if (countPhoto == 0){
                imageProduk1.setImageURI(data?.data)
                imageUri1 = Uri.parse("${data?.data}")
                hapusFoto.visibility = View.VISIBLE
                countPhoto += 1
            }
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

    // Membuat fungsi "namaProdukFocusListener"
    private fun namaProdukFocusListener() {
        // Memastikan apakah etNamaProduk sudah sesuai dengan format pengisian
        etNamaProduk.setOnFocusChangeListener { _, focused ->
            if(!focused){
                namaProdukContainer.helperText = validNamaProduk()
            }
        }
    }

    // Membuat fungsi "validNamaProduk"
    private fun validNamaProduk(): String? {
        val namaProduk = etNamaProduk.text.toString()
        // Jika namaProduk kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (namaProduk.isEmpty()){
            return "Nama Produk Harus Diisi!"
        }
        return null
    }

    // Membuat fungsi "hargaProdukFocusListener"
    private fun hargaProdukFocusListener() {
        // Memastikan apakah etHargaProduk sudah sesuai dengan format pengisian
        etHargaProduk.setOnFocusChangeListener { _, focused ->
            if(!focused){
                hargaProdukContainer.helperText = validHargaProduk()
            }
        }
    }

    // Membuat fungsi "validHargaProduk"
    private fun validHargaProduk(): String? {
        val hargaProduk = etHargaProduk.text.toString()
        // Jika hargaProduk kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (hargaProduk.isEmpty()){
            return "Harga Produk Harus Diisi!"
        }
        return null
    }

    companion object {
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
                    if (!isConnected(this@TambahKustomisasiProdukPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: TambahKustomisasiProdukPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}