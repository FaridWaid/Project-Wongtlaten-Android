package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.core.Products
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates

class UbahProdukPenjualActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference: DatabaseReference
    private lateinit var imageProduk1: RoundedImageView
    private lateinit var imageProduk2: RoundedImageView
    private lateinit var imageProduk3: RoundedImageView
    private lateinit var imageProduk4: RoundedImageView
    private lateinit var imageUri1: Uri
    private lateinit var imageUri2: Uri
    private lateinit var imageUri3: Uri
    private lateinit var imageUri4: Uri
    private lateinit var tambahFoto : TextView
    private lateinit var hapusFoto : TextView
    private lateinit var tambahKategori : TextView
    private lateinit var kategoriProduk : TextView
    private lateinit var etNamaProduk: TextInputEditText
    private lateinit var namaProdukContainer: TextInputLayout
    private lateinit var etHargaProduk: TextInputEditText
    private lateinit var hargaProdukContainer: TextInputLayout
    private lateinit var etStok: EditText
    private lateinit var etMinimumPemesanan: EditText
    private lateinit var etBerat: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var daftarJenisList: ArrayList<String>
    private lateinit var autoCompleteJenis: AutoCompleteTextView
    private lateinit var etPromoProduk: TextInputEditText
    private lateinit var promoProdukContainer: TextInputLayout
    private lateinit var btnSimpan : Button
    private lateinit var category : String
    private lateinit var image1 : String
    private lateinit var image2 : String
    private lateinit var image3 : String
    private lateinit var image4 : String
    private lateinit var idProduk : String
    private var countUpload by Delegates.notNull<Int>()
    private var countPhoto by Delegates.notNull<Int>()
    private var changePhoto1 by Delegates.notNull<Boolean>()
    private var changePhoto2 by Delegates.notNull<Boolean>()
    private var changePhoto3 by Delegates.notNull<Boolean>()
    private var changePhoto4 by Delegates.notNull<Boolean>()
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_produk_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        idProduk = intent.getStringExtra(EXTRA_ID_PRODUCT)!!
        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduk)

        imageProduk1 = findViewById(R.id.imageProduk1)
        imageProduk2 = findViewById(R.id.imageProduk2)
        imageProduk3 = findViewById(R.id.imageProduk3)
        imageProduk4 = findViewById(R.id.imageProduk4)
        tambahFoto = findViewById(R.id.textTambahFoto)
        hapusFoto = findViewById(R.id.hapusFoto)
        tambahKategori = findViewById(R.id.textTambahKategori)
        kategoriProduk = findViewById(R.id.kategoriProduk)
        etNamaProduk = findViewById(R.id.etNamaProduk)
        namaProdukContainer = findViewById(R.id.NamaProdukContainer)
        etHargaProduk = findViewById(R.id.etHargaProduk)
        hargaProdukContainer = findViewById(R.id.HargaProdukContainer)
        etStok = findViewById(R.id.etStok)
        etMinimumPemesanan = findViewById(R.id.etMinimumPemesanan)
        etBerat = findViewById(R.id.etBeratProduk)
        etDeskripsi = findViewById(R.id.etDeskripsiProduk)
        etPromoProduk = findViewById(R.id.etPromoProduk)
        promoProdukContainer = findViewById(R.id.PromoProdukContainer)
        btnSimpan = findViewById(R.id.btnSimpan)
        countPhoto = 4
        countUpload = 0
        changePhoto1 = false
        changePhoto2 = false
        changePhoto3 = false
        changePhoto4 = false
        category = ""
        image1 = ""
        image2 = ""
        image3 = ""
        image4 = ""
        imageUri1 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
        imageUri2 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
        imageUri3 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
        imageUri4 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
        checkClick = true

        hapusFoto.visibility = View.VISIBLE

        // Mendifinisikan autoCompleteJenis
        autoCompleteJenis = findViewById(R.id.autoCompleteTextViewJenis)

        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val produk = dataSnapshot.getValue(Products::class.java)!!

                category = produk.kategoriProduct
                image1 = produk.photoProduct1
                image2 = produk.photoProduct2
                image3 = produk.photoProduct3
                image4 = produk.photoProduct4
                imageUri1 = Uri.parse("${produk.photoProduct1}")
                imageUri2 = Uri.parse("${produk.photoProduct2}")
                imageUri3 = Uri.parse("${produk.photoProduct3}")
                imageUri4 = Uri.parse("${produk.photoProduct4}")
                Picasso.get().load(produk.photoProduct1).into(imageProduk1)
                Picasso.get().load(produk.photoProduct2).into(imageProduk2)
                Picasso.get().load(produk.photoProduct3).into(imageProduk3)
                Picasso.get().load(produk.photoProduct4).into(imageProduk4)
                etNamaProduk.setText(produk.namaProduct)
                etHargaProduk.setText(produk.hargaProduct.toString())
                etStok.setText(produk.stockProduct.toString())
                etMinimumPemesanan.setText(produk.minimumPemesananProduct.toString())
                etBerat.setText(produk.beratProduct.toString())
                kategoriProduk.text = produk.kategoriProduct
                etDeskripsi.setText(produk.deskripsiProduct)
                etPromoProduk.setText(produk.hargaPromoProduct.toString())

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        reference.addListenerForSingleValueEvent(menuListener)

        // Memanggil fungsi "namaProdukFocusListener", "hargaProdukFocusListener", "promoProdukFocusListener"
        namaProdukFocusListener()
        hargaProdukFocusListener()
        promoProdukFocusListener()

        hapusFoto.setOnClickListener {
            if (countPhoto == 1){
                imageUri1 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
                imageProduk1.setImageURI(imageUri1)
                countPhoto -= 1
                hapusFoto.visibility = View.INVISIBLE
            } else if (countPhoto == 2){
                imageUri2 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
                imageProduk2.setImageURI(imageUri2)
                countPhoto -= 1
            } else if (countPhoto == 3){
                imageUri3 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
                imageProduk3.setImageURI(imageUri3)
                countPhoto -= 1
            } else if (countPhoto == 4){
                imageUri4 = Uri.parse("android.resource://com.wongtlaten.application/drawable/picture")
                imageProduk4.setImageURI(imageUri4)
                countPhoto -= 1
            }

        }

        // Mendifinisika "arrayAdapterJenis" sebagai Array Adapter
        daftarJenisList = arrayListOf<String>("NEW", "POPULAR", "FLASH SALE")
        val arrayAdapterJenis = android.widget.ArrayAdapter(this, R.layout.dropdown_item, daftarJenisList)
        autoCompleteJenis.setAdapter(arrayAdapterJenis)

        tambahKategori.setOnClickListener {
            val i = Intent(this, TambahKategoriProdukPenjualActivity::class.java)
            startActivityForResult(i, 1)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        tambahFoto.setOnClickListener {
            if (countPhoto >= 4){
                alertDialog("PERINGATAN!", "Foto produk yang dapat ditambahkan hanya 4 foto!", false)
            } else {
                pickImageGallery()
            }
        }

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
                } else if (category== ""){
                    alertDialog("PERINGATAN!", "Silakan tambahkan kategori produk terlebih dahulu!", false)
                    checkClick = true
                } else {

                    // Membuat variabel baru yang berisi inputan user
                    val namaProduk = etNamaProduk.text.toString().trim()
                    val hargaProduk = etHargaProduk.text.toString().trim()
                    val stokInput = etStok.text.toString().trim()
                    val minimumPemesananInput = etMinimumPemesanan.text.toString().trim()
                    val beratInput = etBerat.text.toString().trim()
                    val deskripsiInput = etDeskripsi.text.toString().trim()
                    val dropDownJenisInput = autoCompleteJenis.text.toString().trim().toLowerCase()
                    val promoProduk = etPromoProduk.text.toString().trim()

                    // Memastikan lagi apakah format yang diinputkan oleh user sudah benar
                    namaProdukContainer.helperText = validNamaProduk()
                    hargaProdukContainer.helperText = validHargaProduk()
                    promoProdukContainer.helperText = validPromoProduk()

                    // Jika sudah benar, maka helper pada edittext diisikan dengan null
                    val validNamaProduk = namaProdukContainer.helperText == null
                    val validHargaProduk = hargaProdukContainer.helperText == null
                    val validPromoProduk = promoProdukContainer.helperText == null

                    // Jika semua sudah diisi maka akan melakukan "addNewProduct"
                    if (validNamaProduk && validHargaProduk && validPromoProduk) {
                        // Jika stokInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (stokInput.isEmpty()){
                            etStok.error = "Masukkan jumlah stok produk terlebih dahulu!"
                            etStok.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }

                        // Jika minimumPemesananInput kosong maka akan muncul error harus isi terlebih dahulu
                        if (minimumPemesananInput.isEmpty()){
                            etMinimumPemesanan.error = "Masukkan jumlah minimum pemesanan produk terlebih dahulu!"
                            etMinimumPemesanan.requestFocus()
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
                        if (dropDownJenisInput == "pilih jenis produk"){
                            autoCompleteJenis.error = "Silakan pilih jenis produk terlebih dahulu!"
                            autoCompleteJenis.requestFocus()
                            checkClick = true
                            return@setOnClickListener
                        }
                        addNewProduct(imageUri1, imageUri2, imageUri3, imageUri4, namaProduk, hargaProduk, stokInput, minimumPemesananInput, beratInput, category, deskripsiInput, dropDownJenisInput, promoProduk)
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

    private fun addNewProduct(
        imageUri1: Uri,
        imageUri2: Uri,
        imageUri3: Uri,
        imageUri4: Uri,
        namaProduk: String,
        hargaProduk: String,
        stokInput: String,
        minimumPemesananInput: String,
        beratInput: String,
        category: String,
        deskripsiInput: String,
        dropDownJenisInput: String,
        promoProduk: String
    ) {
        if (!changePhoto4){
            val productUpdate = Products(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), minimumPemesananInput.toInt(), beratInput.toInt(), category, deskripsiInput, dropDownJenisInput, promoProduk.toLong(), image1, image2, image3, image4, 0F,  0, "active")
            reference.setValue(productUpdate).addOnCompleteListener {
                if (it.isSuccessful){
                    alertDialog("BERHASIL!", "Produk baru berhasil di ubah!", true)
                    checkClick = true
                } else {
                    alertDialog("GAGAL!", "Produk baru gagal di ubah!", false)
                    checkClick = true
                }
            }
        } else if (!changePhoto3){
            countUpload = 3
            var ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
            ref.putFile(imageUri4).addOnSuccessListener {
                FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                    image4 = it.toString()
                    val productUpdate = Products(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), minimumPemesananInput.toInt(), beratInput.toInt(), category, deskripsiInput, dropDownJenisInput, promoProduk.toLong(), image1, image2, image3, image4, 0F,  0, "active")
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
        } else if (!changePhoto2){
            countUpload = 2
            var ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
            ref.putFile(imageUri3).addOnSuccessListener {
                FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                    image3 = it.toString()
                    countUpload += 1
                    ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
                    ref.putFile(imageUri4).addOnSuccessListener {
                        FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                            image4 = it.toString()
                            val productUpdate = Products(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), minimumPemesananInput.toInt(), beratInput.toInt(), category, deskripsiInput, dropDownJenisInput, promoProduk.toLong(), image1, image2, image3, image4, 0F,  0, "active")
                            reference.setValue(productUpdate).addOnCompleteListener {
                                if (it.isSuccessful){
                                    alertDialog("BERHASIL!", "Produk baru berhasil ditambahkan!", true)
                                    checkClick = true
                                } else {
                                    alertDialog("GAGAL!", "Produk baru gagal ditambahkan!", false)
                                    checkClick = true
                                }
                            }
                        }
                    }
                }
            }
        } else if (!changePhoto1){
            countUpload = 1
            var ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
            ref.putFile(imageUri2).addOnSuccessListener {
                FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                    image2 = it.toString()
                    countUpload += 1
                    ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
                    ref.putFile(imageUri3).addOnSuccessListener {
                        FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                            image3 = it.toString()
                            countUpload += 1
                            ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
                            ref.putFile(imageUri4).addOnSuccessListener {
                                FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                                    image4 = it.toString()
                                    val productUpdate = Products(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), minimumPemesananInput.toInt(), beratInput.toInt(), category, deskripsiInput, dropDownJenisInput, promoProduk.toLong(), image1, image2, image3, image4, 0F,  0, "active")
                                    reference.setValue(productUpdate).addOnCompleteListener {
                                        if (it.isSuccessful){
                                            alertDialog("BERHASIL!", "Produk baru berhasil ditambahkan!", true)
                                            checkClick = true
                                        } else {
                                            alertDialog("GAGAL!", "Produk baru gagal ditambahkan!", false)
                                            checkClick = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            var ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
            ref.putFile(imageUri1).addOnSuccessListener {
                FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                    image1 = it.toString()
                    countUpload += 1
                    ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
                    ref.putFile(imageUri2).addOnSuccessListener {
                        FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                            image2 = it.toString()
                            countUpload += 1
                            ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
                            ref.putFile(imageUri3).addOnSuccessListener {
                                FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                                    image3 = it.toString()
                                    countUpload += 1
                                    ref = FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload")
                                    ref.putFile(imageUri4).addOnSuccessListener {
                                        FirebaseStorage.getInstance().reference.child("imgProduct/${idProduk}/$countUpload").downloadUrl.addOnSuccessListener {
                                            image4 = it.toString()
                                            val productUpdate = Products(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), minimumPemesananInput.toInt(), beratInput.toInt(), category, deskripsiInput, dropDownJenisInput, promoProduk.toLong(), image1, image2, image3, image4, 0F,  0, "active")
                                            reference.setValue(productUpdate).addOnCompleteListener {
                                                if (it.isSuccessful){
                                                    alertDialog("BERHASIL!", "Produk baru berhasil ditambahkan!", true)
                                                    checkClick = true
                                                } else {
                                                    alertDialog("GAGAL!", "Produk baru gagal ditambahkan!", false)
                                                    checkClick = true
                                                }
                                            }
                                        }
                                    }
                                }
                            }
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

    // Membuat fungsi "promoProdukFocusListener"
    private fun promoProdukFocusListener() {
        // Memastikan apakah etPromoProduk sudah sesuai dengan format pengisian
        etPromoProduk.setOnFocusChangeListener { _, focused ->
            if(!focused){
                promoProdukContainer.helperText = validPromoProduk()
            }
        }
    }

    // Membuat fungsi "validPromoProduk"
    private fun validPromoProduk(): String? {
        val promoProduk = etPromoProduk.text.toString()
        // Jika promoProduk kosong maka akan gagal membuat user baru dan muncul error harus isi terlebih dahulu
        if (promoProduk.isEmpty()){
            return "Presentase Promo Produk Harus Diisi!"
        }
        if (promoProduk.toInt() > 100){
            return "Presentase Promo Produk Tidak Boleh Lebih Dari 100%!"
        }
        return null
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
        if (requestCode === 1) {
            if (resultCode === RESULT_OK) {
                category = data?.getStringExtra("CATEGORY")!!
                kategoriProduk.setText(category)
            }
        }
        else if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            if (countPhoto == 0){
                imageProduk1.setImageURI(data?.data)
                imageUri1 = Uri.parse("${data?.data}")
                hapusFoto.visibility = View.VISIBLE
                changePhoto1 = true
            }
            if (countPhoto == 1){
                imageProduk2.setImageURI(data?.data)
                imageUri2 = Uri.parse("${data?.data}")
                changePhoto2 = true
            }
            if (countPhoto == 2){
                imageProduk3.setImageURI(data?.data)
                imageUri3 = Uri.parse("${data?.data}")
                changePhoto3 = true
            }
            if (countPhoto == 3){
                imageProduk4.setImageURI(data?.data)
                imageUri4 = Uri.parse("${data?.data}")
                changePhoto4 = true
            }
            countPhoto += 1
        }
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
                    if (!isConnected(this@UbahProdukPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: UbahProdukPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}