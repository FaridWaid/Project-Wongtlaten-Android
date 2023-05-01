package com.wongtlaten.application.modules.penjual.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.wongtlaten.application.R
import com.wongtlaten.application.ResetPasswordActivity
import com.wongtlaten.application.core.Products
import com.wongtlaten.application.core.Transaction
import com.wongtlaten.application.core.Users
import com.wongtlaten.application.modules.penjual.home.PreviewProdukActivity.Companion.EXTRA_ID_PRODUCT
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.properties.Delegates

class LaporanPenjualActivity : AppCompatActivity() {

    private lateinit var textTotalSaldo: TextView
    private lateinit var textJumlahTransaksi: TextView
    private lateinit var textJumlahProduk: TextView
    private lateinit var imageDetailProduk: RoundedImageView
    private lateinit var cardDetailProduk: CardView
    private lateinit var namaProduk: TextView
    private lateinit var price: TextView
    private lateinit var pricePromo: TextView
    private lateinit var stockProduk: TextView
    private lateinit var textJumlahCustomize: TextView
    private lateinit var textJumlahPembeli: TextView
    private lateinit var textTopPembeli: TextView
    private lateinit var textWorstPembeli: TextView
    private var totalSaldoPenjualan by Delegates.notNull<Long>()
    private var totalTransaksi by Delegates.notNull<Int>()
    private var totalCustomize by Delegates.notNull<Int>()
    private var totalPembeli by Delegates.notNull<Int>()
    private lateinit var daftarPembeli: ArrayList<Users>
    private lateinit var daftarProduk: ArrayList<Products>
    private lateinit var daftarTransaksiCancel: ArrayList<Transaction>
    private lateinit var hashTransaksi: HashMap<String, Int>
    private lateinit var idUser : String
    private var countCancel by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan_penjual)

        // Jika tidak ada koneksi internet maka akan memanggil fungsi "showInternetDialog"
        if (!isConnected(this)){
            showInternetDialog()
        }

        textTotalSaldo = findViewById(R.id.textTotalSaldo)
        textJumlahTransaksi = findViewById(R.id.jumlahTransaksi)
        textJumlahProduk = findViewById(R.id.jumlahProduk)
        cardDetailProduk = findViewById(R.id.cardDetailProduk)
        imageDetailProduk = findViewById(R.id.imageDetailProduk)
        namaProduk = findViewById(R.id.nameProduk)
        price = findViewById(R.id.price)
        pricePromo = findViewById(R.id.pricePromo)
        stockProduk = findViewById(R.id.stockProduk)
        textJumlahCustomize = findViewById(R.id.jumlahCustomize)
        textJumlahPembeli = findViewById(R.id.jumlahPembeli)
        textTopPembeli = findViewById(R.id.topPembeli)
        textWorstPembeli = findViewById(R.id.worstPembeli)
        totalSaldoPenjualan = 0
        totalTransaksi = 0
        totalCustomize = 0
        daftarPembeli = arrayListOf()
        daftarProduk = arrayListOf()
        daftarTransaksiCancel = arrayListOf()
        hashTransaksi = hashMapOf()
        idUser = ""
        countCancel = 0

        keepDataSaldoPenjualan()
        keepDataPembeli()
        keepDataProduk()

        // Ketika "backButton" di klik
        // overridePendingTransition digunakan untuk animasi dari intent
        val backButton: AppCompatImageView = findViewById(R.id.prevButton)
        backButton.setOnClickListener {
            // Jika berhasil maka akan pindah ke activity sebelumnya
            onBackPressed()
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }

    }

    private fun keepDataProduk() {
        val reference = FirebaseDatabase.getInstance().getReference("dataProduk")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarProduk.clear()
                    for (i in snapshot.children){
                        val product = i.getValue(Products::class.java)
                        if (product != null){
                            daftarProduk.add(product)
                        }
                    }
                    daftarProduk.sortByDescending { it.jumlahPembelianProduct }
                    if (daftarProduk[0].jumlahPembelianProduct != 0){
                        textJumlahProduk.text = "${daftarProduk[0].namaProduct} (${daftarProduk[0].jumlahPembelianProduct} Kali)"
                        Picasso.get().load(daftarProduk[0].photoProduct1).into(imageDetailProduk)
                        namaProduk.text = "${daftarProduk[0].namaProduct}"
                        val formatter: NumberFormat = DecimalFormat("#,###")
                        val priceReal = daftarProduk[0].hargaProduct
                        val promo = 100 - daftarProduk[0].hargaPromoProduct
                        val totalPromo = ((promo.toFloat()/100) * daftarProduk[0].hargaProduct)
                        val formattedNumberPrice: String = formatter.format(priceReal)
                        val formattedNumberPrice2: String = formatter.format(totalPromo.toLong())
                        price.text = "Rp. ${formattedNumberPrice}"
                        if (daftarProduk[0].jenisProduct == "flash sale"){
                            price.paintFlags = price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
                            pricePromo.visibility = View.VISIBLE
                            pricePromo.text = "Rp. ${formattedNumberPrice2}"
                        }
                        cardDetailProduk.setOnClickListener {
                            if (daftarProduk[0].statusProduct == "deleted"){
                                Toast.makeText(this@LaporanPenjualActivity, "Produk ini sudah dihapus dari database!", Toast.LENGTH_SHORT).show()
                            } else{
                                val moveIntent = Intent(this@LaporanPenjualActivity, PreviewProdukActivity::class.java)
                                moveIntent.putExtra(EXTRA_ID_PRODUCT, daftarProduk[0].idProduct)
                                startActivity(moveIntent)
                            }
                        }
                    } else {
                        textJumlahProduk.text = "Belum ada transaksi pembelian produk"
                    }
                } else {
                    textJumlahProduk.text = "Belum ada produk"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun keepDataPembeli() {
        val reference = FirebaseDatabase.getInstance().getReference("dataAkunUser")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarPembeli.clear()
                    totalPembeli = 0
                    for (i in snapshot.children){
                        val users = i.getValue(Users::class.java)
                        if (users != null && users.accessLevel == "customers"){
                            daftarPembeli.add(users)
                            totalPembeli += 1
                        }
                    }
                    textJumlahPembeli.text = totalPembeli.toString()
                    if (daftarPembeli.isNotEmpty()){
                        daftarPembeli.sortByDescending { it.jumlahTransaksi }
                        if (daftarPembeli[0].jumlahTransaksi == 0){
                            textTopPembeli.text = "Tidak ada pembeli yang melakukan transaksi"
                        } else {
                            textTopPembeli.text = "${daftarPembeli[0].username} (${daftarPembeli[0].jumlahTransaksi} Transaksi)"
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun keepDataSaldoPenjualan() {
        val reference = FirebaseDatabase.getInstance().getReference("dataTransaksi")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    daftarTransaksiCancel.clear()
                    totalSaldoPenjualan = 0
                    totalTransaksi = 0
                    totalCustomize = 0
                    for (i in snapshot.children){
                        val transaction = i.getValue(Transaction::class.java)
                        if (transaction != null && transaction.statusPembayaran == "settlement"){
                            totalSaldoPenjualan += transaction.totalPembayaran.toLong()
                            totalTransaksi += 1
                        }
                        if (transaction != null && transaction.statusPembayaran == "settlement" && transaction.jenisTransaksi == "custom"){
                            totalCustomize += 1
                        }
                        if (transaction != null && transaction.statusPembayaran == "expire"){
                            daftarTransaksiCancel.add(transaction)
                        }
                    }

                    val formatter: NumberFormat = DecimalFormat("#,###")
                    val formattedNumberPrice: String = formatter.format(totalSaldoPenjualan)
                    textTotalSaldo.text = "Rp. $formattedNumberPrice"
                    textJumlahTransaksi.text = "$totalTransaksi Kali"
                    textJumlahCustomize.text = "$totalCustomize Kali"
                    for (i in 0..daftarTransaksiCancel.size - 1){
                        if (daftarTransaksiCancel[i].idUser !in hashTransaksi){
                            hashTransaksi.put(daftarTransaksiCancel[i].idUser, 1)
                        } else {
                            var tempCount = hashTransaksi.getValue(daftarTransaksiCancel[i].idUser) + 1
                            hashTransaksi.put(daftarTransaksiCancel[i].idUser, tempCount)
                        }
                    }
                    if (hashTransaksi.isNotEmpty()){
                        val sortedMap = hashTransaksi.toList().sortedBy { (k, v) -> v }.toMap()
                        idUser = sortedMap.entries.last().key
                        countCancel = sortedMap.entries.last().value
                        // Mengambil data user dengan referen dan dimasukkan kedalam view (text,etc)
                        val referen = FirebaseDatabase.getInstance().getReference("dataAkunUser").child(idUser)
                        val menuListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val users = dataSnapshot.getValue(Users::class.java)!!
                                textWorstPembeli.text = "${users.username} (${countCancel} Kali)"
                            }
                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        }
                        referen.addListenerForSingleValueEvent(menuListener)
                    } else{
                        textWorstPembeli.text = "Tidak ada pembeli yang melakukan cancel transaksi"
                    }
                } else {
                    textJumlahCustomize.text = "0 Kali"
                    textJumlahTransaksi.text = "0 Kali"
                    textTotalSaldo.text = "Rp. 0"
                    textWorstPembeli.text = "Tidak ada pembeli yang melakukan cancel transaksi"
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
                    if (!isConnected(this@LaporanPenjualActivity)){
                        showInternetDialog()
                    }
                })
        }
        alertDialog.show()
    }

    // Fungsi untuk melakukan pengecekan apakah ada internet atau tidak
    private fun isConnected(contextActivity: LaporanPenjualActivity): Boolean {
        val connectivityManager = contextActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }

}