package com.wongtlaten.application.modules.penjual.home

import android.content.DialogInterface
import android.content.Intent
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
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.core.CustomizeProducts
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.core.Products
import kotlin.properties.Delegates

class UbahProdukKustomisasiPenjualActivity : AppCompatActivity() {

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
    private lateinit var etDeskripsi: EditText
    private lateinit var daftarKategoriList: ArrayList<String>
    private lateinit var autoCompleteKategori: AutoCompleteTextView
    private lateinit var btnSimpan : Button
    private lateinit var image1 : String
    private lateinit var idProduk : String
    private var countUpload by Delegates.notNull<Int>()
    private var countPhoto by Delegates.notNull<Int>()
    private var changePhoto1 by Delegates.notNull<Boolean>()
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_produk_kustomisasi_penjual)

        idProduk = intent.getStringExtra(EXTRA_ID_PRODUCT)!!
        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataProdukCustomize").child(idProduk)

        imageProduk1 = findViewById(R.id.imageProduk1)
        tambahFoto = findViewById(R.id.textTambahFoto)
        hapusFoto = findViewById(R.id.hapusFoto)
        etNamaProduk = findViewById(R.id.etNamaProduk)
        namaProdukContainer = findViewById(R.id.NamaProdukContainer)
        etHargaProduk = findViewById(R.id.etHargaProduk)
        hargaProdukContainer = findViewById(R.id.HargaProdukContainer)
        etStok = findViewById(R.id.etStok)
        etBerat = findViewById(R.id.etBeratProduk)
        etDeskripsi = findViewById(R.id.etDeskripsiProduk)
        btnSimpan = findViewById(R.id.btnSimpan)
        countPhoto = 1
        countUpload = 0
        changePhoto1 = false
        image1 = ""
        imageUri1 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
        checkClick = true

        hapusFoto.visibility = View.VISIBLE

        // Mendifinisikan autoCompleteJenis
        autoCompleteKategori = findViewById(R.id.autoCompleteTextViewKategori)

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val produk = dataSnapshot.getValue(CustomizeProducts::class.java)!!

                image1 = produk.photoProduct1
                imageUri1 = Uri.parse("${produk.photoProduct1}")
                Picasso.get().load(produk.photoProduct1).into(imageProduk1)
                etNamaProduk.setText(produk.namaProduct)
                etHargaProduk.setText(produk.hargaProduct.toString())
                etStok.setText(produk.stockProduct.toString())
                etBerat.setText(produk.beratProduct.toString())
                etDeskripsi.setText(produk.deskripsiProduct)

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        reference.addListenerForSingleValueEvent(menuListener)

        // Memanggil fungsi "namaProdukFocusListener", "hargaProdukFocusListener", "promoProdukFocusListener"
        namaProdukFocusListener()
        hargaProdukFocusListener()

        hapusFoto.setOnClickListener {
            if (countPhoto == 1){
                imageUri1 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
                imageProduk1.setImageURI(imageUri1)
                countPhoto -= 1
                hapusFoto.visibility = View.INVISIBLE
            }
        }

        // Mendifinisika "arrayAdapterJenis" sebagai Array Adapter
        daftarKategoriList = arrayListOf<String>("KECIL", "SEDANG", "BESAR")
        val arrayAdapterKategori = android.widget.ArrayAdapter(this, R.layout.dropdown_item, daftarKategoriList)
        autoCompleteKategori.setAdapter(arrayAdapterKategori)

        tambahFoto.setOnClickListener {
            if (countPhoto >= 1){
                alertDialog("PERINGATAN!", "Foto produk yang dapat ditambahkan hanya 1 foto!", false)
            } else {
                pickImageGallery()
            }
        }

        btnSimpan.setOnClickListener {

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
                    val deskripsiInput = etDeskripsi.text.toString().trim()
                    val dropDownKategoriInput = autoCompleteKategori.text.toString().trim().toLowerCase()

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

                        // Jika deskripsiInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (deskripsiInput.isEmpty()){
                            etDeskripsi.error = "Masukkan deskripsi produk terlebih dahulu!"
                            etDeskripsi.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        // Jika dropDownJenisInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (dropDownKategoriInput == "pilih kategori produk"){
                            autoCompleteKategori.error = "Silakan pilih kategori produk terlebih dahulu!"
                            autoCompleteKategori.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }
                        addNewProduct(imageUri1, namaProduk, hargaProduk, stokInput, beratInput, deskripsiInput, dropDownKategoriInput)
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
            overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
        }

    }

    private fun addNewProduct(imageUri1: Uri, namaProduk: String, hargaProduk: String, stokInput: String, beratInput: String, deskripsiInput: String, dropDownKategoriInput: String) {
        if (!changePhoto1){
            val productUpdate = CustomizeProducts(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), beratInput.toInt(), dropDownKategoriInput, deskripsiInput, image1)
            reference.setValue(productUpdate).addOnCompleteListener {
                if (it.isSuccessful){
                    alertDialog("BERHASIL!", "Produk baru berhasil di ubah!", true)
                    checkClick = true
                } else {
                    alertDialog("GAGAL!", "Produk baru gagal di ubah!", false)
                    checkClick = true
                }
            }
        } else {
            var ref = FirebaseStorage.getInstance().reference.child("imgProductCustomize/${idProduk}")
            ref.putFile(imageUri1).addOnSuccessListener {
                FirebaseStorage.getInstance().reference.child("imgProductCustomize/${idProduk}").downloadUrl.addOnSuccessListener {
                    image1 = it.toString()
                    val productUpdate = CustomizeProducts(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), beratInput.toInt(), dropDownKategoriInput, deskripsiInput, image1)
                    reference.setValue(productUpdate).addOnCompleteListener {
                        if (it.isSuccessful){
                            alertDialog("BERHASIL!", "Produk baru berhasil di ubah!", true)
                            checkClick = true
                        } else {
                            alertDialog("GAGAL!", "Produk baru gagal di ubah!", false)
                            checkClick = true
                        }
                    }
                }
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

    // Membuat fungsi "pickImageGallery",
    // Fungsi ini digunakan untuk memilik photo dari gallery
    private fun pickImageGallery() {
        val inten = Intent(Intent.ACTION_PICK)
        inten.type = "image/*"
        startActivityForResult(inten, UbahProdukPenjualActivity.IMAGE_REQUEST_CODE)
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
                changePhoto1 = true
            }
        }
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

    companion object{
        const val EXTRA_ID_PRODUCT = "EXTRA_ID_PRODUCT"
        val IMAGE_REQUEST_CODE = 100
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom)
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

}