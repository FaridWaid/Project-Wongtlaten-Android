package com.wongtlaten.application.modules.penjual.home

import android.R.attr
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.makeramen.roundedimageview.RoundedImageView
import com.wongtlaten.application.R
import com.wongtlaten.application.core.LoadingDialog
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.modules.penjual.profile.UbahDataPribadiPenjualActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.properties.Delegates


class TambahProdukPenjualActivity : AppCompatActivity() {

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
    private lateinit var idProduk : String
    private lateinit var image1 : String
    private lateinit var image2 : String
    private lateinit var image3 : String
    private lateinit var image4 : String
    private var countUpload by Delegates.notNull<Int>()
    private var countPhoto by Delegates.notNull<Int>()
    private var checkClick by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_produk_penjual)

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        reference = FirebaseDatabase.getInstance().getReference("dataProduk")
        idProduk = ""

        // Memanggil value/child terakhir dari database daftarpengumulan untuk mendifinisikan idProduk yang terbaru
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countChild = snapshot.childrenCount.toInt()
                if (countChild == 0){
                    idProduk = "PDT00001"
                } else {
                    val lastChild = reference.limitToLast(1)
                    lastChild.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var lastIdProduct = snapshot.getValue().toString()
                            var newIdProduct = lastIdProduct.substring(4, 9).toLong()
                            newIdProduct += 1
                            if (newIdProduct < 10){
                                idProduk = "PDT0000${newIdProduct}"
                            } else if (newIdProduct < 100){
                                idProduk = "PDT000${newIdProduct}"
                            } else if (newIdProduct < 1000){
                                idProduk = "PDT00${newIdProduct}"
                            } else if (newIdProduct < 10000){
                                idProduk = "PDT0${newIdProduct}"
                            } else if (newIdProduct < 100000){
                                idProduk = "PDT${newIdProduct}"
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
        countPhoto = 0
        countUpload = 0
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

        tambahFoto.setOnClickListener {
            if (countPhoto >= 4){
                alertDialog("PERINGATAN!", "Foto produk yang dapat ditambahkan hanya 4 foto!", false)
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

        tambahKategori.setOnClickListener {
            val i = Intent(this, TambahKategoriProdukPenjualActivity::class.java)
            startActivityForResult(i, 1)
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        // Memanggil fungsi "namaProdukFocusListener", "hargaProdukFocusListener", "promoProdukFocusListener"
        namaProdukFocusListener()
        hargaProdukFocusListener()
        promoProdukFocusListener()

        // Mendifinisika "arrayAdapterJenis" sebagai Array Adapter
        daftarJenisList = arrayListOf<String>("NEW", "POPULAR", "FLASH SALE")
        val arrayAdapterJenis = android.widget.ArrayAdapter(this, R.layout.dropdown_item, daftarJenisList)
        // Mendifinisikan autoCompleteJenis dan menggunakan "arrayAdapterJenis" sebagai adapter
        autoCompleteJenis = findViewById(R.id.autoCompleteTextViewJenis)
        autoCompleteJenis.setAdapter(arrayAdapterJenis)

        btnSimpan.setOnClickListener {

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

                        // Memanggil fungsi "addNewProduct" dengan membawa berbagai variabel,
                        // Fungsi ini digunakan untuk nemabahkan produk baru
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
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    fun addNewProduct(
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
                                            val productUpdate = Products(idProduk, namaProduk, hargaProduk.toLong(), stokInput.toInt(), minimumPemesananInput.toInt(), beratInput.toInt(), category, deskripsiInput, dropDownJenisInput, promoProduk.toLong(), image1, image2, image3, image4, 0F,  0)
                                            reference.child("$idProduk").setValue(productUpdate).addOnCompleteListener {
                                                if (it.isSuccessful){
                                                    val loading = LoadingDialog(this@TambahProdukPenjualActivity)
                                                    loading.startDialog()
                                                    loading.isDissmis()
                                                    alertDialog("BERHASIL!", "Produk baru berhasil ditambahkan!", true)
                                                    checkClick = true
                                                } else {
                                                    val loading = LoadingDialog(this@TambahProdukPenjualActivity)
                                                    loading.startDialog()
                                                    loading.isDissmis()
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
            }
            if (countPhoto == 1){
                imageProduk2.setImageURI(data?.data)
                imageUri2 = Uri.parse("${data?.data}")
            }
            if (countPhoto == 2){
                imageProduk3.setImageURI(data?.data)
                imageUri3 = Uri.parse("${data?.data}")
            }
            if (countPhoto == 3){
                imageProduk4.setImageURI(data?.data)
                imageUri4 = Uri.parse("${data?.data}")
            }
            countPhoto += 1
        }
    }

    //back button
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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

}