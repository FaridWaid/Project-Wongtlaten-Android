package com.wongtlaten.application.modules.pembeli.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.midtrans.sdk.corekit.models.snap.TransactionStatusResponse
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.api.RetrofitClient
import com.wongtlaten.application.core.*
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.modules.pembeli.wishlist.KeranjangPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PembayaranPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.EXTRA_PRODUK
import com.wongtlaten.application.modules.pembeli.wishlist.PengirimanPembeliActivity.Companion.TOTAL_PRODUK
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity
import com.wongtlaten.application.modules.penjual.home.RecomViewPagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates

class DetailProdukPembeliActivity : AppCompatActivity() {

    // Mendefinisikan variabel global untuk connect ke Firebase
    private lateinit var reference : DatabaseReference
    private lateinit var referenceCart : DatabaseReference
    private lateinit var typeProduk : TextView
    private lateinit var namaProduk : TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var textRate : TextView
    private lateinit var komentar : TextView
    private lateinit var deskripsiProduk : TextView
    private lateinit var totalPrice : TextView
    private lateinit var totalPriceSlash : TextView
    private lateinit var totalPricePromo : TextView
    private lateinit var textBacaSelengkapnya : TextView
    private lateinit var recomViewPager: ViewPager2
    private lateinit var daftarRecomList: ArrayList<Products>
    private lateinit var adapterRecom: RecomViewPagerAdapter
    private lateinit var loveInactive: ImageView
    private lateinit var loveActive: ImageView
    private lateinit var itemFiturPayment: CardView
    private lateinit var itemFiturChat: CardView
    private lateinit var itemFiturCart: CardView
    private lateinit var itemFiturPlus : CardView
    private lateinit var itemFiturMinus : CardView
    private lateinit var totalProduk : TextView
    private lateinit var btnKeranjang : Button
    private lateinit var btnBeli : Button
    private var countTotalProduk by Delegates.notNull<Int>()
    private lateinit var textDetail : TextView
    private var isProductOnCart by Delegates.notNull<Boolean>()
    private lateinit var textChat : TextView
    private lateinit var textKeranjang : TextView
    private lateinit var textPayment : TextView
    private var stokProduk by Delegates.notNull<Int>()
    private var minimumPemesanan by Delegates.notNull<Int>()
    private lateinit var tempProduk: ArrayList<Products>
    private var countPayment by Delegates.notNull<Int>()
    private lateinit var idUser: String
    private lateinit var daftarCartList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk_pembeli)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        typeProduk = findViewById(R.id.typeProduk)
        namaProduk = findViewById(R.id.nameProduk)
        ratingBar = findViewById(R.id.ratingBar)
        textRate = findViewById(R.id.textRate)
        komentar = findViewById(R.id.komentar)
        deskripsiProduk = findViewById(R.id.deskripsiProduk)
        totalPrice = findViewById(R.id.totalPrice)
        totalPriceSlash = findViewById(R.id.totalPriceSlash)
        totalPricePromo = findViewById(R.id.totalPricePromo)
        textBacaSelengkapnya = findViewById(R.id.textBacaSelengkapnya)
        recomViewPager = findViewById(R.id.recomViewPager)
        recomViewPager.clipToPadding = false
        recomViewPager.clipChildren = false
        recomViewPager.offscreenPageLimit = 3
        recomViewPager.getChildAt(0)
        recomViewPager.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        loveInactive = findViewById(R.id.loveInactivated)
        loveActive = findViewById(R.id.loveActivated)
        itemFiturChat = findViewById(R.id.itemFiturChat)
        itemFiturPayment = findViewById(R.id.itemFiturPayment)
        itemFiturCart = findViewById(R.id.itemFiturCart)
        itemFiturPlus = findViewById(R.id.itemFiturPlus)
        itemFiturMinus = findViewById(R.id.itemFiturMinus)
        totalProduk = findViewById(R.id.totalProduk)
        btnKeranjang = findViewById(R.id.btnKeranjang)
        btnBeli = findViewById(R.id.btnBeli)
        textDetail = findViewById(R.id.textDetailProduk)
        textChat = findViewById(R.id.textChat)
        textKeranjang = findViewById(R.id.textKeranjang)
        textPayment = findViewById(R.id.textPayment)
        isProductOnCart = false
        stokProduk = 0
        minimumPemesanan = 0
        tempProduk = arrayListOf<Products>()
        daftarCartList = arrayListOf()
        countPayment = 0

        val auth = FirebaseAuth.getInstance()
        val userIdentity = auth.currentUser!!
        val idPenjual = "kJKchPfDIjVh0xhh9mEyixSFoso1"

        idUser = userIdentity.uid

        val idProduct = intent.getStringExtra(PreviewProdukActivity.EXTRA_ID_PRODUCT)!!
        reference = FirebaseDatabase.getInstance().getReference("dataProduk").child(idProduct)

        showListProduk()

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        referenceCart = FirebaseDatabase.getInstance().getReference("dataCartProduk").child(userIdentity.uid)

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        val referenceWishlist = FirebaseDatabase.getInstance().getReference("dataWishlistProduk").child(userIdentity.uid)
        referenceWishlist.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (i in snapshot.children){
                        val productWishlist = i.getValue(WishlistProducts::class.java)!!
                        if (productWishlist.idProduct == idProduct){
                            loveActive.visibility = View.VISIBLE
                        } else {
                            loveInactive.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        itemFiturPlus.setOnClickListener {
            if (countTotalProduk == stokProduk){
                alertDialog("GAGAL!", "Jumlah stock produk ini hanya $countTotalProduk stok", false)
            } else {
                countTotalProduk += 1
                totalProduk.text = "$countTotalProduk"
            }
        }
        itemFiturMinus.setOnClickListener {
            countTotalProduk -= 1
            if (countTotalProduk <= 0){
                alertDialog("GAGAL!", "Minimal pembelian produk harus 1", false)
                countTotalProduk += 1
            } else {
                totalProduk.text = "$countTotalProduk"
            }
        }

        loveInactive.setOnClickListener {
            loveActive.visibility = View.VISIBLE
            loveInactive.visibility = View.INVISIBLE
            val cartUpdate = CartProducts(userIdentity.uid, idProduct)
            referenceWishlist.child(idProduct).setValue(cartUpdate)
        }
        loveActive.setOnClickListener {
            loveInactive.visibility = View.VISIBLE
            loveActive.visibility = View.INVISIBLE
            referenceWishlist.child(idProduct).removeValue()
        }

        // Memasukkan data DaftarProduk ke dalam "daftarRecomList" sebagai array list
        daftarRecomList = arrayListOf<Products>()
        // Memanggil fungsi "showListProduk" yang digunakan untuk menampilkan recyclerview dari data yang sudah ada,
        // pada list
        showListProdukPopular()

        textBacaSelengkapnya = findViewById(R.id.textBacaSelengkapnya)
        textBacaSelengkapnya.setOnClickListener {
            Intent(applicationContext, DetailDeskripsiProdukPembeliActivity::class.java).also {
                it.putExtra("EXTRA_ID_PRODUCT", idProduct)
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top)
            }
        }

        itemFiturChat.setOnClickListener {
            Intent(applicationContext, ChatPembeliActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        itemFiturCart.setOnClickListener {
            Intent(applicationContext, KeranjangPembeliActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        itemFiturPayment.setOnClickListener {
            Intent(applicationContext, PembayaranPembeliActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        checkIsProductOnDatabase(idProduct)
        updateTransaction()
        updateNewChat(idPenjual, idUser)

        // Membuat reference yang nantinya akan digunakan untuk melakukan aksi ke database
        val referenceReview = FirebaseDatabase.getInstance().getReference("dataReviewProduk").child(idProduct)
        referenceReview.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var countReview = 0
                    for (i in snapshot.children){
                        val review = i.getValue(ReviewProduct::class.java)!!
                        if (review.review != ""){
                            countReview += 1
                        }
                    }
                    komentar.text = "Komentar ($countReview)"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        komentar.setOnClickListener {
            Intent(applicationContext, KomentarProdukPembeliActivity::class.java).also {
                it.putExtra("EXTRA_ID_PRODUCT", idProduct)
                startActivity(it)
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        btnBeli.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (countTotalProduk == 0){
                alertDialog("GAGAL!", "Produk ini gagal di beli karena stok habis", false)
            } else if (countTotalProduk < minimumPemesanan){
                alertDialog("GAGAL!", "Minimum pembelian produk ini adalah $minimumPemesanan produk", false)
            } else {
                Intent(applicationContext, PengirimanPembeliActivity::class.java).also {
                    it.putExtra(EXTRA_PRODUK, tempProduk)
                    it.putExtra(TOTAL_PRODUK, countTotalProduk)
                    startActivity(it)
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                    finish()
                }
            }
        }

        btnKeranjang.setOnClickListener {

            // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
            if (!isConnected(this)){
                showInternetDialog()
            }

            if (isProductOnCart){
                Toast.makeText(this, "Produk tersebut sudah ada di keranjang", Toast.LENGTH_SHORT).show()
            } else {
                val cartUpdate = CartProducts(userIdentity.uid, idProduct)
                referenceCart.child(idProduct).setValue(cartUpdate).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Produk tersebut berhasil ditambahkan di keranjang", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Produk tersebut gagal ditambahkan di keranjang", Toast.LENGTH_SHORT).show()
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

    private fun checkIsProductOnDatabase(idProduct: String) : Boolean{

        referenceCart.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                daftarCartList.clear()
                if (snapshot.exists()){
                    for (i in snapshot.children){
                        val carts = i.getValue(CartProducts::class.java)!!
                        daftarCartList.add(carts.idProduct)
                        if (carts.idProduct == idProduct){
                            isProductOnCart = true
                        }
                    }
                }
                val ref = FirebaseDatabase.getInstance().getReference("dataProduk")
                ref.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            var countCart = 0
                            for (i in snapshot.children){
                                val products = i.getValue(Products::class.java)!!
                                if (products.idProduct in daftarCartList && products.statusProduct != "deleted"){
                                    countCart += 1
                                }
                            }
                            if (countCart > 0){
                                textKeranjang.visibility = View.VISIBLE
                                textKeranjang.text = " $countCart "
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        return isProductOnCart
    }

    private fun showListProduk(){
        tempProduk.clear()
        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
        val menuListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val produk = dataSnapshot.getValue(Products::class.java)!!

                typeProduk.text = produk.kategoriProduct
                stokProduk = produk.stockProduct
                if (stokProduk != 0){
                    countTotalProduk = 1
                } else {
                    countTotalProduk = 0
                }
                minimumPemesanan = produk.minimumPemesananProduct
                totalProduk.text = "$countTotalProduk"
                namaProduk.text = produk.namaProduct
                ratingBar.rating = produk.ratingProduct
                ratingBar.isEnabled = false
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.CEILING
                textRate.setText("(${df.format(produk.ratingProduct)})")
                deskripsiProduk.text = produk.deskripsiProduct
                val formatter: NumberFormat = DecimalFormat("#,###")
                val price = produk.hargaProduct
                val promo = 100 - produk.hargaPromoProduct
                val totalPromo = ((promo.toFloat()/100) * produk.hargaProduct)
                val formattedNumberPrice: String = formatter.format(price)
                val formattedNumberPrice2: String = formatter.format(totalPromo.toLong())
                if (produk.jenisProduct == "flash sale"){
                    totalPriceSlash.paintFlags = totalPriceSlash.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
                    totalPriceSlash.visibility = View.VISIBLE
                    totalPriceSlash.text = "Rp. ${formattedNumberPrice}"
                    totalPricePromo.visibility = View.VISIBLE
                    totalPricePromo.text = "Rp. ${formattedNumberPrice2}"
                } else {
                    totalPrice.visibility = View.VISIBLE
                    totalPrice.text = "Rp. ${formattedNumberPrice}"
                }

                val imageSlider = findViewById<ImageSlider>(R.id.imageSlider)
                val imageList = ArrayList<SlideModel>()

                imageList.add(SlideModel(produk.photoProduct1))
                imageList.add(SlideModel(produk.photoProduct2))
                imageList.add(SlideModel(produk.photoProduct3))
                imageList.add(SlideModel(produk.photoProduct4))

                imageSlider.setImageList(imageList, ScaleTypes.FIT)

                tempProduk.add(produk)

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // handle error
            }
        }
        reference.addListenerForSingleValueEvent(menuListener)
    }

    // Membuat fungsi "showListProduk" yang digunakan untuk menampilkan data dari database ke dalam,
    // recyclerview
    private fun showListProdukPopular() {
        val ref = FirebaseDatabase.getInstance().getReference("dataProduk")

        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarRecomList.clear()
                    for (i in snapshot.children){
                        val products = i.getValue(Products::class.java)
                        if (products?.jenisProduct == "popular"){
                            daftarRecomList.add(products)
                        }
                    }

                    adapterRecom = RecomViewPagerAdapter(daftarRecomList)
                    recomViewPager.adapter = adapterRecom

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateTransaction() {
        val reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    countPayment = 0
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)!!
                        if (transaction.idUser == idUser && transaction.statusPembayaran == "pending"){
                            countPayment += 1
                        }
                    }
                    if (countPayment > 0){
                        textPayment.visibility = View.VISIBLE
                        textPayment.text = " $countPayment "
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun updateNewChat(senderId: String, receiverId: String){
        val databaseReferenceChat: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("dataChatting").child(idUser)

        databaseReferenceChat.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var countNewChat = 0
                for (i in snapshot.children) {
                    val chat = i.getValue(Chat::class.java)!!
                    if (chat.senderId.equals(senderId) && chat.receiverId.equals(receiverId)
                    ) {
                        if (chat.statusMessage == "0"){
                            countNewChat += 1
                        }
                    }
                }
                if (countNewChat > 0){
                    textChat.visibility = View.VISIBLE
                    textChat.text = " $countNewChat "
                }
            }
        })
    }

    companion object{
        const val EXTRA_ID_PRODUCT = "EXTRA_ID_PRODUCT"
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
                    if (!isConnected(this@DetailProdukPembeliActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: DetailProdukPembeliActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}